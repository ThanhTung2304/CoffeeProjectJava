CREATE DATABASE IF NOT EXISTS coffee
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

drop database coffee;
USE coffee;

CREATE TABLE IF NOT EXISTS account (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employee (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        account_id INT NOT NULL UNIQUE,
                                        name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    position VARCHAR(50) NOT NULL,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_account
    FOREIGN KEY (account_id) REFERENCES account(id)
                                           ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS customer (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        account_id INT UNIQUE NULL,
                                        code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    point INT NOT NULL DEFAULT 0,
    status TINYINT(1) NOT NULL DEFAULT 1,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_account
    FOREIGN KEY (account_id) REFERENCES account(id)
                                            ON DELETE SET NULL ON UPDATE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         product_id INT NOT NULL UNIQUE,
                                         quantity INT NOT NULL DEFAULT 0,
                                         createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         updatedTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                         CONSTRAINT fk_inventory_product
                                         FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory_history (
                                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                                 product_id INT NOT NULL,
                                                 quantity_change INT NOT NULL,
                                                 action VARCHAR(20) NOT NULL,
    note VARCHAR(255),
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_product
    FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      product_id INT NOT NULL,
                                      ingredient_name VARCHAR(100) NOT NULL,
    amount DECIMAL(12,3) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    createdTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_recipe_product
    FOREIGN KEY (product_id) REFERENCES product(id)
                                                   ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservations (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            customer_name VARCHAR(100) NOT NULL,
    table_number INT NOT NULL,
    time DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    note VARCHAR(255)
    ) ENGINE=InnoDB;
ALTER TABLE reservations ADD COLUMN customer_id INT NULL AFTER id;

CREATE TABLE IF NOT EXISTS shift (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    createdTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updateTime DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employee_shift (
                                              id INT AUTO_INCREMENT PRIMARY KEY,
                                              employee_id INT NOT NULL,
                                              shift_id INT NOT NULL,
                                              work_date DATE NOT NULL,
                                              register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              CONSTRAINT uk_employee_shift UNIQUE (employee_id, shift_id, work_date),
    CONSTRAINT fk_es_employee
    FOREIGN KEY (employee_id) REFERENCES employee(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_es_shift
    FOREIGN KEY (shift_id) REFERENCES shift(id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS vouchers (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        code VARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    usage_limit INT NOT NULL DEFAULT 0,
    used_count INT NOT NULL DEFAULT 0,
    note VARCHAR(255)
    ) ENGINE=InnoDB;

DROP TABLE IF EXISTS `tables`;
CREATE TABLE IF NOT EXISTS tables (
                                      table_number INT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 2,
    floor INT NOT NULL DEFAULT 1,
    status VARCHAR(30) NOT NULL DEFAULT 'EMPTY',
    note VARCHAR(255)
    ) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS orders (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      order_code VARCHAR(30) NOT NULL UNIQUE,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note VARCHAR(255),
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

ALTER TABLE orders
    ADD COLUMN subtotal_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN voucher_code VARCHAR(50) NULL;

CREATE TABLE IF NOT EXISTS order_detail (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            order_id INT NOT NULL,
                                            product_id INT NOT NULL,
                                            product_name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_od_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_od_product
    FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB;

-- Seed data: chi chay neu chua ton tai.
-- Mat khau admin nay chi phu hop cho moi truong hoc tap; nen doi ngay khi trien khai.
INSERT INTO account (username, password, role, is_active)
SELECT 'admin', '123456789', 'ADMIN', 1
    WHERE NOT EXISTS (SELECT 1 FROM account WHERE username = 'admin');

INSERT INTO employee (account_id, name, phone, position)
SELECT a.id, 'Nguyen Van A', '0901234567', 'Admin'
FROM account a
WHERE a.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM employee e WHERE e.account_id = a.id
);

INSERT INTO product (name, price, is_active)
SELECT 'Black Coffee', 20000, 1
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Black Coffee');

INSERT INTO product (name, price, is_active)
SELECT 'Milk Coffee', 25000, 1
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Milk Coffee');

-- Kiem tra lien ket hai chieu.
SELECT e.id, e.name, e.account_id, a.username, a.role, a.is_active
FROM employee e
         JOIN account a ON a.id = e.account_id;

SELECT c.id, c.name, c.account_id, a.username, a.role, a.is_active
FROM customer c
         LEFT JOIN account a ON a.id = c.account_id;

INSERT INTO `tables` (name, capacity, floor, status, note) VALUES
                                                               ('Bàn 1', 4, 1, 'Trống', ''), ('Bàn 2', 4, 1, 'Trống', ''), ('Bàn 3', 4, 1, 'Trống', ''),
                                                               ('Bàn 4', 4, 1, 'Trống', ''), ('Bàn 5', 4, 1, 'Trống', ''), ('Bàn 6', 4, 1, 'Trống', ''),
                                                               ('Bàn 7', 4, 1, 'Trống', ''), ('Bàn 8', 4, 1, 'Trống', ''), ('Bàn 9', 4, 1, 'Trống', ''),
                                                               ('Bàn 10', 4, 1, 'Trống', ''), ('Bàn 11', 4, 1, 'Trống', ''), ('Bàn 12', 4, 1, 'Trống', ''),
                                                               ('Bàn 13', 4, 1, 'Trống', ''), ('Bàn 14', 4, 1, 'Trống', ''), ('Bàn 15', 4, 1, 'Trống', ''),
                                                               ('Bàn 16', 4, 2, 'Trống', ''), ('Bàn 17', 4, 2, 'Trống', ''), ('Bàn 18', 4, 2, 'Trống', ''),
                                                               ('Bàn 19', 4, 2, 'Trống', ''), ('Bàn 20', 4, 2, 'Trống', ''), ('Bàn 21', 4, 2, 'Trống', ''),
                                                               ('Bàn 22', 4, 2, 'Trống', ''), ('Bàn 23', 4, 2, 'Trống', ''), ('Bàn 24', 4, 2, 'Trống', ''),
                                                               ('Bàn 25', 4, 2, 'Trống', ''), ('Bàn 26', 4, 2, 'Trống', ''), ('Bàn 27', 4, 2, 'Trống', ''),
                                                               ('Bàn 28', 4, 2, 'Trống', ''), ('Bàn 29', 4, 2, 'Trống', ''), ('Bàn 30', 4, 2, 'Trống', '');

INSERT INTO product (name, price, is_active)
VALUES
    ('Espresso', 30000, 1),
    ('Americano', 35000, 1),
    ('Cappuccino', 45000, 1),
    ('Latte', 45000, 1),
    ('Mocha', 50000, 1),
    ('Caramel Macchiato', 55000, 1),
    ('Cold Brew', 45000, 1),
    ('Cold Brew Sữa Tươi', 50000, 1),
    ('Cà Phê Đen', 25000, 1),
    ('Cà Phê Sữa', 30000, 1),
    ('Bạc Xỉu', 35000, 1),
    ('Cà Phê Muối', 40000, 1),
    ('Trà Đào Cam Sả', 45000, 1),
    ('Trà Vải', 40000, 1),
    ('Trà Chanh', 30000, 1),
    ('Matcha Latte', 50000, 1),
    ('Chocolate Đá Xay', 55000, 1),
    ('Matcha Đá Xay', 55000, 1),
    ('Sinh Tố Xoài', 45000, 1),
    ('Nước Cam Ép', 40000, 1);

-- =========================================================
-- CÔNG THỨC PHA CHẾ 20 SẢN PHẨM
-- =========================================================

-- 1. ESPRESSO
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cà phê Espresso', 18, 'g'
FROM product WHERE name = 'Espresso';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Nước nóng', 30, 'ml'
FROM product WHERE name = 'Espresso';


-- 2. AMERICANO
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Espresso', 30, 'ml'
FROM product WHERE name = 'Americano';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Nước nóng', 120, 'ml'
FROM product WHERE name = 'Americano';


-- 3. CAPPUCCINO
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Espresso', 30, 'ml'
FROM product WHERE name = 'Cappuccino';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 100, 'ml'
FROM product WHERE name = 'Cappuccino';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Bọt sữa', 50, 'ml'
FROM product WHERE name = 'Cappuccino';


-- 4. LATTE
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Espresso', 30, 'ml'
FROM product WHERE name = 'Latte';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 180, 'ml'
FROM product WHERE name = 'Latte';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Bọt sữa', 20, 'ml'
FROM product WHERE name = 'Latte';


-- 5. MOCHA
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Espresso', 30, 'ml'
FROM product WHERE name = 'Mocha';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 150, 'ml'
FROM product WHERE name = 'Mocha';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sốt Chocolate', 25, 'ml'
FROM product WHERE name = 'Mocha';


-- 6. CARAMEL MACCHIATO
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Espresso', 30, 'ml'
FROM product WHERE name = 'Caramel Macchiato';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 150, 'ml'
FROM product WHERE name = 'Caramel Macchiato';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Syrup Caramel', 20, 'ml'
FROM product WHERE name = 'Caramel Macchiato';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sốt Caramel', 10, 'ml'
FROM product WHERE name = 'Caramel Macchiato';


-- 7. COLD BREW
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cà phê Cold Brew', 100, 'ml'
FROM product WHERE name = 'Cold Brew';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Nước lọc', 50, 'ml'
FROM product WHERE name = 'Cold Brew';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 100, 'g'
FROM product WHERE name = 'Cold Brew';


-- 8. COLD BREW SỮA TƯƠI
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cà phê Cold Brew', 100, 'ml'
FROM product WHERE name = 'Cold Brew Sữa Tươi';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 80, 'ml'
FROM product WHERE name = 'Cold Brew Sữa Tươi';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 100, 'g'
FROM product WHERE name = 'Cold Brew Sữa Tươi';


-- 9. CÀ PHÊ ĐEN
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cà phê rang xay', 25, 'g'
FROM product WHERE name = 'Cà Phê Đen';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Nước nóng', 80, 'ml'
FROM product WHERE name = 'Cà Phê Đen';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đường', 10, 'g'
FROM product WHERE name = 'Cà Phê Đen';


-- 10. CÀ PHÊ SỮA
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cà phê rang xay', 25, 'g'
FROM product WHERE name = 'Cà Phê Sữa';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Nước nóng', 80, 'ml'
FROM product WHERE name = 'Cà Phê Sữa';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa đặc', 30, 'ml'
FROM product WHERE name = 'Cà Phê Sữa';


-- 11. BẠC XỈU
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cà phê', 20, 'ml'
FROM product WHERE name = 'Bạc Xỉu';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa đặc', 30, 'ml'
FROM product WHERE name = 'Bạc Xỉu';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 120, 'ml'
FROM product WHERE name = 'Bạc Xỉu';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 100, 'g'
FROM product WHERE name = 'Bạc Xỉu';


-- 12. CÀ PHÊ MUỐI
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cà phê', 40, 'ml'
FROM product WHERE name = 'Cà Phê Muối';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa đặc', 25, 'ml'
FROM product WHERE name = 'Cà Phê Muối';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Kem béo', 40, 'ml'
FROM product WHERE name = 'Cà Phê Muối';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Muối', 1, 'g'
FROM product WHERE name = 'Cà Phê Muối';


-- 13. TRÀ ĐÀO CAM SẢ
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Trà đào', 150, 'ml'
FROM product WHERE name = 'Trà Đào Cam Sả';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Syrup đào', 20, 'ml'
FROM product WHERE name = 'Trà Đào Cam Sả';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đào miếng', 40, 'g'
FROM product WHERE name = 'Trà Đào Cam Sả';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cam', 30, 'g'
FROM product WHERE name = 'Trà Đào Cam Sả';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sả', 10, 'g'
FROM product WHERE name = 'Trà Đào Cam Sả';


-- 14. TRÀ VẢI
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Trà', 150, 'ml'
FROM product WHERE name = 'Trà Vải';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Syrup vải', 25, 'ml'
FROM product WHERE name = 'Trà Vải';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Vải', 40, 'g'
FROM product WHERE name = 'Trà Vải';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 100, 'g'
FROM product WHERE name = 'Trà Vải';


-- 15. TRÀ CHANH
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Trà', 150, 'ml'
FROM product WHERE name = 'Trà Chanh';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Nước cốt chanh', 20, 'ml'
FROM product WHERE name = 'Trà Chanh';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đường', 20, 'g'
FROM product WHERE name = 'Trà Chanh';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 100, 'g'
FROM product WHERE name = 'Trà Chanh';


-- 16. MATCHA LATTE
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Bột Matcha', 5, 'g'
FROM product WHERE name = 'Matcha Latte';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 180, 'ml'
FROM product WHERE name = 'Matcha Latte';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Syrup đường', 20, 'ml'
FROM product WHERE name = 'Matcha Latte';


-- 17. CHOCOLATE ĐÁ XAY
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Bột Chocolate', 20, 'g'
FROM product WHERE name = 'Chocolate Đá Xay';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 120, 'ml'
FROM product WHERE name = 'Chocolate Đá Xay';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa đặc', 20, 'ml'
FROM product WHERE name = 'Chocolate Đá Xay';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 150, 'g'
FROM product WHERE name = 'Chocolate Đá Xay';


-- 18. MATCHA ĐÁ XAY
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Bột Matcha', 6, 'g'
FROM product WHERE name = 'Matcha Đá Xay';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 120, 'ml'
FROM product WHERE name = 'Matcha Đá Xay';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa đặc', 20, 'ml'
FROM product WHERE name = 'Matcha Đá Xay';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 150, 'g'
FROM product WHERE name = 'Matcha Đá Xay';


-- 19. SINH TỐ XOÀI
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Xoài', 150, 'g'
FROM product WHERE name = 'Sinh Tố Xoài';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa tươi', 80, 'ml'
FROM product WHERE name = 'Sinh Tố Xoài';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Sữa đặc', 20, 'ml'
FROM product WHERE name = 'Sinh Tố Xoài';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 100, 'g'
FROM product WHERE name = 'Sinh Tố Xoài';


-- 20. NƯỚC CAM ÉP
INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Cam tươi', 250, 'g'
FROM product WHERE name = 'Nước Cam Ép';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đường', 10, 'g'
FROM product WHERE name = 'Nước Cam Ép';

INSERT INTO recipe (product_id, ingredient_name, amount, unit)
SELECT id, 'Đá viên', 100, 'g'
FROM product WHERE name = 'Nước Cam Ép';

SHOW COLUMNS FROM employee LIKE 'account_id';