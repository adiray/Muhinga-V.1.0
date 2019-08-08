package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class HousesDetails extends AppCompatActivity {

    ArrayList<String> housesItemImageReferences = new ArrayList<>();
    ArrayList<HousesDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();


    String housesDetailsTitle, housesDetailsRentOrSale, housesDetailsPrice, housesDetailsDescription, housesDetailsLocation;
    String houseDetailsViewingDates, houseDetailsOwnerPhone;

    String shareMessage;


    //view objects
    TextView titleTv, rentSaleTv, priceTv, descriptionTv, locationTv;
    Toolbar houseDetailsToolbar;
    ImageView shareHouseButton, viewHouseButton, callOwnerButton;


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
        houseDetailsViewingDates = intent.getStringExtra(Houses.EXTRA_VIEWING_DATES);
        houseDetailsOwnerPhone = intent.getStringExtra(Houses.EXTRA_OWNER_PHONE);

        //get references to the view objects
        locationTv = findViewById(R.id.house_details_item_location);
        titleTv = findViewById(R.id.house_details_item_title);
        priceTv = findViewById(R.id.house_details_item_price);
        rentSaleTv = findViewById(R.id.house_details_item_sale_rent);
        descriptionTv = findViewById(R.id.house_details_item_description);


        //buttons
        shareHouseButton = findViewById(R.id.share_house_house_details_layout);
        viewHouseButton = findViewById(R.id.view_house_houses_details_layout);
        callOwnerButton = findViewById(R.id.houses_details_call_owner);


        //toolbar
        houseDetailsToolbar = findViewById(R.id.house_details_activity_action_bar);
        houseDetailsToolbar.setTitle(R.string.House_details);
        setSupportActionBar(houseDetailsToolbar);


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


        initializeViews();


    }


    void initializeViews() {


        shareHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareHouseButtonClicked();

            }
        });

        callOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callOwnerButtonClicked();

            }
        });


        viewHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHouseButtonClicked();
            }
        });


    }


    void viewHouseButtonClicked() {
        String titleString, subTitleString;
        titleString = getString(R.string.available_viewing_dates);
        subTitleString = houseDetailsViewingDates + " " + getString(R.string.contact_owner_for_more_details);


        new iOSDialogBuilder(HousesDetails.this)
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



        /*.setNegativeListener(getString(R.string.dismiss), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })*/


    }


    void callOwnerButtonClicked() {

        //called when the call button is pressed

        String phoneNumber = "tel:" + houseDetailsOwnerPhone;
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
        startActivity(i);

    }


    void shareHouseButtonClicked() {


        shareMessage = "Check out this great property on the MUHINGA app." + " " + housesDetailsTitle + ". " + housesDetailsRentOrSale + ". Price:" +
                housesDetailsPrice + ". Location:" + housesDetailsLocation + ". Find other great properties on the MUHINGA APP!";


        createSharingConfirmationDialog();


    }


    void createSharingConfirmationDialog() {

/*
        new TTFancyGifDialog.Builder(HousesDetails.this)
                .setTitle("This is the message you will share.")
                .setMessage(shareMessage + "/n Proceed?")
                .setPositiveBtnText("Share")
                .setPositiveBtnBackground("#22b573")
                .setNegativeBtnText("CANCEL")
                .setNegativeBtnBackground("#FF3D00")
                .setGifResource(R.drawable.sign_in_opt_one)      //pass your gif, png or jpg
                .isCancellable(true)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {



                    }
                })
                .OnNegativeClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {


                    }
                })
                .build();*/


        new iOSDialogBuilder(HousesDetails.this)
                .setTitle("This is the message you will share.")
                .setSubtitle(shareMessage + " Proceed?")
                .setBoldPositiveLabel(true)
                .setCancelable(true)
                .setPositiveListener(getString(R.string.okay), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        shareHouseDetails();
                    }
                }).setNegativeListener(getString(R.string.cancel), new iOSDialogClickListener() {
            @Override
            public void onClick(iOSDialog dialog) {
                dialog.dismiss();
            }
        }).build().show();





    }


    void shareHouseDetails() {


        String shareBody = shareMessage;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MUHINGA ");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

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


    //get reference to sharing button and add feature


}
