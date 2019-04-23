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
import android.widget.ImageView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.vince.easysave.EasySave;
import br.vince.easysave.LoadAsyncCallback;

public class MainActivity extends AppCompatActivity {

    //Declare views
    ImageView housesButton, landButton, venuesButton, musicButton, profilePictureButton;

    //TOOLBAR VIEWS
    Toolbar homeMainToolBar;

    //Utilities
    BroadcastReceiver broadcastReceiverFinishActivity;

    //others
    Boolean isLoggedIn = false; //value of the 'logged_in' user property. it's pulled from the server
    Boolean pausedForProfileActivity = false; //used to determine if the user paused this activty to go to the profile activity
    BackendlessUser globalCurrentUser = new BackendlessUser(); //the currently logged in user

    //extra keys
    public static final String EXTRA_USER_ID_STRING = "com.example.muhinga.userIdString";
    public static final String EXTRA_GLOBAL_USER = "com.example.muhinga.globalUser";


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

        if (broadcastReceiverFinishActivity != null) {
            unregisterReceiver(broadcastReceiverFinishActivity);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (pausedForProfileActivity) {

            new EasySave(MainActivity.this).retrieveModelAsync("current_saved_user", BackendlessUser.class, new LoadAsyncCallback<BackendlessUser>() {
                @Override
                public void onComplete(BackendlessUser user) {

                    if (user != null) {

                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.user_profile_icon_white_filled)
                                .error(R.drawable.user_profile_icon_white_filled)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .priority(Priority.HIGH)
                                .dontAnimate()
                                .dontTransform();

                        Glide.with(MainActivity.this).load(user.getProperty("profile_picture")).apply(options).into(profilePictureButton);
                    }

                    pausedForProfileActivity =false;

                }

                @Override
                public void onError(String s) {

                    if (s != null) {
                        Log.d("myLogsOnResume", "failed to reload new profile picture from cache. Error: " + s);
                    }


                }
            });


        }


    }


    /*************************************************************************************************************************************************/


    /*************************************************************************************************************************************************/

    void createBroadcastReceiver() {


        broadcastReceiverFinishActivity = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action != null) {

                    if (action.equals("finish_activity")) {
                        finish();
                    } else if (action.equals("recreate_activity")) {
                        recreate();
                    }


                }

            }
        };

        registerReceiver(broadcastReceiverFinishActivity, new IntentFilter("finish_activity"));


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

                break;

            case (R.id.home_sign_up_button):
                createSignupOrLoginDialog();

                break;

            case (R.id.home_app_bar_log_out_button):

                if (isLoggedIn) {
                    createLogoutConfirmationDialog();
                }

                break;
            default:
                break;
        }
        return true;
    }


    /*************************************************************************************************************************************************/

    //todo reconfigure glide behaviour when an image is too large glide fails to load

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

                profilePictureButtonClicked();
            }
        });
    }


    void profilePictureButtonClicked() {

        if (isLoggedIn) {

            pausedForProfileActivity = true;
            Intent intent = new Intent(MainActivity.this, Profile.class);
            Gson gson = new Gson();
            intent.putExtra(EXTRA_GLOBAL_USER, gson.toJson(globalCurrentUser));
            MainActivity.this.startActivity(intent);
        } else {
            createSignupOrLoginDialog();
        }

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

        //todo find a way of retreiving the user object


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

                    //this is an instance of the retrieved user. i use it when i need i user object to retrieve user information or update user information
                    globalCurrentUser = backendlessUser;


                    //retrieving user properties using the user Id and then determining if the user has logged in
                    BackendlessUser currentUser = new BackendlessUser();
                    Map<String, Object> userDetailsMap = new HashMap<>();
                    Backendless.Data.of(BackendlessUser.class).findById(backendlessUser.getObjectId(), new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {


                            isLoggedIn = (Boolean) response.getProperty("logged_in");
                            if (isLoggedIn != null) {
                                Log.d("myLogsUsrCcheRetSxLgdIn", isLoggedIn.toString());

                                if (isLoggedIn) {
                                    String profilePicture = (String) response.getProperty("profile_picture");
                                    if (profilePicture != null) {

                                        RequestOptions options = new RequestOptions()
                                                .centerCrop()
                                                .placeholder(R.drawable.user_profile_icon_white_filled)
                                                .error(R.drawable.user_profile_icon_white_filled)
                                                .skipMemoryCache(true)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .priority(Priority.HIGH)
                                                .dontAnimate()
                                                .dontTransform();

                                        Glide.with(MainActivity.this).load(profilePicture).apply(options).into(profilePictureButton);
                                        Log.d("myLogsUserCacheRetSx", profilePicture);
                                    }


                                }

                            }


                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Log.d("myLogsUserCacheRetSx", "user retrieval failed : error: " + fault.toString());


                        }
                    });


                }


            }

            @Override
            public void onError(String s) {

                Log.d("myLogsUserCacheRetFail", s);


            }
        });


    }

    /*************************************************************************************************************************************************/

    void createSignupOrLoginDialog() {

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


    /*************************************************************************************************************************************************/

    void createLogoutConfirmationDialog() {

        new TTFancyGifDialog.Builder(MainActivity.this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to log out?")
                .setPositiveBtnText("LOG OUT")
                .setPositiveBtnBackground("#22b573")
                .setNegativeBtnText("CANCEL")
                .setNegativeBtnBackground("#FF3D00")
                .setGifResource(R.drawable.sign_in_opt_one)      //pass your gif, png or jpg
                .isCancellable(true)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        logUserOutBackendless();

                    }
                })
                .OnNegativeClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .build();


    }


    /*************************************************************************************************************************************************/

    void logUserOutBackendless() {

        BackendlessUser localUser = new BackendlessUser();
        localUser = globalCurrentUser;
        localUser.setProperty("logged_in", false);
        final BackendlessUser finalLocalUser = globalCurrentUser;


        Backendless.UserService.update(localUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {


                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {

                        //todo this is wea you stopped

                        Toast.makeText(MainActivity.this, "You have successfully logged out", Toast.LENGTH_LONG).show();
                        UserTokenStorageFactory.instance().getStorage().set(null);
                        Log.d("myLogsLogOutSuccess", "logged user out after update to logged in value: RESPONSE: ");

                        // Delete local cache dir (ignoring any errors):
                        FileUtils.deleteQuietly(getApplicationContext().getCacheDir());

                        recreate();


                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        finalLocalUser.setProperty("logged_in", true);
                        Backendless.UserService.update(finalLocalUser, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {

                                Log.d("myLogsLogOutFail", "failed to log user out after status update: REVERT USER : " + response.toString());


                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Log.d("myLogsLogOutFail", "failed to revert logged in to true after user log out failed: ERROR : " + fault.toString());


                            }
                        });

                        Toast.makeText(MainActivity.this, "There has been an error. Please retry later", Toast.LENGTH_LONG).show();


                    }
                });


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsLogOutFail", "failed to update the logged in value: ERROR : " + fault.toString());


            }
        });


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


            }




                     Backendless.Data.of("Users").findById(backendlessUser.getObjectId(), new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map response) {

                            isLoggedIn = (Boolean) response.get("logged_in");
                            if (isLoggedIn != null) {
                                Log.d("myLogsUsrCcheRetSxLgdIn", isLoggedIn.toString());

                                if (isLoggedIn) {
                                    String profilePicture = (String) innerClassUser.getProperty("profile_picture");
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


                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Log.d("myLogsUserCacheRetSx", "user retrieval failed : error: " + fault.toString());


                        }
                    });










            */