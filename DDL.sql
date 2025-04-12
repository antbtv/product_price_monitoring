CREATE TABLE IF NOT EXISTS users
(
    user_id       SERIAL PRIMARY KEY,
    username      VARCHAR(50) UNIQUE                                      NOT NULL,
    password      VARCHAR(255)                                            NOT NULL,
    email         VARCHAR(100) UNIQUE                                     NOT NULL,
    role          VARCHAR(50) CHECK (role IN ('ROLE_ADMIN', 'ROLE_USER')) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS categories
(
    category_id   SERIAL PRIMARY KEY,
    category_name VARCHAR(100) UNIQUE NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS stores
(
    store_id   SERIAL PRIMARY KEY,
    store_name VARCHAR(100) NOT NULL,
    address    VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS products
(
    product_id   SERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category_id  INT REFERENCES categories (category_id) ON DELETE CASCADE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS prices
(
    price_id    SERIAL PRIMARY KEY,
    product_id  INT REFERENCES products (product_id) ON DELETE CASCADE,
    store_id    INT REFERENCES stores (store_id) ON DELETE CASCADE,
    price       INT NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (product_id, store_id, recorded_at)
    );

CREATE TABLE IF NOT EXISTS price_history
(
    price_history_id SERIAL PRIMARY KEY,
    product_id       INT REFERENCES products (product_id) ON DELETE CASCADE,
    store_id         INT REFERENCES stores (store_id) ON DELETE CASCADE,
    price            INT NOT NULL,
    recorded_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
