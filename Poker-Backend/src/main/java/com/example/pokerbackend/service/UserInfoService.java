package com.example.pokerbackend.service;

import com.example.pokerbackend.Exceptions.ImageTooBigException;
import com.example.pokerbackend.model.ProfileImage;
import com.example.pokerbackend.model.User;
import com.example.pokerbackend.repository.ProfileImageRepository;
import com.example.pokerbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;

    public UserInfoService(UserRepository userRepository, ProfileImageRepository profileImageRepository) {
        this.userRepository = userRepository;
        this.profileImageRepository = profileImageRepository;
    }

    public void saveProfilePicture(MultipartFile image, String username) throws ImageTooBigException, IOException {
        User user = userRepository.findUserByUsername(username);
        ProfileImage profileImage = userRepository.findProfileImageByUser(user);
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
}
