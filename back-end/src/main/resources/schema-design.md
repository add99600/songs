# Songs 프로젝트 DB 스키마 설계

## 개요

| 저장소 | 담당 도메인 |
|---|---|
| PostgreSQL | 유저, 인증(OAuth2), 플레이리스트, 공유, 플레이리스트-곡 관계 |
| MongoDB | 노래 메타데이터 (크롤링 원본 데이터) |

---

## PostgreSQL 스키마

### ERD (텍스트)

```
users
 └─< playlists (owner_id → users.id)
       └─< playlist_songs (playlist_id → playlists.id)
       └─< playlist_shares (playlist_id → playlists.id)
             └─── shared_with_user_id → users.id
```

---

### 테이블 정의

#### 1. users

유저 기본 정보. 구글 OAuth2 전용이므로 password 컬럼 없음.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 내부 유저 식별자 |
| oauth_provider | VARCHAR(20) | NOT NULL | OAuth 제공자 (현재는 'google' 고정) |
| oauth_subject | VARCHAR(255) | NOT NULL | 제공자 측 고유 ID (Google sub claim) |
| email | VARCHAR(255) | NOT NULL | 유저 이메일 |
| nickname | VARCHAR(100) | NOT NULL | 유저 표시 이름 |
| profile_image_url | TEXT | NULLABLE | 프로필 이미지 URL |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT now() | 가입 일시 |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT now() | 마지막 수정 일시 |

제약조건:
- UNIQUE (oauth_provider, oauth_subject) : 동일 제공자에서 중복 계정 방지
- UNIQUE (email) : 이메일 중복 방지

```sql
CREATE TABLE users (
    id                BIGSERIAL       PRIMARY KEY,
    oauth_provider    VARCHAR(20)     NOT NULL,
    oauth_subject     VARCHAR(255)    NOT NULL,
    email             VARCHAR(255)    NOT NULL,
    nickname          VARCHAR(100)    NOT NULL,
    profile_image_url TEXT,
    created_at        TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT uq_users_provider_subject UNIQUE (oauth_provider, oauth_subject),
    CONSTRAINT uq_users_email            UNIQUE (email)
);
```

---

#### 2. playlists

유저가 소유한 플레이리스트. 한 유저가 여러 개 생성 가능.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 플레이리스트 식별자 |
| owner_id | BIGINT | NOT NULL, FK → users.id | 소유자 |
| name | VARCHAR(100) | NOT NULL | 플레이리스트 이름 |
| description | TEXT | NULLABLE | 설명 |
| is_public | BOOLEAN | NOT NULL DEFAULT false | 공개 여부 (공개 시 비공유 유저도 읽기 가능) |
| created_at | TIMESTAMPTZ | NOT NULL DEFAULT now() | 생성 일시 |
| updated_at | TIMESTAMPTZ | NOT NULL DEFAULT now() | 수정 일시 |

```sql
CREATE TABLE playlists (
    id          BIGSERIAL    PRIMARY KEY,
    owner_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    is_public   BOOLEAN      NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
```

---

#### 3. playlist_songs

플레이리스트에 담긴 곡 목록. song_id는 MongoDB의 songs 컬렉션 ObjectId를 문자열로 저장.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 행 식별자 |
| playlist_id | BIGINT | NOT NULL, FK → playlists.id | 소속 플레이리스트 |
| song_id | VARCHAR(24) | NOT NULL | MongoDB ObjectId (songs 컬렉션 참조) |
| sort_order | INTEGER | NOT NULL DEFAULT 0 | 재생 순서 |
| lyrics_note | TEXT | NULLABLE | 유저가 직접 입력한 가사 또는 메모 |
| extra_info | TEXT | NULLABLE | 유저 추가 정보 (자유 텍스트) |
| added_at | TIMESTAMPTZ | NOT NULL DEFAULT now() | 곡 추가 일시 |

제약조건:
- UNIQUE (playlist_id, song_id) : 동일 플레이리스트에 같은 곡 중복 추가 방지

```sql
CREATE TABLE playlist_songs (
    id           BIGSERIAL    PRIMARY KEY,
    playlist_id  BIGINT       NOT NULL REFERENCES playlists(id) ON DELETE CASCADE,
    song_id      VARCHAR(24)  NOT NULL,
    sort_order   INTEGER      NOT NULL DEFAULT 0,
    lyrics_note  TEXT,
    extra_info   TEXT,
    added_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_playlist_songs UNIQUE (playlist_id, song_id)
);
```

---

#### 4. playlist_shares

플레이리스트 공유 관계. 소유자가 다른 유저에게 권한을 부여.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 행 식별자 |
| playlist_id | BIGINT | NOT NULL, FK → playlists.id | 대상 플레이리스트 |
| shared_with_user_id | BIGINT | NOT NULL, FK → users.id | 공유 받은 유저 |
| permission | VARCHAR(10) | NOT NULL DEFAULT 'READ' | 권한 (READ / EDIT) |
| shared_at | TIMESTAMPTZ | NOT NULL DEFAULT now() | 공유 시작 일시 |

제약조건:
- UNIQUE (playlist_id, shared_with_user_id) : 동일 유저에게 중복 공유 방지
- CHECK permission IN ('READ', 'EDIT')

