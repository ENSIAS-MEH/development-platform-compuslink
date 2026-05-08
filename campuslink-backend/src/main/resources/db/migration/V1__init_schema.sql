-- =============================================================================
-- CampusLink  –  V1 Full Schema Initialisation
-- =============================================================================

-- ---------------------------------------------------------------------------
-- 1.  PostgreSQL ENUM types
-- ---------------------------------------------------------------------------

CREATE TYPE user_role         AS ENUM ('STUDENT', 'RECRUITER', 'ADMIN');
CREATE TYPE item_condition    AS ENUM ('NEW', 'LIKE_NEW', 'GOOD', 'FAIR');
CREATE TYPE item_status       AS ENUM ('OPEN', 'SOLD', 'CLOSED');
CREATE TYPE coloc_status      AS ENUM ('OPEN', 'FULL', 'CLOSED');
CREATE TYPE housing_type      AS ENUM ('APARTMENT', 'HOUSE', 'STUDIO', 'ROOM');
CREATE TYPE offer_type        AS ENUM ('JOB', 'INTERNSHIP', 'PFE');
CREATE TYPE offer_status      AS ENUM ('OPEN', 'CLOSED');
CREATE TYPE app_status        AS ENUM ('PENDING', 'SEEN', 'ACCEPTED', 'REJECTED');
CREATE TYPE interest_status   AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED');
CREATE TYPE location_type     AS ENUM ('ON_SITE', 'REMOTE', 'HYBRID');
CREATE TYPE experience_level  AS ENUM ('STUDENT', 'JUNIOR', 'SENIOR');
CREATE TYPE amenity_type      AS ENUM (
    'WIFI', 'PARKING', 'ELEVATOR', 'AC',
    'WASHING_MACHINE', 'WATER_HEATER', 'BALCONY'
);
CREATE TYPE target_type       AS ENUM ('ITEM', 'COLOC', 'OFFER', 'USER');
CREATE TYPE report_reason     AS ENUM ('SPAM', 'INAPPROPRIATE', 'FAKE', 'ALREADY_SOLD', 'OTHER');
CREATE TYPE report_status     AS ENUM ('PENDING', 'REVIEWED', 'DISMISSED');

-- ---------------------------------------------------------------------------
-- 2.  users  (root table – no FKs)
-- ---------------------------------------------------------------------------

CREATE TABLE users (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255),
    refresh_token_hash  VARCHAR(255),
    role                user_role,
    full_name           VARCHAR(255),
    university          VARCHAR(255),
    city                VARCHAR(255),
    phone_number        VARCHAR(50),
    bio                 TEXT,
    profile_pic_url     VARCHAR(500),
    cv_url              VARCHAR(500),
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    is_verified         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role  ON users (role);

-- ---------------------------------------------------------------------------
-- 3.  items
-- ---------------------------------------------------------------------------

CREATE TABLE items (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id   UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title       VARCHAR(255)    NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2),
    city        VARCHAR(255),
    condition   item_condition,
    category    VARCHAR(100),
    status      item_status     NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_items_seller_id ON items (seller_id);
CREATE INDEX idx_items_status    ON items (status);
CREATE INDEX idx_items_city      ON items (city);

-- ---------------------------------------------------------------------------
-- 4.  item_images
-- ---------------------------------------------------------------------------

