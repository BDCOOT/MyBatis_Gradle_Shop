package com.example.shop.mapper;

import com.example.shop.models.Cart;
import com.example.shop.models.Order;
import com.example.shop.models.Product;
import com.example.shop.models.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {


    @Insert("INSERT INTO yb_product (id, name, shop_id, category_id, price, quantity, img) VALUES (#{id}, #{name}, #{shop_id}, #{category_id}, #{price}, #{quantity}, #{img})")
    int productRegister(Product product);

//    @Update("UPDATE yb_product " +
//            "JOIN yb_shop ON yb_product.shop_id = #{shop_id} " +
//            "SET yb_product.name = #{name}, updatedAt=now() " +
//            "WHERE yb_product.id = #{id} AND yb_shop.user_id = #{user_id}")
//    int modifyProduct(String shop_id, String id, String name, String user_id);

    @Update("UPDATE yb_product SET name=#{name}, quantity=#{quantity},price=#{price}, img=#{img}, updatedAt=now() WHERE id=#{id} ")
    void modifyProduct(Product product);

    @Delete("DELETE FROM yb_product WHERE id=#{id}")
    int deleteProduct(String id);


    @Select("SELECT * FROM yb_product WHERE id=#{product_id}")
    Product getProductById(String product_id);

    @Select("SELECT yb_product.*, yb_category.name as category_name FROM yb_product inner JOIN yb_category ON yb_product.category_id = yb_category.id WHERE yb_category.id=#{category_id}")
    List<Map<String, Object>> getProductListByCategoryId(int category_id);

    @Insert("INSERT INTO yb_order (id, product_id, user_id, total_price, total_count) VALUES (#{id},#{product_id},#{user_id},#{total_price},#{total_count})")
    int purchaseProduct(Order order);

    @Update("UPDATE yb_product SET quantity=#{quantity},updatedAt=now() WHERE id=#{product_id}")
    void decreaseQuantity(String product_id, int quantity);

    @Insert("INSERT INTO yb_review (id,order_id, product_id, user_id, description, img) VALUES (#{id},#{order_id},#{product_id}, #{user_id}, #{description}, #{img})")
    int writeReview(Review review);

    @Select("Select * FROM yb_review WHERE product_id=#{product_id}")
    List<Review> getReviews(String product_id);

    @Update("UPDATE yb_review SET description=#{description}, img=#{img}, updatedAt=now() WHERE user_id=#{user_id} AND id=#{id}")
    int updateReview(Review review);

    @Delete("DELETE FROM yb_review WHERE id=#{id}")
    int deleteReview(String id);

    @Insert("INSERT INTO yb_cart (id, product_id, user_id, total_count) VALUES (#{id},#{product_id},#{user_id},#{total_count})")
    int addCart(Cart cart);

    @Select("SELECT * FROM yb_cart WHERE user_id=#{user_id}")
    List<Cart> getCartList(String user_id);

    @Delete("DELETE FROM yb_cart WHERE user_id=#{user_id} AND product_id=#{product_id}")
    int deleteProductInCart(String user_id,String product_id);

    @Select("SELECT * FROM yb_review WHERE id=#{id}")
    Review getReviewById(String id);

    @Select("SELECT yb_product.*, yb_shop.id FROM yb_product INNER JOIN yb_shop ON yb_product.shop_id = yb_shop.id WHERE yb_shop.id=#{shop_id}")
    List<Map<String, Object>> getSellingList(String shop_id);

    @Update("UPDATE yb_product SET name=#{name}, img=#{img},updatedAt=now() WHERE id=#{id}")
    void modifyProductByAdmin(Product product);

    @Delete("DELETE FROM yb_product WHERE id=#{id}")
    int deleteProductByAdmin(Product product);

    @Delete("DELETE FROM yb_review WHERE product_id=#{product_id}")
    void deleteWithReviews(String product_id);
}
