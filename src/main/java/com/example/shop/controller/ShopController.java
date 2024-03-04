package com.example.shop.controller;

import com.example.shop.models.Shop;
import com.example.shop.models.User;
import com.example.shop.service.ShopService;
import com.example.shop.service.UserService;
import com.example.shop.utils.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shop")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ShopController {

    //터미널에  ./gradlew bootRun  하면 postapplicationd이 실행된다!
    private final Jwt jwt;
    private final UserService userService;
    private final ShopService shopService;

    @PostMapping("/applyseller")
    public ResponseEntity<Object> applySeller(@RequestHeader String token, @RequestBody Shop req) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedtoken = jwt.VerifyToken(token);

            req.setUser_id(decodedtoken);
            // id 생성
            UUID uuid = UUID.randomUUID();
            long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
            String shortUUID = Long.toString(l, Character.MAX_RADIX);
            req.setId(shortUUID);

            int result = shopService.userApplySeller(req);
            if(result != 1){
                map.put("result", "판매자 신청 실패");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", "판매자 신청 성공");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("result", "판매자 신청 실패");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @PostMapping("/adminpermitseller")
    public ResponseEntity<Object> adminPermitSeller(@RequestHeader String token, @RequestBody User req) throws Exception{
        try{
            Map<String, String> map = new HashMap<>();

            String decodedtoken = jwt.VerifyToken(token);

            User user = userService.findByUserId(req.getUser_id());
            User adminUser = userService.findByUserId(decodedtoken);

            if(adminUser.getGrade().equals("관리자")){
                shopService.adminPermitSeller(user.getUser_id());
                userService.userUpdateSeller(user.getUser_id());
                map.put("result", "판매자 등록 완료");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            map.put("result", " 판매자 등록 실패");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }catch(Exception e){
            Map<String, String> map = new HashMap<>();
            map.put("result", "판매자 등록 실패");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

    }




}//class
