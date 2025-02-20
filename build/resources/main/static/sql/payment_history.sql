CREATE TABLE payment_history (
     payment_history_idx INT AUTO_INCREMENT PRIMARY KEY,
     payment_idx INT NOT NULL,
     payment_status VARCHAR(10) NOT NULL,
     action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     merchant_uid VARCHAR(30) NOT NULL
);


# 자동 payment_idx update
DELIMITER $$

CREATE TRIGGER update_payment_idx_in_history
    AFTER INSERT ON PAYMENT
    FOR EACH ROW
BEGIN
    UPDATE payment_history
    SET payment_idx = NEW.payment_idx
    WHERE merchant_uid = NEW.merchant_uid;
END $$

DELIMITER ;

SHOW TRIGGERS;

select * from payment_history;

