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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class LandDetails extends AppCompatActivity {

    ArrayList<String> itemImageReferences = new ArrayList<>();
    String landDetailsTitle, landDetailsSize, landDetailsPrice, landDetailsDescription, landDetailsLocation;
    String ownerPhone, viewingDates;

    //miscellaneous objects
    ArrayList<LandDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();
    String shareMessage;

    //view objects
    TextView titleTv, sizeTv, priceTv, descriptionTv, locationTv;
    Toolbar landDetailsToolbar;
    ImageView callButton, viewLandButton, shareLandButton;

    //recyclerView objects
    RecyclerView landDetailsImageRecView;

    //fast adapter objects
    FastItemAdapter<LandDetailsViewSingleImage> landDetailsViewSingleImageFastAdapter = new FastItemAdapter<>(); //create our FastAdapter which will manage everything


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_details);
        // Get the Intent that started this activity and extract the image references array
        Intent intent = getIntent();
        itemImageReferences = intent.getStringArrayListExtra(Land.EXTRA_ARRAY);
        landDetailsDescription = intent.getStringExtra(Land.EXTRA_DESCRIPTION);
        landDetailsLocation = intent.getStringExtra(Land.EXTRA_LOCATION);
        landDetailsPrice = intent.getStringExtra(Land.EXTRA_PRICE);
        landDetailsSize = intent.getStringExtra(Land.EXTRA_SIZE);
        landDetailsTitle = intent.getStringExtra(Land.EXTRA_TITLE);
        ownerPhone = intent.getStringExtra(Land.EXTRA_OWNER_PHONE);
        viewingDates = intent.getStringExtra(Land.EXTRA_VIEWING_DATES);


        //get references to the view objects
        locationTv = findViewById(R.id.land_details_item_location);
        titleTv = findViewById(R.id.land_details_item_title);
        priceTv = findViewById(R.id.land_details_item_price);
        sizeTv = findViewById(R.id.land_details_item_size);
        descriptionTv = findViewById(R.id.land_details_item_description);

        //buttons
        callButton = findViewById(R.id.land_details_call_owner);
        shareLandButton = findViewById(R.id.share_land_land_details_layout);
        viewLandButton = findViewById(R.id.view_land_land_details_layout);

        initializeViews();


        //toolbar
        landDetailsToolbar = findViewById(R.id.land_details_activity_action_bar);
        landDetailsToolbar.setTitle(R.string.land_details);
        setSupportActionBar(landDetailsToolbar);


        //get reference to recycler view and build it out
        landDetailsImageRecView = findViewById(R.id.land_details_view_recycler_view);
        landDetailsImageRecView.setHasFixedSize(true);
        landDetailsImageRecView.setLayoutManager(new LinearLayoutManager(this, 0, false));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(landDetailsImageRecView);


        //fill the data source array
        createLandDetailsSingleImageObjects();

        landDetailsViewSingleImageFastAdapter.add(mainImageDataSource);
        landDetailsImageRecView.setAdapter(landDetailsViewSingleImageFastAdapter);


        //assign the strings to the text views
        titleTv.setText(landDetailsTitle);
        locationTv.setText(landDetailsLocation);
        priceTv.setText(landDetailsPrice);
        sizeTv.setText(landDetailsSize);
        descriptionTv.setText(landDetailsDescription);


    }


    void initializeViews() {

        viewLandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewLandButtonClicked();

            }
        });


        shareLandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareLandButtonClicked();
            }
        });


        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callButtonClicked();
            }
        });


    }


    void viewLandButtonClicked() {

        String titleString, subTitleString;
        titleString = getString(R.string.available_viewing_dates);
        subTitleString = viewingDates + ". " + getString(R.string.contact_owner_for_more_details);


        new iOSDialogBuilder(LandDetails.this)
                .setTitle(titleString)
                .setSubtitle(subTitleString)
                .setBoldPositiveLabel(true)
                .setCancelable(true)
                .setPositiveListener(getString(R.string.okay), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        dialog.dismiss();
                    }
                })
                .build().show();


    }


    void shareLandButtonClicked() {


        shareMessage = "Check out this great property on the MUHINGA app." + " " + landDetailsTitle + ". " + " Price: " +
                landDetailsPrice + ". Location:" + landDetailsLocation + ". Find other great properties on the MUHINGA APP!";


        new iOSDialogBuilder(LandDetails.this)
                .setTitle("This is the message you will share.")
                .setSubtitle(shareMessage + " Proceed?")
                .setBoldPositiveLabel(true)
                .setCancelable(true)
                .setPositiveListener(getString(R.string.okay), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        shareLandDetails();
                    }
                }).setNegativeListener(getString(R.string.cancel), new iOSDialogClickListener() {
            @Override
            public void onClick(iOSDialog dialog) {
                dialog.dismiss();
            }
        }).build().show();


    }


    void shareLandDetails() {


        String shareBody = shareMessage;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MUHINGA ");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

    }


    void callButtonClicked() {


        String phoneNumber = "tel:" + ownerPhone;
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
        startActivity(i);

    }


    void createLandDetailsSingleImageObjects() {

        int count = itemImageReferences.size();
        LandDetailsViewSingleImage imageHolder;

        for (int i = 0; i < count; i++) {


            if (itemImageReferences.get(i) != null) {
                imageHolder = new LandDetailsViewSingleImage(itemImageReferences.get(i));
                mainImageDataSource.add(imageHolder);
            }


        }


    }

}
