package com.example.dell.muhingalayoutprototypes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class HousesDetailsViewSingleImage extends AbstractItem<HousesDetailsViewSingleImage, HousesDetailsViewSingleImage.HousesImageViewHolder> {


    private String housesDetailsImageReference;

    public String getHousesDetailsImageReference() {
        return housesDetailsImageReference;
    }

    public void setHousesDetailsImageReference(String housesDetailsImageReference) {
        this.housesDetailsImageReference = housesDetailsImageReference;
    }

    public HousesDetailsViewSingleImage(String housesDetailsImageReference) {
        this.housesDetailsImageReference = housesDetailsImageReference;
    }


    /*******************************************************************************************************************************************************
     * FAST ADAPTER CODE STARTS HERE
     *
     */


    @Override
    public HousesDetailsViewSingleImage.HousesImageViewHolder getViewHolder(View v) {
        return new HousesImageViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.houses_details_view_main_slider_single_object;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.houses_details_view_image_slider_single_item;
    }


    @Override
    public void bindView(HousesImageViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        Glide.with(holder.itemView).load(housesDetailsImageReference).into(holder.houses_details_main_image_vh);


    }

    protected static class HousesImageViewHolder extends RecyclerView.ViewHolder {

        //declaring the views
        ImageView houses_details_main_image_vh;

        public HousesImageViewHolder(View itemView) {
            super(itemView);

            //assigning the previously declared views
            houses_details_main_image_vh = itemView.findViewById(R.id.houses_details_view_main_slider_single_object);
        }
    }


}
