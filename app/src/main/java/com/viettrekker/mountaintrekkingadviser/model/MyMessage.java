package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author LongNC
 * 25/07/2018
 */
public class MyMessage {
    @Expose
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
