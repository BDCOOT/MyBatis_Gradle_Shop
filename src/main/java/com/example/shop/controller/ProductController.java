package com.example.shop.controller;

import com.example.shop.models.*;
import com.example.shop.service.CommentService;
import com.example.shop.service.ProductService;
import com.example.shop.service.ShopService;
import com.example.shop.service.UserService;
import com.example.shop.utils.GenerateId;
import com.example.shop.utils.ImageRegister;
import com.example.shop.utils.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/product")
public class ProductController {

    private final Jwt jwt;
    private final GenerateId generateId;

    private final UserService userService;
    private final ShopService shopService;
    private final ProductService productService;
    private final CommentService commentService;
    private final ImageRegister imageRegister;

    @PostMapping("/addproduct")
    public ResponseEntity<Object> addProduct(@RequestHeader String token,
                                             @ModelAttribute Product req,
                                             @RequestPart(required = false) MultipartFile[] image) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            Shop shop = shopService.shopFindByUserId(decodedToken);

            // id 생성
            String shortUUID = generateId.shortUUID();
            req.setId(shortUUID);
            req.setShop_id(shop.getId());

            if(image != null){
                List<String> images = imageRegister.CreateImages(image);
                String multiImages = String.join(",", images);

                req.setImg(multiImages);
            }else{
                req.setImg(null);
            }

            int result = productService.productRegister(req);
            if(result != 1){
                map.put("result", "상품 등록 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            map.put("result", "상품 등록 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/modifyproduct")
    public ResponseEntity<Object> modifyProduct(@RequestHeader String token,
                                                @ModelAttribute Product req,
                                                @RequestPart(required = false) MultipartFile[] image) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            Shop shop = shopService.shopFindByUserId(decodedToken);
            Product previousProduct = productService.getProductById(req.getId());
            System.out.println(req.getId());

             //프로덕트의 상점id 랑 나의 상점 id랑 일치하느지 확인
            if(!shop.getId().equals(previousProduct.getShop_id())){
                map.put("result", "등록자만 수정할 수 있습니다.");
                return  new ResponseEntity<>(map, HttpStatus.OK);
            }


            // 이렇게 하면 왜 안도리가 ?
//            List<String> previousImages = null;
//            if(previousProduct.getImg() != null){
//                previousImages = List.of(previousProduct.getImg().split(","));
//            }


            //기존이미지 담음
            List<String> previousImages = null;
            if(previousProduct.getImg() != null){
                previousImages = List.of(previousProduct.getImg().split(","));
            }

            List<String> alreadyImages = new ArrayList<>(previousImages);

            //삭제할 이미지가 있으면
            if(req.getDeleteimage() != null){
                List<String> deleteImages = List.of(req.getDeleteimage().split(","));
                for(String img : deleteImages){
                    //기존이미지에서 삭제
                    System.out.println("삭제할 이미지" + img);
                    alreadyImages.remove(img);
                    //경로 찾아 파일 삭제
                    imageRegister.DeleteFile(img);
                }
            }

            //추가로 이미지 삽입
            if(image != null){
                List<String> images = imageRegister.CreateImages(image);
                alreadyImages.addAll(images);
                System.out.println("재삽입 목록 확인 :" +  previousImages);
            }
            String finalImage = String.join(",", previousImages);
            req.setImg(finalImage);

            productService.modifyProduct(req);

            map.put("result", "상품 수정 성공");
            return  new ResponseEntity<>(map, HttpStatus.OK);
        } catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }


