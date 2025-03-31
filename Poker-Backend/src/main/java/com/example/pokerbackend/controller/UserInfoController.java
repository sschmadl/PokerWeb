package com.example.pokerbackend.controller;

import com.example.pokerbackend.Exceptions.ImageTooBigException;
import com.example.pokerbackend.Exceptions.InvalidImageFormat;
import com.example.pokerbackend.service.UserInfoService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Iterator;

@RestController
@RequestMapping("/user-info")
public class UserInfoController {
    public UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-profile-picture")
    public ResponseEntity<?> changeProfilePicture(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body(new ErrorResponse("File is empty"));
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userInfoService.saveProfilePicture(file, authentication.getName());
        } catch (ImageTooBigException | InvalidImageFormat e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("File is not an image"));
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete-profile-picture")
    public ResponseEntity<?> deleteProfilePicture() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userInfoService.deleteProfilePicture(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username) {
        String contentType;
        byte[] imageData;
        try {
            imageData = userInfoService.getProfileImageData(username);

            if (imageData == null) {
                ClassPathResource defaultImage = new ClassPathResource("static/default.jpg");
                InputStream inputStream = defaultImage.getInputStream();
                imageData = inputStream.readAllBytes();


                // Detect content type from file extension
                contentType = "image/jpg";
            } else {
                contentType = getImageContentType(imageData);
            }
            // Ensure contentType is valid
            if (contentType == null) {
                contentType = "image/jpg"; // Fallback default
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (InvalidImageFormat e) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Final Content-Type: " + contentType);
        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(imageData);
    }

    // helper method to find image type for http header
    private String getImageContentType(byte[] imageData) throws IOException {
        // Create an ImageInputStream from the byte array
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData)) {
            ImageInputStream iis = ImageIO.createImageInputStream(byteArrayInputStream);

            // Find the format of the image
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                String formatName = reader.getFormatName();  // Get format, e.g., "jpeg", "png", etc.

                switch (formatName.toLowerCase()) {
                    case "jpeg":
                        return "image/jpeg";
                    case "jpg":
                        return "image/jpg";
                    case "png":
                        return "image/png";
                }
            }
        }
        return "application/octet-stream";  // If format couldn't be detected
    }
}