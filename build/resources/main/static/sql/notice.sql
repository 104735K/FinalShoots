DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
                        notice_idx INT AUTO_INCREMENT PRIMARY KEY,
                        writer INT,
                        title VARCHAR(100) NOT NULL,
                        content TEXT NOT NULL,
                        notice_file VARCHAR(50),
                        notice_original varchar(50),
                        register_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                        readcount INT DEFAULT 0,
                        FOREIGN KEY (writer) REFERENCES regular_user(idx) ON DELETE CASCADE
);
