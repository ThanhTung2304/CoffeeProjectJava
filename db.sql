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
