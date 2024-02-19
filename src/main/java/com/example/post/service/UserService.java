package com.example.post.service;

import com.example.post.mapper.UserMapper;
import com.example.post.models.Cart;
import com.example.post.models.Notice;
import com.example.post.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public int signUp(User user) throws Exception {
        try {
            return userMapper.create(user);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String userIdCheck(String userId) throws Exception {
        try{
            return userMapper.userIdCheck(userId);
        }
        catch (Exception e){
            throw new Exception(e);
        }
    }


    public int updateUserInfo(User user) throws Exception {
        try{
            return userMapper.updateUserInfo(user);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void modifyUserAppKey(String new_app_key, String user_id) throws Exception {
        try{
            userMapper.modifyUserAppKey(new_app_key, user_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public User findByUserId(String userId) throws Exception {
        try{
            return userMapper.findByUserId(userId);
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    public User login(String user_id) throws Exception{
        try{
            return userMapper.login(user_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int deleteUser(String user_id) throws Exception {
        try{
            return userMapper.deleteUser(user_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void userUpdateSeller(String user_id) throws Exception{
        try{
            userMapper.userUpdateSeller(user_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public List<Map<String, Object>> getApplySellerList() throws Exception{
        try{
            return userMapper.getApplySellerList();
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int writeNotice(Notice notice) throws Exception{
        try{
            return userMapper.writeNotice(notice);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public List<Map<String, Object>> getNoticeList() throws Exception{
        try{
            return userMapper.getNoticeList();
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void modifyNotice(Notice notice) throws Exception{
        try{
            userMapper.modifyNotice(notice);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int deleteNotice(String id) throws Exception{
        try{
            return userMapper.deleteNotice(id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

}//class
