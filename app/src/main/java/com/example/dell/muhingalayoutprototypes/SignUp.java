package com.example.dell.muhingalayoutprototypes;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.github.florent37.materialtextfield.MaterialTextField;

import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.vince.easysave.EasySave;
import br.vince.easysave.SaveAsyncCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {


    //miscellaneous objects
    String firstName, lastName, password, email;  //these hold the user's submitted details. Updated when the user hits submit button
    Map<String, Object> userDetailsMap = new HashMap<>();  //used to create a backendless user object
    Boolean isStaySignedIn = false, canSignIn = false;
    alertDialogHelper mAlertDialogHelper = new alertDialogHelper();
    String startingActivity; //the name of the activity that sign up was started from


    //declare views
    EditText emailET, firstNameET, lastNameET, passwordET;
    Button submitDetailsButton;
    TextView signInTextButton;
    CheckBox termsAndConditionsCheckBox, stayLoggedInCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //initialize views
        initializeViews();


    }


    /*************************************************************************************************************************************************/


    void initializeViews() {


        //get references to the views
        emailET = findViewById(R.id.sign_up_email);
        firstNameET = findViewById(R.id.sign_up_first_name);
        lastNameET = findViewById(R.id.sign_up_last_name);
        passwordET = findViewById(R.id.sign_up_password);
        submitDetailsButton = findViewById(R.id.sign_up_submit_button);
        signInTextButton = findViewById(R.id.sign_up_sign_in_prompt_text);
        termsAndConditionsCheckBox = findViewById(R.id.sign_up_terms_and_conditions_checkbox);
        stayLoggedInCheckBox = findViewById(R.id.sign_up_stay_signed_in_checkbox);

        submitDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserObject();
                if (canSignIn) {
                    userSignUpBackendless();
                }
            }
        });

        signInTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open houses activity
                Intent intent = new Intent(SignUp.this, Login.class);
                SignUp.this.startActivity(intent);

            }
        });

    }


    /*************************************************************************************************************************************************/

    void createUserObject() {

        //get the users details from the edit texts and create the user object

        //todo check to make sure fields are not null
        if (!TextUtils.isEmpty(emailET.getText()) && !TextUtils.isEmpty(passwordET.getText())) {

            if (termsAndConditionsCheckBox.isChecked()) {

                if (!TextUtils.isEmpty(lastNameET.getText())) {
                    lastName = lastNameET.getText().toString();
                    userDetailsMap.put("last_name", lastName);
                }

                if (!TextUtils.isEmpty(firstNameET.getText())) {
                    firstName = firstNameET.getText().toString();
                    userDetailsMap.put("first_name", firstName);
                }

                email = emailET.getText().toString();
                password = passwordET.getText().toString();
                isStaySignedIn = stayLoggedInCheckBox.isChecked();
                canSignIn = true;
                userDetailsMap.put("email", email);
                userDetailsMap.put("password", password);
            } else {


                mAlertDialogHelper.createTermsAndConditionsDialog();

            }


        } else {

            mAlertDialogHelper.createEmailMissingDialog();


        }


    }


    /*************************************************************************************************************************************************/


    class alertDialogHelper {


        void createEmailMissingDialog() {

            new TTFancyGifDialog.Builder(SignUp.this)
                    .setTitle("SORRY!")
                    .setMessage("Email and Password are required fields!")
                    .setPositiveBtnText("OK")
                    .setPositiveBtnBackground("#22b573")
                    .setGifResource(R.drawable.angry_missing_details)      //pass your gif, png or jpg
                    .isCancellable(true)
                    .OnPositiveClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    })
                    .build();


        }


        void createTermsAndConditionsDialog() {

            new TTFancyGifDialog.Builder(SignUp.this)
                    .setTitle("SORRY!")
                    .setMessage("You must agree to the terms and conditions!")
                    .setPositiveBtnText("OK")
                    .setPositiveBtnBackground("#22b573")
                    .setGifResource(R.drawable.cat_reading_documents)      //pass your gif, png or jpg
                    .isCancellable(true)
                    .OnPositiveClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    })
                    .build();

        }


        void createSignUpSuccessDialog() {

            new TTFancyGifDialog.Builder(SignUp.this)
                    .setTitle("SUCCESS")
                    .setMessage("You have successfully signed up!")
                    .setPositiveBtnText("Log in")
                    .setPositiveBtnBackground("#22b573")
                    .setNegativeBtnText("Go home")
                    .setNegativeBtnBackground("#c1272d")
                    .setGifResource(R.drawable.success_option_one)      //pass your gif, png or jpg
                    .isCancellable(false)
                    .OnPositiveClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                            submitDetailsButton.setEnabled(false);
                            logUserInBackendless();

                        }
                    })
                    .OnNegativeClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                            //sendUserActionEvent() mView == null

                            Intent intentEndMainActivity = new Intent("finish_activity");
                            sendBroadcast(intentEndMainActivity);
                            Intent intentEndLogInActivity = new Intent("finish_activity");
                            sendBroadcast(intentEndLogInActivity);

                            Intent startMainActivity = new Intent(SignUp.this, MainActivity.class);
                            startActivity(startMainActivity);

                            finish();

                        }
                    })
                    .build();


        }


        void createLogInSuccessDialog() {

            new TTFancyGifDialog.Builder(SignUp.this)
                    .setTitle("SUCCESS")
                    .setMessage("You have successfully logged in")
                    .setPositiveBtnText("OK")
                    .setPositiveBtnBackground("#22b573")
                    .setGifResource(R.drawable.thumbs_up_success)      //pass your gif, png or jpg
                    .isCancellable(false)
                    .OnPositiveClicked(new TTFancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                            //end main activity
                            Intent intentEndMainActivity = new Intent("finish_activity");
                            sendBroadcast(intentEndMainActivity);
                            //end log in activity
                            Intent intentEndLogInActivity = new Intent("finish_activity");
                            sendBroadcast(intentEndLogInActivity);

                            //restart main activity
                            Intent startMainActivity = new Intent(SignUp.this, MainActivity.class);
                            startActivity(startMainActivity);

                            //end this activity
                            finish();

                        }
                    })
                    .build();


        }
    }

    /*************************************************************************************************************************************************/


    void logUserInBackendless() {

        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {


                updateLoggedInStatus(response);


            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        }, isStaySignedIn);


    }


    /*************************************************************************************************************************************************/


    void cacheUserInstance(BackendlessUser user) {

        if (isStaySignedIn) {

            //todo cache an instance of the user object
            new EasySave(SignUp.this).saveModelAsync("current_saved_user", user, new SaveAsyncCallback<BackendlessUser>() {
                @Override
                public void onComplete(BackendlessUser backendlessUser) {

                    if (backendlessUser != null) {
                        Log.d("myLogsUserCacheSxSU", backendlessUser.toString());
                    }
                }

                @Override
                public void onError(String s) {

                    Log.d("myLogsUserCacheFailSU", s);


                }
            });


        }


    }


    /*************************************************************************************************************************************************/


    void userSignUpBackendless() {

        BackendlessUser newBackendlessUser = new BackendlessUser();
        newBackendlessUser.putProperties(userDetailsMap);


        Backendless.UserService.register(newBackendlessUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

                Log.d("myLogsBkReg", response.getProperties().toString());
                mAlertDialogHelper.createSignUpSuccessDialog();


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsBkReg", fault.toString());


            }
        });


    }


    /*************************************************************************************************************************************************/


    /*************************************************************************************************************************************************/

    void updateLoggedInStatus(BackendlessUser user) {


        user.setProperty("logged_in", true);
        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

                Log.d("myLogsBackAuthStatus", "Status set to logged in");
                cacheUserInstance(response);

                mAlertDialogHelper.createLogInSuccessDialog();
                submitDetailsButton.setEnabled(true);


            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.d("myLogsBackAuthStatus", "There was an error : " + fault);


            }
        });


    }


    /*************************************************************************************************************************************************/


}






/*  void createAlertDialogs() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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


    }*/
