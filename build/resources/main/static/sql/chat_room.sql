DROP TABLE IF EXISTS chat_room_log;
DROP TABLE IF EXISTS chat_participants;
DROP TABLE IF EXISTS chat_room;
CREATE TABLE chat_room
(
    chat_room_idx INT AUTO_INCREMENT PRIMARY KEY,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_buyer_idx ON payment(buyer_idx);
CREATE TABLE chat_participants
(
    chat_room_idx INT NOT NULL,
    user_idx      INT NOT NULL,
    match_idx     INT default 0,
    joined_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chat_room_idx, user_idx),
    FOREIGN KEY (chat_room_idx) REFERENCES chat_room (chat_room_idx) ON DELETE CASCADE,
    FOREIGN KEY (user_idx) REFERENCES payment(BUYER_IDX) ON DELETE CASCADE,
    FOREIGN KEY (match_idx) REFERENCES match_post(MATCH_IDX) ON DELETE CASCADE
);
CREATE TABLE chat_room_log
(
    chat_room_log_idx INT AUTO_INCREMENT PRIMARY KEY,
    chat_room_idx     INT NOT NULL,
    content           text,
    sender            text,
    sent_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_room_idx) REFERENCES chat_room (chat_room_idx) ON DELETE CASCADE
);
