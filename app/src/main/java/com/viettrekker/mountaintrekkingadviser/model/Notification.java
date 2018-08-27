package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("countNew")
    @Expose
    int countNew;
    @SerializedName("oldestId")
    @Expose
    int oldestId;
    @SerializedName("oldestIdinHistoryId")
    @Expose
    int oldestIdinHistoryId;
    @SerializedName("typeId")
    @Expose
    int typeId;
    @SerializedName("sourceId")
    @Expose
    int sourceId;
    @SerializedName("targetId")
    @Expose
    int targetId;
    @SerializedName("content")
    @Expose
    String content;
    @SerializedName("state")
    @Expose
    int state;
    @SerializedName("source")
    @Expose
    private User user;
    @SerializedName("post")
    @Expose
    private Post post;
    @SerializedName("comment")
    @Expose
    private Comment comment;
    @SerializedName("updated_at")
    @Expose
    private Date updated_at;
    @SerializedName("targetOwner")
    @Expose
    private User targetOwner;

    public Notification(int typeId) {

        this.typeId = typeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
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

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public int getOldestId() {
        return oldestId;
    }

    public void setOldestId(int oldestId) {
        this.oldestId = oldestId;
    }

    public int getOldestIdinHistoryId() {
        return oldestIdinHistoryId;
    }

    public void setOldestIdinHistoryId(int oldestIdinHistoryId) {
        this.oldestIdinHistoryId = oldestIdinHistoryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public int getCountNew() {
        return countNew;
    }

    public void setCountNew(int countNew) {
        this.countNew = countNew;
    }

    public User getTargetOwner() {
        return targetOwner;
    }

    public void setTargetOwner(User targetOwner) {
        this.targetOwner = targetOwner;
    }
}
