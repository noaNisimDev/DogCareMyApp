package com.example.dogcaremyapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Dog {

    private String id;
    private String name;
    private int numOfWalksPerDay;
    private int numOfMealsPerDay;
    private List<Walk> todayWalks;
    private List<Meal> todayMeals;
    private String imageUri;

    //to use from firebase
    public Dog(String name, int numOfWalksPerDay, int numOfMealsPerDay, List<Walk> todayWalks, List<Meal> todayMeals, String imageUri, String id) {
        this.id = id;
        this.name = name;
        this.numOfWalksPerDay = numOfWalksPerDay;
        this.numOfMealsPerDay = numOfMealsPerDay;
        this.todayWalks = todayWalks;
        this.todayMeals = todayMeals;
        this.imageUri = imageUri;
    }

    //new dog
    public Dog(String name, int numOfWalksPerDay, int numOfMealsPerDay, String imageUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.numOfWalksPerDay = numOfWalksPerDay;
        this.numOfMealsPerDay = numOfMealsPerDay;
        this.todayWalks = new ArrayList<Walk>(numOfWalksPerDay);
        this.todayMeals = new ArrayList<Meal>(numOfMealsPerDay);
        this.imageUri = imageUri;

        Date zeroDate = new Date(0);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
        final String zeroString = formatter.format(zeroDate);

        for (int i = 0; i < numOfWalksPerDay; i++) {
            todayWalks.add(new Walk(zeroString));
        }
        for (int i = 0; i < numOfMealsPerDay; i++) {
            todayMeals.add(new Meal(zeroString));
        }
    }

    public Dog() {

    }

    public String getId() {
        return id;
    }


    public List<Walk> getTodayWalks() {
        return todayWalks;
    }

    public void setTodayWalks(List<Walk> todayWalks) {
        this.todayWalks = todayWalks;
    }

    public List<Meal> getTodayMeals() {
        return todayMeals;
    }

    public void setTodayMeals(List<Meal> todayMeals) {
        this.todayMeals = todayMeals;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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