CREATE TABLE item_images (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id    UUID         NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    url        VARCHAR(500) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    is_cover   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_item_images_item_id ON item_images (item_id);

-- ---------------------------------------------------------------------------
-- 5.  item_interests
-- ---------------------------------------------------------------------------

CREATE TABLE item_interests (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id    UUID        NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    message    TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_item_interests_item_user UNIQUE (item_id, user_id)
);

CREATE INDEX idx_item_interests_item_id ON item_interests (item_id);
CREATE INDEX idx_item_interests_user_id ON item_interests (user_id);

-- ---------------------------------------------------------------------------
-- 6.  coloc_posts
-- ---------------------------------------------------------------------------

CREATE TABLE coloc_posts (
    id               UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    poster_id        UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title            VARCHAR(255)    NOT NULL,
    description      TEXT,
    city             VARCHAR(255),
    address          VARCHAR(500),
    start_date       DATE,
    spots_needed     INT             NOT NULL,
    spots_confirmed  INT             NOT NULL DEFAULT 0,
    housing_type     housing_type,
    rent_per_person  NUMERIC(10, 2),
    furnished        BOOLEAN         NOT NULL DEFAULT FALSE,
    status           coloc_status    NOT NULL DEFAULT 'OPEN',
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_coloc_posts_poster_id ON coloc_posts (poster_id);
CREATE INDEX idx_coloc_posts_status    ON coloc_posts (status);
CREATE INDEX idx_coloc_posts_city      ON coloc_posts (city);

-- ---------------------------------------------------------------------------
-- 7.  coloc_images
-- ---------------------------------------------------------------------------

CREATE TABLE coloc_images (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id    UUID         NOT NULL REFERENCES coloc_posts (id) ON DELETE CASCADE,
    url        VARCHAR(500) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    is_cover   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_coloc_images_post_id ON coloc_images (post_id);

-- ---------------------------------------------------------------------------
-- 8.  coloc_amenities
-- ---------------------------------------------------------------------------

CREATE TABLE coloc_amenities (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id      UUID         NOT NULL REFERENCES coloc_posts (id) ON DELETE CASCADE,
    amenity_type amenity_type NOT NULL,
    CONSTRAINT uq_coloc_amenities_post_type UNIQUE (post_id, amenity_type)
);

CREATE INDEX idx_coloc_amenities_post_id ON coloc_amenities (post_id);

-- ---------------------------------------------------------------------------
-- 9.  coloc_interests
-- ---------------------------------------------------------------------------

CREATE TABLE coloc_interests (
    id         UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id    UUID            NOT NULL REFERENCES coloc_posts (id) ON DELETE CASCADE,
    user_id    UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    message    TEXT,
    status     interest_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ     NOT NULL DEFAULT now(),
    CONSTRAINT uq_coloc_interests_post_user UNIQUE (post_id, user_id)
);

CREATE INDEX idx_coloc_interests_post_id ON coloc_interests (post_id);
CREATE INDEX idx_coloc_interests_user_id ON coloc_interests (user_id);

-- ---------------------------------------------------------------------------
-- 10. offers
-- ---------------------------------------------------------------------------

CREATE TABLE offers (
    id               UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    poster_id        UUID             NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    type             offer_type       NOT NULL,
    title            VARCHAR(255)     NOT NULL,
    company          VARCHAR(255),
    city             VARCHAR(255),
    location_type    location_type,
    experience_level experience_level,
    duration         VARCHAR(100),
    description      TEXT,
    domain           VARCHAR(100),
    deadline         DATE,
    status           offer_status     NOT NULL DEFAULT 'OPEN',
    created_at       TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE INDEX idx_offers_poster_id ON offers (poster_id);
CREATE INDEX idx_offers_status    ON offers (status);
CREATE INDEX idx_offers_type      ON offers (type);

-- ---------------------------------------------------------------------------
-- 11. applications
-- ---------------------------------------------------------------------------

CREATE TABLE applications (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    offer_id         UUID        NOT NULL REFERENCES offers (id) ON DELETE CASCADE,
    applicant_id     UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    cv_url_snapshot  VARCHAR(500),
    message          TEXT,
    status           app_status  NOT NULL DEFAULT 'PENDING',
    applied_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_applications_offer_applicant UNIQUE (offer_id, applicant_id)
);

CREATE INDEX idx_applications_offer_id     ON applications (offer_id);
CREATE INDEX idx_applications_applicant_id ON applications (applicant_id);

-- ---------------------------------------------------------------------------
-- 12. reports  (polymorphic — no FK on target_id by design)
-- ---------------------------------------------------------------------------

CREATE TABLE reports (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_id UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    target_type target_type   NOT NULL,
    target_id   UUID          NOT NULL,
    reason      report_reason NOT NULL,
    details     TEXT,
    status      report_status NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    reviewed_at TIMESTAMPTZ
);

CREATE INDEX idx_reports_reporter_id           ON reports (reporter_id);
CREATE INDEX idx_reports_target_type_target_id ON reports (target_type, target_id);
CREATE INDEX idx_reports_status                ON reports (status);

-- ---------------------------------------------------------------------------
-- 13. saved_items  (polymorphic — no FK on target_id by design)
-- ---------------------------------------------------------------------------

CREATE TABLE saved_items (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    target_type target_type NOT NULL,
    target_id   UUID        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_saved_items_user_type_target UNIQUE (user_id, target_type, target_id)
);

CREATE INDEX idx_saved_items_user_id               ON saved_items (user_id);
CREATE INDEX idx_saved_items_target_type_target_id ON saved_items (target_type, target_id);
