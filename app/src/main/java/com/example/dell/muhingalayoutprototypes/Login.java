package com.example.dell.muhingalayoutprototypes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.commons.io.FileUtils;

import java.util.ArrayList;

import br.vince.easysave.EasySave;
import br.vince.easysave.SaveAsyncCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {


    //miscellaneous objects
    String email, password;
    String userSessionToken;
    Boolean isEmailGiven = false, isPasswordGiven = false, isTermsAgreed = false, isStaySignedIn = false, canSignIn = false;
    alertDialogHelper mAlertDialogHelper;
    BackendlessUser classWideUser = new BackendlessUser();

    //broadcastReceiever
    BroadcastReceiver signUpActivityBR;

    //view objects
    EditText emailET, passwordET;
    Button detailsSubmitButton;
    CheckBox termsAndConditionsCheckBox, stayLoggedInCheckBox;
    TextView signUpTextPromt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();


    }

    /*************************************************************************************************************************************************/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (signUpActivityBR != null) {
            unregisterReceiver(signUpActivityBR);
        }

    }


    /*************************************************************************************************************************************************/


    void initializeViews() {

        //get references to the views
        emailET = findViewById(R.id.sign_in_email);
        passwordET = findViewById(R.id.sign_in_password);
        detailsSubmitButton = findViewById(R.id.sign_in_submit_button);
        stayLoggedInCheckBox = findViewById(R.id.sign_in_stay_signed_in_checkbox);
        termsAndConditionsCheckBox = findViewById(R.id.sign_in_terms_and_conditions_checkbox);
        signUpTextPromt = findViewById(R.id.sign_in_sign_up_prompt_text);

        signUpTextPromt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createBroadcastReceiver();
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);


            }
        });


        detailsSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLoginDetails();
                if (canSignIn) {
                    logUserInBackendless();
                }
            }
        });


    }


    /*************************************************************************************************************************************************/

    void createLoginDetails() {

        if (!TextUtils.isEmpty(emailET.getText()) && !TextUtils.isEmpty(passwordET.getText())) {

            if (termsAndConditionsCheckBox.isChecked()) {

                email = emailET.getText().toString();
                password = passwordET.getText().toString();
                isStaySignedIn = stayLoggedInCheckBox.isChecked();
                canSignIn = true;

            } else {

                //display an alert dialog to prompt the user to input the required details
                mAlertDialogHelper = new alertDialogHelper(Login.this, true, false);
                mAlertDialogHelper.createFormAlertDialog();

            }


        } else {

            //display an alert dialog to prompt the user to input the required details
            mAlertDialogHelper = new alertDialogHelper(Login.this, false, true);
            mAlertDialogHelper.createFormAlertDialog();

        }


    }


    /*************************************************************************************************************************************************/

    void updateLoggedInStatus(BackendlessUser user, Boolean isLoggedIn) {


        if (isLoggedIn) {

            user.setProperty("logged_in", true);
            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser response) {

                    Log.d("myLogsBackAuthStatus", "Status set to logged in");
                    cacheUserInstance(response);

                    alertDialogHelper mAlertDialog = new alertDialogHelper();
                    mAlertDialog.createLogInSuccessDialog();

                    detailsSubmitButton.setEnabled(true);


                }

                @Override
                public void handleFault(BackendlessFault fault) {

                    Log.d("myLogsBackAuthStatus", "There was an error : " + fault);


                }
            });
        }else{

            Backendless.UserService.logout(new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {

                    Toast.makeText(Login.this, "You have successfully logged out", Toast.LENGTH_LONG).show();


                    // Delete local cache dir (ignoring any errors):
                    FileUtils.deleteQuietly(getApplicationContext().getCacheDir());

                }

                @Override
                public void handleFault(BackendlessFault fault) {

                    Toast.makeText(Login.this, "There has been an error. Please retry later", Toast.LENGTH_LONG).show();


                }
            });




        }


    }


    /*************************************************************************************************************************************************/


    void logUserInBackendless() {

        detailsSubmitButton.setEnabled(false);
        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

                String userProperties = response.getProperties().toString();
                String userToken = UserTokenStorageFactory.instance().getStorage().get();
                Log.d("myLogsBackSxProps", userProperties);
                Log.d("myLogsBackSxToken", userToken);
                classWideUser = response;

                updateLoggedInStatus(response, true);


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsBackFailError", fault.toString());

                alertDialogHelper mAlertDialog = new alertDialogHelper();
                mAlertDialog.createLogInErrorDialog();


            }
        }, isStaySignedIn);


    }


    /*************************************************************************************************************************************************/

    void cacheUserInstance(BackendlessUser user) {

        if (isStaySignedIn) {
            new EasySave(Login.this).saveModelAsync("current_saved_user", user, new SaveAsyncCallback<BackendlessUser>() {
                @Override
                public void onComplete(BackendlessUser backendlessUser) {

                    if (backendlessUser != null) {
                        //  Log.d("myLogsUserCacheSx", backendlessUser.getPassword());
                        Log.d("myLogsUserCacheSxLI", backendlessUser.toString());

                    }
                }

                @Override
                public void onError(String s) {

                    Log.d("myLogsUserCacheFail", s);


                }
            });

        }
    }


    /*************************************************************************************************************************************************/

    class alertDialogHelper {

        Context context;
        Boolean isEmailGiven;

        public alertDialogHelper() {
        }

        public alertDialogHelper(Context context, Boolean isEmailGiven, Boolean isTermsAgreed) {
            this.context = context;
            this.isEmailGiven = isEmailGiven;
            this.isTermsAgreed = isTermsAgreed;
        }

        Boolean isTermsAgreed;


        void createFormAlertDialog() {

            if (!isEmailGiven) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle(R.string.error);
                builder.setMessage(R.string.email_is_required);
                builder.setIcon(R.drawable.cancel_red_filled);
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else if (!isTermsAgreed) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle(R.string.error);
                builder.setMessage(R.string.you_must_agree_to_t_and_c);
                builder.setIcon(R.drawable.cancel_red_filled);
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        }


        void createLogInSuccessDialog() {

            new TTFancyGifDialog.Builder(Login.this)
                    .setTitle("SUCCESS")
                    .setMessage("You have successfully logged in")
                    .setPositiveBtnText("OK")
                    .setPositiveBtnBackground("#22b573")
                    .setNegativeBtnText("Logout")
                    .setNegativeBtnBackground("#c1272d")
                    .setGifResource(R.drawable.success_option_one)      //pass your gif, png or jpg
                    .isCancellable(false)
                    .OnPositiveClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                            Intent intent = new Intent("finish_activity");
                            sendBroadcast(intent);

                            Intent startMainActivity = new Intent(Login.this, MainActivity.class);
                            startActivity(startMainActivity);

                            finish();

                        }
                    })
                    .OnNegativeClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {
                            logoutUserBackendless(classWideUser);

                        }
                    })
                    .build();


        }


        void createLogInErrorDialog() {

            new TTFancyGifDialog.Builder(Login.this)
                    .setTitle("Error")
                    .setMessage("The login attempt has failed")
                    .setPositiveBtnText("Retry")
                    .setPositiveBtnBackground("#22b573")
                    .setNegativeBtnText("Cancel")
                    .setNegativeBtnBackground("#c1272d")
                    .setGifResource(R.drawable.error_404_travolta)      //pass your gif, png or jpg
                    .isCancellable(true)
                    .OnPositiveClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                            logUserInBackendless();

                        }
                    })
                    .OnNegativeClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                            Toast.makeText(Login.this, "There has been a login error. Please retry later", Toast.LENGTH_LONG).show();


                        }
                    })
                    .build();


        }


    }

    /*************************************************************************************************************************************************/


    void logoutUserBackendless(BackendlessUser user) {

        updateLoggedInStatus(user, false);


    }


    /*************************************************************************************************************************************************/

    void createBroadcastReceiver() {

        //this BR is used to end this activity from the sign up activity.
        // cases; After the user has completed sign up and chooses to log in

        signUpActivityBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action != null && action.equals("finish_login_activity_from_sign_up_activity")) {
                    finish();
                }

            }
        };

        registerReceiver(signUpActivityBR, new IntentFilter("finish_login_activity_from_sign_up_activity"));


    }


}






