import os
import re

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
from bs4 import BeautifulSoup
import pandas as pd
from pymongo import MongoClient, UpdateOne
from pymongo.errors import BulkWriteError
import time
import logging

# ── 로깅 설정 (콘솔 + UTF-8 파일) ─────────────────────────────
logger = logging.getLogger()
logger.setLevel(logging.INFO)

_formatter = logging.Formatter("%(asctime)s %(message)s")

_stream_handler = logging.StreamHandler()
_stream_handler.setFormatter(_formatter)
logger.addHandler(_stream_handler)

_file_handler = logging.FileHandler("crawling.log", encoding="utf-8")
_file_handler.setFormatter(_formatter)
logger.addHandler(_file_handler)
# ──────────────────────────────────────────────────────────────

# ── 재시도 로직이 적용된 requests.Session ─────────────────────
_retry_strategy = Retry(
    total=3,
    backoff_factor=0.5,
    status_forcelist=[429, 500, 502, 503, 504],
)
_adapter = HTTPAdapter(max_retries=_retry_strategy)

session = requests.Session()
session.mount("https://", _adapter)
session.mount("http://", _adapter)
# ──────────────────────────────────────────────────────────────

# ── MongoDB 설정 ────────────────────────────────────────────────
MONGODB_URI = os.getenv("MONGODB_URI", "mongodb://localhost:27017")
DB_NAME = os.getenv("MONGODB_DB", "songs")
COLLECTION_NAME = "tj_songs"
# ────────────────────────────────────────────────────────────────

BASE_URL = "https://www.tjmedia.com/song/accompaniment_search"
HEADERS  = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "Referer": "https://www.tjmedia.com/song/accompaniment",
}

# nationType: 가요=KOR, 팝송=ENG, 일본곡=JPN
NATION_MAP = {"가요": "KOR", "팝송": "ENG", "일본곡": "JPN"}

