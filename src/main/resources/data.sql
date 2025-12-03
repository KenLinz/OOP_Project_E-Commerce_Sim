INSERT INTO users (id, username, password, email, state)
VALUES (1, 'admin', '123', 'admin@gmail.com', 'CO');

INSERT INTO products (name, cost)
VALUES ('Keyboard', 125.99), ('Mouse', 45.00), ('Monitor', 499.99), ('Webcam', 75.50);

INSERT INTO carts (user_id)
VALUES (1);

INSERT INTO cart_items (cart_id, product_id, quantity)
VALUES (1, 1, 2);

INSERT INTO cart_items (cart_id, product_id, quantity)
VALUES (1, 2, 3);

INSERT INTO cart_items (cart_id, product_id, quantity)
VALUES (1, 3, 1);

INSERT INTO payment_methods (user_id, payment_type, balance, payment_card_number, payment_card_pin, payment_card_name)
VALUES (1, 'Visa', 1000.00, '1234123412341234', '1234', 'ADMIN ADMIN');

INSERT INTO payment_methods (user_id, payment_type, balance, payment_email, payment_password)
VALUES (1, 'Paypal', 200.50, 'admin.paypal@example.com', '123');

ALTER TABLE users ALTER COLUMN id RESTART WITH 2;