/* new TTFancyGifDialog.Builder(PlayMusic.this)
                                .setTitle("Download Error")
                                .setMessage("The download encountered an error. Would you like to cancel or try to resume downloading?")
                                .setPositiveBtnText("Resume")
                                .setPositiveBtnBackground("#22b573")
                                .setNegativeBtnText("Cancel")
                                .setNegativeBtnBackground("#c1272d")
                                .setGifResource(R.drawable.error_404_travolta)      //pass your gif, png or jpg
                                .isCancellable(true)
                                .OnPositiveClicked(new TTFancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {

                                        downloadSong();
                                        Toast.makeText(PlayMusic.this, "Download will be resumed", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .OnNegativeClicked(new TTFancyGifDialogListener() {
                                    @Override
                                    public void OnClick() {

                                        Toast.makeText(PlayMusic.this, "The download has been removed", Toast.LENGTH_SHORT).show();

                                        fetch.delete(requestId);

                                    }
                                })
                                .build();
*/


//todo if they clicked stay signed in, take user data store in cache
//todo use cache only when user comes bk, otherwise pass user data around as copies of the original


/*
  OPTION ONE
  user logs in
* when response is returned display a dialog informing successful login or error
* user presses okay, create a new instance of main activity and destroy the old one
*
*
* OPTION 2
* STEPS
*
* set the login button to check if user is logged in
* if user logged in, ask them if want to log out, if yes then log out, display sucess or failure dialog and remove prof pic and other user specifc info
* after logout display success dialog
* if user not logged in, open login activity, use a broadcast reciever, if user doesn't complete login, do nothin, if user completes login, finish
* main activty, show success dialog and res rart main activty when user clicks OK
* login response recieved
* cache user data, cache session code, finish previous activity, show success dialog,user presses ok,
 * turn data to json and pass it via intent to next activity, start main activty


* onCreate; check if there is a user logged in, if yes, load user pic and data, if no skip the user code
*
*
*
*
* option 3
*
* create boolean isLoginAct , set to false, set to true wen user chooses to log out
* in onstart add a method
* check if is a new user boolean is true (means we need to load new profile picture and other user data)
* check if the user is logged in by requesting session key
* if true retrieve user properties and profile picture
* if false move on
*
*todo when youre logging user out, remove them from cache, only save them to cache if they click stay signed in
*
*
* */











