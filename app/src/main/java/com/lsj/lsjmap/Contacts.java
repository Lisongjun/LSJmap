package com.lsj.lsjmap;

import android.content.Context;

/**
 * Created by Administrator on 2016/10/7.
 */
public class Contacts {
    private String name;
    private boolean flag = false;
    private String phoneNum;
    private double latitude,longgitude;

    public Contacts(String name,String phoneNum,double latitude,double longgitude,boolean flag){
        this.flag = flag;
        this.name=name;
        this.phoneNum=phoneNum;
        this.latitude=latitude;
        this.longgitude=longgitude;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag(){
        return flag;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLonggitude() {
        return longgitude;
    }

    public void setLonggitude(double longgitude) {
        this.longgitude = longgitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
