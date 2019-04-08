package com.example.dell.muhingalayoutprototypes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.florent37.materialtextfield.MaterialTextField;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {


    //declare views
    EditText emailET, firstNameET, lastNameET, passwordET;
    Button submitDetailsButton;
    //MaterialTextField materialTextField;


    //declare the retrofit objects. All these are used with retrofit
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<User> registerNewUserCall;
    User newUserObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //get references to the views
        emailET = findViewById(R.id.sign_up_email);
        firstNameET = findViewById(R.id.sign_up_first_name);
        lastNameET = findViewById(R.id.sign_up_last_name);
        passwordET = findViewById(R.id.sign_up_password);
        submitDetailsButton = findViewById(R.id.sign_up_submit_button);

        buildRetrofitClient();


        submitDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserObject();
                postUserObject();
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

    void createUserObject() {

        String firstName, lastName, password, email;


        email = emailET.getText().toString();
        lastName = lastNameET.getText().toString();
        firstName = firstNameET.getText().toString();
        password = passwordET.getText().toString();

        newUserObject = new User(email, password, firstName, lastName);


    }


    /*************************************************************************************************************************************************/

    void postUserObject() {

        Log.d("myLogsRegister", "user object" + newUserObject.getEmail());


        //create your call using the retrofit client
        registerNewUserCall = myWebClient.createUser(newUserObject);

        registerNewUserCall.clone().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                

                Log.d("myLogsRegister", "response received");


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                String causes;

                if (t.getCause() != null) {
                    causes = t.getCause().toString();
                    Log.d("myLogsRegisterError", "error cause " + causes);
                }

                Log.d("myLogsRegisterError", "error message " + t.getMessage());

                t.printStackTrace();


            }
        });


    }


}

//todo make sure the edit text os not null before you pick up the text