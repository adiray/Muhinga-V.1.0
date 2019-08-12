package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SavedCategories extends AppCompatActivity {


    //post link for image attribution; https://icons8.com


    //user
    String globalCurrentUserJson;


    //initialize views
    //buttons
    ImageView housesButton, venuesButton, landButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_categories);


        receiveIntents();
        initializeViews();








    }



    void receiveIntents(){

        //receive intents
        Intent intent = getIntent();
        globalCurrentUserJson = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);

    }




    void initializeViews(){


        housesButton = findViewById(R.id.saved_houses_category_image);
        venuesButton = findViewById(R.id.saved_venues_category_image);
        landButton = findViewById(R.id.saved_land_category_image);



        housesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SavedCategories.this, SavedItems.class);
                intent.putExtra("clicked_category","houses");
                intent.putExtra("globalCurrentUser",globalCurrentUserJson);
                startActivity(intent);




            }
        });


        venuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SavedCategories.this, SavedItems.class);
                intent.putExtra("clicked_category","venues");
                intent.putExtra("globalCurrentUser",globalCurrentUserJson);
                startActivity(intent);



            }
        });


        landButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SavedCategories.this, SavedItems.class);
                intent.putExtra("clicked_category","land");
                intent.putExtra("globalCurrentUser",globalCurrentUserJson);
                startActivity(intent);



            }
        });





    }




}
