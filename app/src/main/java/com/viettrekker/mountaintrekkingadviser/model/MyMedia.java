package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author LongNC
 * 25/07/2018
 */
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
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;

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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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
