package com.example.rentmystuff;

import java.util.Calendar;

public class Notification implements Comparable<Notification>{
    private String post_id;
    private boolean approved;
    private String date;
    private String creation_date;
    private String interested_id;

    public Notification(String post_id, boolean approved, String date, String creation_date) {
        this.post_id = post_id;
        this.approved = approved;
        this.date = date;
        this.creation_date = creation_date;
    }

    public Notification(String post_id, boolean approved) {
        this.post_id = post_id;
        this.approved = approved;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        this.creation_date = day + "/" + month + "/" + year;
    }

    public Notification(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }


    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getInterested_id() {
        return interested_id;
    }

    public void setInterested_id(String interested_id) {
        this.interested_id = interested_id;
    }

    @Override
    public int compareTo(Notification notification) {
        String [] this_date = this.creation_date.split("/");
        String [] noti_date = this.creation_date.split("/");
        String[] arr = this.getDate().split("/");

        int this_year =Integer.parseInt(this_date[2]);
        int this_month =Integer.parseInt(this_date[1]);
        int this_day =Integer.parseInt(this_date[0]);

        int noti_year =Integer.parseInt(noti_date[2]);
        int noti_month =Integer.parseInt(noti_date[1]);
        int noti_day =Integer.parseInt(noti_date[0]);
        //noti_create = 1.1.2023
        //this_date = 2.1.2023
        if(this_year > noti_year){
            return 1;
        }
        if(this_year < noti_year){
            return -1;
        }
        if(this_month > noti_month){
            return 1;
        }
        if(this_month < noti_month){
            return -1;
        }
        if(this_day > noti_day){
            return 1;
        }
        if(this_day < noti_day){
            return -1;
        }

        return 0;
    }
}
