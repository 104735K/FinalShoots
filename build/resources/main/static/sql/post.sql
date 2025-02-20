DROP TABLE IF EXISTS post;
CREATE TABLE post (
      post_idx INT AUTO_INCREMENT PRIMARY KEY, -- 게시글 식별 번호 (글번호)
      writer INT NOT NULL, -- 작성자 (regular_user 테이블 참조)
      category CHAR(1) NOT NULL CHECK (category IN ('A', 'B')), -- 글 종류
      title VARCHAR(100) NOT NULL, -- 제목
      content TEXT NOT NULL, -- 내용
      post_file VARCHAR(200), -- 첨부파일 실제 저장된 파일이름
      post_original VARCHAR(200), -- 첨부파일 첨부될 파일 명
      price INTEGER DEFAULT 0, -- 가격
      register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 등록일
      readcount INT DEFAULT 0, -- 조회수
      status VARCHAR(20) DEFAULT 'available' CHECK (status IN ('available', 'completed')), -- 거래 상태
      report_status varchar(10) default 'unblock', -- 신고 후 차단 상태
      CONSTRAINT fk_writer FOREIGN KEY (writer) REFERENCES regular_user(idx) ON DELETE CASCADE
);

# ALTER TABLE post MODIFY post_file VARCHAR(200);
# ALTER TABLE post MODIFY post_original VARCHAR(200);

ALTER TABLE post
    ADD COLUMN status VARCHAR(20) DEFAULT 'available' CHECK (status IN ('available', 'completed'));


ALTER TABLE post
    DROP CONSTRAINT post_chk_2;  -- 제약 조건 삭제

ALTER TABLE post
    ADD CONSTRAINT post_chk_2 CHECK (status IN ('available', 'completed'));  -- 새로운 제약 조건 추가


drop table post;


select *
from post
where category = 'A';


