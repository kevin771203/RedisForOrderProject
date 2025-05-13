CREATE TABLE IF NOT EXISTS product
(
    product_id         BIGINT          NOT NULL PRIMARY KEY,
    product_name       VARCHAR(128)  NOT NULL,
    category           VARCHAR(32)  NOT NULL,
    image_url          VARCHAR(256) NOT NULL,
    price              INT          NOT NULL,
    stock              INT          NOT NULL,
    description        VARCHAR(1024),
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id            BIGINT          NOT NULL PRIMARY KEY,
    email              VARCHAR(256) NOT NULL UNIQUE,
    password           VARCHAR(256) NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS `order`
(
    order_id           BIGINT       NOT NULL PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    total_amount       INT       NOT NULL,
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item
(
    order_item_id BIGINT NOT NULL PRIMARY KEY,
    order_id      BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    quantity      INT NOT NULL,
    amount        INT NOT NULL
);

CREATE TABLE IF NOT EXISTS role
(
    role_id   INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_has_role
(
    user_has_role_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id          BIGINT NOT NULL,
    role_id            INT NOT NULL
);



