package com.example.rentmystuff;

/**
 * This is the Post class.
 * It represents a user post in the application.
 * It contains the full post information.
 */
public class Post {
    private String post_id; // id of the post in firebase.
    private String publisher_email;
    private String category; //category of the post.
    private String title; // name of the post.
    private String description;
    private String imageURL; // URL of the post image.
    private String address; // address of the posts user.
    private String price; // price of the post.

    /**
     * Empty Constructor of the post class.
     */
    public Post() {
    }

    /**
     * Constructor of the post class.
     */
    public Post(String publisher_email, String category, String title, String description, String imageURL, String address, String price) {
        this.publisher_email = publisher_email;
        this.category = category;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.address = address;
        this.price = price;
    }

    /**
     * Getters:
     */
    public String getPublisher_email() {
        return publisher_email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    /**
     * Setters:
     */
    public void setPublisher_email(String publisher_email) {
        this.publisher_email = publisher_email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
