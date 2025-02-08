package com.Shoots.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PaymentHistory {
    private int payment_history_idx;
    private int payment_idx;
    private String payment_status;
    private LocalDate action_date;
}
