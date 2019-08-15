package com.example.dell.muhingalayoutprototypes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class SavedLandResponse extends AbstractItem<SavedLandResponse, SavedLandResponse.SavedLandViewHolder> {


    @SerializedName("owner")
    @Expose
    private Object owner;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("viewing_dates")
    @Expose
    private String viewingDates;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("SKU")
    @Expose
    private Object sKU;
    @SerializedName("created")
    @Expose
    private long created;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("img_2")
    @Expose
    private String img2;
    @SerializedName("img_3")
    @Expose
    private String img3;
    @SerializedName("img_4")
    @Expose
    private String img4;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("img_5")
    @Expose
    private String img5;
    @SerializedName("ownerId")
    @Expose
    private Object ownerId;
    @SerializedName("objectId")
    @Expose
    private String objectId;
    @SerializedName("Location")
    @Expose
    private String location;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("updated")
    @Expose
    private long updated;
    @SerializedName("mian_image_reference")
    @Expose
    private String mianImageReference;
    @SerializedName("___class")
    @Expose
    private String _class;

    /**
     * No args constructor for use in serialization
     */
    public SavedLandResponse() {
    }


    /**
     * @param img2
     * @param sKU
     * @param location
     * @param ownerId
     * @param size
     * @param title
     * @param _class
     * @param price
     * @param updated
     * @param created
     * @param description
     * @param objectId
     * @param owner
     * @param mianImageReference
     * @param img5
     * @param img3
     * @param img4
     */
    public SavedLandResponse(Object owner, String title, Object sKU, Integer created, String description, String img2, String img3, String img4, String size, String img5, Object ownerId, String objectId, String location, String price, Integer updated, String mianImageReference, String _class) {
        super();
        this.owner = owner;
        this.title = title;
        this.sKU = sKU;
        this.created = created;
        this.description = description;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.size = size;
        this.img5 = img5;
        this.ownerId = ownerId;
        this.objectId = objectId;
        this.location = location;
        this.price = price;
        this.updated = updated;
        this.mianImageReference = mianImageReference;
        this._class = _class;
    }


    public void setViewingDates(String viewingDates) {
        this.viewingDates = viewingDates;
    }


    public String getViewingDates() {
        return viewingDates;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getPhone() {
        return phone;
    }


    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getSKU() {
        return sKU;
    }

    public void setSKU(Object sKU) {
        this.sKU = sKU;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public Object getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Object ownerId) {
        this.ownerId = ownerId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getMianImageReference() {
        return mianImageReference;
    }

    public void setMianImageReference(String mianImageReference) {
        this.mianImageReference = mianImageReference;
    }

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }


    public SavedLandResponse(String phone, String viewingDates, String title, String description, String img2, String img3, String img4, String size, String img5, String objectId, String location, String price, String mianImageReference) {
        this.phone = phone;
        this.viewingDates = viewingDates;
        this.title = title;
        this.description = description;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.size = size;
        this.img5 = img5;
        this.objectId = objectId;
        this.location = location;
        this.price = price;
        this.mianImageReference = mianImageReference;
    }

    /*******************************************************************************************************************************************************
     * FAST ADAPTER CODE STARTS HERE
     *
     */






    //constructor













    @Override
    public int getType() {
        return R.id.saved_land_rec_view_object_main_image;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.saved_land_rec_view_single_item;
    }


    @Override
    public void bindView(SavedLandViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.location_vh.setText(getLocation());
        holder.price_vh.setText(getPrice());
        holder.size_vh.setText(getSize());
        holder.title_vh.setText(getTitle());
        Glide.with(holder.itemView).load(mianImageReference).into(holder.land_main_image_vh);

    }

    @Override
    public SavedLandViewHolder getViewHolder(View view) {
        return new SavedLandViewHolder(view);
    }


    protected static class SavedLandViewHolder extends RecyclerView.ViewHolder {

        //declaring the views
        TextView title_vh, location_vh, size_vh, price_vh;
        ImageView land_main_image_vh, deleteItem;

        public SavedLandViewHolder(View itemView) {
            super(itemView);

            //assigning the previously declared views

            title_vh = itemView.findViewById(R.id.saved_land_rec_view_object_title);
            location_vh = itemView.findViewById(R.id.saved_land_rec_view_object_location);
            size_vh = itemView.findViewById(R.id.saved_land_rec_view_object_size);
            price_vh = itemView.findViewById(R.id.saved_land_rec_view_object_price);
            land_main_image_vh = itemView.findViewById(R.id.saved_land_rec_view_object_main_image);
            deleteItem = itemView.findViewById(R.id.saved_land_unsave_land_button);


        }
    }


}
