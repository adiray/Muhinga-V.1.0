package com.example.dell.muhingalayoutprototypes;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("lastLogin")
    private Object lastLogin;

    @SerializedName("userStatus")
    private String userStatus;

    @SerializedName("created")
    private long created;

    @SerializedName("___class")
    private String ___class;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("socialAccount")
    private String socialAccount;

    @SerializedName("ownerId")
    private String ownerId;

    @SerializedName("updated")
    private Object updated;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("email")
    private String email;

    @SerializedName("objectId")
    private String objectId;

    public void setLastLogin(Object lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Object getLastLogin() {
        return lastLogin;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public void setClass(String ___class) {
        this.___class = ___class;
    }

    public String get___class() {
        return ___class;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setSocialAccount(String socialAccount) {
        this.socialAccount = socialAccount;
    }

    public String getSocialAccount() {
        return socialAccount;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setUpdated(Object updated) {
        this.updated = updated;
    }

    public Object getUpdated() {
        return updated;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return
                "UserResponse{" +
                        "lastLogin = '" + lastLogin + '\'' +
                        ",userStatus = '" + userStatus + '\'' +
                        ",created = '" + created + '\'' +
                        ",___class = '" + ___class + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",socialAccount = '" + socialAccount + '\'' +
                        ",ownerId = '" + ownerId + '\'' +
                        ",updated = '" + updated + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",email = '" + email + '\'' +
                        ",objectId = '" + objectId + '\'' +
                        "}";
    }
}