package com.example.dell.muhingalayoutprototypes;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


public class SavedHousesResponse extends AbstractItem<SavedHousesResponse, SavedHousesResponse.savedHousesViewHolder> {


    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("img_5")
    @Expose
    private Object img5;
    @SerializedName("mian_image_reference")
    @Expose
    private String mianImageReference;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("img_3")
    @Expose
    private Object img3;
    @SerializedName("img_4")
    @Expose
    private Object img4;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("Land")
    @Expose
    private Object land;
    @SerializedName("ownerId")
    @Expose
    private Object ownerId;
    @SerializedName("img_2")
    @Expose
    private Object img2;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("for_sale")
    @Expose
    private Boolean forSale;
    @SerializedName("updated")
    @Expose
    private Integer updated;
    @SerializedName("viewing_dates")
    @Expose
    private String viewingDates;
    @SerializedName("SKU")
    @Expose
    private String sKU;
    @SerializedName("objectId")
    @Expose
    private String objectId;
    @SerializedName("Location")
    @Expose
    private String location;
    @SerializedName("Rent")
    @Expose
    private Boolean rent;
    @SerializedName("___class")
    @Expose
    private String _class;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Object getImg5() {
        return img5;
    }

    public void setImg5(Object img5) {
        this.img5 = img5;
    }

    public String getMianImageReference() {
        return mianImageReference;
    }

    public void setMianImageReference(String mianImageReference) {
        this.mianImageReference = mianImageReference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Object getImg3() {
        return img3;
    }

    public void setImg3(Object img3) {
        this.img3 = img3;
    }

    public Object getImg4() {
        return img4;
    }

    public void setImg4(Object img4) {
        this.img4 = img4;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getLand() {
        return land;
    }

    public void setLand(Object land) {
        this.land = land;
    }

    public Object getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Object ownerId) {
        this.ownerId = ownerId;
    }

    public Object getImg2() {
        return img2;
    }

    public void setImg2(Object img2) {
        this.img2 = img2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Integer getUpdated() {
        return updated;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public String getViewingDates() {
        return viewingDates;
    }

    public void setViewingDates(String viewingDates) {
        this.viewingDates = viewingDates;
    }

    public String getSKU() {
        return sKU;
    }

    public void setSKU(String sKU) {
        this.sKU = sKU;
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

    public Boolean getRent() {
        return rent;
    }

    public void setRent(Boolean rent) {
        this.rent = rent;
    }

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }



    //Fast Adapter methods and code starts here













    @Override
    public savedHousesViewHolder getViewHolder(View view) {
        return new savedHousesViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.saved_houses_houses_rec_view_single_object_image;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.saved_items_activity_rec_view_single_house_object;
    }


    @Override
    public void bindView(savedHousesViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);



        if (getRent()) {
            holder.rent_vh.setText(R.string.ForRent);
        } else {
            holder.rent_vh.setText(R.string.ForSale);
        }

        holder.title_vh.setText(title);

        String mainImageRef = mianImageReference;


        Glide.with(holder.itemView).load(mianImageReference).into(holder.house_main_image_vh);





    }

    protected static class savedHousesViewHolder extends RecyclerView.ViewHolder{


        //declaring the views
        TextView title_vh, location_vh, rent_vh, price_vh;
        ImageView house_main_image_vh;


        public savedHousesViewHolder(@NonNull View itemView) {
            super(itemView);

            //assigning the previously declared views
            title_vh = itemView.findViewById(R.id.saved_houses_houses_rec_view_single_object_title);
            location_vh = itemView.findViewById(R.id.saved_houses_houses_rec_view_single_object_location);
            price_vh = itemView.findViewById(R.id.saved_houses_houses_rec_view_single_object_price);
            rent_vh = itemView.findViewById(R.id.saved_houses_houses_rec_view_single_object_rent_sale);
            house_main_image_vh = itemView.findViewById(R.id.saved_houses_houses_rec_view_single_object_image);



        }
    }



}
