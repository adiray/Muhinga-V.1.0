package com.example.dell.muhingalayoutprototypes;

public class User {

    String eMail, passWord, firstName, lastName;

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User(String eMail, String passWord, String firstName, String lastName) {
        this.eMail = eMail;
        this.passWord = passWord;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
