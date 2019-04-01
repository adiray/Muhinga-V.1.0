package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView housesButton, landButton, venuesButton, musicButton;

    //TOOLBAR VIEWS
    Toolbar homeMainToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the views
        initializeViews();


    }



    /*************************************************************************************************************************************************/




    //inflate the menu layout file for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_app_bar_menu_file, menu);
        return true;
    }


    //specify the actions that happen when each menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.home_app_bar_settings_button):

            case (R.id.home_sign_up_button):


                break;
            default:
                break;
        }
        return true;
    }


    /*************************************************************************************************************************************************/



    void initializeViews() {

        //get references to the views
        housesButton = findViewById(R.id.houses_services_user_area);
        landButton = findViewById(R.id.land_services_user_area);
        venuesButton = findViewById(R.id.venues_services_user_area);
        musicButton = findViewById(R.id.music_services_user_area);

        //toolbar
        homeMainToolBar = findViewById(R.id.main_activity_app_bar);
        homeMainToolBar.setTitle("Muhinga");
        Objects.requireNonNull(homeMainToolBar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.my_color_white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(homeMainToolBar);

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


/*<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/user_area_top_bar_layout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:background="@color/my_color_primary"
    android:gravity="center_horizontal"
    android:orientation="horizontal"
    android:paddingBottom="5dp">

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginEnd="35dp"
        android:layout_marginStart="20dp"
        android:gravity="center_horizontal"
        android:orientation="vertical">

        <ImageView
            android:layout_width="25dp"
            android:layout_height="25dp"
            android:src="@drawable/user_profile_icon_white_filled" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/profile"
            android:textColor="@color/cardview_light_background"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />

    </LinearLayout>


    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginEnd="35dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"

        >

        <ImageView
            android:layout_width="25dp"
            android:layout_height="25dp"
            android:src="@drawable/user_wallet_icon_white_filled" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/wallet"
            android:textColor="@color/cardview_light_background"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginEnd="35dp"
        android:gravity="center_horizontal"
        android:orientation="vertical">

        <ImageView
            android:layout_width="25dp"
            android:layout_height="25dp"
            android:src="@drawable/user_orders_icon_white_filled" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/history"
            android:textColor="@color/cardview_light_background"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />

    </LinearLayout>

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginEnd="20dp"
        android:gravity="center_horizontal"
        android:orientation="vertical">


        <ImageView
            android:layout_width="25dp"
            android:layout_height="25dp"
            android:src="@drawable/user_likes_icon_white_filled"

            />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/saved"
            android:textColor="@color/cardview_light_background"
            android:textSize="12sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


</LinearLayout>


*/