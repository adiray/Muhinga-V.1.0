package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class VenuesDetails extends AppCompatActivity {

    ArrayList<String> itemImageReferences = new ArrayList<>();
    String venuesDetailsTitle, venuesDetailsSize, venuesDetailsPrice, venuesDetailsDescription, venuesDetailsLocation, venuesOwnerPhone;

    //miscellaneous objects
    ArrayList<VenuesDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();

    //view objects
    TextView titleTv, sizeTv, priceTv, descriptionTv, locationTv;
    ImageView callOwner, shareVenue, bookVenue, saveVenue;

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
        venuesOwnerPhone = intent.getStringExtra(Venues.EXTRA_PHONE);


        //get references to the view objects
        locationTv = findViewById(R.id.venues_details_item_location);
        titleTv = findViewById(R.id.venues_details_item_title);
        priceTv = findViewById(R.id.venues_details_item_price);
        sizeTv = findViewById(R.id.venues_details_item_size);
        descriptionTv = findViewById(R.id.venues_details_item_description);
        callOwner = findViewById(R.id.venues_details_call_owner);
        shareVenue = findViewById(R.id.share_venue_venues_details_layout);
        bookVenue = findViewById(R.id.book_venue_venues_details_layout);
        saveVenue = findViewById(R.id.save_venue_venues_details_layout);


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


        callOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOwnerPressed();

            }
        });


    }


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


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


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    public void callOwnerPressed() {
        //called when the call button is pressed

        String phoneNumber = "tel:" + venuesOwnerPhone;
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
        startActivity(i);

    }


}


//TODO ADD THE NUMBER MEMBER TO THE VENUES POJO THEN PASS THE VLUE TO THIS ACTIVTY AND COMPLETE THE CALL OWNER ONCLICK