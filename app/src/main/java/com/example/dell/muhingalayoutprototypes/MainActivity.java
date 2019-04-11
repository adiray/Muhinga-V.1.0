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


import com.backendless.Backendless;
import com.backendless.BackendlessUser;

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

        //initialize backendless
        initializeBackendless();






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
                //open sign up activity
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                MainActivity.this.startActivity(intent);


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


    /*************************************************************************************************************************************************/




    /*************************************************************************************************************************************************/





    void initializeBackendless(){
        //initialize backendless
        Backendless.initApp( getApplicationContext(), "125AF8BD-1879-764A-FF22-13FB1C162400", "589220C9-1E2A-1EA2-FFCF-A0CFF76F5A00" );
    }


    /*************************************************************************************************************************************************/


}

//todo edit the user sign up to use backendless
//todo when user signs up, log them in
//todo switch the activties so sign in shows up first and add option to stay signed in
//todo add a profile pic field in backendless, a likes field
//todo add a progress bar to the sign up and sign in actvity
// todo add a masked input to the password field
// todo add the eeror handling and error type information
//todo handle the no internets situaton