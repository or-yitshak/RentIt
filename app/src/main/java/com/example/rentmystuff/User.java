package com.example.rentmystuff;

public class User {
//    private String password;
    private String first_name;
    private String last_name;

    public User(){
        //empty constructor
    }

    public User(String first_name, String last_name) {
//        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
