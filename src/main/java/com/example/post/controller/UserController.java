package com.example.post.controller;

import com.example.post.models.Comment;
import com.example.post.models.Notice;
import com.example.post.models.Product;
import com.example.post.models.User;
import com.example.post.service.UserService;
import com.example.post.utils.Bcrypt;
import com.example.post.utils.GenerateId;
import com.example.post.utils.Jwt;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final Bcrypt bcrypt;
    // 숙제 : DB ID컬럼 pk 추가 , 실제로 받는 컬럼들 다 추가하기 (email, phonenumber, adress)
    // 실제 쇼핑몰처럼 가입할 때 ID(username)중복확인 API만들기, 비밀번호 수정, 회원탈퇴 등 user가 할 수 있는 것 다 만들기
    // 식당예약 사이트 만들어보고싶다. 두루뭉실하게 생각하지말고 정확한 기획을 만들어서 구상해오기
    private final GenerateId generateId;
    private final Jwt jwt;
    private final UserService userService;

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> helloPage() throws  Exception{
        try{
            Map<String, String> map = new HashMap<>();
            map.put("result", "hello world");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch (Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(
            @RequestBody User req
    ) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();


            // id 생성
            UUID uuid = UUID.randomUUID();
            long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
            String shortUUID = Long.toString(l, Character.MAX_RADIX);
            req.setId(shortUUID);
            // 비밀번호 암호화
            String hashPassword = bcrypt.HashPassword(req.getApp_key());
            req.setApp_key(hashPassword);

            int result = userService.signUp(req);
            if(result == 1){
                map.put("result", "success");
            } else{
                map.put("result", "이미 가입된 닉네임입니다.");
            }
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @GetMapping("/useridcheck")
    public ResponseEntity<Object> useridCheck(@RequestParam String userId) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();
            String alreadyUser = userService.userIdCheck(userId);
            if(alreadyUser == null){
                map.put("result", "success");
            } else {
                map.put("result", "중복된 아이디입니다.");
            }
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/update/userinfo")
    public ResponseEntity<Object> updateUserInfo(@RequestHeader String token, @RequestBody User req) throws  Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            req.setUser_id(decodedToken);

            int result = userService.updateUserInfo(req);
            if(result == 1){
                map.put("result", "성공");
            } else{
                map.put("result", "실패");
            }
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    //비밀번호 수정
    @PostMapping("/modify/userappkey")
    public ResponseEntity<Object> modifyUserAppKey(@RequestHeader String token, @RequestBody Map<String, String> req) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);

            User nowUser = userService.findByUserId(decodedToken);
            Boolean checking = bcrypt.CompareHash(req.get("app_key"),nowUser.getApp_key());

            String hashPassword = bcrypt.HashPassword(req.get("new_app_key"));
            if(!checking){
                map.put("result","실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            userService.modifyUserAppKey(hashPassword, nowUser.getUser_id());
            map.put("result","변경완료");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> signIn(@RequestBody User req) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();
            User getUser = userService.login(req.getUser_id());

            Boolean checking = bcrypt.CompareHash(req.getApp_key(), getUser.getApp_key());

            if (!checking) {
                    map.put("result", "로그인 실패");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
            String token = jwt.CreateToken(req.getUser_id());

            map.put("token", token);
            return new ResponseEntity<>(map,HttpStatus.OK);
        } catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    // 회원탈퇴
    @PostMapping("/unregister")
    public ResponseEntity<Object> unRegister(@RequestHeader String token, @RequestBody User req) throws Exception {
        try {
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            User user = userService.findByUserId(decodedToken);

            Boolean checking = bcrypt.CompareHash(req.getApp_key(), user.getApp_key());

            if (!checking) {
                map.put("result", "비밀번호가 일치하지 않습니다, 탈퇴가 불가능합니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            int result = userService.deleteUser(decodedToken);

            if (result != 1) {
                map.put("result", "삭제 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "삭제 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }


    //header랑 param에 담기는 건 Get에 사용해도 무방하다
    @GetMapping("/mypage")
    public ResponseEntity<Object> myPage(@RequestParam String user_id){
        try{
            Map<String, String> map = new HashMap<>();

            User user = userService.findByUserId(user_id);

            map.put("result", String.valueOf(user));

            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/applysellerlist")
    public ResponseEntity<Object> applySellerList(@RequestHeader String token){
        try{
            Map<String, Object> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            User user = userService.findByUserId(decodedToken);

            if(user.getGrade().equals("관리자")){
                List<Map<String, Object>> applySellerList = userService.getApplySellerList();
                map.put("rows", applySellerList);
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "판매자 신청리스트 조회 실패");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/writenotice")
    public ResponseEntity<Object> writeNotice(@RequestHeader String token,@RequestBody Notice notice){
        try{
            Map<String, Object> map = new HashMap<>();
            
            String shortUuid = generateId.shortUUID();
            notice.setId(shortUuid);
            
            String decodedToken = jwt.VerifyToken(token);
            User user = userService.findByUserId(decodedToken);
            notice.setUser_id(decodedToken);

            if(user.getGrade().equals("관리자")){
                int result = userService.writeNotice(notice);
                if(result != 1){
                    map.put("result", "공지사항 작성 실패");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
                map.put("result", "공지사항 작성 성공");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "관리자가 아닌 접근입니다.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @GetMapping("/notice")
    public ResponseEntity<Object> readNotice(){
        try{
            Map<String, Object> map = new HashMap<>();

            List<Map<String, Object>> noticeList = userService.getNoticeList();

            map.put("rows", noticeList);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/modifynotice")
    public ResponseEntity<Object> modifyNotice(@RequestHeader String token,
                                               @RequestBody Notice req){
        try{
            Map<String, String> map = new HashMap<>();

            String decodedToken = jwt.VerifyToken(token);
            User user = userService.findByUserId(decodedToken);

            if(!user.getGrade().equals("관리자")){
                map.put("result", "관리자가 아닌 접근입니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }

            userService.modifyNotice(req);
            map.put("result", "공지사항 수정 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);

        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/deletenotice")
    public ResponseEntity<Object> deleteNotice(@RequestHeader String token,
                                               @RequestBody Notice req){
        try{

            Map<String, String> map = new HashMap<>();
            String decodedToken = jwt.VerifyToken(token);
            User user = userService.findByUserId(decodedToken);

            if(!user.getGrade().equals("관리자")){
                map.put("result", "잘못된 접근입니다.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            //왜안될까
            int result = userService.deleteNotice(req.getId());
            if(result != 1){
                map.put("result", "공지사항 삭제 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "공지사항 삭제 성공");

            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }



}//class
