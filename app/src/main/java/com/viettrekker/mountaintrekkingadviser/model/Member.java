package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Member {
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("roleInGroupId")
    @Expose
    private int roleInGroupId;
    @SerializedName("sosDelay")
    @Expose
    private int sosDelay;
    @SerializedName("vehicule")
    @Expose
    private String vehicule;
    @SerializedName("carry")
    @Expose
    private int carry;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public int getRoleInGroupId() {
        return roleInGroupId;
    }

    public void setRoleInGroupId(int roleInGroupId) {
        this.roleInGroupId = roleInGroupId;
    }


    public int getSosDelay() {
        return sosDelay;
    }

    public void setSosDelay(int sosDelay) {
        this.sosDelay = sosDelay;
    }

    public String getVehicule() {
        return vehicule;
    }

    public void setVehicule(String vehicule) {
        this.vehicule = vehicule;
    }

    public int getCarry() {
        return carry;
    }

    public void setCarry(int carry) {
        this.carry = carry;
    }
}
