# Songs - 플레이리스트 관리 서비스

노래를 검색하고 나만의 플레이리스트를 만들어 공유할 수 있는 웹 서비스입니다.

---

## 프로젝트 구조

```
songs/
├── front-end/       # React + TypeScript + Vite 프론트엔드
├── back-end/        # Spring Boot 4.0.3 + Groovy 백엔드
└── song-crawling/   # Python 크롤링 스크립트
```

---

## 주요 기능

- 구글 OAuth2 로그인 (Google 계정으로 소셜 로그인, JWT 발급)
- 노래 검색
- 내 플레이리스트 관리 (생성 · 수정 · 삭제)
- 플레이리스트 공유 (공개/비공개)
- 곡에 가사 / 메모 추가

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| 프론트엔드 | React 18 + TypeScript + Vite + shadcn/ui + Tailwind CSS |
| 백엔드 | Spring Boot 4.0.3 + Groovy + Spring Security + JPA |
| DB | PostgreSQL (유저, 플레이리스트) + MongoDB (노래 데이터, 예정) |
| 인증 | Google OAuth2 + JWT |
| 크롤러 | Python 3 |

---

## 인증 흐름 (Google OAuth2 + JWT)

```
1. 프론트 → GET /oauth2/authorization/google
2. 구글 로그인 페이지로 리다이렉트 (Spring Security 자동 처리)
3. 구글 로그인 완료 → GET /login/oauth2/code/google (Spring Security 처리)
4. CustomOAuth2UserService → DB에 유저 저장/조회
5. OAuth2SuccessHandler → JWT 발급
6. 프론트 /oauth2/callback?token=JWT 로 리다이렉트
7. 프론트에서 JWT를 localStorage에 저장
```

---

## 로컬 실행 방법

**백엔드**
- IntelliJ에서 `BackEndSpringbootApplication` 실행
- 포트: 8080

**프론트엔드**
```bash
cd front-end
npm install
npm run dev
```
- 포트: 5173

**크롤러**
```bash
cd song-crawling
pip install -r requirements.txt
python crawling.py
```
