package com.example.dell.muhingalayoutprototypes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


public class SongsItem extends AbstractItem<SongsItem, SongsItem.SongItemViewHolder> {

    @SerializedName("Artist")
    private String artist;

    @SerializedName("file")
    private String file;

    @SerializedName("created")
    private long created;

    @SerializedName("___class")
    private String ___class;

    @SerializedName("cover_image")
    private String coverImage;

    @SerializedName("title")
    private String title;

    @SerializedName("ownerId")
    private String ownerId;

    @SerializedName("updated")
    private long updated;

    @SerializedName("objectId")
    private String objectId;

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Object getOwnerId() {
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
                "SongsItem{" +
                        "artist = '" + artist + '\'' +
                        ",file = '" + file + '\'' +
                        ",created = '" + created + '\'' +
                        ",___class = '" + ___class + '\'' +
                        ",cover_image = '" + coverImage + '\'' +
                        ",title = '" + title + '\'' +
                        ",ownerId = '" + ownerId + '\'' +
                        ",updated = '" + updated + '\'' +
                        ",objectId = '" + objectId + '\'' +
                        "}";
    }


    /*******************************************************************************************************************************************************
     * FAST ADAPTER CODE STARTS HERE
     *
     */


    @Override
    public SongsItem.SongItemViewHolder getViewHolder(View v) {
        return new SongItemViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.single_song_song_cover_image;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.album_view_songs_rec_view_single_object;
    }


    @Override
    public void bindView(SongItemViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);


        holder.title_vh.setText(getTitle());
        Glide.with(holder.itemView).load(getCoverImage()).into(holder.song_cover_vh);


    }

    protected static class SongItemViewHolder extends RecyclerView.ViewHolder {

        //declaring the views
        TextView title_vh;
        ImageView song_cover_vh;


        public SongItemViewHolder(View itemView) {
            super(itemView);

            //assigning the previously declared views
            title_vh = itemView.findViewById(R.id.single_song_item_song_name);
            song_cover_vh = itemView.findViewById(R.id.single_song_song_cover_image);


        }
    }


}
