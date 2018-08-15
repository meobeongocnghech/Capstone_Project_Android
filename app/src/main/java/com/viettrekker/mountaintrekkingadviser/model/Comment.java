package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Comment {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("targetId")
    @Expose
    private int targetId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("state")
    @Expose
    private int state;
    @SerializedName("liked")
    @Expose
    private int liked;
    @SerializedName("likesCount")
    @Expose
    private int likesCount;
    @SerializedName("commentsCount")
    @Expose
    private int commentsCount;
    @SerializedName("updated_at")
    @Expose
    private Date updated_at;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("children")
    @Expose
    private List<Comment> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
}
