package com.example.shop.service;

import com.example.shop.mapper.ProductMapper;
import com.example.shop.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductMapper productMapper;


    public int productRegister(Product product) throws Exception {
        try{
            return productMapper.productRegister(product);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public Product getProductById(String product_id) throws Exception {
        try{
            return productMapper.getProductById(product_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public List<Map<String, Object>> getProductListByCategoryId(int category_id) throws Exception{
        try{
            return productMapper.getProductListByCategoryId(category_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }


    public void modifyProduct(Product product) throws Exception{
        try{
            productMapper.modifyProduct(product);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int deleteProduct(String id)throws Exception {
        try{
            return productMapper.deleteProduct(id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }


    public int purchaseProduct(Order order) throws Exception{
        try{
            return productMapper.purchaseProduct(order);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void decreaseQuantity(String product_id, int quantity) throws Exception{
        try{
            productMapper.decreaseQuantity(product_id, quantity);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int writeReview(Review review) throws Exception{
        try{
            return productMapper.writeReview(review);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int addCart(Cart cart) throws Exception{
        try{
            return productMapper.addCart(cart);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public List<Cart> getCartList(String user_id) throws Exception{
        try{
            return productMapper.getCartList(user_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }


    public int deleteProductInCart(String user_id, String product_id) throws Exception {
        try {
            return productMapper.deleteProductInCart(user_id, product_id);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public List<Review> getReviews(String product_id) throws Exception {
        try {
            return productMapper.getReviews(product_id);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public int updateReview(Review review) throws Exception{
        try{
            return productMapper.updateReview(review);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int deleteReview(String id) throws Exception{
        try{
            return productMapper.deleteReview(id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public Review getReviewById(String id) throws Exception{
        try{
            return productMapper.getReviewById(id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public List<Map<String, Object>> getSellingList(String shop_id) throws Exception{
        try{
            return productMapper.getSellingList(shop_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void modifyProductByAdmin(Product product) throws Exception{
        try{
            productMapper.modifyProductByAdmin(product);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int deleteProductByAdmin(Product product) throws Exception{
        try{
            return productMapper.deleteProductByAdmin(product);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void deleteWithReviews(String product_id) throws Exception{
        try{
            productMapper.deleteWithReviews(product_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }


}
