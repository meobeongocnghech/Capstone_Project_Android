package com.viettrekker.mountaintrekkingadviser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {
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
    private SourceNotification source;
    @SerializedName("post")
    @Expose
    private PostNotification post;
    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    public Notification(int oldestId, int oldestIdinHistoryId, int typeId, int sourceId, int targetId, String content, int state, SourceNotification source, PostNotification post, Date updated_at) {
        this.oldestId = oldestId;
        this.oldestIdinHistoryId = oldestIdinHistoryId;
        this.typeId = typeId;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.content = content;
        this.state = state;
        this.source = source;
        this.post = post;
        this.updated_at = updated_at;
    }

    public Notification(int typeId) {

        this.typeId = typeId;
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

    public SourceNotification getSource() {
        return source;
    }

    public void setSource(SourceNotification source) {
        this.source = source;
    }

    public PostNotification getPost() {
        return post;
    }

    public void setPost(PostNotification post) {
        this.post = post;
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
}
