package com.example.dell.muhingalayoutprototypes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.vince.easysave.EasySave;
import br.vince.easysave.SaveAsyncCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;


public class Profile extends AppCompatActivity {

    //declare views
    ImageView refreshProfilePictureButton;
    CircleImageView profilePicture;
    TextView logOutTextView, fullNameTextView, userNameTextView;
    Button deleteAccountButton;
    Toolbar mainToolBar;

    //others
    BackendlessUser globalCurrentUser = new BackendlessUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        initializeViews();

        loadUserData();


    }


    /**************************************************************************************************************************************************/


    void initializeViews() {

        profilePicture = findViewById(R.id.profile_activity_profile_picture_image);
        logOutTextView = findViewById(R.id.profile_activity_log_out_button);
        fullNameTextView = findViewById(R.id.profile_activity_name_textview);
        userNameTextView = findViewById(R.id.profile_activity_username_textview);
        deleteAccountButton = findViewById(R.id.profile_activity_delete_account_button);
        refreshProfilePictureButton = findViewById(R.id.profile_activity_refresh_profile_picture);


       //tool bar
        mainToolBar = findViewById(R.id.profile_activity_action_bar);
        mainToolBar.setTitle("Profile");
        Objects.requireNonNull(mainToolBar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.my_color_white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(mainToolBar);



        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAccount();


            }
        });


        logOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createLogoutConfirmationDialog();

            }
        });


        refreshProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshProfilePicture();

            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeProfilePicture();


            }
        });


    }


    /**************************************************************************************************************************************************/


    void deleteAccount() {


        Backendless.Persistence.of(BackendlessUser.class).remove(globalCurrentUser, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {

                createDeleteAccountSuccessDialog();


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(Profile.this, "There has been an error. Please retry later", Toast.LENGTH_LONG).show();


            }
        });


    }


    /**************************************************************************************************************************************************/

    void logUserOut() {


        //change user login prppty to logged out
        //log user out
        //send user back to main activty and destroy this activity


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


                        Toast.makeText(Profile.this, "You have successfully logged out", Toast.LENGTH_LONG).show();
                        UserTokenStorageFactory.instance().getStorage().set(null);
                        Log.d("myLogsLogOutSuccess", "logged user out after update to logged in value: RESPONSE: ");

                        // Delete local cache dir (ignoring any errors):
                        FileUtils.deleteQuietly(getApplicationContext().getCacheDir());

                        createLogOutSuccessDialog();


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

                        Toast.makeText(Profile.this, "There has been an error. Please retry later", Toast.LENGTH_LONG).show();


                    }
                });


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsLogOutFail", "failed to update the logged in value: ERROR : " + fault.toString());


            }
        });


    }


    /*************************************************************************************************************************************************/

    void createLogoutConfirmationDialog() {

        new TTFancyGifDialog.Builder(Profile.this)
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

                        logUserOut();

                    }
                })
                .OnNegativeClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .build();


    }


    void createLogOutSuccessDialog() {

        new TTFancyGifDialog.Builder(Profile.this)
                .setTitle("SUCCESS")
                .setMessage("You have successfully logged out")
                .setPositiveBtnText("OK")
                .setPositiveBtnBackground("#22b573")
                .setGifResource(R.drawable.success_option_one)      //pass your gif, png or jpg
                .isCancellable(false)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        Intent intent = new Intent("finish_activity");
                        sendBroadcast(intent);

                        Intent startMainActivity = new Intent(Profile.this, MainActivity.class);
                        startActivity(startMainActivity);

                        finish();

                    }
                })
                .build();


    }


    void createDeleteAccountSuccessDialog() {

        new TTFancyGifDialog.Builder(Profile.this)
                .setTitle("SUCCESS")
                .setMessage("You have successfully deleted your account")
                .setPositiveBtnText("OK")
                .setPositiveBtnBackground("#22b573")
                .setGifResource(R.drawable.angry_missing_details)      //pass your gif, png or jpg
                .isCancellable(false)
                .OnPositiveClicked(new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                        Intent intent = new Intent("finish_activity");
                        sendBroadcast(intent);

                        Intent startMainActivity = new Intent(Profile.this, MainActivity.class);
                        startActivity(startMainActivity);

                        finish();

                    }
                })
                .build();


    }


    /*************************************************************************************************************************************************/

