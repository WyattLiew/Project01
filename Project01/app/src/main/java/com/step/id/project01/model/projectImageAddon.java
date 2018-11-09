package com.step.id.project01.model;

public class projectImageAddon {
    private String id;
    private String imgURL;

    public projectImageAddon() {
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

    public projectImageAddon(String id, String imgURL) {
        this.id = id;
        this.imgURL = imgURL;
    }
}
