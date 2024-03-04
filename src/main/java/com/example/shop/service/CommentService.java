package com.example.shop.service;

import com.example.shop.mapper.CommentMapper;
import com.example.shop.models.Comment;
import com.example.shop.models.Recomment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {



    private final CommentMapper commentMapper;

    public int writeComment(Comment comment) throws Exception{
        try{
            return commentMapper.writeComment(comment);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void modifyComment(Comment comment) throws Exception{
        try{
            commentMapper.modifyComment(comment);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int deleteComment(Comment comment) throws Exception{
        try{
            return commentMapper.deleteComment(comment);
        }catch(Exception e){
            throw new Exception(e);
        }
    }
    public int writeRecoment(Recomment recomment) throws Exception{
        try{
            return commentMapper.writeRecomment(recomment);
        }catch(Exception e){
            throw new Exception(e);
        }
    }


    public void modifyRecomment(Recomment recomment) throws Exception{
        try{
            commentMapper.modifyRecomment(recomment);
        }catch(Exception e){
            throw new Exception(e);
        }
    }


    public int deleteRecomment(Recomment recomment) throws Exception{
        try{
            return commentMapper.deleteRecomment(recomment);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void deleteWithComments(String product_id) throws Exception{
        try{
            commentMapper.deleteWithComments(product_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public void deleteWithRecomments(String product_id) throws Exception{
        try{
            commentMapper.deleteWithRecomments(product_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public Comment getCommentById(String id) throws Exception {
        try{
            return commentMapper.getCommentById(id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int checkPurchase(String order_id) throws Exception{
        try{
            return commentMapper.checkPurchase(order_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    public int checkReview(String order_id) throws Exception{
        try{
            return commentMapper.checkReview(order_id);
        }catch(Exception e){
            throw new Exception(e);
        }
    }
}
