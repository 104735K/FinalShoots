-- DROP TABLE과 관련된 코드
DROP TABLE IF EXISTS business_user;

CREATE TABLE business_user(
    business_idx    INT AUTO_INCREMENT PRIMARY KEY,
    business_id     VARCHAR(30)  NOT NULL,
    password        VARCHAR(100)  NOT NULL,
    business_name   VARCHAR(100) NOT NULL,
    business_number varchar(10)       NOT NULL,
    tel             VARCHAR(15)  NOT NULL,
    email           VARCHAR(30)  NOT NULL,
    post            varchar(7)         NOT NULL,
    address         VARCHAR(100) NOT NULL,
    description     TEXT,
    register_date   TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    login_status    VARCHAR(9) DEFAULT 'pending',
    role          VARCHAR(10) DEFAULT 'business' NOT NULL
);

-- DROP SEQUENCE과 관련된 코드
-- MySQL에서는 AUTO_INCREMENT를 사용하여 Sequence 대체

-- SELECT 문
SELECT * FROM business_user;

-- UPDATE 문
UPDATE business_user
SET login_status = 'access';

-- INSERT 문
INSERT INTO business_user (business_idx, business_id, password, business_name, business_number, tel, email, post,
                           address, description, business_file, register_date, login_status)
VALUES (NULL, -- AUTO_INCREMENT 컬럼은 NULL 또는 값을 생략
        'qwerty1',
        '1234',
        '종로구 풋살장 A',
        1234567891,
        '028088080',
        'JongRsc@gmail.com',
        10882,
        '서울시 종로구 종로3가',
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        'approved'),
       (NULL,
        'qwerty2',
        '1234',
        '강남구 풋살장 B',
        1234567891,
        '354345',
        '222@gmail.com',
        6090,
        '서울특별시 강남구 학동로 426 (삼성동, 강남구청)',
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        'approved'),
       (NULL,
        'qwerty3',
        '1234',
        '강남구 풋살장 C',
        1234567891,
        '354345',
        '222@gmail.com',
        6090,
        '서울특별시 강남구 학동로 426 (삼성동, 강남구청)',
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        'approved'),
       (NULL,
        'qwerty4',
        'a1',
        'testsetstest',
        987654321232,
        '91099999999',
        'JongRsc@gmail.com',
        10882,
        '서울시 종로구 종로3가',
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        'pending');

-- DELETE 문
DELETE FROM business_user WHERE business_id = 'qwerty4';

-- SELECT 문
SELECT * FROM business_user;

-- UPDATE로 모든 tel 변경
UPDATE business_user SET tel = '01099999999';

-- ALTER TABLE로 컬럼 추가
ALTER TABLE business_user ADD COLUMN login_status VARCHAR(9) DEFAULT 'pending';
