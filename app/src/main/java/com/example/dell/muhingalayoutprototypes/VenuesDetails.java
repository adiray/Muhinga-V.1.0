package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VenuesDetails extends AppCompatActivity {

    ArrayList<String> itemImageReferences = new ArrayList<>();
    String venuesDetailsId, venuesDetailsTitle, venuesDetailsSize, venuesDetailsPrice, venuesDetailsDescription, venuesDetailsLocation, venuesOwnerPhone;
    String globalCurrentUserJson;

    //miscellaneous objects
    ArrayList<VenuesDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();
    ArrayList<String> bookedDays = new ArrayList<>();

    //view objects
    TextView titleTv, sizeTv, priceTv, descriptionTv, locationTv;
    ImageView callOwner, shareVenue, bookVenue, saveVenue;
    Toolbar venueDetailsToolbar;

    //recyclerView objects
    RecyclerView venuesDetailsImageRecView;

    //fast adapter objects
    FastItemAdapter<VenuesDetailsViewSingleImage> venuesDetailsViewSingleImageFastAdapter = new FastItemAdapter<>(); //create our FastAdapter which will manage everything

    //intent ids
    public static final String EXTRA_VENUE_ID = "com.example.muhinga.venuesID";
    public static final String  EXTRA_VENUE_BOOKINGS = "com.example.muhinga.venuesBookings";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_details);

        // Get the Intent that started this activity and extract the image references array
        final Intent intent = getIntent();
        itemImageReferences = intent.getStringArrayListExtra(Venues.EXTRA_ARRAY);
        venuesDetailsDescription = intent.getStringExtra(Venues.EXTRA_DESCRIPTION);
        venuesDetailsLocation = intent.getStringExtra(Venues.EXTRA_LOCATION);
        venuesDetailsPrice = intent.getStringExtra(Venues.EXTRA_PRICE);
        venuesDetailsSize = intent.getStringExtra(Venues.EXTRA_SIZE);
        venuesDetailsTitle = intent.getStringExtra(Venues.EXTRA_TITLE);
        venuesOwnerPhone = intent.getStringExtra(Venues.EXTRA_PHONE);
        venuesDetailsId = intent.getStringExtra(Venues.EXTRA_ID);
        globalCurrentUserJson = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);


        //get references to the view objects
        locationTv = findViewById(R.id.venues_details_item_location);
        titleTv = findViewById(R.id.venues_details_item_title);
        priceTv = findViewById(R.id.venues_details_item_price);
        sizeTv = findViewById(R.id.venues_details_item_size);
        descriptionTv = findViewById(R.id.venues_details_item_description);
        callOwner = findViewById(R.id.venues_details_call_owner);
        shareVenue = findViewById(R.id.share_venue_venues_details_layout);
        bookVenue = findViewById(R.id.book_venue_venues_details_layout);



        //toolbar
        venueDetailsToolbar = findViewById(R.id.venue_details_activity_action_bar);
        venueDetailsToolbar.setTitle(R.string.add_filters);
        setSupportActionBar(venueDetailsToolbar);

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
        bookVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VenuesDetails.this, BookVenue.class);
                intent.putExtra(EXTRA_VENUE_ID, venuesDetailsId);
                intent.putExtra(EXTRA_VENUE_BOOKINGS, bookedDays);
                intent.putExtra(MainActivity.EXTRA_GLOBAL_USER, globalCurrentUserJson);
                startActivity(intent);


            }
        });


        retrieveBookings();


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


    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void retrieveBookings() {


        // loadingView.start();

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        String whereClause = "venues[bookings]" +
                ".objectId='" + venuesDetailsId + "'";
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("bookings").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {

                if (!response.isEmpty()) {

                    int counter = response.size();
                    Integer startDate, endDate, year, month;
                    StringBuilder stringBuilder = new StringBuilder();
                    String fullDate;

                    for (int i = 0; i < counter; i++) {

                        startDate = (Integer) response.get(i).get("start_date");
                        endDate = (Integer) response.get(i).get("end_date");
                        year = (Integer) response.get(i).get("year");
                        month = (Integer) response.get(i).get("month");


                        if (startDate != null && endDate != null && year != null && month != null) {
                            for (int c = startDate; c <= endDate; c++) {


                                stringBuilder.append(year);
                                stringBuilder.append("-");
                                stringBuilder.append(month);
                                stringBuilder.append("-");
                                stringBuilder.append(c);
                                fullDate = stringBuilder.toString();
                                Log.d("myLogsBookVenue", "full retrieved Date : " + fullDate);

                                bookedDays.add(fullDate);


                                stringBuilder.delete(0, stringBuilder.length());
                            }


                        }


                    }


                    Log.d("myLogsVenueDetails", "Booked days array list : " + bookedDays.toString());


                }


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                //loadingView.stop();

                Log.d("myLogsVenueDetails", "failed to retrieve bookings. ERROR : " + fault.toString());


            }
        });

    }


}


//TODO ADD THE NUMBER MEMBER TO THE VENUES POJO THEN PASS THE VLUE TO THIS ACTIVTY AND COMPLETE THE CALL OWNER ONCLICK