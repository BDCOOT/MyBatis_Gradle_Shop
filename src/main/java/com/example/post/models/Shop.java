package com.example.post.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

    private String id;
    private String name;
    private String user_id;
    private boolean allowed;
    //alter table yb_user modify createdAt datetime not null default CURRENT_TIMESTAMP
}
