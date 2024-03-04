package com.example.shop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private String id;
    private String product_id;
    private String user_id;
    private int total_count;
    //alter table yb_user modify createdAt datetime not null default CURRENT_TIMESTAMP
}
