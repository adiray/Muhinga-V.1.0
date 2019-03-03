package com.example.dell.muhingalayoutprototypes;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class VenuesResponse extends AbstractItem<VenuesResponse, VenuesResponse.VenuesViewHolder> {

    @SerializedName("img_4")
    private Object img4;

    @SerializedName("img_5")
    private Object img5;

    @SerializedName("created")
    private long created;

    @SerializedName("description")
    private String description;

    @SerializedName("title")
    private String title;

    @SerializedName("img_2")
    private Object img2;

    @SerializedName("ownerId")
    private Object ownerId;

    @SerializedName("img_3")
    private Object img3;

    @SerializedName("capacity")
    private String capacity;

    @SerializedName("main_image_reference")
    private String mainImageReference;

    @SerializedName("price")
    private String price;

    @SerializedName("___class")
    private String ___class;

    @SerializedName("location")
    private String location;

    @SerializedName("updated")
    private long updated;

    @SerializedName("objectId")
    private String objectId;

    public void setImg4(Object img4) {
        this.img4 = img4;
    }

    public Object getImg4() {
        return img4;
    }

    public void setImg5(Object img5) {
        this.img5 = img5;
    }

    public Object getImg5() {
        return img5;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setImg2(Object img2) {
        this.img2 = img2;
    }

    public Object getImg2() {
        return img2;
    }

    public void setOwnerId(Object ownerId) {
        this.ownerId = ownerId;
    }

    public Object getOwnerId() {
        return ownerId;
    }

    public void setImg3(Object img3) {
        this.img3 = img3;
    }

    public Object getImg3() {
        return img3;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setMainImageReference(String mainImageReference) {
        this.mainImageReference = mainImageReference;
    }

    public String getMainImageReference() {
        return mainImageReference;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setClass(String ___class) {
        this.___class = ___class;
    }

    public String get___class() {
        return ___class;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
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
                "VenuesResponse{" +
                        "img_4 = '" + img4 + '\'' +
                        ",img_5 = '" + img5 + '\'' +
                        ",created = '" + created + '\'' +
                        ",description = '" + description + '\'' +
                        ",title = '" + title + '\'' +
                        ",img_2 = '" + img2 + '\'' +
                        ",ownerId = '" + ownerId + '\'' +
                        ",img_3 = '" + img3 + '\'' +
                        ",capacity = '" + capacity + '\'' +
                        ",main_image_reference = '" + mainImageReference + '\'' +
                        ",price = '" + price + '\'' +
                        ",___class = '" + ___class + '\'' +
                        ",location = '" + location + '\'' +
                        ",updated = '" + updated + '\'' +
                        ",objectId = '" + objectId + '\'' +
                        "}";
    }


    /*******************************************************************************************************************************************************
     * FAST ADAPTER CODE STARTS HERE
     *
     */


    @Override
    public VenuesResponse.VenuesViewHolder getViewHolder(View v) {
        return new VenuesViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.venues_rec_view_object_main_image;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.venues_rec_view_single_object;
    }


    @Override
    public void bindView(VenuesViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.location_vh.setText(getLocation());
        holder.price_vh.setText(getPrice());
        holder.size_vh.setText(getCapacity());
        holder.title_vh.setText(getTitle());
        Glide.with(holder.itemView).load(mainImageReference).into(holder.venues_main_image_vh);
    }


    static class VenuesViewHolder extends RecyclerView.ViewHolder {

        //declaring the views
        TextView title_vh, location_vh, size_vh, price_vh;
        ImageView venues_main_image_vh;

        public VenuesViewHolder(View itemView) {
            super(itemView);
            //assigning the previously declared views

            title_vh = itemView.findViewById(R.id.venues_rec_view_object_title);
            location_vh = itemView.findViewById(R.id.venues_rec_view_object_location);
            size_vh = itemView.findViewById(R.id.venues_rec_view_object_size);
            price_vh = itemView.findViewById(R.id.venues_rec_view_object_price);
            venues_main_image_vh = itemView.findViewById(R.id.venues_rec_view_object_main_image);
        }
    }


}