package com.step.id.project01.firebase;

public class User {
    private String id,name,email,phone;
    private String imgURL;

    public User(){

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public User(String id, String name, String email, String phone, String imgURL) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imgURL = imgURL;
    }
}
