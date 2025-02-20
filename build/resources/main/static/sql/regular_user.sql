DROP TABLE IF EXISTS regular_user;
CREATE TABLE regular_user
(
    idx           INT AUTO_INCREMENT PRIMARY KEY,
    user_id       VARCHAR(60)                  NOT NULL,
    password      VARCHAR(100)                  NOT NULL,
    name          VARCHAR(20)                  NOT NULL,
    jumin         VARCHAR(9),
    gender        TINYINT,
    tel           VARCHAR(15),
    email         VARCHAR(30),
    register_date TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    role          VARCHAR(10) DEFAULT 'common'
) ENGINE = InnoDB;

-- 아이디가 admin일 경우 role 업데이트
UPDATE regular_user
SET role = 'admin'
WHERE user_id = 'admin';

-- 테스트 데이터 삽입
INSERT INTO regular_user (user_id, password, name, jumin, gender, tel, email, register_date, role)
VALUES
    ('youngsoo1', '1', '일수', '111111', 1, '01012345678', '1@1.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('youngsoo2', '2', '이수', '222222', 2, '01012345678', '2@2.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('youngsoo3', '3', '삼수', '333333', 3, '01012345678', '3@3.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test', 'a1', '강성현', '000305', 4, '01097117305', 'shk7357@naver.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test1', 'a1', '강강강', '990101', 1, '01000001234', 'nid@naver.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test2', 'a1', '김동휘', '020102', 3, '01099829384', 'gid@gmail.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test3', 'a1', '임현빈', '880910', 2, '01026374637', 'did@daum.net', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test4', 'a1', '최영수', '890207', 2, '01026737374', 'sidi@naver.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test5', 'a1', '최주경', '981215', 1, '01092837465', 'lkjfd@naver.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test6', 'a1', '김임최', '840506', 2, '01083848384', 'iudhsf7@naver.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test7', 'a1', '강김임최', '980919', 2, '01098723231', 'uiasdf@naver.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('test8', 'a1', '강김임', '910203', 1, '01083848181', 'opghjk@naver.com', NULL, NULL, CURRENT_TIMESTAMP, 'common'),
    ('Admin', 'a1', '관리자', '990101', 1, '01074839283', 'admin@gmail.com', NULL, NULL, CURRENT_TIMESTAMP, 'admin');
    
-- 모든 사용자의 비밀번호를 'a1'로 업데이트
UPDATE regular_user
SET password = 'a1';

-- 특정 결제 상태 조회용 SELECT 쿼리
SELECT *
FROM regular_user u
         JOIN payment p ON u.idx = p.buyer
WHERE p.match_id = 31 AND p.status = 'SUCCESS';

select * from regular_user;

-- 아이디 찾을때 이메일을 적고, 해당 이메일 주소가 있으면 해당 id를 뽑아내도록 함.
select user_id from regular_user
where email = '1@1.111'
and tel = '1';