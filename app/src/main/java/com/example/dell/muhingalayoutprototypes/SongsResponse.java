package com.example.dell.muhingalayoutprototypes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class SongResponse {

    @SerializedName("created")
    private long created;

    @SerializedName("songs")
    private ArrayList<SongsItem> songs;

    @SerializedName("name")
    private String name;

    @SerializedName("___class")
    private String ___class;

    @SerializedName("cover_image")
    private String coverImage;

    @SerializedName("ownerId")
    private String ownerId;

    @SerializedName("updated")
    private long updated;

    @SerializedName("objectId")
    private String objectId;

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public void setSongs(ArrayList<SongsItem> songs) {
        this.songs = songs;
    }

    public ArrayList<SongsItem> getSongs() {
        return songs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setClass(String ___class) {
        this.___class = ___class;
    }

    public String get___class() {
        return ___class;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getUpdated() {
        return updated;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return
                "SongResponse{" +
                        "created = '" + created + '\'' +
                        ",songs = '" + songs + '\'' +
                        ",name = '" + name + '\'' +
                        ",___class = '" + ___class + '\'' +
                        ",cover_image = '" + coverImage + '\'' +
                        ",ownerId = '" + ownerId + '\'' +
                        ",updated = '" + updated + '\'' +
                        ",objectId = '" + objectId + '\'' +
                        "}";
    }
}
