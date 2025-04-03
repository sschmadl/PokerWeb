package com.example.pokerbackend.model;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "player")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "usename", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "password_changed_date")
    private Date passwordChangedDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ProfileImage getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public Date getPasswordChangedDate() {
        return passwordChangedDate;
    }

    public void setPasswordChangedDate(Date passwordChangedDate) {
        this.passwordChangedDate = passwordChangedDate;
    }
}
