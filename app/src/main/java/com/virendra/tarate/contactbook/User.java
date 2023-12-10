package com.virendra.tarate.contactbook;

public class User {
    private String name;
    private String mobileNumber;
    private String ID;

//    Empty Constructor for Firebase
    public User(){

    }

    //Constructor
    public User(String name, String mobileNumber, String Id) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.ID = Id;
    }

    //Getters and Setters


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

}
