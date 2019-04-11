package com.example.dell.muhingalayoutprototypes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {


    //miscellaneous objects
    String email, password;
    String userSessionToken;

    //view objects
    EditText emailET, passwordET;
    Button detailsSubmitButton;
    //todo add the text vie to go to sign up page


    //retrofit objects
    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<UserLoginResponse> loginUserCall;  //call to email the user
    UserLogin newUserLoginObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //get references to the views
        emailET = findViewById(R.id.sign_in_email);
        passwordET = findViewById(R.id.sign_in_password);
        detailsSubmitButton = findViewById(R.id.sign_in_submit_button);

        buildRetrofitClient();

        detailsSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLoginDetails();
                logUserIn();
            }
        });



    }


    /*************************************************************************************************************************************************/


    void buildRetrofitClient() {

        //initialize the retrofit client builder using the backendless.com api
        builder = new Retrofit.Builder();
        builder.baseUrl("http://api.backendless.com/125AF8BD-1879-764A-FF22-13FB1C162400/6F40C4D4-6CFB-E66A-FFC7-D71E4A8BF100/data/")
                .addConverterFactory(GsonConverterFactory.create());

        //use your builder to build a retrofit object
        myRetrofit = builder.build();

        //create a retrofit client using the retrofit object
        myWebClient = myRetrofit.create(RetrofitClient.class);


    }


    /*************************************************************************************************************************************************/

    void createLoginDetails() {

        //todo check to make sure fields are not null

        email = emailET.getText().toString();
        password = passwordET.getText().toString();

        newUserLoginObject = new UserLogin(password,email);

        Gson gson = new Gson();
        String json = gson.toJson(newUserLoginObject);


        Log.d("myLogsLgn2Json", "json : " + json);




    }


    /*************************************************************************************************************************************************/


    void logUserIn() {


        //create your call using the retrofit client
        loginUserCall = myWebClient.loginUser(newUserLoginObject);
       loginUserCall.clone().enqueue(new Callback<UserLoginResponse>() {
           @Override
           public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {





               Log.d("myLogsLogInSuccess", "got a response");


               if (response.isSuccessful()) {


                   Log.d("myLogsLogInSuccess", "got a response, level 2");
               }


                   if (response.body() != null) {

                       Log.d("myLogsLogInSuccess", "got a response, level 3");
                       userSessionToken = response.body().getUserToken();
                       Log.d("myLogsLogInSuccess", "userToken : " + userSessionToken);

                   }












           }

           @Override
           public void onFailure(Call<UserLoginResponse> call, Throwable t) {


               Log.d("myLogsLogInError", "error message : " + t.getCause());
               Log.d("myLogsLogInError", "error message : " + t.getMessage());
               t.printStackTrace();


           }
       });




    }


}


//todo test the email , then add the rest of the features





/*
        loginUserCall.clone().enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {

                Log.d("myLogsLogInSuccess", "got a response");


                if (response.isSuccessful()) {


                    Log.d("myLogsLogInSuccess", "got a response, level 2");



                    if (response.body() != null) {

                        Log.d("myLogsLogInSuccess", "got a response, level 3");



                        if (response.body().getCode() == null) {//check if it is an error response
                            Log.d("myLogsLogInSuccess", "got a response, level 4");

                            userSessionToken = response.body().getUser_token();
                            Log.d("myLogsLogInSuccess", "userToken : " + userSessionToken);
                        } else {
                            Log.d("myLogsLogInResponse", "error message : " + response.body().getMessage() + " code " + response.body().getCode());
                        }




                    }


                }











            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {

                Log.d("myLogsLogInError", "error message : " + t.getCause());
                Log.d("myLogsLogInError", "error message : " + t.getMessage());
                t.printStackTrace();



            }
        });

   */





