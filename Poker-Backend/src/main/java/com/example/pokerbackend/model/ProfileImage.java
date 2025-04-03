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

    @Lob  // Large object for storing binary data (Blob)
    @Column(name = "image_data", columnDefinition = "MEDIUMBLOB")
    private byte[] imageData;

    @OneToOne(mappedBy = "profileImage")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public User getPlayer() {
        return user;
    }

    public void setPlayer(User user) {
        this.user = user;
    }
}
