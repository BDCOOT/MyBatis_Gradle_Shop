package com.example.shop.mapper;

import com.example.shop.models.Shop;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShopMapper {

    @Select("SELECT * FROM yb_shop WHERE user_id=#{user_id}")
    Shop shopFindByUserId(String user_id);

    @Insert("INSERT INTO yb_shop (id, name, user_id) VALUES (#{id}, #{name}, #{user_id})")
    int userApplySeller(Shop shop);

    @Update("UPDATE yb_shop SET allowed=true, updatedAt=now() WHERE user_id=#{user_id}")
    void adminPermitSeller(String user_id);



}
