package com.step.id.project01.model;

import android.net.Uri;

public class defectImage {
    private String name;
    private Uri uri;

    public defectImage() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public defectImage(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }
}
