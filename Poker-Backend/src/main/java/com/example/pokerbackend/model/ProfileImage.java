package com.example.pokerbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Setter
@Getter
@Entity
@Table(name = "profile_image")
public class ProfileImage {
    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imageName")
    private String imageName;

    @Lob  // Large object for storing binary data (Blob)
    @Column(name = "image_data")
    private byte[] imageData;

    @Column(name = "imageType")
    private String imageType;  // e.g., image/jpeg, image/png, etc.

    @OneToOne(mappedBy = "profileImage")
    private Player player;

}
