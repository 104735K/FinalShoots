CREATE TABLE match_post (
    match_idx INT(10) AUTO_INCREMENT PRIMARY KEY,
    writer INT(10),
    match_date DATE NOT NULL,
    match_time VARCHAR(10) NOT NULL,
    player_max INT(2) NOT NULL,
    player_min INT(2) NOT NULL,
    player_gender CHAR(1) NOT NULL,
    match_level VARCHAR(10) NOT NULL,
    team_style VARCHAR(15) NOT NULL,
    price INT(10) NOT NULL,
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- FOREIGN KEY (WRITER) REFERENCES BUSINESS_USER(BUSINESS_IDX) ON DELETE CASCADE

select * from match_post;
delete from match_post;
drop table match_post;

update match_post set match_time = '14:49:00' where match_idx = 36;
update match_post set match_time = '14:49:00' where match_idx = 37;




