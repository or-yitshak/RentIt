package com.example.rentmystuff;

import java.util.ArrayList;

/**
 * This is the User class.
 * It represents a user in the application.
 * It contains the users full name and profile image URL.
 */
public class User {
    private String first_name;
    private String last_name;
    private String image_URL;
    private String phone_number="";

    /**
     * Empty Constructor for the User class:
     */
    public User() {
        //empty constructor
    }

    public User(String first_name, String last_name, String image_URL, String phone_number) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.image_URL = image_URL;
        this.phone_number = phone_number;
    }

    /**
     * Constructor for the User class:
     */
    public User(String first_name, String last_name, String image_URL) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.image_URL = image_URL;
    }

    /**
     * Constructor for the User class:
     */
    public User(String first_name, String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    /**
     * Getters:
     */
    public String getImage_URL() {
        return image_URL;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    /**
     * Setters:
     */
    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
