package com.example.post.service;

import com.example.post.mapper.ShopMapper;
import com.example.post.models.Product;
import com.example.post.models.Shop;
import com.example.post.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopMapper shopMapper;

    public int userApplySeller(Shop shop) throws Exception {
        try{
            return shopMapper.userApplySeller(shop);
        }
        catch(Exception e){
            throw new Exception(e);
        }
    }

    public void adminPermitSeller(String user_id) throws Exception{
        try{
            shopMapper.adminPermitSeller(user_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public Shop shopFindByUserId(String user_id) throws Exception{
        try{
            return shopMapper.shopFindByUserId(user_id);
        }catch (Exception e){
            throw new Exception(e);
        }
    }


}//class
