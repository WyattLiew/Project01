package com.step.id.project01.model;

public class ProjectAddOnProvider {

    public static final String TAG = "ProjectAddOnProvider";

    private String id;
    private String imgURL;
    private String notes;
    private String date;
    private String Status;

    public ProjectAddOnProvider() {
    }

    public static String getTAG() {
        return TAG;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ProjectAddOnProvider(String id, String imgURL, String notes, String date, String status) {
        this.id = id;
        this.imgURL = imgURL;
        this.notes = notes;
        this.date = date;
        Status = status;
    }
}


