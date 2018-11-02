package com.step.id.project01.model;

public class defectImageAddon {
    private String id;
    private String imgURL;

    public defectImageAddon() {
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

    public defectImageAddon( String id,String imgURL) {
        this.id = id;
        this.imgURL = imgURL;
    }
}
