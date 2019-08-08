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

public class HousesResponse extends AbstractItem<HousesResponse, HousesResponse.housesViewHolder> {


    @SerializedName("owner")
    private String owner;

    @SerializedName("img_4")
    private String img4;

    @SerializedName("img_5")
    private String img5;

    @SerializedName("mian_image_reference")
    private String mianImageReference;

    @SerializedName("created")
    private long created;

    @SerializedName("description")
    private String description;

    @SerializedName("ownerId")
    private Object ownerId;

    @SerializedName("img_2")
    private String img2;

    @SerializedName("phone")
    @Expose
    private String phone;


    @SerializedName("title")
    private String title;

    @SerializedName("img_3")
    private String img3;

    @SerializedName("Land")
    private Object land;

    @SerializedName("price")
    private String price;

    @SerializedName("___class")
    private String ___class;

    @SerializedName("for_sale")
    private Object forSale;

    @SerializedName("updated")
    private long updated;

    @SerializedName("objectId")
    private String objectId;

    @SerializedName("Location")
    private String location;

    @SerializedName("Rent")
    private boolean rent;


    @SerializedName("viewing_dates")
    @Expose
    private String viewingDates;







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

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public String getImg5() {
        return img5;
    }

    public void setMianImageReference(String mianImageReference) {
        this.mianImageReference = mianImageReference;
    }

    public String getMianImageReference() {
        return mianImageReference;
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

    public void setOwnerId(Object ownerId) {
        this.ownerId = ownerId;
    }

    public Object getOwnerId() {
        return ownerId;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg2() {
        return img2;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg3() {
        return img3;
    }

    public void setLand(Object land) {
        this.land = land;
    }

    public Object getLand() {
        return land;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void set___Class(String ___class) {
        this.___class = ___class;
    }

    public String get___Class() {
        return ___class;
    }

    public void setForSale(Object forSale) {
        this.forSale = forSale;
    }

    public Object getForSale() {
        return forSale;
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

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setRent(boolean rent) {
        this.rent = rent;
    }

    public boolean isRent() {
        return rent;
    }

    @Override
    public String toString() {
        return
                "HousesResponse{" +
                        "owner = '" + owner + '\'' +
                        ",img_4 = '" + img4 + '\'' +
                        ",img_5 = '" + img5 + '\'' +
                        ",mian_image_reference = '" + mianImageReference + '\'' +
                        ",created = '" + created + '\'' +
                        ",description = '" + description + '\'' +
                        ",ownerId = '" + ownerId + '\'' +
                        ",img_2 = '" + img2 + '\'' +
                        ",title = '" + title + '\'' +
                        ",img_3 = '" + img3 + '\'' +
                        ",land = '" + land + '\'' +
                        ",price = '" + price + '\'' +
                        ",___class = '" + ___class + '\'' +
                        ",for_sale = '" + forSale + '\'' +
                        ",updated = '" + updated + '\'' +
                        ",objectId = '" + objectId + '\'' +
                        ",location = '" + location + '\'' +
                        ",rent = '" + rent + '\'' +
                        "}";
    }

    //Fast Adapter methods and code starts here

    @Override
    public housesViewHolder getViewHolder(View v) {
        return new housesViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.houses_rec_view_single_object_image;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.houses_rec_view_single_object;
    }

    @Override
    public void bindView(housesViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.location_vh.setText(location);
        viewHolder.price_vh.setText(price);

        if (isRent()) {
            viewHolder.rent_vh.setText(R.string.ForRent);
        } else {
            viewHolder.rent_vh.setText(R.string.ForSale);
        }

        viewHolder.title_vh.setText(title);

        Glide.with(viewHolder.itemView).load(mianImageReference).into(viewHolder.house_main_image_vh);

    }


    protected static class housesViewHolder extends RecyclerView.ViewHolder {

        //declaring the views
        TextView title_vh, location_vh, rent_vh, price_vh;
        ImageView house_main_image_vh;

        public housesViewHolder(View itemView) {
            super(itemView);

            //assigning the previously declared views
            title_vh = itemView.findViewById(R.id.houses_rec_view_single_object_title);
            location_vh = itemView.findViewById(R.id.houses_rec_view_single_object_location);
            price_vh = itemView.findViewById(R.id.houses_rec_view_single_object_price);
            rent_vh = itemView.findViewById(R.id.houses_rec_view_single_object_rent_sale);
            house_main_image_vh = itemView.findViewById(R.id.houses_rec_view_single_object_image);

        }
    }


}
