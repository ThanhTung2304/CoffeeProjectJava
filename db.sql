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

