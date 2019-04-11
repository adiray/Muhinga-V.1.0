package com.example.dell.muhingalayoutprototypes;

import com.google.gson.annotations.SerializedName;

public class UserLogin  {

    @SerializedName("password")
    private String password;

    public UserLogin(String password, String login) {
        this.password = password;
        this.login = login;
    }

    @SerializedName("login")
    private String login;

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public String getLogin(){
        return login;
    }

    @Override
    public String toString(){
        return
                "Responsey{" +
                        "password = '" + password + '\'' +
                        ",login = '" + login + '\'' +
                        "}";
    }

}
