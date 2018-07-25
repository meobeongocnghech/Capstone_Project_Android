package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyMedia {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("typeId")
    @Expose
    private int type;
    @SerializedName("link")
    @Expose
    private String path;
    @SerializedName("caption")
    @Expose
    private String caption;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String toString() {
        return "MyMedia{" +
                "id=" + id +
                ", type=" + type +
                ", path='" + path + '\'' +
                ", caption='" + caption + '\'' +
                '}';
    }
}
