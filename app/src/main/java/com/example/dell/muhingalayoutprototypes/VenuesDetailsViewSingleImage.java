package com.example.dell.muhingalayoutprototypes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class VenuesDetailsViewSingleImage extends AbstractItem<VenuesDetailsViewSingleImage, VenuesDetailsViewSingleImage.VenuesImageViewHolder> {


    private String venuesDetailsImageReference;


    public VenuesDetailsViewSingleImage(String venuesDetailsImageReference) {
        this.venuesDetailsImageReference = venuesDetailsImageReference;
    }

    public String getVenuesDetailsImageReference() {
        return venuesDetailsImageReference;
    }

    public void setVenuesDetailsImageReference(String venuesDetailsImageReference) {
        this.venuesDetailsImageReference = venuesDetailsImageReference;
    }


    /*******************************************************************************************************************************************************
     * FAST ADAPTER CODE STARTS HERE
     *
     */


    @Override
    public VenuesDetailsViewSingleImage.VenuesImageViewHolder getViewHolder(View v) {
        return new VenuesImageViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.venues_details_view_main_slider_single_object;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.venues_details_view_image_slider_single_object;
    }

    @Override
    public void bindView(VenuesImageViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading_default_img_square).fallback(R.drawable.default_image_fallback_169)
                .error(R.drawable.default_error_img);


        Glide.with(holder.itemView).load(venuesDetailsImageReference).apply(options).into(holder.venues_details_main_image_vh);

    }

    protected static class VenuesImageViewHolder extends RecyclerView.ViewHolder {

        //declaring the views
        ImageView venues_details_main_image_vh;


        public VenuesImageViewHolder(View itemView) {
            super(itemView);
            venues_details_main_image_vh = itemView.findViewById(R.id.venues_details_view_main_slider_single_object);
        }
    }
}
