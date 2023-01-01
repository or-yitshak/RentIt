package com.example.rentmystuff;

public class Notification {
    private String post_id;
    private boolean approved;
    private String date;

    public Notification(String post_id, boolean approved) {
        this.post_id = post_id;
        this.approved = approved;
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
}
