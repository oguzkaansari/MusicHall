package com.example.musichall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class User{

    private String device_token;
    private String email;
    private String password;
    private String name;
    private String birthDate;
    private String currentUrl;
    private String playLists;
    private String haveProfilePic;
    private List<String> friendsList = new ArrayList<>();
    private int position;
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPlayLists() {
        return playLists;
    }

    public void setPlayLists(String playLists) {
        this.playLists = playLists;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }


    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
