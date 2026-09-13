USE coffee;

-- Chạy một lần trên database cũ nếu account_id đã bị xóa khỏi employee.
ALTER TABLE employee
    ADD COLUMN account_id INT UNIQUE NULL;

ALTER TABLE employee
    ADD CONSTRAINT fk_employee_account
    FOREIGN KEY (account_id)
    REFERENCES account(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Sau đó gán tài khoản cho nhân viên hiện có theo đúng ID thực tế.
-- Ví dụ:
-- UPDATE employee SET account_id = 1 WHERE id = 1;
