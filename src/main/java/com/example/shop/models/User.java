package com.example.shop.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String user_id;
    private String username;
    private String app_key;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private LocalDate birth;
    private String grade;

    //alter table yb_user modify createdAt datetime not null default CURRENT_TIMESTAMP
}
