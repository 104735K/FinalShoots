CREATE TABLE bc_blacklist (
        bc_blacklist_idx INT AUTO_INCREMENT PRIMARY KEY,
        target_idx INT NOT NULL,
        business_idx INT NOT NULL,
        reason TEXT NOT NULL,
        status ENUM('block', 'unblock') DEFAULT 'block',
        blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        unblocked_at DATETIME DEFAULT NULL
);

SELECT status
FROM bc_blacklist
WHERE target_idx = ? AND business_idx = ?
ORDER BY blocked_at DESC
    LIMIT 1;

select * from bc_blacklist;

drop table bc_blacklist;

select * from bc_blacklist where unblocked_at is  null;

