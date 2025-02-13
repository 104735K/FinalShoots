package com.Shoots.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BcBlacklist {
    private int bc_blacklist_idx;
    private int target_idx;
    private int business_idx;
    private String reason;
    private String status;
    private LocalDateTime blocked_at;
    private LocalDateTime unblocked_at;
}
