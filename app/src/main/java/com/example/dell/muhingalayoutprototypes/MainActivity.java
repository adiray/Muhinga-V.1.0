package com.example.dell.muhingalayoutprototypes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;

import br.vince.easysave.EasySave;
import br.vince.easysave.LoadAsyncCallback;

public class MainActivity extends AppCompatActivity {

    ImageView housesButton, landButton, venuesButton, musicButton, profilePictureButton;

    //TOOLBAR VIEWS
    Toolbar homeMainToolBar;

    //Utilities
    BroadcastReceiver broadcastReceiverLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the views
        initializeViews();

        //initialize backendless
        initializeBackendless();

        //automatically log the user in
        automaticallyLogUserIn();




        //todo you stopped here



    }


    /*************************************************************************************************************************************************/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (broadcastReceiverLogin != null) {
            unregisterReceiver(broadcastReceiverLogin);
        }

    }

    /*************************************************************************************************************************************************/

    void createBroadcastReceiver() {


        broadcastReceiverLogin = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action != null && action.equals("finish_activity")) {
                    finish();
                }

            }
        };

        registerReceiver(broadcastReceiverLogin, new IntentFilter("finish_activity"));


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
               createSignupOrLoginDialog();

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
        profilePictureButton = findViewById(R.id.home_profile_picture);

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
        profilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    void automaticallyLogUserIn() {

        String userToken = UserTokenStorageFactory.instance().getStorage().get();

        if (userToken != null && !userToken.equals("")) {
            // user login is available, skip the login activity/login form
            Log.d("myLogsUserLogMATkn", userToken);
            retrieveCachedUser();
        }


    }

    /*************************************************************************************************************************************************/


    void initializeBackendless() {
        Backendless.initApp(getApplicationContext(), "125AF8BD-1879-764A-FF22-13FB1C162400", "589220C9-1E2A-1EA2-FFCF-A0CFF76F5A00");
    }


    /*************************************************************************************************************************************************/


    void retrieveCachedUser() {



        new EasySave(MainActivity.this).retrieveModelAsync("current_saved_user", BackendlessUser.class, new LoadAsyncCallback<BackendlessUser>() {
            @Override
            public void onComplete(BackendlessUser backendlessUser) {

                if (backendlessUser != null) {

                    Log.d("myLogsUserCacheRetSx", backendlessUser.toString());

                    String profilePicture = (String) backendlessUser.getProperty("profile_picture");
                    if (profilePicture != null) {


                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.user_profile_icon_white_filled)
                                .error(R.drawable.user_profile_icon_white_filled)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .priority(Priority.HIGH)
                                .dontAnimate()
                                .dontTransform();

                        Glide.with(MainActivity.this).load(profilePicture).apply(options).into(profilePictureButton);
                        Log.d("myLogsUserCacheRetSx", profilePicture);

                    }



                }
            }

            @Override
            public void onError(String s) {

                Log.d("myLogsUserCacheRetFail", s);


            }
        });


    }

    /*************************************************************************************************************************************************/

    void createSignupOrLoginDialog(){

        new TTFancyGifDialog.Builder(MainActivity.this)
                .setTitle("Confirm")
                .setMessage("Would you like to log in or sign up?")
                .setPositiveBtnText("LOG IN")
                .setPositiveBtnBackground("#22b573")
                .setNegativeBtnText("SIGN UP")
                .setNegativeBtnBackground("#FF3D00")
                .setGifResource(R.drawable.sign_in_opt_one)      //pass your gif, png or jpg
                .isCancellable(true)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        //open log in activity
                        createBroadcastReceiver();
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        MainActivity.this.startActivity(intent);


                    }
                })
                .OnNegativeClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        //open sign up activity
                        Intent intent = new Intent(MainActivity.this, SignUp.class);
                        MainActivity.this.startActivity(intent);


                    }
                })
                .build();





    }



}

//todo display login/signup success dialog
//todo edit the user sign up to use backendless DONE
//todo switch the activties so sign in shows up first and add option to stay signed in
//todo when user signs up, log them in
//todo add a profile pic field in backendless, a likes field
//todo add a progress bar to the sign up and sign in actvity
// todo add a masked input to the password field
// todo add the eeror handling and error type information
//todo handle the no internets situaton
//todo get drawables for the different dialog images
//todo add a terms and conditions dialog




/*BackendlessUser user = Backendless.UserService.CurrentUser();
            if (user != null) {


                String profilePicture = (String) user.getProperty("profile_picture");
                String userString = user.toString();
                Log.d("myLogsUserLogMA", userString);


                if (profilePicture != null) {

                    Glide.with(this).load(profilePicture).into(profilePictureButton);
                    Log.d("myLogsBackSxProps", profilePicture);

                }


            }*/