package com.example.dogcaremyapp;

import android.media.Image;

import java.util.Date;
import java.util.UUID;

public class Dog {

    private String id;
    private String name;
    private int numOfWalksPerDay;
    private int numOfMealsPerDay;
    private Date[] todayWalks;
    private Date[] todayMeals;
    private Image image;

    //to use from firebase
    public Dog(String name, int numOfWalksPerDay, int numOfMealsPerDay, Date[] todayWalks, Date[] todayMeals, Image image, String id) {
        this.id = id;
        this.name = name;
        this.numOfWalksPerDay = numOfWalksPerDay;
        this.numOfMealsPerDay = numOfMealsPerDay;
        this.todayWalks = todayWalks;
        this.todayMeals = todayMeals;
        this.image = image;
    }

    //new dog
    public Dog(String name, int numOfWalksPerDay, int numOfMealsPerDay, Image image) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.numOfWalksPerDay = numOfWalksPerDay;
        this.numOfMealsPerDay = numOfMealsPerDay;
        this.todayWalks = new Date[numOfWalksPerDay];
        this.todayMeals = new Date[numOfMealsPerDay];
        this.image = image;
    }

    public Date[] getTodayWalks() {
        return todayWalks;
    }

    public void setTodayWalks(Date[] todayWalks) {
        this.todayWalks = todayWalks;
    }

    public Date[] getTodayMeals() {
        return todayMeals;
    }

    public void setTodayMeals(Date[] todayMeals) {
        this.todayMeals = todayMeals;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfWalksPerDay() {
        return numOfWalksPerDay;
    }

    public void setNumOfWalksPerDay(int numOfWalksPerDay) {
        this.numOfWalksPerDay = numOfWalksPerDay;
    }

    public int getNumOfMealsPerDay() {
        return numOfMealsPerDay;
    }

    public void setNumOfMealsPerDay(int numOfMealsPerDay) {
        this.numOfMealsPerDay = numOfMealsPerDay;
    }

}
