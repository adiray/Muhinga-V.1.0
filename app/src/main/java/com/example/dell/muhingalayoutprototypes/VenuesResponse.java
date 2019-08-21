package com.example.dell.muhingalayoutprototypes;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

    @SerializedName("phone")
    private String phone;

    public void setImg4(Object img4) {
        this.img4 = img4;
    }

    public Object getImg4() {
        return img4;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
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


        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading_default_img_square).fallback(R.drawable.default_image_fallback_169)
                .error(R.drawable.default_error_img);


        Glide.with(holder.itemView).load(mainImageReference).apply(options).into(holder.venues_main_image_vh);
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










/*
* <?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/venues_details_item_price"
        android:layout_width="wrap_content"
        android:layout_height="0dp"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:fontFamily="@font/montserrat_regular"
        android:text="TextView"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="15sp"
        app:layout_constraintStart_toEndOf="@+id/venues_details_item_location"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_title" />

    <TextView
        android:id="@+id/venues_details_item_location"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:fontFamily="@font/montserrat_regular"
        android:text="TextView"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="15sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_title" />

    <TextView
        android:id="@+id/venues_details_item_title"

        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="8dp"
        android:fontFamily="@font/montserrat_bold"
        android:text="TextView"
        android:textColor="@color/my_color_secondary"
        android:textSize="15sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/venues_details_item_size_label"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:fontFamily="@font/montserrat_regular"
        android:text="@string/capacitycolon"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="15sp"
        app:layout_constraintStart_toEndOf="@+id/venues_details_item_price"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_title" />


    <TextView
        android:id="@+id/venues_details_item_size"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginTop="16dp"
        android:fontFamily="@font/montserrat_regular"
        android:text="TextView"
        android:textAlignment="center"
        android:textColor="@color/my_color_secondary"
        android:textSize="15sp"
        app:layout_constraintStart_toEndOf="@+id/venues_details_item_size_label"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_title" />

    <TextView
        android:id="@+id/venues_details_item_description"

        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="16dp"
        android:fontFamily="@font/montserrat_regular"
        android:scrollbars="vertical"
        android:text="TextView thdysh sjshsns shsjsjsnsh susn,sdnsdkjkdsk sailsidhnkd dlashdnxdis adlhlhdsjskl skllck"
        android:textAlignment="viewStart"
        android:textColor="@color/my_color_secondary"
        android:textSize="13sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/divider" />


    <View
        android:id="@+id/divider"
        android:layout_width="0dp"
        android:layout_height="3dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="8dp"
        android:background="@color/my_color_bg"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_size" />


    <LinearLayout
        android:id="@+id/linearLayout1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"

        android:layout_marginTop="16dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        app:layout_constraintEnd_toStartOf="@+id/linearLayout2"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_description">


        <android.support.v7.widget.CardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="4dp"
            app:cardElevation="2dp"
            app:contentPadding="10dp"

            >

            <ImageView
                android:id="@+id/venues_details_call_owner"
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:src="@drawable/phone_colored_green_filled" />

        </android.support.v7.widget.CardView>


        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/call"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


    <LinearLayout
        android:id="@+id/linearLayout2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"

        android:layout_marginTop="16dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        app:layout_constraintEnd_toStartOf="@+id/linearLayout3"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/linearLayout1"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_description">


        <android.support.v7.widget.CardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="4dp"
            app:cardElevation="2dp"
            app:contentPadding="10dp"

            >

            <ImageView
                android:id="@+id/save_venue_venues_details_layout"
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:src="@drawable/cash_hand_colored_filled" />

        </android.support.v7.widget.CardView>


        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/book"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


    <LinearLayout
        android:id="@+id/linearLayout3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"

        android:layout_marginTop="16dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        app:layout_constraintEnd_toStartOf="@+id/linearLayout4"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/linearLayout2"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_description">


        <android.support.v7.widget.CardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="4dp"
            app:cardElevation="2dp"
            app:contentPadding="10dp"

            >

            <ImageView
                android:id="@+id/book_venue_venues_details_layout"
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:src="@drawable/share_colored_blue_filled" />

        </android.support.v7.widget.CardView>


        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/share"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


    <LinearLayout
        android:id="@+id/linearLayout4"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"

        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/linearLayout3"
        app:layout_constraintTop_toBottomOf="@+id/venues_details_item_description">


        <android.support.v7.widget.CardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="4dp"
            app:cardElevation="2dp"
            app:contentPadding="10dp"

            >

            <ImageView
                android:id="@+id/share_venue_venues_details_layout"
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:src="@drawable/contacts_colored_red_filled" />

        </android.support.v7.widget.CardView>


        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/save"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


</android.support.constraint.ConstraintLayout>
*
*
*
* */




