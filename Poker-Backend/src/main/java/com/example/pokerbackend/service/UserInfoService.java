package com.example.pokerbackend.service;

import com.example.pokerbackend.Exceptions.ImageTooBigException;
import com.example.pokerbackend.Exceptions.InvalidImageFormat;
import com.example.pokerbackend.model.ProfileImage;
import com.example.pokerbackend.model.User;
import com.example.pokerbackend.repository.ProfileImageRepository;
import com.example.pokerbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

@Service
public class UserInfoService {
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;

    public UserInfoService(UserRepository userRepository, ProfileImageRepository profileImageRepository) {
        this.userRepository = userRepository;
        this.profileImageRepository = profileImageRepository;
    }

    public void saveProfilePicture(MultipartFile image, String username) throws ImageTooBigException, IOException , InvalidImageFormat {
        String[] validFormats = {"png", "jpg", "jpeg"};

        // Check the format of the uploaded image
        String imageFormat = getImageFormat(image);
        if (imageFormat == null || !Arrays.asList(validFormats).contains(imageFormat.toLowerCase())) {
            throw new InvalidImageFormat("Invalid image format. Only PNG, JPG, and JPEG are allowed.");
        }

        User user = userRepository.findUserByUsername(username);
        ProfileImage profileImage = profileImageRepository.findProfileImageByUser(user);
        if (image.getSize() > (10*1024*1024)) throw new ImageTooBigException("Image is too big");
        if (profileImage == null) {
            profileImage = new ProfileImage();
            profileImage.setImageName(image.getOriginalFilename());
            profileImage.setImageData(image.getBytes());
            profileImageRepository.save(profileImage);
            user.setProfileImage(profileImage);
            userRepository.save(user);
        }else {
            profileImage.setImageData(image.getBytes());
            profileImage.setImageName(image.getOriginalFilename());
            profileImageRepository.save(profileImage);
        }
    }

    public void deleteProfilePicture(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) return;
        ProfileImage profileImage = profileImageRepository.findProfileImageByUser(user);
        if (profileImage != null) {
            profileImageRepository.delete(profileImage);
        }
    }
    
    public byte[] getProfileImageData(String username){
        User user = userRepository.findUserByUsername(username);
        if (user == null) return null;
        ProfileImage profileImage = profileImageRepository.findProfileImageByUser(user);
        if (profileImage == null) return null;
        return profileImage.getImageData();
    }

    private String getImageFormat(MultipartFile image) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image.getBytes())) {
            // Use ImageIO to determine the image format
            String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(byteArrayInputStream)).next().getFormatName();
            return formatName;
        } catch (Exception e) {
            // If image format is not recognized
            return null;
        }
    }
}
