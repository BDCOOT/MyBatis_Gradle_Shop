package com.example.shop.utils;


import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.UUID;

@Service
public class GenerateId {

    public String shortUUID(){
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        String shortUUID = Long.toString(l, Character.MAX_RADIX);

        return shortUUID;
    }

}
