package com.example.dell.muhingalayoutprototypes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SavedCategories extends AppCompatActivity {


    //post link for image attribution; https://icons8.com


    //user
    String globalCurrentUserJson;


    //initialize views
    //buttons
    ImageView housesButton, venuesButton, landButton;

    //Toolbar objects
    Toolbar mainToolBar;




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



        //initialize the toolbar
        mainToolBar = findViewById(R.id.saved_categories_action_bar);
        mainToolBar.setTitle(R.string.saved_categories);
        setSupportActionBar(mainToolBar);



        /*Glide.with(this).load(R.drawable.demo)
  .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
  .into(imageView)*/


        /*  RoundedCorners -> Glide.with(context)
          .load(R.drawable.demo)
          .apply(bitmapTransform(RoundedCornersTransformation(45, 0,
              RoundedCornersTransformation.CornerType.BOTTOM)))
          .into(holder.image)
*/


        Glide.with(this).load(R.drawable.houses_saved_categories_img).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(45, 0,
                RoundedCornersTransformation.CornerType.BOTTOM))).into(housesButton);

        Glide.with(this).load(R.drawable.venues_saved_categories_img).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(45, 0,
                RoundedCornersTransformation.CornerType.BOTTOM))).into(venuesButton);

        Glide.with(this).load(R.drawable.land_saved_categories_img).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(45, 0,
                RoundedCornersTransformation.CornerType.BOTTOM))).into(landButton);


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















/*<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:cardview="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SavedCategories">


    <android.support.v7.widget.Toolbar
        android:id="@+id/saved_categories_action_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/my_color_primary"
        android:minHeight="64dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:titleTextColor="@color/my_color_bg" />


    <TextView
        android:id="@+id/textView3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:text="@string/categories"
        android:textColor="@color/my_color_secondary"
        android:textSize="16sp"
        app:fontFamily="@font/montserrat_regular"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/saved_categories_action_bar" />


    <LinearLayout
        android:id="@+id/linearLayout1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"

        android:layout_marginStart="32dp"
        android:layout_marginTop="24dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView3">


        <android.support.v7.widget.CardView
            android:layout_width="150dp"
            android:layout_height="150dp"
            app:cardCornerRadius="10dp"
            app:cardElevation="0dp"
            cardview:cardUseCompatPadding="true"


            >

            <ImageView

                android:id="@+id/saved_houses_category_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_alignParentTop="true"
                android:scaleType="centerCrop"
                />

        </android.support.v7.widget.CardView>


        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/houses"
            android:textSize="16sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


    <LinearLayout
        android:id="@+id/linearLayout2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"

        android:layout_marginStart="16dp"
        android:layout_marginTop="24dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/linearLayout1"
        app:layout_constraintTop_toBottomOf="@+id/textView3">


        <android.support.v7.widget.CardView
            android:layout_width="150dp"
            android:layout_height="150dp"
            app:cardCornerRadius="10dp"
            app:cardElevation="0dp"
            cardview:cardUseCompatPadding="true"


            >

            <ImageView

                android:id="@+id/saved_venues_category_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_alignParentTop="true"
                android:scaleType="centerCrop"
                />

        </android.support.v7.widget.CardView>


        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/venues"
            android:textSize="16sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


    <LinearLayout
        android:id="@+id/linearLayout3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"

        android:layout_marginStart="32dp"
        android:layout_marginTop="40dp"
        android:gravity="center_horizontal"
        android:orientation="vertical"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/linearLayout1">


        <android.support.v7.widget.CardView
            android:layout_width="150dp"
            android:layout_height="150dp"
            app:cardCornerRadius="10dp"
            app:cardElevation="0dp"
            cardview:cardUseCompatPadding="true"


            >

            <ImageView

                android:id="@+id/saved_land_category_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_alignParentTop="true"
                android:scaleType="centerCrop"
                />

        </android.support.v7.widget.CardView>


        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/land"
            android:textSize="16sp"
            app:fontFamily="@font/montserrat_regular" />


    </LinearLayout>


</android.support.constraint.ConstraintLayout>*/








/*<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:cardview="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SavedCategories">


    <android.support.v7.widget.Toolbar
        android:id="@+id/saved_categories_action_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/my_color_primary"
        android:minHeight="64dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:titleTextColor="@color/my_color_bg" />


    <TextView
        android:id="@+id/textView3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:text="@string/categories"
        android:textColor="@color/my_color_secondary"
        android:textSize="16sp"
        app:fontFamily="@font/montserrat_regular"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/saved_categories_action_bar" />


    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:fillViewport="true"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView3">


        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">


            <ImageView
                android:id="@+id/saved_houses_category_image"
                android:layout_width="match_parent"
                android:layout_height="250dp"
                android:layout_marginStart="16dp"
                android:layout_marginTop="8dp"
                android:layout_marginEnd="16dp"

                android:src="@drawable/venues_saved_categories_img"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="parent" />


            <ImageView
                android:id="@+id/saved_venues_category_image"
                android:layout_width="match_parent"
                android:layout_height="110dp"
                android:layout_marginStart="16dp"
                android:layout_marginTop="16dp"
                android:layout_marginEnd="16dp"


                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/saved_houses_category_image" />


            <ImageView
                android:id="@+id/saved_land_category_image"
                android:layout_width="match_parent"
                android:layout_height="110dp"
                android:layout_marginStart="16dp"
                android:layout_marginTop="16dp"
                android:layout_marginEnd="16dp"


                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/saved_venues_category_image" />


        </LinearLayout>


    </ScrollView>


</android.support.constraint.ConstraintLayout>*/