package com.example.post.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String id;
    private String product_id;
    private String user_id;
    private int total_price;
    private int total_count;
    //alter table yb_user modify createdAt datetime not null default CURRENT_TIMESTAMP
}
