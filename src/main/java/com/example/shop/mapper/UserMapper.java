package com.example.shop.mapper;

import com.example.shop.models.Notice;
import com.example.shop.models.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO yb_user (id, user_id, username, app_key, email, phone, address, gender, birth) VALUES (#{id}, #{user_id},#{username},#{app_key},#{email},#{phone},#{address},#{gender},#{birth})")
    int create(User user);

    @Select("SELECT user_id FROM yb_user WHERE user_id=#{user_id}")
    String userIdCheck(String userId);

    @Select("SELECT * FROM yb_user WHERE user_id=#{user_id}")
    User findByUserId(String userId);

    @Update("UPDATE yb_user SET email=#{email}, phone=#{phone}, address=#{address}, birth=#{birth} WHERE user_id=#{user_id}")
    int updateUserInfo(User user);

    @Update("UPDATE yb_user SET app_key=#{new_app_key} WHERE user_id=#{user_id}")
    void modifyUserAppKey(String new_app_key, String user_id);

    @Select ("SELECT * FROM yb_user WHERE user_id=#{user_id}")
    User login(String user_id);

    @Delete ("DELETE FROM yb_user WHERE user_id=#{user_id}")
    int deleteUser(String user_id);

    @Update("UPDATE yb_user SET grade='판매회원' WHERE user_id=#{user_id}")
    void userUpdateSeller(String user_id);

    @Select("Select * FROM yb_shop WHERE allowed=0")
    List<Map<String, Object>> getApplySellerList();

    @Insert("INSERT INTO yb_notice (id, user_id, title, description) VALUES (#{id}, #{user_id}, #{title}, #{description})")
    int writeNotice(Notice notice);

    @Select("Select * FROM yb_notice")
    List<Map<String, Object>> getNoticeList();

    @Update("UPDATE yb_notice SET title=#{title}, description=#{description}, updatedAt=now() WHERE id=#{id}")
    void modifyNotice(Notice notice);

    @Delete("DELETE FROM yb_notice WHERE id=#{id}")
    int deleteNotice(String id);
}//interface
