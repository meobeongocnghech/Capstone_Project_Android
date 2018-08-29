package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Plan {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("timelines")
    @Expose
    private String timelines;
    @SerializedName("checklist")
    @Expose
    private CheckList checklist;
    @SerializedName("state")
    @Expose
    private int state;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("finishTime")
    @Expose
    private String finishTime;
    @SerializedName("group")
    @Expose
    private Group group;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("direction")
    @Expose
    private Direction direction;
    @SerializedName("isPublic")
    @Expose
    private int isPublic;
    @SerializedName("rowCount")
    @Expose
    private int rowCount;
    @SerializedName("carry")
    @Expose
    private int carry;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimelines() {
        return timelines;
    }

    public void setTimelines(String timelines) {
        this.timelines = timelines;
    }

    public CheckList getChecklist() {
        return checklist;
    }

    public void setChecklist(CheckList checklist) {
        this.checklist = checklist;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getCarry() {
        return carry;
    }

    public void setCarry(int carry) {
        this.carry = carry;
    }
}