# accompaniment_search는 단일 초성(ㄱ) 미지원 -> 첫 글자 사용
# 한글: 모음별 첫 글자 + 영문 대소문자 + 숫자
KO_FIRST_CHARS = list(set(
    list("가나다라마바사아자차카타파하")   # ㅏ
    + list("거너더러머버서어저처커터퍼허")   # ㅓ
    + list("고노도로모보소오조초코토포호")   # ㅗ
    + list("구누두루무부수우주추쿠투푸후")   # ㅜ
    + list("그느드르므브스으즈츠크트프흐")   # ㅡ
    + list("기니디리미비시이지치키티피히")   # ㅣ
    + list("개내대래매배새애재채캐태패해")   # ㅐ
    + list("게네데레메베세에제체케테페헤")   # ㅔ
    + list("갸냐댜랴먀뱌샤야쟈챠캬탸퍄햐")   # ㅑ
    + list("겨녀뎌려며벼셔여져쳐켜텨펴혀")   # ㅕ
    + list("교뇨됴료묘뵤쇼요죠쵸쿄톄표효")   # ㅛ
    + list("규뉴듀류뮤뷰슈유쥬츄큐튜퓨휴")   # ㅠ
    + list("걔냬댸럐먜뱨섀얘쟤챼켸톄펴혜")   # ㅒ
    + list("계녜뎨례몌볘셰예졔쳬켸톄폐혜")   # ㅖ
    + list("봐놔돠롸뫄봐솨와좌촤콰톼퐈화")   # ㅘ
    + list("봬놰됴롸뭐봬쉐왜좨쵀쾌퇘퐤홰")   # ㅙ
    + list("뇌되뢰뫼뵈쇠외죄최쾨톄푀회")     # ㅚ
    + list("눠둬뤄뭐붜숴워줘춰쿼퉈풔훠")     # ㅝ
    + list("눼둬뤠뭬붸쉐웨줴췌쿼퉤풰훼")     # ㅞ
    + list("뉘뒤뤼뮈뷔쉬위쥐취퀴튀퓌휘")     # ㅟ
    + list("늬듸릐믜븨싀의즤츼킈틔픠희")     # ㅢ
))
EN_CHARS_UPPER = list("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
EN_CHARS_LOWER = list("abcdefghijklmnopqrstuvwxyz")
DIGITS = list("0123456789")
# 곡 제목 첫 글자에 등장할 수 있는 특수문자
SPECIAL_CHARS = list("!@#$%^&*()-_+=[]{}|;:'\",.<>?/~`")
JP_CHARS = list("アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン"
                "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん")

_ALL = KO_FIRST_CHARS + EN_CHARS_UPPER + EN_CHARS_LOWER + DIGITS + SPECIAL_CHARS

SEARCH_MAP = {
    # 한국곡: 한글 + 영문 + 숫자 + 특수문자
    "가요":  _ALL,
    # 팝송: 영문 + 한글 + 숫자 + 특수문자
    "팝송":  EN_CHARS_UPPER + EN_CHARS_LOWER + KO_FIRST_CHARS + DIGITS + SPECIAL_CHARS,
    # 일본곡: 가타카나 + 히라가나 + 한글 + 영문 + 숫자 + 특수문자
    "일본곡": JP_CHARS + KO_FIRST_CHARS + EN_CHARS_UPPER + EN_CHARS_LOWER + DIGITS + SPECIAL_CHARS,
}
# ──────────────────────────────────────────────────────────────

DETAIL_URL = "https://www.tjmedia.com/song/song_view"

BULK_BATCH_SIZE = 5000


def _parse_song_row(container) -> dict | None:
    """ul.grid-container.list 항목을 {brand, no, title, singer, composer, lyricist, release} 스키마로 변환"""
    # accompaniment_search: 곡번호(0), 곡제목(1), 가수(2), 작사가(3), 작곡가(4)
    # accompaniment(TOP100): 순위(0), 곡번호(1), 곡제목(2), 가수(3), 작사가(4), 작곡가(5)
    grid_items = container.select("li.grid-item")
    if len(grid_items) < 5:
        return None

    # 곡번호: .num2 span에서 추출 (곡번호30681 형태 방지)
    no_el = container.select_one("span.num2")
    no = no_el.get_text(strip=True) if no_el else ""
    if not no or not no.isdigit():
        return None

    # pos-type이 첫 번째면 accompaniment_search(0~4), 아니면 accompaniment(1~5)
    first_has_pos = container.select_one("li.grid-item.pos-type") == grid_items[0]
    idx = (0, 1, 2, 3, 4) if first_has_pos else (1, 2, 3, 4, 5)
    if idx[4] >= len(grid_items):
        return None

    return {
        "brand": "tj", # kumyoung, tj
        "no": no,      # 곡번호
        "title": grid_items[idx[1]].get_text(strip=True),  # 곡제목
        "singer": grid_items[idx[2]].get_text(strip=True), # 가수
        "composer": grid_items[idx[4]].get_text(strip=True), # 작곡가
        "lyricist": grid_items[idx[3]].get_text(strip=True), # 작사가
        "release": "", # 발매일
    }


def fetch_release(no: str, _delay: float = 0.2) -> str:
    """상세 페이지에서 발매일 수집. 실패 시 빈 문자열. YYYY-MM-DD 형식 반환."""
    try:
        res = session.get(
            DETAIL_URL,
            params={"intSeqNo": no},
            headers=HEADERS,
            timeout=10,
        )
        res.raise_for_status()
        res.encoding = "utf-8"
        soup = BeautifulSoup(res.text, "html.parser")
        text = soup.get_text()
        # YYYY-MM-DD, YYYY.MM.DD, YYYYMMDD 등 패턴
        m = re.search(r"(20\d{2})[-.]?(\d{2})[-.]?(\d{2})", text)
        if m:
            return f"{m.group(1)}-{m.group(2)}-{m.group(3)}"
    except Exception as e:
        logging.warning(f"발매일 수집 실패 (no={no}): {e}")
    return ""


def fetch_page(keyword: str, nat_type: str, page: int) -> tuple[list[dict], bool]:
    """한 페이지 파싱. (결과 리스트, 다음 페이지 존재 여부) 반환"""
    nation = NATION_MAP.get(nat_type, "KOR")
    params = {
        "pageNo": page,
        "pageRowCnt": 100,
        "nationType": nation,
        "strType": "1",       # 1=곡제목 검색
        "searchTxt": keyword,
    }
    try:
        res = session.get(BASE_URL, params=params, headers=HEADERS, timeout=15)
        res.raise_for_status()
        res.encoding = "utf-8"
    except requests.RequestException as e:
        logging.warning(f"요청 실패 ({keyword}/{nat_type}/{page}): {e}")
        return [], False

    soup = BeautifulSoup(res.text, "html.parser")
    # accompaniment_search: ul.grid-container.list (li > ul 구조)
    rows = soup.select("ul.grid-container.list")

    songs = []
    for row in rows:
        song = _parse_song_row(row)
        if song:
            songs.append(song)

    # 다음 페이지: pageNo 링크 또는 마지막 페이지 여부
    has_next = bool(
        soup.select_one(f"a[href*='pageNo={page + 1}']")
        or (len(songs) >= 100 and page < 100)  # 100곡이면 다음 페이지 시도
    )
    return songs, (has_next and len(songs) > 0)


def crawl_by_keyword(keyword: str, nat_type: str, delay: float) -> list[dict]:
    """초성/알파벳 하나에 대해 전 페이지 수집"""
    all_songs, page = [], 1
    while True:
        songs, has_next = fetch_page(keyword, nat_type, page)
        all_songs.extend(songs)
        if not has_next or not songs:
            break
        page += 1
        time.sleep(delay)
    return all_songs


def crawl_all(delay: float = 0.4, fetch_release_detail: bool = False) -> pd.DataFrame:
    """전체 크롤링. fetch_release_detail=True면 상세 페이지에서 발매일 수집 (요청 수 증가)"""
    seen = set()  # 중복 제거용 (no 기준)
    all_rows = []

    for nat_type, keywords in SEARCH_MAP.items():
        total_keywords = len(keywords)
        logging.info(f"== {nat_type} 시작 (키워드 {total_keywords}개) ==")
        for kw_idx, kw in enumerate(keywords, start=1):
            songs = crawl_by_keyword(kw, nat_type, delay)
            new = 0
            for s in songs:
                if s["no"] and s["no"] not in seen:
                    seen.add(s["no"])
                    if fetch_release_detail and not s.get("release"):
                        s["release"] = fetch_release(s["no"], delay)
                        time.sleep(delay)
                    all_rows.append(s)
                    new += 1
            logging.info(
                f"  [{nat_type}] ({kw_idx}/{total_keywords}) '{kw}' "
                f"-> 신규 {new}곡 | 누적 {len(all_rows)}곡"
            )
            time.sleep(delay)

    return pd.DataFrame(all_rows)


def _chunked(lst: list, size: int):
    """리스트를 size 단위로 분할하는 제너레이터"""
    for i in range(0, len(lst), size):
        yield lst[i : i + size]


def save_to_mongodb(
    df: pd.DataFrame,
    uri: str | None = None,
    db_name: str | None = None,
    collection_name: str | None = None,
) -> int:
    """DataFrame을 MongoDB에 upsert. brand+no 기준 중복 제거. 반환: 저장된 문서 수"""
    uri = uri or MONGODB_URI
    db_name = db_name or DB_NAME
    collection_name = collection_name or COLLECTION_NAME

    with MongoClient(uri) as client:
        db = client[db_name]
        coll = db[collection_name]

        # brand+no 기준 유니크 인덱스 (재실행 시 upsert용)
        coll.create_index([("brand", 1), ("no", 1)], unique=True)

        records = [r for r in df.to_dict("records") if r.get("no")]
        if not records:
            logging.warning("저장할 데이터가 없습니다.")
            return 0

        total_upserted = 0
        total_modified = 0

        for batch in _chunked(records, BULK_BATCH_SIZE):
            operations = [
                UpdateOne(
                    {"brand": "tj", "no": doc["no"]},
                    {"$set": doc},
                    upsert=True,
                )
                for doc in batch
            ]
            try:
                result = coll.bulk_write(operations, ordered=False)
                total_upserted += result.upserted_count
                total_modified += result.modified_count
            except BulkWriteError as bwe:
                logging.warning(
                    f"bulk_write 부분 실패: {bwe.details.get('writeErrors', [])}"
                )
                # 부분 성공 결과 반영
                total_upserted += bwe.details.get("nUpserted", 0)
                total_modified += bwe.details.get("nModified", 0)

        saved = total_upserted + total_modified
        logging.info(
            f"MongoDB 저장: {saved}건 (신규 {total_upserted}, 수정 {total_modified})"
        )
        return saved


if __name__ == "__main__":
    # fetch_release_detail=True: 상세 페이지에서 발매일 수집 (요청 수 대폭 증가)
    df = crawl_all(delay=0.4, fetch_release_detail=False)
    df.sort_values("no", inplace=True)

    # MongoDB 저장
    # save_to_mongodb(df)

    # CSV 추출
    df.to_csv("tj_all_songs.csv", index=False, encoding="utf-8-sig")
    print(f"\n완료! 총 {len(df)}곡 -> tj_all_songs.csv 저장")
