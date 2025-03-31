package com.example.pokerbackend.controller;

import com.example.pokerbackend.Exceptions.ImageTooBigException;
import com.example.pokerbackend.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user-info")
public class UserInfoController {
    public UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping("/change-profile-picture")
    public ResponseEntity<?> changeProfilePicture(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) return ResponseEntity.badRequest().body(new ErrorResponse("File is empty"));
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userInfoService.saveProfilePicture(file, authentication.getName());
        }catch(ImageTooBigException e){
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponse("File is not an image"));
        }
        return ResponseEntity.ok().build();
    }

}