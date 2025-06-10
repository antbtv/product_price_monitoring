INSERT INTO users (username, password, email, role, first_name, last_name, date_of_birth, phone_number, is_verified)
VALUES
    -- Пароль: admin
    ('admin1', '$2a$10$Vhe5Kn1nUwyrHN.bwoaMz.KT1PAFM/VQbej9FPTJq.ohYPfHe2MTu', 'admin1@example.com', 'ROLE_ADMIN', 'Сергей', 'Иванов', NULL, '11234567890', TRUE),
    -- Пароль: user
    ('user1', '$2a$10$UEtG91m2E3zTTlJMhoJlwuXrkyXoq/KR5YS.KjBuAdEsvfOQBvREK', 'user1@example.com', 'ROLE_USER', 'Иван', 'Сергеев', '1990-01-01', '+1234567890', FALSE);

INSERT INTO categories (category_name, parent_id)
VALUES
    ('Молочные продукты', NULL),
    ('Хлебобулочные изделия', NULL),
    ('Овощи и фрукты', NULL),
    ('Напитки', NULL),
    ('Сладости', NULL),
    ('Замороженные продукты', NULL);

INSERT INTO stores (store_name, address)
VALUES
    ('Пятёрочка', 'Москва, ул. Ленина, д. 1'),
    ('Перекрёсток', 'Москва, ул. Мира, д. 2'),
    ('ВкусВилл', 'Москва, ул. Ани Гайтеровой, д. 3'),
    ('Магнит', 'Москва, ул. Октябрьская, д. 4'),
    ('Азбука Вкуса', 'Москва, ул. Льва Толстого, д. 5'),
    ('Ашан', 'Москва, ул. Липецкая, д. 6'),
    ('Бристоль', 'Москва, ул. Пушкина, д. 7'),
    ('Лента', 'Москва, ул. Садовая, д. 8');

INSERT INTO products (product_name, category_id)
VALUES
    ('Молоко', 1),
    ('Хлеб', 2),
    ('Яблоки', 3),
    ('Сок', 4),
    ('Шоколад', 5),
    ('Мороженое', 6),
    ('Кефир', 1),
    ('Батон', 2);

INSERT INTO prices (product_id, store_id, price)
VALUES
    (1, 1, 60),
    (1, 2, 65),
    (2, 1, 30),
    (2, 3, 35),
    (3, 4, 50),
    (4, 5, 80),
    (5, 6, 100),
    (6, 7, 120),
    (7, 1, 55),
    (8, 2, 25);

INSERT INTO price_history (product_id, store_id, price)
VALUES
    (1, 2, 60),
    (2, 1, 30),
    (3, 4, 50),
    (4, 5, 80),
    (5, 6, 100),
    (6, 7, 120),
    (7, 1, 55),
    (8, 2, 25);

-- Данные для отображения графика
INSERT INTO price_history (product_id, store_id, price, recorded_at)
VALUES
    (1, 1, 60, '2025-05-01 10:00:00'),
    (1, 1, 62, '2025-05-08 10:00:00'),
    (1, 1, 61, '2025-05-15 10:00:00'),
    (1, 1, 63, '2025-05-22 10:00:00'),
    (1, 1, 64, '2025-05-29 10:00:00');

