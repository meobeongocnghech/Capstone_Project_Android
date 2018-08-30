package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class Member{
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("groupId")
    @Expose
    private int groupId;
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
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("avatar")
    @Expose
    private MyMedia avatar;

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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public MyMedia getAvatar() {
        return avatar;
    }

    public void setAvatar(MyMedia avatar) {
        this.avatar = avatar;
    }

}
