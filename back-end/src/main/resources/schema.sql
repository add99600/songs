-- =============================================
-- Songs Project - PostgreSQL DDL
-- =============================================

-- users
CREATE TABLE IF NOT EXISTS users (
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

-- playlists
CREATE TABLE IF NOT EXISTS playlists (
    id          BIGSERIAL       PRIMARY KEY,
    owner_id    BIGINT          NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    description TEXT,
    is_public   BOOLEAN         NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT fk_playlists_owner_id
        FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

-- playlist_songs
CREATE TABLE IF NOT EXISTS playlist_songs (
    id          BIGSERIAL       PRIMARY KEY,
    playlist_id BIGINT          NOT NULL,
    song_id     VARCHAR(24)     NOT NULL
                    CHECK (song_id ~ '^[a-f0-9]{24}$'),   -- MongoDB ObjectId hex format
    sort_order  INT             NOT NULL DEFAULT 0,
    lyrics_note TEXT,
    extra_info  TEXT,
    added_at    TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT uq_playlist_songs
        UNIQUE (playlist_id, song_id),
    CONSTRAINT fk_playlist_songs_playlist_id
        FOREIGN KEY (playlist_id) REFERENCES playlists (id) ON DELETE CASCADE
);

-- playlist_shares
CREATE TABLE IF NOT EXISTS playlist_shares (
    id                   BIGSERIAL   PRIMARY KEY,
    playlist_id          BIGINT      NOT NULL,
    shared_with_user_id  BIGINT      NOT NULL,
    permission           VARCHAR(10) NOT NULL DEFAULT 'READ'
                             CHECK (permission IN ('READ', 'EDIT')),
    shared_at            TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_playlist_shares
        UNIQUE (playlist_id, shared_with_user_id),
    CONSTRAINT fk_playlist_shares_playlist_id
        FOREIGN KEY (playlist_id) REFERENCES playlists (id) ON DELETE CASCADE,
    CONSTRAINT fk_playlist_shares_shared_with_user_id
        FOREIGN KEY (shared_with_user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- =============================================
-- 인덱스
-- =============================================
CREATE INDEX IF NOT EXISTS idx_playlists_owner_id         ON playlists (owner_id);
CREATE INDEX IF NOT EXISTS idx_playlist_songs_playlist_id ON playlist_songs (playlist_id, sort_order ASC);
CREATE INDEX IF NOT EXISTS idx_playlist_songs_song_id     ON playlist_songs (song_id);
CREATE INDEX IF NOT EXISTS idx_playlist_shares_playlist_id ON playlist_shares (playlist_id);
CREATE INDEX IF NOT EXISTS idx_playlist_shares_user_id    ON playlist_shares (shared_with_user_id);
CREATE INDEX IF NOT EXISTS idx_playlists_is_public        ON playlists (id) WHERE is_public = true;

-- =============================================
-- updated_at 자동 갱신 트리거
-- =============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE TRIGGER trg_playlists_updated_at
    BEFORE UPDATE ON playlists
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
