package com.example.Model;

public class DeliveryBoy {

    private String address, bid, city, date, image, name, password, phone, time;

    public DeliveryBoy() {
    }

    public DeliveryBoy(String address, String bid, String city, String date, String image, String name, String password, String phone, String time) {
        this.address = address;
        this.bid = bid;
        this.city = city;
        this.date = date;
        this.image = image;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
