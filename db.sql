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

CREATE TABLE vouchers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,          -- Mã voucher
    discount_type ENUM('PERCENT','AMOUNT') NOT NULL, -- Loại giảm: % hoặc số tiền
    discount_value DECIMAL(10,2) NOT NULL,     -- Giá trị giảm
    start_date DATE NOT NULL,                  -- Ngày bắt đầu
    end_date DATE NOT NULL,                    -- Ngày kết thúc
    status ENUM('ACTIVE','EXPIRED','USED') NOT NULL DEFAULT 'ACTIVE', -- Trạng thái
    usage_limit INT DEFAULT 0,                 -- Giới hạn số lượt (0 = không giới hạn)
    used_count INT DEFAULT 0,                  -- Số lượt đã dùng
    note VARCHAR(255)                          -- Ghi chú
);

ALTER TABLE vouchers
    MODIFY status ENUM('Còn hiệu lực','Hết hạn','Đã sử dụng')
    NOT NULL DEFAULT 'Còn hiệu lực';
ALTER TABLE vouchers
    MODIFY discount_type ENUM('Phần trăm','Số tiền') NOT NULL;