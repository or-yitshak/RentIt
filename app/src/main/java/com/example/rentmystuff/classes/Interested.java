package com.example.rentmystuff.classes;

import java.util.Calendar;

/**
 * This is the Interested class.
 * It is an object containing the email of the user and the date he is interested in the post.
 */
public class Interested {

    private String email="";
    private String date="";
    private boolean approved = false;
    private String post_id = "";
    private String interested_id = "";
    private String notification_id = "";

    /** Empty Constructor for the Interested class: */
    public Interested() {}

    /** Constructor for the Interested class: */
    public Interested(String email, String date, String post_id) {
        this.email = email;
        this.date = date;
        this.post_id = post_id;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    /** Setters: */
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setApproved(boolean approved) { this.approved = approved; }

    public void setPost_id(String post_id) { this.post_id = post_id; }

    public void setInterested_id(String interested_id) { this.interested_id = interested_id; }

    /** Getters: */
    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public boolean isApproved() { return approved; }

    public String getPost_id() { return post_id; }

    public String getInterested_id() { return interested_id; }

    public boolean isDatePassed(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String[] arr = this.getDate().split("/");
        int rent_year =Integer.parseInt(arr[2]);
        int rent_month =Integer.parseInt(arr[1]);
        int rent_day =Integer.parseInt(arr[0]);
        if(year > rent_year){
            return true;
        }
        if(year < rent_year){
            return false;
        }
        if(month > rent_month){
            return true;
        }
        if(month < rent_month){
            return false;
        }
        if(day > rent_day){
            return true;
        }
        if(day < rent_day){
            return false;
        }

        return false;
    }
}
