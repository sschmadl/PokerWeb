package com.example.pokerbackend.repository;

import com.example.pokerbackend.model.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Integer> {
}
