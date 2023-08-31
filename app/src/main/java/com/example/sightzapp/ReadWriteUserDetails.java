package com.example.sightzapp;

public class ReadWriteUserDetails {
    public String doB, gender, mobile;

    //Constructor Class
    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile) {

        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;

    }
}
