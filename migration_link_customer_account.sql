USE coffee;

-- Chay mot lan tren database cu neu customer chua co account_id.
ALTER TABLE customer
    ADD COLUMN account_id INT UNIQUE NULL;

ALTER TABLE customer
    ADD CONSTRAINT fk_customer_account
    FOREIGN KEY (account_id)
    REFERENCES account(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Gan tai khoan cho khach hang hien co theo dung ID thuc te.
-- Vi du:
-- UPDATE customer SET account_id = 7 WHERE id = 1;
