package com.example.momentu.ecommercemobileapp;

public class userData {

public String name,username,phone,address,gender,date;
public userData(){

}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public userData(String name, String username, String phone, String address, String gender, String date) {
        this.name = name;
        this.username = username;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.date=date;
    }
}
