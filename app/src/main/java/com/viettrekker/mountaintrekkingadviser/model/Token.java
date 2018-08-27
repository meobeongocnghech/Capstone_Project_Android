package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("tokenId")
    @Expose
    private String token;

    public void setToken(String token) {
        this.token = token;
    }
}
