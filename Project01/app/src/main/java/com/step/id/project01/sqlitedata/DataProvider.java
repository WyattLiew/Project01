package com.step.id.project01.sqlitedata;

public class DataProvider {
    private byte[] img;
    private String location;
    private String name;
    private String number;
    private String defect1;
    private String defect2;
    private String defect3;
    private String comments;
    private String projManager;
    private String date;
    private String id;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getDefect1() {
        return defect1;
    }

    public void setDefect1(String defect1) {
        this.defect1 = defect1;
    }

    public String getDefect2() {
        return defect2;
    }

    public void setDefect2(String defect2) {
        this.defect2 = defect2;
    }

    public String getDefect3() {
        return defect3;
    }

    public void setDefect3(String defect3) {
        this.defect3 = defect3;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getProjManager() {
        return projManager;
    }

    public void setProjManager(String projManager) {
        this.projManager = projManager;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataProvider(String id,String location, String name, String number, String projManager, String date, String defect1, String defect2, String defect3, String comments, byte[] img){
        this.img = img;
        this.location = location;
        this.name =name;
        this.number =number;
        this.projManager = projManager;
        this.date =date;
        this.defect1 =defect1;
        this.defect2 = defect2;
        this.defect3 = defect3;
        this.comments = comments;
        this.id =id;
    }

}


