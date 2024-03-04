package com.example.shop.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String id;
    private String name;
    private String shop_id;
    private int category_id;
    private int price;
    private int quantity;
    private String img;
    private String deleteimage;
    //alter table yb_user modify createdAt datetime not null default CURRENT_TIMESTAMP
}

