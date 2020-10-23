package com.example.dogcaremyapp;

public class Meal {

    private String mealDate;

    public Meal(String mealDate) {
        this.mealDate = mealDate;
    }

    public Meal() {
    }

    public String getMealDate() {
        return mealDate;
    }

    public void setMealDate(String mealDate) {
        this.mealDate = mealDate;
    }

}