```sql
CREATE TABLE playlist_shares (
    id                   BIGSERIAL   PRIMARY KEY,
    playlist_id          BIGINT      NOT NULL REFERENCES playlists(id) ON DELETE CASCADE,
    shared_with_user_id  BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission           VARCHAR(10) NOT NULL DEFAULT 'READ',
    shared_at            TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_playlist_shares        UNIQUE (playlist_id, shared_with_user_id),
    CONSTRAINT chk_playlist_shares_perm  CHECK  (permission IN ('READ', 'EDIT'))
);
```

---

### 인덱스 전략

```sql
-- users
CREATE INDEX idx_users_email           ON users(email);
CREATE INDEX idx_users_provider_subject ON users(oauth_provider, oauth_subject);

-- playlists
CREATE INDEX idx_playlists_owner_id    ON playlists(owner_id);

-- playlist_songs
CREATE INDEX idx_playlist_songs_playlist_id ON playlist_songs(playlist_id);
CREATE INDEX idx_playlist_songs_song_id     ON playlist_songs(song_id);
-- sort_order 정렬 쿼리 최적화
CREATE INDEX idx_playlist_songs_order       ON playlist_songs(playlist_id, sort_order);

-- playlist_shares
CREATE INDEX idx_playlist_shares_playlist_id         ON playlist_shares(playlist_id);
CREATE INDEX idx_playlist_shares_shared_with_user_id ON playlist_shares(shared_with_user_id);
```

인덱스 설계 근거:

| 인덱스 | 이유 |
|---|---|
| users.email | OAuth2 로그인 시 이메일로 기존 유저 조회 |
| users.(oauth_provider, oauth_subject) | Google sub claim으로 유저 신원 확인 |
| playlists.owner_id | 내 플레이리스트 목록 조회 (가장 빈번한 쿼리) |
| playlist_songs.playlist_id | 플레이리스트 곡 목록 조회 |
| playlist_songs.song_id | 특정 곡이 담긴 플레이리스트 역조회 |
| playlist_songs.(playlist_id, sort_order) | 곡 순서대로 정렬 조회 최적화 |
| playlist_shares.playlist_id | 플레이리스트 공유자 목록 조회 |
| playlist_shares.shared_with_user_id | 나에게 공유된 플레이리스트 조회 |

---

## MongoDB 스키마

### 컬렉션: songs

크롤링으로 수집한 노래 메타데이터를 저장. PostgreSQL의 playlist_songs.song_id가 이 컬렉션의 _id를 참조.

```json
{
  "_id": "ObjectId",
  "title": "String",
  "artist": "String",
  "album": "String | null",
  "release_date": "String | null",
  "genre": ["String"],
  "duration_sec": "Number | null",
  "lyrics": "String | null",
  "source_url": "String | null",
  "crawled_at": "ISODate",
  "extra": {
    "melon_id": "String | null",
    "genie_id": "String | null",
    "bugs_id": "String | null"
  }
}
```

필드 설명:

| 필드 | 타입 | 설명 |
|---|---|---|
| _id | ObjectId | MongoDB 자동 생성 PK. PostgreSQL의 song_id로 참조 |
| title | String | 곡명 |
| artist | String | 아티스트명 |
| album | String (nullable) | 앨범명 |
| release_date | String (nullable) | 발매일 (YYYY-MM-DD) |
| genre | String[] | 장르 태그 목록 |
| duration_sec | Number (nullable) | 재생 시간(초) |
| lyrics | String (nullable) | 크롤링된 원본 가사 |
| source_url | String (nullable) | 크롤링 출처 URL |
| crawled_at | ISODate | 크롤링 수행 일시 |
| extra.melon_id | String (nullable) | 멜론 곡 ID |
| extra.genie_id | String (nullable) | 지니 곡 ID |
| extra.bugs_id | String (nullable) | 벅스 곡 ID |

MongoDB 인덱스:

```javascript
// 제목 + 아티스트로 검색
db.songs.createIndex({ title: "text", artist: "text" });

// 아티스트명 단독 조회
db.songs.createIndex({ artist: 1 });

// 크롤링 소스 중복 방지 (출처 URL 유일성)
db.songs.createIndex({ source_url: 1 }, { unique: true, sparse: true });

// 외부 서비스 ID로 역조회
db.songs.createIndex({ "extra.melon_id": 1 }, { sparse: true });
db.songs.createIndex({ "extra.genie_id": 1 }, { sparse: true });
db.songs.createIndex({ "extra.bugs_id": 1 }, { sparse: true });
```

---

## 크로스-DB 참조 흐름

```
PostgreSQL                          MongoDB
─────────────────────────           ─────────────────────
playlist_songs.song_id  ──────────► songs._id (ObjectId)
  (VARCHAR(24))                       (ObjectId → 문자열 변환)
```

- PostgreSQL은 MongoDB의 ObjectId를 24자리 HEX 문자열(VARCHAR(24))로 저장.
- JPA 엔티티에서는 song_id를 String 타입으로 관리하며, 노래 상세 정보가 필요한 경우 MongoRepository를 통해 별도 조회.
- 두 DB 간 외래 키 제약조건은 애플리케이션 레이어에서 관리(DB 레벨 강제 불가).

---

## JPA 엔티티 매핑 요약

| 테이블 | Groovy 엔티티 클래스 | 패키지 |
|---|---|---|
| users | User | auth |
| playlists | Playlist | playlist |
| playlist_songs | PlaylistSong | playlist |
| playlist_shares | PlaylistShare | playlist |

MongoDB songs 컬렉션은 `@Document` 어노테이션을 사용하는 별도 Document 클래스로 관리.

| 컬렉션 | Groovy Document 클래스 | 패키지 |
|---|---|---|
| songs | Song | song |
