package com.example.dell.muhingalayoutprototypes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class real_estate extends AbstractItem<real_estate, real_estate.housesViewHolder> {





    private String img_5;
    private String ownerId;
    private String img_4;
    private java.util.Date updated;
    private String Location;
    private String objectId;
    private String owner;
    private String img_2;
    private String price;
    private Boolean Land;
    private String SKU;
    private Boolean Rent;
    private java.util.Date created;
    private String img_3;
    private String mian_image_reference;
    private String title;
    private String description;
    private Boolean for_sale;

    public String getImg_5() {
        return img_5;
    }

    public void setImg_5(String img_5) {
        this.img_5 = img_5;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getImg_4() {
        return img_4;
    }

    public void setImg_4(String img_4) {
        this.img_4 = img_4;
    }

    public java.util.Date getUpdated() {
        return updated;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImg_2() {
        return img_2;
    }

    public void setImg_2(String img_2) {
        this.img_2 = img_2;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getLand() {
        return Land;
    }

    public void setLand(Boolean Land) {
        this.Land = Land;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public Boolean getRent() {
        return Rent;
    }

    public void setRent(Boolean Rent) {
        this.Rent = Rent;
    }

    public java.util.Date getCreated() {
        return created;
    }

    public String getImg_3() {
        return img_3;
    }

    public void setImg_3(String img_3) {
        this.img_3 = img_3;
    }

    public String getMian_image_reference() {
        return mian_image_reference;
    }

    public void setMian_image_reference(String mian_image_reference) {
        this.mian_image_reference = mian_image_reference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFor_sale() {
        return for_sale;
    }

    public void setFor_sale(Boolean for_sale) {
        this.for_sale = for_sale;
    }


    public real_estate save() {
        return Backendless.Data.of(real_estate.class).save(this);
    }

    public void saveAsync(AsyncCallback<real_estate> callback) {
        Backendless.Data.of(real_estate.class).save(this, callback);
    }

    public Long remove() {
        return Backendless.Data.of(real_estate.class).remove(this);
    }

    public void removeAsync(AsyncCallback<Long> callback) {
        Backendless.Data.of(real_estate.class).remove(this, callback);
    }

    public static real_estate findById(String id) {
        return Backendless.Data.of(real_estate.class).findById(id);
    }

    public static void findByIdAsync(String id, AsyncCallback<real_estate> callback) {
        Backendless.Data.of(real_estate.class).findById(id, callback);
    }

    public static real_estate findFirst() {
        return Backendless.Data.of(real_estate.class).findFirst();
    }

    public static void findFirstAsync(AsyncCallback<real_estate> callback) {
        Backendless.Data.of(real_estate.class).findFirst(callback);
    }

    public static real_estate findLast() {
        return Backendless.Data.of(real_estate.class).findLast();
    }

    public static void findLastAsync(AsyncCallback<real_estate> callback) {
        Backendless.Data.of(real_estate.class).findLast(callback);
    }

    public static List<real_estate> find(DataQueryBuilder queryBuilder) {
        return Backendless.Data.of(real_estate.class).find(queryBuilder);
    }

    public static void findAsync(DataQueryBuilder queryBuilder, AsyncCallback<List<real_estate>> callback) {
        Backendless.Data.of(real_estate.class).find(queryBuilder, callback);
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
        viewHolder.location_vh.setText(getLocation());
        viewHolder.price_vh.setText(price);

        if (getRent()!=null && getRent()) {
            viewHolder.rent_vh.setText(R.string.ForRent);
        } else {
            viewHolder.rent_vh.setText(R.string.ForSale);
        }

        viewHolder.title_vh.setText(title);

        Glide.with(viewHolder.itemView).load(getMian_image_reference()).into(viewHolder.house_main_image_vh);

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