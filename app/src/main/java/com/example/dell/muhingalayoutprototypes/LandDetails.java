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

public class LandDetails extends AppCompatActivity {

    ArrayList<String> itemImageReferences = new ArrayList<>();
    String landDetailsTitle, landDetailsSize, landDetailsPrice, landDetailsDescription, landDetailsLocation;

    //miscellaneous objects
    ArrayList<LandDetailsViewSingleImage> mainImageDataSource = new ArrayList<>();

    //view objects
    TextView titleTv, sizeTv, priceTv, descriptionTv, locationTv;

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

        //get references to the view objects
        locationTv = findViewById(R.id.land_details_item_location);
        titleTv = findViewById(R.id.land_details_item_title);
        priceTv = findViewById(R.id.land_details_item_price);
        sizeTv = findViewById(R.id.land_details_item_size);
        descriptionTv = findViewById(R.id.land_details_item_description);

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
