ALTER TABLE transactions
    ADD COLUMN balance NUMERIC(19, 2);

UPDATE transactions
SET balance = amount;

ALTER TABLE transactions
    ALTER COLUMN balance SET NOT NULL;

ALTER TABLE transactions
    ADD CONSTRAINT ck_transactions_balance_range CHECK (
        (amount < 0 AND balance >= amount AND balance <= 0)
        OR (amount > 0 AND balance >= 0 AND balance <= amount)
    );