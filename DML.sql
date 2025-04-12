INSERT INTO users (username, password, email, role)
VALUES ('admin', 'admin_password', 'admin@m.ru', 'ROLE_ADMIN'),
       ('user1', 'user1_password', 'user1@m.ru', 'ROLE_USER'),
       ('user2', 'user2_password', 'user2@m.ru', 'ROLE_USER');

INSERT INTO categories (category_name)
VALUES ('Молочные продукты'),
       ('Хлебобулочные изделия'),
       ('Овощи и фрукты'),
       ('Напитки'),
       ('Сладости');

INSERT INTO stores (store_name, address)
VALUES ('Пятёрочка', 'Москва, ул. Ленина, д. 1'),
       ('Перекрёсток', 'Москва, ул. Мира, д. 2'),
       ('ВкусВилл', 'Москва, ул. Ани Гайтеровой, д. 3'),
       ('Магнит', 'Москва, ул. Октябрьская, д. 4'),
       ('Азбука Вкуса', 'Москва, ул. Льва Толстого, д. 5'),
       ('Ашан', 'Москва, ул. Липецкая, д. 6'),
       ('Бристоль', 'Москва, ул. Пушкина, д. 7');

INSERT INTO products (product_name, category_id)
VALUES ('Молоко', 1),
       ('Хлеб', 2),
       ('Яблоки', 3),
       ('Сок', 4),
       ('Шоколад', 5);

INSERT INTO prices (product_id, store_id, price)
VALUES (1, 1, 60),
       (1, 2, 65),
       (2, 1, 30),
       (2, 3, 35),
       (3, 4, 50),
       (4, 5, 80),
       (5, 6, 100);

INSERT INTO price_history (product_id, store_id, price)
VALUES (1, 1, 60),
       (2, 1, 30),
       (3, 4, 50),
       (4, 5, 80),
       (5, 6, 100);
