package com.Shoots.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Payment {
    private int payment_idx;
    private int match_idx;
    private int seller_idx;
    private int buyer_idx;
    private String payment_method;
    private int payment_amount;
    private LocalDateTime payment_date;
    private String payment_status;
    private String merchant_uid;
    private String imp_uid;

    @Override
    public String toString() {
        return "Payment{" +
                "payment_idx=" + payment_idx +
                ", match_idx=" + match_idx +
                ", seller_idx=" + seller_idx +
                ", buyer_idx=" + buyer_idx +
                ", payment_method='" + payment_method + '\'' +
                ", payment_amount=" + payment_amount +
                ", payment_date=" + payment_date +
                ", payment_status='" + payment_status + '\'' +
                ", merchant_uid='" + merchant_uid + '\'' +
                ", imp_uid='" + imp_uid + '\'' +
                '}';
    }
}
