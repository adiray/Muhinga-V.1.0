package com.example.dell.muhingalayoutprototypes;

import com.google.gson.annotations.SerializedName;

public class UserLoginResponse {


    @SerializedName("lastLogin")
    private long lastLogin;

    @SerializedName("userStatus")
    private String userStatus;

    @SerializedName("created")
    private long created;

    @SerializedName("___class")
    private String ___class;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("user-token")
    private String userToken;

    @SerializedName("socialAccount")
    private String socialAccount;

    @SerializedName("ownerId")
    private String ownerId;

    @SerializedName("updated")
    private long updated;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("email")
    private String email;

    @SerializedName("objectId")
    private String objectId;

    public void setLastLogin(long lastLogin){
        this.lastLogin = lastLogin;
    }

    public long getLastLogin(){
        return lastLogin;
    }

    public void setUserStatus(String userStatus){
        this.userStatus = userStatus;
    }

    public String getUserStatus(){
        return userStatus;
    }

    public void setCreated(long created){
        this.created = created;
    }

    public long getCreated(){
        return created;
    }

    public void setClass(String ___class){
        this.___class = ___class;
    }

    public String get___class(){
        return ___class;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setUserToken(String userToken){
        this.userToken = userToken;
    }

    public String getUserToken(){
        return userToken;
    }

    public void setSocialAccount(String socialAccount){
        this.socialAccount = socialAccount;
    }

    public String getSocialAccount(){
        return socialAccount;
    }

    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }

    public String getOwnerId(){
        return ownerId;
    }

    public void setUpdated(long updated){
        this.updated = updated;
    }

    public long getUpdated(){
        return updated;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setObjectId(String objectId){
        this.objectId = objectId;
    }

    public String getObjectId(){
        return objectId;
    }

    @Override
    public String toString(){
        return
                "login response{" +
                        "lastLogin = '" + lastLogin + '\'' +
                        ",userStatus = '" + userStatus + '\'' +
                        ",created = '" + created + '\'' +
                        ",___class = '" + ___class + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",user-token = '" + userToken + '\'' +
                        ",socialAccount = '" + socialAccount + '\'' +
                        ",ownerId = '" + ownerId + '\'' +
                        ",updated = '" + updated + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",email = '" + email + '\'' +
                        ",objectId = '" + objectId + '\'' +
                        "}";
    }

}



/*


{
  "objectId" : value,
  "user-token": value,
  "lastLogin": 1554848941000,
  "userStatus": "ENABLED",
  "socialAccount": "BACKENDLESS",
  "created": 1554737700875,
  "email": "3re@out.com",
  "updated": 1554848911967,
  "objectId": "09B75B6C-BC1A-F88C-FF67-89387FD8D600",
  "last_name": "uy",
  "first_name": "ty",
  "ownerId": "09B75B6C-BC1A-F88C-FF67-89387FD8D600",
  "___class": "Users"
}



*/