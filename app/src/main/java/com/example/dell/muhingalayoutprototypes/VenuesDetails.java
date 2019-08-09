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
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenuesDetails extends AppCompatActivity {

    ArrayList<String> itemImageReferences = new ArrayList<>();
    String venuesDetailsId, venuesDetailsTitle, venuesDetailsSize, venuesDetailsPrice, venuesDetailsDescription, venuesDetailsLocation, venuesOwnerPhone;

    //current user
    String globalCurrentUserJson, currentUserId;
    BackendlessUser backendlessUser;

    //miscellaneous objects
    ArrayList<VenuesDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();
    ArrayList<String> bookedDays = new ArrayList<>();
    String shareMessage;

    //view objects
    TextView titleTv, sizeTv, priceTv, descriptionTv, locationTv;
    ImageView callOwner, shareVenue, bookVenue, saveVenueButton;
    Toolbar venueDetailsToolbar;

    //recyclerView objects
    RecyclerView venuesDetailsImageRecView;

    //fast adapter objects
    FastItemAdapter<VenuesDetailsViewSingleImage> venuesDetailsViewSingleImageFastAdapter = new FastItemAdapter<>(); //create our FastAdapter which will manage everything

    //intent ids
    public static final String EXTRA_VENUE_ID = "com.example.muhinga.venuesID";
    public static final String EXTRA_VENUE_BOOKINGS = "com.example.muhinga.venuesBookings";


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

        //get user
        globalCurrentUserJson = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);
        Gson gson = new Gson();
        backendlessUser = gson.fromJson(globalCurrentUserJson, BackendlessUser.class);
        currentUserId = backendlessUser.getObjectId();


        //get references to the view objects
        locationTv = findViewById(R.id.venues_details_item_location);
        titleTv = findViewById(R.id.venues_details_item_title);
        priceTv = findViewById(R.id.venues_details_item_price);
        sizeTv = findViewById(R.id.venues_details_item_size);
        descriptionTv = findViewById(R.id.venues_details_item_description);
        callOwner = findViewById(R.id.venues_details_call_owner);
        shareVenue = findViewById(R.id.share_venue_venues_details_layout);
        bookVenue = findViewById(R.id.book_venue_venues_details_layout);
        saveVenueButton = findViewById(R.id.save_venue_venue_details_layout);


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


        shareVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareVenueClicked();
            }
        });


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

        saveVenueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveVenue();

            }
        });


        retrieveBookings();


    }

    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */



    void shareVenueClicked(){


        shareMessage = "Check out this great property on the MUHINGA app." + " " + venuesDetailsTitle + ". " + " Price: " +
                venuesDetailsPrice + ". Location:" + venuesDetailsLocation + ". Find other great properties on the MUHINGA APP!";


        new iOSDialogBuilder(VenuesDetails.this)
                .setTitle("This is the message you will share.")
                .setSubtitle(shareMessage + " Proceed?")
                .setBoldPositiveLabel(true)
                .setCancelable(true)
                .setPositiveListener(getString(R.string.okay), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        shareVenueDetails();
                    }
                }).setNegativeListener(getString(R.string.cancel), new iOSDialogClickListener() {
            @Override
            public void onClick(iOSDialog dialog) {
                dialog.dismiss();
            }
        }).build().show();






    }



    void shareVenueDetails() {


        String shareBody = shareMessage;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MUHINGA ");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

    }









    /**
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    void saveVenue() {


        //disable the button and show that its disabled so user doesn't press twice
        saveVenueButton.setEnabled(false);
        saveVenueButton.setImageResource(R.drawable.contacts_saving_progress);

        BackendlessUser currentUserCopy = backendlessUser; //you can use the original user variable, just used the copy as a precaution incase i updated the value
        Log.d("myLogSaveVenue", "current user id: " + currentUserId);


        //house object to be saved in the relations column
        Map<String, Object> venueObject = new HashMap<String, Object>();
        venueObject.put("objectId", venuesDetailsId);
        Log.d("myLogSaveVenue", "venue id: " + venuesDetailsId);


        ArrayList<Map> venuesToSave = new ArrayList<>();
        venuesToSave.add(venueObject);

        Backendless.Data.of(BackendlessUser.class).addRelation(currentUserCopy, "saved_venues", venuesToSave, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {


                if (response == 0) {

                    Toast.makeText(VenuesDetails.this, "The property has already been saved before", Toast.LENGTH_LONG).show();

                } else if (response >= 1) {


                    Toast.makeText(VenuesDetails.this, "The property has been saved successfully", Toast.LENGTH_LONG).show();


                }


                Log.d("myLogSaveVenue", "User object updated with saved venue: " + response.toString());
                saveVenueButton.setEnabled(true);
                saveVenueButton.setImageResource(R.drawable.contacts_colored_red_filled);


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsSaveHouse", "saved house relation upload failed: " + fault.toString());
                saveVenueButton.setEnabled(true);
                saveVenueButton.setImageResource(R.drawable.contacts_colored_red_filled);


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