//inflate the menu layout file for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_activity_app_bar_menu, menu);
        return true;
    }


    //specify the actions that happen when each menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.profile_app_bar_log_out_button):
                createLogoutConfirmationDialog();
                break;
            default:
                break;
        }
        return true;
    }


    /**************************************************************************************************************************************************/


    void refreshProfilePicture() {

        Log.d("myLogsRefreshPP", "Refresh prfile picture clicked ");


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.profile_picture_colored)
                .error(R.drawable.profile_picture_colored)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();


        Glide.with(Profile.this).load(globalCurrentUser.getProperty("profile_picture")).apply(options).into(profilePicture);


    }


    /**************************************************************************************************************************************************/


    void changeProfilePicture() {

        startFilePicker();

    }


    /**************************************************************************************************************************************************/


    void loadUserData() {

        Intent intent = getIntent();
        String userJson = intent.getStringExtra(MainActivity.EXTRA_GLOBAL_USER);

        if (userJson != null) {
            Gson gson = new Gson();
            globalCurrentUser = gson.fromJson(userJson, BackendlessUser.class);

            if (globalCurrentUser != null) {
                if (globalCurrentUser.getProperty("profile_picture") != null) {

                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.profile_picture_colored)
                            .error(R.drawable.profile_picture_colored)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform();

                    Glide.with(Profile.this).load(globalCurrentUser.getProperty("profile_picture")).apply(options).into(profilePicture);


                }


                if (globalCurrentUser.getProperty("email") != null) {

                    userNameTextView.setText((String) globalCurrentUser.getProperty("email"));

                }


                if (getUserFullName() != null) {

                    fullNameTextView.setText(getUserFullName());
                }

            }

        }


    }


    String getUserFullName() {

        String fullName = null;

        if (globalCurrentUser.getProperty("first_name") != null) {

            fullName = (String) globalCurrentUser.getProperty("first_name");

        }

        if (globalCurrentUser.getProperty("last_name") != null) {

            fullName = fullName +"  "+ (String) globalCurrentUser.getProperty("last_name");
        }


        return fullName;
    }

    /**************************************************************************************************************************************************/


    void startFilePicker() {


        ArrayList<String> filePaths = new ArrayList<>();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.LibAppTheme)
                .pickPhoto(this);

        Log.d("myLogsFilePicker", "ON file selected triggered ");


    }


    /**************************************************************************************************************************************************/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

                    Bitmap bitmap = BitmapFactory.decodeFile(photoPaths.get(0));
                    final BackendlessUser user = globalCurrentUser;
                    Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.JPEG, 100,
                            user.getUserId() + ".jpg", "AppData/profile_pictures", true,
                            new AsyncCallback<BackendlessFile>() {
                                @Override
                                public void handleResponse(BackendlessFile response) {

                                    Log.d("myLogsFilePickerBkEnd", "profile picture uploaded successfully. " + response.toString());


                                    user.setProperty("profile_picture", response.getFileURL());
                                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser response) {

                                            globalCurrentUser = response;

                                            Log.d("myLogsFilePickerBkEnd", "user profile picture url updated successfully. " + response.toString());
                                            updateCahedUser(response);


                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {

                                            Log.d("myLogsFilePickerBkEnd", "profile picture update failed. " + fault.toString());


                                        }
                                    });


                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                    Log.d("myLogsFilePickerBkEnd", "image file upload unsuccesful. " + fault.toString());


                                }
                            });


                }
                break;

        }
    }


    /**************************************************************************************************************************************************/

/*
    void createFilePickerDialog() {

        DialogConfig dialogConfig = new DialogConfig.Builder()
                .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android") // default is sdcard
                .supportFiles(new SupportFile(".jpg", 0), new SupportFile(".png", 0), new SupportFile(".jpeg", 0)) // default is showing all file types.
                .build();

        new FilePickerDialogFragment.Builder()
                .configs(dialogConfig)
                .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                    @Override
                    public void onFileSelected(List<File> list) {

                        Log.d("myLogsFilePicker", "ON file selected triggered ");


                        if (list != null) {

                            File image = list.get(0);
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
                            final BackendlessUser user = globalCurrentUser;
                            Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.JPEG, 100,
                                    user.getUserId() + ".jpg", "AppData/profile_pictures",
                                    new AsyncCallback<BackendlessFile>() {
                                        @Override
                                        public void handleResponse(BackendlessFile response) {

                                            Log.d("myLogsFilePickerBkEnd", "profile picture uploaded successfully. " + response.toString());


                                            user.setProperty("profile_picture", response.getFileURL());
                                            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                                                @Override
                                                public void handleResponse(BackendlessUser response) {

                                                    globalCurrentUser = response;

                                                    Log.d("myLogsFilePickerBkEnd", "user profile picture url updated successfully. " + response.toString());


                                                    RequestOptions options = new RequestOptions()
                                                            .centerCrop()
                                                            .placeholder(R.drawable.profile_picture_colored)
                                                            .error(R.drawable.profile_picture_colored)
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .priority(Priority.HIGH)
                                                            .dontAnimate()
                                                            .dontTransform();

                                                    Glide.with(Profile.this).load(response.getProperty("profile_picture")).apply(options).into(profilePicture);

                                                    updateCahedUser(response);




                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {

                                                    Log.d("myLogsFilePickerBkEnd", "profile picture update failed. " + fault.toString());


                                                }
                                            });


                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {

                                            Log.d("myLogsFilePickerBkEnd", "image file upload unsuccesful. " + fault.toString());


                                        }
                                    });

                        }


                    }
                })
                .build()
                .show(getSupportFragmentManager(), null);


    }








       /*   RequestOptions options = new RequestOptions()
                                                    .centerCrop()
                                                    .placeholder(R.drawable.profile_picture_colored)
                                                    .error(R.drawable.profile_picture_colored)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .priority(Priority.HIGH)
                                                    .dontAnimate()
                                                    .dontTransform();

                                            Glide.with(Profile.this).load(response.getProperty("profile_picture")).apply(options).into(profilePicture);
*/


    /**************************************************************************************************************************************************/


    void updateCahedUser(BackendlessUser user) {


        new EasySave(Profile.this).saveModelAsync("current_saved_user", user, new SaveAsyncCallback<BackendlessUser>() {
            @Override
            public void onComplete(BackendlessUser backendlessUser) {

                if (backendlessUser != null) {
                    //  Log.d("myLogsUserCacheSx", backendlessUser.getPassword());
                    Log.d("myLogsUserCacheSxPR", backendlessUser.toString());

                }
            }

            @Override
            public void onError(String s) {

                Log.d("myLogsUserCacheFail", s);


            }
        });


    }


}
