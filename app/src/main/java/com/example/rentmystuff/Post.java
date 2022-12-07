package com.example.rentmystuff;

public class Post {
    private String post_id;
    private String publisher_email;
    private String category;
    private String title;
    private String description;
    private String imageURL;
    private String address;
    private String price;
    private String time_unit;


    public Post(String publisher_email, String category, String title, String description, String imageURL) {
        this.publisher_email = publisher_email;
        this.category = category;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
    }

    public String getPublisher_email() {
        return publisher_email;
    }

    public void setPublisher_email(String publisher_email) {
        this.publisher_email = publisher_email;
    }

    public Post() {
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
