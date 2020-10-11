package com.example.dogcaremyapp;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class User {

    private List<Dog> dogs;
    private TextInputLayout user_login_phone;

    public User(List<Dog> user_dog, TextInputLayout user_login_phone) {
        this.dogs = user_dog;
        this.user_login_phone = user_login_phone;
    }

    public User(TextInputLayout user_login_phone) {
        this.dogs = new ArrayList<>();
        this.user_login_phone = user_login_phone;
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }

    public TextInputLayout getUser_login_phone() {
        return user_login_phone;
    }

    public void setUser_login_phone(TextInputLayout user_login_phone) {
        this.user_login_phone = user_login_phone;
    }
}
