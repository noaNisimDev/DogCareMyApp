package com.example.dogcaremyapp;

import java.util.ArrayList;
import java.util.List;

public class User {

    private List<String> dogs;
    private String user_login_phone;

    public User(List<String> user_dog, String user_login_phone) {
        this.dogs = user_dog;
        this.user_login_phone = user_login_phone;
    }

    public User(String user_login_phone) {
        this.dogs = new ArrayList<String>();
        this.user_login_phone = user_login_phone;
    }

    public List<String> getDogs() {
        return dogs;
    }

    public void setDogs(List<String> dogs) {
        this.dogs = dogs;
    }

    public String getUser_login_phone() {
        return user_login_phone;
    }

    public void setUser_login_phone(String user_login_phone) {
        this.user_login_phone = user_login_phone;
    }
}
