package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Post {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("typeId")
    @Expose
    private int typeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("state")
    @Expose
    private int state;
    @SerializedName("gallery")
    @Expose
    private MyGallery gallery;
    @SerializedName("directionId")
    @Expose
    private int directionId;
    @SerializedName("liked")
    @Expose
    private int liked;
    @SerializedName("commentsCount")
    @Expose
    private int commentsCount;
    @SerializedName("likesCount")
    @Expose
    private int likesCount;
    @SerializedName("reportsCount")
    @Expose
    private int reportsCount;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("updated_at")
    @Expose
    private Date updated_at;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments;
    

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public MyGallery getGallery() {
        return gallery;
    }

    public void setGallery(MyGallery gallery) {
        this.gallery = gallery;
    }

    public int getDirectionId() {
        return directionId;
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getReportsCount() {
        return reportsCount;
    }

    public void setReportsCount(int reportsCount) {
        this.reportsCount = reportsCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", gallery=" + gallery +
                ", directionId=" + directionId +
                ", liked=" + liked +
                ", commentsCount=" + commentsCount +
                ", likesCount=" + likesCount +
                ", reportsCount=" + reportsCount +
                ", user=" + user +
                ", updated_at=" + updated_at +
                '}';
    }
}
