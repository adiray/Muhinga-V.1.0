package com.example.dell.muhingalayoutprototypes;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


public class LandDetailsViewSingleImage extends AbstractItem<LandDetailsViewSingleImage, LandDetailsViewSingleImage.LandImageViewHolder> {


    private String landDetailsImageReference;


    public String getLandDetailsImageReference() {
        return landDetailsImageReference;
    }

    public void setLandDetailsImageReference(String landDetailsImageReference) {
        this.landDetailsImageReference = landDetailsImageReference;
    }

    public LandDetailsViewSingleImage(String landDetailsImageReference) {
        this.landDetailsImageReference = landDetailsImageReference;
    }


    /*******************************************************************************************************************************************************
     * FAST ADAPTER CODE STARTS HERE
     *
     */

    @Override
    public LandImageViewHolder getViewHolder(View v) {
        return new LandImageViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.land_details_view_main_slider_single_object;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.land_details_view_image_slider_single_item;
    }


    @Override
    public void bindView(LandImageViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);


        Glide.with(holder.itemView).load(landDetailsImageReference).into(holder.land_details_main_image_vh);


    }


    protected static class LandImageViewHolder extends RecyclerView.ViewHolder {

        //declaring the views
        ImageView land_details_main_image_vh;

        public LandImageViewHolder(View itemView) {
            super(itemView);

            //assigning the previously declared views
            land_details_main_image_vh = itemView.findViewById(R.id.land_details_view_main_slider_single_object);
        }
    }


}
