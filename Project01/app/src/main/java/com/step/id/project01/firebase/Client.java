package com.step.id.project01.firebase;

public class Client {
    private String id;
    private String name;
    private String number;
    private String email;
    private String location;

    public Client() {
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Client(String id, String name, String number, String email, String location) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.location = location;
    }
}
