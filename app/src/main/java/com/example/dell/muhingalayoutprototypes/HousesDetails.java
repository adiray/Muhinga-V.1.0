package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class HousesDetails extends AppCompatActivity {

    ArrayList<String> housesItemImageReferences = new ArrayList<>();
    ArrayList<HousesDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();


    String housesDetailsTitle, housesDetailsRentOrSale, housesDetailsPrice, housesDetailsDescription, housesDetailsLocation;


    //view objects
    TextView titleTv, rentSaleTv, priceTv, descriptionTv, locationTv;

    //recyclerView objects
    RecyclerView housesDetailsImageRecView;


    //fast adapter objects
    FastItemAdapter<HousesDetailsViewSingleImage> housesDetailsViewSingleImageFastAdapter = new FastItemAdapter<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houses_details);


        // Get the Intent that started this activity and extract the image references array
        Intent intent = getIntent();
        housesItemImageReferences = intent.getStringArrayListExtra(Houses.EXTRA_ARRAY_H);
        housesDetailsDescription = intent.getStringExtra(Houses.EXTRA_DESCRIPTION_H);
        housesDetailsLocation = intent.getStringExtra(Houses.EXTRA_LOCATION_H);
        housesDetailsPrice = intent.getStringExtra(Houses.EXTRA_PRICE_H);
        housesDetailsRentOrSale = intent.getStringExtra(Houses.EXTRA_RENT_SALE_H);
        housesDetailsTitle = intent.getStringExtra(Houses.EXTRA_TITLE_H);


        //get references to the view objects
        locationTv = findViewById(R.id.house_details_item_location);
        titleTv = findViewById(R.id.house_details_item_title);
        priceTv = findViewById(R.id.house_details_item_price);
        rentSaleTv = findViewById(R.id.house_details_item_sale_rent);
        descriptionTv = findViewById(R.id.house_details_item_description);


        //get reference to recycler view and build it out
        housesDetailsImageRecView = findViewById(R.id.houses_details_view_recycler_view);
        housesDetailsImageRecView.setHasFixedSize(true);
        housesDetailsImageRecView.setLayoutManager(new LinearLayoutManager(this, 0, false));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(housesDetailsImageRecView);

        //fill the data source array
        createHousesDetailsSingleImageObjects();

        housesDetailsViewSingleImageFastAdapter.add(mainImageDataSource);
        housesDetailsImageRecView.setAdapter(housesDetailsViewSingleImageFastAdapter);


        //assign the strings to the text views
        titleTv.setText(housesDetailsTitle);
        locationTv.setText(housesDetailsLocation);
        priceTv.setText(housesDetailsPrice);
        rentSaleTv.setText(housesDetailsRentOrSale);
        descriptionTv.setText(housesDetailsDescription);


    }


    void createHousesDetailsSingleImageObjects() {

        int count = housesItemImageReferences.size();
        HousesDetailsViewSingleImage imageHolder;

        for (int i = 0; i < count; i++) {


            if (housesItemImageReferences.get(i) != null) {
                imageHolder = new HousesDetailsViewSingleImage(housesItemImageReferences.get(i));
                mainImageDataSource.add(imageHolder);
            }


        }


    }


}
