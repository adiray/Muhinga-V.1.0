package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import net.cachapa.expandablelayout.ExpandableLayout;

public class MainActivity extends AppCompatActivity {

    ImageView housesButton, landButton, venuesButton, musicButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the views
        initializeViews();


    }


    void initializeViews() {

        //get references to the views
        housesButton = findViewById(R.id.houses_services_user_area);
        landButton = findViewById(R.id.land_services_user_area);
        venuesButton = findViewById(R.id.venues_services_user_area);
        musicButton = findViewById(R.id.music_services_user_area);

        //set the on click listeners
        housesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                housesButtonClicked();
            }
        });
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicButtonClicked();
            }
        });
        venuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venuesButtonClicked();
            }
        });
        landButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                landButtonClicked();
            }
        });

    }


    void housesButtonClicked() {

        //open houses activity
        Intent intent = new Intent(MainActivity.this, Houses.class);
        MainActivity.this.startActivity(intent);

    }

    void musicButtonClicked() {

        //open music activity
        Intent intent = new Intent(MainActivity.this, Music.class);
        MainActivity.this.startActivity(intent);

    }

    void landButtonClicked() {

        //open land activity
        Intent intent = new Intent(MainActivity.this, Land.class);
        MainActivity.this.startActivity(intent);


    }

    void venuesButtonClicked() {

        //open the venues activity
        Intent intent = new Intent(MainActivity.this, Venues.class);
        MainActivity.this.startActivity(intent);


    }


}
