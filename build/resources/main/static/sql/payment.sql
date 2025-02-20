CREATE TABLE payment (
     payment_idx INT AUTO_INCREMENT PRIMARY KEY,
     match_idx INT NOT NULL,
     seller_idx INT NOT NULL,
     buyer_idx INT NOT NULL,
     payment_method VARCHAR(10) NOT NULL,
     payment_amount DECIMAL(10, 2) NOT NULL,
     payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     payment_status VARCHAR(10) NOT NULL,
     merchant_uid VARCHAR(30) NOT NULL,
     imp_uid VARCHAR(30) NOT NULL
);

#                          FOREIGN KEY (match_idx) REFERENCES match_post(match_idx),  -- 외래 키 제약
#                          FOREIGN KEY (buyer_idx) REFERENCES regular_user(idx),  -- 외래 키 제약
#                          FOREIGN KEY (seller_idx) REFERENCES business_user(business_idx)  -- 외래 키 제약

select * from payment where match_idx = 24;

insert into payment (
    match_idx, seller_idx, buyer_idx, payment_method, payment_amount,
    payment_date, payment_status, merchant_uid, imp_uid
)
values (27, 1, 4, 'card', 1000,
                   CURRENT_TIMESTAMP, 'paid', 'merchant_27_1', 'imp_111'
       );

select * from payment where payment_status='paid' and seller_idx = 1;


