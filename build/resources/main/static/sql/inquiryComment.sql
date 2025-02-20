DROP TABLE IF EXISTS inquiry_comment;

CREATE TABLE inquiry_comment (
    i_comment_idx  INT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- 댓글 번호: primary 키
    inquiry_idx    INT NOT NULL,                            -- 문의글 ID (외래 키) : 몇번 문의글에 댓글 달건지.
    writer        INT NOT NULL,                            -- 작성자 ID : idx / business_idx 가져옴.
    content       TEXT NOT NULL,                               -- 댓글 내용
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- 댓글 등록일
    CONSTRAINT fk_inquiry FOREIGN KEY (inquiry_idx) REFERENCES inquiry (inquiry_idx) ON DELETE CASCADE
);

select * from inquiry_comment;


-- 1. 문의댓글을 쓴 사람 (writer)의 모든 문의댓글 정보와 그 작성자의 닉네임 (user_id)를 조회하는 조인 커리문 (writer = idx면 select)
select ic.*, r.user_id
from inquiry_comment ic
join regular_user r
on ic.writer = r.idx;

#2. 위의 1번 에서 문의글 번호 (inquiry_idx)가 23번인 문의댓글의 정보들과 그 댓글의 작성자 닉네임 (user_id)을 뽑아내는 커리문 + 내림차순 정렬 (신규 댓글이 밑으로)
select * from (
	select ic.*, r.user_id
	from inquiry_comment ic
	join regular_user r
	on ic.writer = r.idx)
where inquiry_idx = 23
order by i_comment_idx asc;



#3. inquiry list 를 뽑을때 inquiry_idx (=a) 값이 같이 뽑힘. 그러면 select * from inquiry_comment where a = ? 커리문을 써서 메서드 만들고 메서드 반환값은 boolean
-- true 나오면 List.jsp 에서 [답변완료] 를 남김