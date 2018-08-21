package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Direction {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("points")
    @Expose
    private String planLocation;
    @SerializedName("placeId")
    @Expose
    private int placeId;
    @SerializedName("place")
    @Expose
    private Place place;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanLocation() {
        return planLocation;
    }

    public void setPlanLocation(String planLocation) {
        this.planLocation = planLocation;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