    @GetMapping("/productlist/bycategory")
    public ResponseEntity<Object> productListByCategory(@RequestParam int category_id) throws Exception{
        try{
            Map<String, Object> map = new HashMap<>();

            List<Map<String, Object>> productList = productService.getProductListByCategoryId(category_id);

            map.put("rows", productList);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/purchase/product")
    public ResponseEntity<Object> pharchaseProduct(@RequestHeader String token, @RequestBody Order req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();
            String decodedToken = jwt.VerifyToken(token);

            Product product = productService.getProductById(req.getProduct_id());
            String shortUuid = generateId.shortUUID();

            req.setId(shortUuid);
            req.setUser_id(decodedToken);
            req.setTotal_price(product.getPrice() * req.getTotal_count());

            if(req.getTotal_count() > product.getQuantity()){
                map.put("result", "재고가 부족합니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            int result = productService.purchaseProduct(req);
            
            if(result != 1){
                map.put("result", "상품 구매 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);    
            }
            
            map.put("result", "상품 구매 완료");
            productService.decreaseQuantity(req.getProduct_id(), product.getQuantity() - req.getTotal_count());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }


    @PostMapping("/deleteproduct")
    public ResponseEntity<Object> deleteProduct(@RequestHeader String token,
                                                @ModelAttribute Review req,
                                                @RequestPart(required = false) MultipartFile[] image)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();
            String decodedToken = jwt.VerifyToken(token);

            Product product = productService.getProductById(req.getId());
            Shop myShop = shopService.shopFindByUserId(decodedToken);

            System.out.println(req.getDeleteimage());

            if(!(product.getShop_id().equals(myShop.getId()))){
                map.put("result", "상점 주인만 삭제할 수 있습니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

                List<String> previousImages = null;

                if(product.getImg() != null){
                    previousImages = List.of(product.getImg().split(","));
                }

                List<String> alreadyImages = new ArrayList<>(previousImages);

                //삭제할 이미지가 있으면
                if(req.getDeleteimage() != null){
                    List<String> deleteImages = List.of(req.getDeleteimage().split(","));
                    for(String img : deleteImages){
                        //기존이미지에서 삭제
                        System.out.println("삭제할 이미지" + img);
                        alreadyImages.remove(img);
                        //경로 찾아 파일 삭제
                        imageRegister.DeleteFile(img);
                    }
                }

                int result = productService.deleteProduct(req.getId());
                if(result != 1){
                    map.put("result", "상품 삭제 실패");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }


                //상품에 달려있던 리뷰,댓글,답글 삭제
                productService.deleteWithReviews(req.getId());
                commentService.deleteWithComments(req.getId());
                commentService.deleteWithRecomments(req.getId());

            map.put("result", "상품 삭제 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/writecomment")
    public ResponseEntity<Object> writeComment(@RequestHeader String token,
                                               @RequestBody Comment req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String shortUuid = generateId.shortUUID();
            String decodedToken = jwt.VerifyToken(token);

            req.setId(shortUuid);
            req.setUser_id(decodedToken);

            int result = commentService.writeComment(req);
            if(result != 1){
                map.put("result", "문의 남기기 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "문의 남기기 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/modifycomment")
    public ResponseEntity<Object> modifyComment(@RequestHeader String token, @RequestBody Comment req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            if(decodedToken.equals(req.getUser_id())){
                commentService.modifyComment(req);
                map.put("result", "문의 수정 성공");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            map.put("result", "문의 수정 실패");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/deletecomment")
    public ResponseEntity<Object> deletecomment(@RequestHeader String token, @RequestBody Comment req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            Comment comment = commentService.getCommentById(req.getId());

            if(decodedToken.equals(comment.getUser_id())){
                int result = commentService.deleteComment(req);
                if(result != 1){
                    map.put("result", "문의 삭제 실패");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
                map.put("result", "문의 삭제 성공");
                commentService.deleteWithRecomments(req.getId());
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            map.put("result", "답글 삭제 시도 실패");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/writerecomment")
    public ResponseEntity<Object> writerecomment(@RequestHeader String token,
                                                 @RequestBody Recomment req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();
            // comment id를 받아 답글 작성
            // comment 테이블에 product_id가 product 테이블에 나의shop_id 에 속한 제품이여야 답글 달 수 있음
            //

            String decodedToken = jwt.VerifyToken(token);
            String shortUuid = generateId.shortUUID();
            req.setId(shortUuid);
            req.setUser_id(decodedToken);

            Comment comment = commentService.getCommentById(req.getComment_id());
            Product product = productService.getProductById(comment.getProduct_id());
            req.setProduct_id(product.getId());

            Shop shop = shopService.shopFindByUserId(decodedToken);

            if(shop == null){
                map.put("result", "답글 권한이 없습니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            if(product.getShop_id().equals(shop.getId())){
                int result = commentService.writeRecoment(req);
                if(result != 1){
                    map.put("result", "답글 달기 실패");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
                map.put("result", "답글 달기 성공");
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else{
                map.put("result", "답글 권한이 없습니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/modifyrecomment")
    public ResponseEntity<Object> modifyRecomment(@RequestHeader String token, @RequestBody Recomment req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            if(decodedToken.equals(req.getUser_id())){
                commentService.modifyRecomment(req);
                map.put("result", "답글 수정 성공");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            map.put("result", "답글 수정 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/deleterecomment")
    public ResponseEntity<Object> deleteRecomment(@RequestHeader String token, @RequestBody Recomment req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            if(decodedToken.equals(req.getUser_id())){
                int result = commentService.deleteRecomment(req);
                if(result != 1){
                    map.put("result", "답글 삭제 실패");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
                map.put("result", "답글 삭제 성공");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            map.put("result", "답글 삭제 시도 실패");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/addcart")
    public ResponseEntity<Object> addCart(@RequestHeader String token, @RequestBody Cart req)throws Exception{
        try{
            Map< String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            String shortUuid = generateId.shortUUID();

            req.setId(shortUuid);
            req.setUser_id(decodedToken);
            
            int result = productService.addCart(req);
            if(result != 1){
                map.put("result", "장바구니 담기 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "장바구니 담기 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
            
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @GetMapping("/cartlist")
    public ResponseEntity<Object> getCartList(@RequestHeader String token)throws Exception{
        try{
            Map<String, Object> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            List<Cart> cartList = productService.getCartList(decodedToken);

            map.put("result", cartList);

            return new ResponseEntity<>(map, HttpStatus.OK);

        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/deletecart")
    public ResponseEntity<Object> deleteCart(@RequestHeader String token, @RequestBody Cart req)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            int result = productService.deleteProductInCart(decodedToken, req.getProduct_id());


            if(result != 1){
                map.put("result", "장바구니에서 품목삭제 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "장바구니에서 품목삭제 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @GetMapping("/showreview")
    public ResponseEntity<Object> showReview(@RequestParam String product_id)throws Exception{
        try{
            List<Review> ReviewList = productService.getReviews(product_id);

            return new ResponseEntity<>(ReviewList, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/writereview")
    public ResponseEntity<Object> writeReview(@RequestHeader String token,
                                              @ModelAttribute Review req,
                                              @RequestPart(required = false) MultipartFile[] image)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            String shortUuid = generateId.shortUUID();

            req.setId(shortUuid);
            req.setUser_id(decodedToken);


            int checkOrder = commentService.checkPurchase(req.getOrder_id());
            if(checkOrder < 1){
                map.put("result", "리뷰 작성 권한이 없습니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            int review_count = commentService.checkReview(req.getOrder_id());
            System.out.println("checkreview " + review_count);
            if(review_count >= 1){
                map.put("result", "이미 리뷰를 작성한 상품입니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            if(image != null){
                List<String> result = imageRegister.CreateImages(image);
                String multiImages = String.join(",",result);

                req.setImg(multiImages);
                System.out.println(req.getImg());

            } else{
                req.setImg(null);
            }

            int result = productService.writeReview(req);
            if(result != 1){
                map.put("result", "리뷰 작성 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            map.put("result", "리뷰 작성 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }



    @PostMapping("/modifyreview")
    public ResponseEntity<Object> modifyReview(@RequestHeader String token,
                                             @ModelAttribute Review req,
                                             @RequestPart(required = false) MultipartFile[] image)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            //단일리뷰조회
            Review alreadyReview = productService.getReviewById(req.getId());

            if((!alreadyReview.getUser_id().equals(decodedToken))){
                map.put("result", "리뷰 작성자만 수정할 수 있습니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            List<String> imageSet = null;
            if(alreadyReview.getImg() != null){
                imageSet = List.of(alreadyReview.getImg().split(","));
            }

            List<String> alreadyImages = new ArrayList<>(imageSet);
            //DB에 있는 이미지 중 통신으로 받아온 이미지 삭제
            System.out.println(req.getDeleteimage());
            if(req.getDeleteimage() != null){
                List<String> deleteImages = List.of(req.getDeleteimage().split(","));
                for(String img : deleteImages){
                    alreadyImages.remove(img);
                    imageRegister.DeleteFile(img);
                }
            }
            //새로 들어온 이미지 변환
            if(image != null){
                List<String> images = imageRegister.CreateImages(image);
                alreadyImages.addAll(images);
            }
            String finalImage = String.join(",", alreadyImages);
            alreadyReview.setImg(finalImage);

            int result = productService.updateReview(alreadyReview);

            if(result != 1){
                map.put("result", "리뷰 수정 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            map.put("result", "리뷰 수정 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }
    
    @PostMapping("/deletereview")
    public ResponseEntity<Object> deletereview(@RequestHeader String token,
                                               @ModelAttribute Review req,
                                               @RequestPart(required = false) MultipartFile[] image)throws Exception {
        try {
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            //리뷰 이미지 삭제
            Review review = productService.getReviewById(req.getId());

            if(!(review.getUser_id().equals(decodedToken))){
                map.put("result", "작성자 본인만 삭제할 수 있습니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            if (review.getImg() != null) {
                if (req.getDeleteimage() != null) {
                    List<String> deleteImages = List.of(req.getDeleteimage().split(","));
                    for (String img : deleteImages) {
                        imageRegister.DeleteFile(img);
                    }
                }
            }


            int result = productService.deleteReview(req.getId());
                if(result != 1){
                    map.put("result", "리뷰 삭제 실패");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
                map.put("result", "리뷰 삭제 성공");
                return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @GetMapping("/getsellinglist")
    public ResponseEntity<Object> getSellingList(@RequestParam String shop_id)throws Exception{
        try{
            Map<String, Object> map = new HashMap<>();

            List<Map<String, Object>> sellingList = productService.getSellingList(shop_id);
            map.put("rows", sellingList);

            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/modifybyadmin")
    public ResponseEntity<Object> modifyByAdmin(@RequestHeader String token,
                                                @ModelAttribute Product req,
                                                @RequestPart(required = false) MultipartFile[] image)throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            User user = userService.findByUserId(decodedToken);
            Product product = productService.getProductById(req.getId());

            if (!(user.getGrade().equals("관리자"))) {
                map.put("result", "관리자가 아닌 접근입니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            //이미지
            List<String> previousImages = null;

            if(product.getImg() != null){
                previousImages = List.of(product.getImg().split(","));
            }

            List<String> updateImages = new ArrayList<>(previousImages);

            if(req.getDeleteimage() != null) {
                List<String> deleteImages = List.of(req.getDeleteimage().split(","));
                for(String img : deleteImages){
                    updateImages.remove(img);
                    imageRegister.DeleteFile(img);
                }
            }

            //formdata의 image
            if(image!=null){
                List<String> images = imageRegister.CreateImages(image);
                updateImages.addAll(images);
            }
            String finallImage = String.join(",", updateImages);
            req.setImg(finallImage);


            productService.modifyProductByAdmin(req);

            map.put("result", "관리자권한으로 물품 변경 완료");
            return new ResponseEntity<>(map, HttpStatus.OK);

        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/deletebyadmin")
    public ResponseEntity<Object> deleteByAdmin(@RequestHeader String token, @RequestBody Product req)throws Exception {
        try {
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            User user = userService.findByUserId(decodedToken);
            Product product = productService.getProductById(req.getId());

            if(!(user.getGrade().equals("관리자"))){
                map.put("result", "관리자가 아닌 접근입니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }


            if (product.getImg() != null) {
                if (req.getDeleteimage() != null) {
                    List<String> deleteImages = List.of(req.getDeleteimage().split(","));
                    for (String img : deleteImages) {
                        imageRegister.DeleteFile(img);
                    }
                }
            }

            int result = productService.deleteProductByAdmin(req);
            if(result != 1){
                map.put("result", "관리자권한으로 물품 삭제 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            //상품에 달려있던 리뷰,댓글,답글 삭제
            productService.deleteWithReviews(req.getId());
            commentService.deleteWithComments(req.getId());
            commentService.deleteWithRecomments(req.getId());

            map.put("result", "관리자권한으로 물품 삭제 완료");
            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }
}
