package com.rohail.beyondinfinity.news.hub.newshub.models;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Author Shahzad hemani
 */

public class CustomerInfoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mobileNo;
    private String cnic;
    private String email;
    private String fname;
    private String lname;
    private String gender;
    private String dob;
    private String occupation;
    private String udid;
    private String transAmt;
    private String uid;
    private String rDate;
    private String isExpired;
    private String city;
    private String streetNo;
    private String houseNo;
    private String area;
    private String block;

    public CustomerInfoModel() {

    }

    @Override
    public String toString() {
        return "CustomerInfoModel{" +
                "mobileNo='" + mobileNo + '\'' +
                ", cnic='" + cnic + '\'' +
                ", email='" + email + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", gender='" + gender + '\'' +
                ", dob='" + dob + '\'' +
                ", occupation='" + occupation + '\'' +
                ", udid='" + udid + '\'' +
                ", transAmt='" + transAmt + '\'' +
                ", uid='" + uid + '\'' +
                ", rDate='" + rDate + '\'' +
                ", isExpired='" + isExpired + '\'' +
                ", city='" + city + '\'' +
                ", streetNo='" + streetNo + '\'' +
                ", houseNo='" + houseNo + '\'' +
                ", area='" + area + '\'' +
                ", block='" + block + '\'' +
                '}';
    }

    public void setAddress(String city, String block, String streetNo, String houseNo, String area) {
        setCity(city);
        setBlock(block);
        setStreetNo(streetNo);
        setHouseNo(houseNo);
        setArea(area);
    }

    public String getFullAddress() {

        String address = "";

        if (!getHouseNo().isEmpty()) {
            address = address + getHouseNo();
        }
        if (!getBlock().isEmpty()) {
            address = address + (", " + getBlock());
        }
        if (!getStreetNo().isEmpty()) {
            address = address + (", " + getStreetNo());
        }
        if (!getArea().isEmpty()) {
            address = address + (", " + getArea());
        }
        if (!getCity().isEmpty()) {
            address = address + (", " + getCity());
        }

        return address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        String fName = TextUtils.isEmpty(getFname()) ? "" : getFname();
        String lName = TextUtils.isEmpty(getLname()) ? "" : getLname();
        return fname + " " + lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(String transAmt) {
        this.transAmt = transAmt;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }

}
