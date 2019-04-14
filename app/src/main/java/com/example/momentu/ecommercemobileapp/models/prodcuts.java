package com.example.momentu.ecommercemobileapp.models;

public class prodcuts
{
    private String description,date,category,image,name,price,productid,quantity,time,oprice,oquantity,location;

    public prodcuts(){

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public prodcuts(String description, String date, String category, String image, String name, String price, String productid, String quantity, String time, String oprice, String oquantity) {
        this.description = description;
        this.date = date;
        this.category = category;
        this.image = image;
        this.name = name;
        this.price = price;
        this.productid = productid;
        this.quantity = quantity;
        this.time = time;
        this.oprice=oprice;
        this.oquantity=oquantity;
    }

    public String getOquantity() {
        return oquantity;
    }

    public void setOquantity(String oquantity) {
        this.oquantity = oquantity;
    }

    public String getOprice() {
        return oprice;
    }

    public void setOprice(String oprice) {
        this.oprice = oprice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
