package com.step.id.project01.model;

public class Pending {
    private String id;
    private String title;
    private String description;
    private String name;
    private String number;
    private String email;
    private String date;
    private String location;
    private String notes;

    public Pending() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Pending(String id, String title, String description, String name, String number, String email, String date, String location, String notes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.name = name;
        this.number = number;
        this.email = email;
        this.date = date;
        this.location = location;
        this.notes = notes;
    }
}
