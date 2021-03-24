CREATE TABLE IF NOT EXISTS messages
(
    id VARCHAR NOT NULL DEFAULT uuid_generate_v1() PRIMARY KEY,
    content                VARCHAR      NOT NULL,
    content_type           VARCHAR(128) NOT NULL,
    sent                   TIMESTAMP    NOT NULL,
    username               VARCHAR(60)  NOT NULL,
    user_avatar_image_link VARCHAR(256) NOT NULL
);