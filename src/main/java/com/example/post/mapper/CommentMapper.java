package com.example.post.mapper;

import com.example.post.models.Comment;
import com.example.post.models.Recomment;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO yb_comment (id, product_id, user_id, description) VALUES (#{id},#{product_id},#{user_id},#{description})")
    int writeComment(Comment comment);

    @Insert("INSERT INTO yb_recomment (id, product_id, comment_id, user_id, description) VALUES (#{id}, #{product_id},#{comment_id},#{user_id},#{description})")
    int writeRecomment(Recomment recomment);

    @Update("UPDATE yb_comment SET description=#{description}, updatedAt=now() WHERE id=#{id}")
    void modifyComment(Comment comment);

    @Delete("DELETE FROM yb_comment WHERE id=#{id}")
    int deleteComment(Comment comment);

    @Update("UPDATE yb_recomment SET description=#{description}, updatedAt=now() WHERE id=#{id}")
    void modifyRecomment(Recomment recomment);

    @Delete("DELETE FROM yb_recomment WHERE id=#{id}")
    int deleteRecomment(Recomment recomment);

    @Delete("DELETE FROM yb_comment WHERE product_id=#{product_id}")
    void deleteWithComments(String product_id);

    @Delete("DELETE FROM yb_recomment WHERE product_id=#{product_id}")
    void deleteWithRecomments(String product_id);

    @Select("SELECT * FROM yb_comment WHERE id = #{id}")
    Comment getCommentById(String id);

    @Select("SELECT COUNT(*) FROM yb_order WHERE id=#{id}")
    int checkPurchase(String id);

    @Select("SELECT COUNT(*) AS review_count FROM yb_review WHERE order_id=#{order_id}")
    int checkReview(String order_id);

}//interface
