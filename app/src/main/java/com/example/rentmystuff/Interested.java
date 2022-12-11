package com.example.rentmystuff;
/**
 * This is the Interested class.
 * It is an object containing the email of the user and the date he is interested in the post.
 */
public class Interested {

    private String email="";
    private String date="";

    /** Empty Constructor for the Interested class: */
    public Interested() {}

    /** Constructor for the Interested class: */
    public Interested(String email, String date) {
        this.email = email;
        this.date = date;
    }

    /** Setters: */
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /** Getters: */
    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }
}
