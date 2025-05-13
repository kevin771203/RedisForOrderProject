-- product
INSERT INTO product (product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES (1,'蘋果（澳洲）', 'FOOD', 'https://cdn.pixabay.com/photo/2016/11/30/15/00/apples-1872997_1280.jpg', 30, 10, '這是來自澳洲的蘋果！', '2022-03-19 17:00:00', '2022-03-22 18:00:00');
INSERT INTO product (product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES (2,'蘋果（日本北海道）', 'FOOD', 'https://cdn.pixabay.com/photo/2017/09/26/13/42/apple-2788662_1280.jpg', 300, 5, '這是來自日本北海道的蘋果！', '2022-03-19 18:30:00', '2022-03-19 18:30:00');
INSERT INTO product (product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES (3,'好吃又鮮甜的蘋果橘子', 'FOOD', 'https://cdn.pixabay.com/photo/2021/07/30/04/17/orange-6508617_1280.jpg', 10, 50, null, '2022-03-20 09:00:00', '2022-03-24 15:00:00');
INSERT INTO product (product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES (4,'Toyota', 'CAR', 'https://cdn.pixabay.com/photo/2014/05/18/19/13/toyota-347288_1280.jpg', 100000, 5, null, '2022-03-20 09:20:00', '2022-03-20 09:20:00');
INSERT INTO product (product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES (5,'BMW', 'CAR', 'https://cdn.pixabay.com/photo/2018/02/21/03/15/bmw-m4-3169357_1280.jpg', 500000, 3, '渦輪增壓，直列4缸，DOHC雙凸輪軸，16氣門', '2022-03-20 12:30:00', '2022-03-20 12:30:00');
INSERT INTO product (product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES (6,'Benz', 'CAR', 'https://cdn.pixabay.com/photo/2017/03/27/14/56/auto-2179220_1280.jpg', 600000, 2, null, '2022-03-21 20:10:00', '2022-03-22 10:50:00');
INSERT INTO product (product_id, product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES (7,'Tesla', 'CAR', 'https://cdn.pixabay.com/photo/2021/01/15/16/49/tesla-5919764_1280.jpg', 450000, 5, '世界最暢銷的充電式汽車', '2022-03-21 23:30:00', '2022-03-21 23:30:00');


-- user
INSERT INTO users (user_id, email, password, created_date, last_modified_date) VALUES (1,'user1@gmail.com', '$2a$10$I8ZXl7tSfZg31fqFX9h/COCm7aftOvdN2C4gE4QI5hePkQOi7iXIS', '2022-06-30 10:30:00', '2022-06-30 10:30:00');
INSERT INTO users (user_id, email, password, created_date, last_modified_date) VALUES (2,'user2@gmail.com', '$2a$10$I8ZXl7tSfZg31fqFX9h/COCm7aftOvdN2C4gE4QI5hePkQOi7iXIS', '2022-06-30 10:40:00', '2022-06-30 10:40:00');

-- order, order_item

INSERT INTO `order` (order_id, user_id, total_amount, created_date, last_modified_date) VALUES (1,1, 500690, '2022-06-30 11:10:00', '2022-06-30 11:10:00');
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (1,1, 1, 3, 90);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (2,1, 2, 2, 600);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (3,1, 5, 1, 500000);

INSERT INTO `order` (order_id, user_id, total_amount, created_date, last_modified_date) VALUES (2,1, 100000, '2022-06-30 12:03:00', '2022-06-30 12:03:00');
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (4,2, 4, 1, 100000);

-- role
INSERT INTO role(role_name) VALUES ('ROLE_ADMIN');
INSERT INTO role(role_name) VALUES ('ROLE_NORMAL_MEMBER');

INSERT INTO user_has_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_has_role (user_id, role_id) VALUES (2, 2);


