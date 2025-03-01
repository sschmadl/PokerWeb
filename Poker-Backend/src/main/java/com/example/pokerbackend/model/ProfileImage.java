package com.example.pokerbackend.model;

import jakarta.persistence.*;


import java.util.Arrays;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
