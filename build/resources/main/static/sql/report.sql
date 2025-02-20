DROP TABLE IF EXISTS Report;

-- CREATE TABLE Report (
--     reportIdx INT AUTO_INCREMENT PRIMARY KEY,
--     reporterUser varchar(30) NOT NULL ,
--     reportedUser varchar(30) NOT NULL ,
--     category varchar(10) NOT NULL CHECK (category in ('POST','COMMENT','USER')),
--     content text DEFAULT '',
--     PostIdx INT DEFAULT 0,
--     CommentIdx INT DEFAULT 0
-- )CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;


CREATE TABLE Report (
reportIdx INT AUTO_INCREMENT PRIMARY KEY,
reporter varchar(30) NOT NULL ,
reportedUser varchar(30) NOT NULL ,
category varchar(10) NOT NULL CHECK (category in ('POST','COMMENT')),
content text, -- DEFAULT '' << 제거
detail text, -- 신고 시 추가내용 작성 >> 얘 추가하면 신고후 알림창 undefined 뜨고 신고 안됨
PostIdx INT DEFAULT 0,
CommentIdx INT DEFAULT 0,
registryDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;


SHOW TABLE STATUS WHERE Name = 'report';
SHOW FULL COLUMNS FROM report;


insert into report(reporter, reportedUser, category, PostIdx, CommentIdx)
values('admin', 'userA', 'USER', 0, 0);

select * from report
where reporter='admin';
# 신고 누름 > DB 등록
# List 조회시 if board.username == List foreach(reportUser) 면 List 안뜸

# reportIdx
# reporterUser - 신고버튼을 누른 순간 테이블에 등록
# reportedUser - 신고 당함
# Category - Post,Comment,User - 게시글/댓글/유저
# PostIdx - 신고한 글 번호 /  Null 가능
# CommentIdx - 신고한 댓글 번호 / Null 가능

# <컬럼추가 안함>
# Content - 신고당한 내용 select content from post where postidx 해서 content만 가져옴

CREATE USER 'shoots'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON *.* TO 'shoots'@'localhost' WITH GRANT OPTION;
CREATE DATABASE shoots;