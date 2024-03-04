package com.example.shop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private String id;
    private String order_id;
    private String product_id;
    private String user_id;
    private String description;
    private String img;
    private String deleteimage;
    //alter table yb_user modify createdAt datetime not null default CURRENT_TIMESTAMP
}
