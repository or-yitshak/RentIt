package com.example.rentmystuff;

public class Interested {
    private String email;
    private String date;

    public Interested() {

    }

    public Interested(String email, String date) {
        this.email = email;
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }
}
