package com.step.id.project01.model;

public class defect {
    private String id;
    private String imgURL;
    private String defect;
    private String date;
    private String comments;

    public defect() {
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

    public String getDefect() {
        return defect;
    }

    public void setDefect(String defect) {
        this.defect = defect;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public defect(String id, String imgURL, String defect, String date, String comments) {
        this.id = id;
        this.imgURL = imgURL;
        this.defect = defect;
        this.date = date;
        this.comments = comments;
    }
}


