package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class VenuesDetails extends AppCompatActivity {

    ArrayList<String> itemImageReferences = new ArrayList<>();
    String venuesDetailsTitle, venuesDetailsSize, venuesDetailsPrice, venuesDetailsDescription, venuesDetailsLocation;

    //miscellaneous objects
    ArrayList<VenuesDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();

    //view objects
    TextView titleTv, sizeTv, priceTv, descriptionTv, locationTv;

    //recyclerView objects
    RecyclerView venuesDetailsImageRecView;

    //fast adapter objects
    FastItemAdapter<VenuesDetailsViewSingleImage> venuesDetailsViewSingleImageFastAdapter = new FastItemAdapter<>(); //create our FastAdapter which will manage everything

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_details);

        // Get the Intent that started this activity and extract the image references array
        Intent intent = getIntent();
        itemImageReferences = intent.getStringArrayListExtra(Venues.EXTRA_ARRAY);
        venuesDetailsDescription = intent.getStringExtra(Venues.EXTRA_DESCRIPTION);
        venuesDetailsLocation = intent.getStringExtra(Venues.EXTRA_LOCATION);
        venuesDetailsPrice = intent.getStringExtra(Venues.EXTRA_PRICE);
        venuesDetailsSize = intent.getStringExtra(Venues.EXTRA_SIZE);
        venuesDetailsTitle = intent.getStringExtra(Venues.EXTRA_TITLE);


        //get references to the view objects
        locationTv = findViewById(R.id.venues_details_item_location);
        titleTv = findViewById(R.id.venues_details_item_title);
        priceTv = findViewById(R.id.venues_details_item_price);
        sizeTv = findViewById(R.id.venues_details_item_size);
        descriptionTv = findViewById(R.id.venues_details_item_description);


        //get reference to recycler view and build it out
        venuesDetailsImageRecView = findViewById(R.id.venues_details_view_recycler_view);
        venuesDetailsImageRecView.setHasFixedSize(true);
        venuesDetailsImageRecView.setLayoutManager(new LinearLayoutManager(this, 0, false));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(venuesDetailsImageRecView);

        //fill the data source array
        createVenuesDetailsSingleImageObjects();

        venuesDetailsViewSingleImageFastAdapter.add(mainImageDataSource);
        venuesDetailsImageRecView.setAdapter(venuesDetailsViewSingleImageFastAdapter);


        //assign the strings to the text views
        titleTv.setText(venuesDetailsTitle);
        locationTv.setText(venuesDetailsLocation);
        priceTv.setText(venuesDetailsPrice);
        sizeTv.setText(venuesDetailsSize);
        descriptionTv.setText(venuesDetailsDescription);

    }


    void createVenuesDetailsSingleImageObjects() {

        int count = itemImageReferences.size();
        VenuesDetailsViewSingleImage imageHolder;

        for (int i = 0; i < count; i++) {


            if (itemImageReferences.get(i) != null) {
                imageHolder = new VenuesDetailsViewSingleImage(itemImageReferences.get(i));
                mainImageDataSource.add(imageHolder);
            }


        }


    }
}
