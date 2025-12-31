CREATE DATABASE IF NOT EXISTS coffee;
USE coffee;

CREATE TABLE IF NOT EXISTS account (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    role VARCHAR(20),
    is_active TINYINT(1) NOT NULL DEFAULT 1
    );

-- Thêm admin nếu chưa tồn tại
INSERT INTO account (username, password, is_active)
SELECT 'admin', '123456789', 1
    WHERE NOT EXISTS (
    SELECT 1 FROM account WHERE username = 'admin'
);

CREATE TABLE IF NOT EXISTS employee (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    position VARCHAR(50),

    account_id INT UNIQUE,  -- 1 account ↔ 1 employee

    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee_account
    FOREIGN KEY (account_id)
    REFERENCES account(id)
                                           ON DELETE SET NULL
                                           ON UPDATE CASCADE
    );
INSERT INTO employee (name, phone, position, account_id)
VALUES
    ('Nguyễn Văn A', '0901234567', 'Admin', 1),
    ('Trần Thị B', '0912345678', 'Staff', NULL);
select *from employee;

SELECT id, name, account_id FROM employee;
UPDATE employee SET account_id = 2 WHERE id = 1;
UPDATE employee SET account_id = 1 WHERE id = 2;

SELECT 
    e.name,
    a.username
FROM employee e
LEFT JOIN account a ON e.account_id = a.id;

CREATE TABLE IF NOT EXISTS product (

                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedTime TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP
    );

                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,

    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,

    price DECIMAL(12,2) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedTime TIMESTAMP NULL DEFAULT NULL

    ON UPDATE CURRENT_TIMESTAMP
    );

        ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS reservations (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            customer_id INT NOT NULL,
                                            table_number INT NOT NULL,
                                            time DATETIME NOT NULL,
                                            status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES account(id)
    );

DROP TABLE IF EXISTS reservations;

CREATE TABLE reservations (

                              id INT AUTO_INCREMENT PRIMARY KEY,
                              customer_name VARCHAR(100) NOT NULL,
                              table_number INT NOT NULL,
                              time DATETIME NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              note VARCHAR(255) NULL

);

);

    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    table_number INT NOT NULL,
    time DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    note VARCHAR(255) NULL
);


CREATE TABLE inventory (
                           id INT AUTO_INCREMENT PRIMARY KEY,

                           product_id INT NOT NULL UNIQUE,
                           quantity INT NOT NULL DEFAULT 0,

                           createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updatedTime TIMESTAMP NULL DEFAULT NULL
        ON UPDATE CURRENT_TIMESTAMP,

                           CONSTRAINT fk_inventory_product
                               FOREIGN KEY (product_id)
                                   REFERENCES product(id)
                                   ON DELETE CASCADE
);
CREATE TABLE inventory_history (
                                   id INT AUTO_INCREMENT PRIMARY KEY,

                                   product_id INT NOT NULL,
                                   quantity_change INT NOT NULL, -- + nhập | - xuất
                                   action ENUM('IMPORT', 'EXPORT') NOT NULL,
                                   note VARCHAR(255),

                                   createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_history_product
                                       FOREIGN KEY (product_id)
                                           REFERENCES product(id)
                                           ON DELETE CASCADE
);


select * from inventory;
select* from inventory_history;
