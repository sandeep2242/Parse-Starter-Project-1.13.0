package com.parse.makemymosaic;

import com.parse.ParseGeoPoint;

import java.util.List;

/**
 * Created by Lenovo on 23-10-2017.
 */

public class Cabs_G_S {


    private String CabImages, DriverName, Price,ObjId,CabName;
    private int TotalSeats, BookedSeats,Discount;
    private List Seats;
    private ParseGeoPoint DriverLocation;

    public String getCabImages() {
        return CabImages;
    }

    public void setCabImages(String cabImages) {
        CabImages = cabImages;
    }

    public String getObjId() {
        return ObjId;
    }

    public void setObjId(String objId) {
        ObjId = objId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getCabName() {
        return CabName;
    }

    public void setCabName(String cabName) {
        CabName = cabName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public List getSeats() {
        return Seats;
    }

    public void setSeats(List seats) {
        Seats = seats;
    }

    public int getTotalSeats() {
        return TotalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        TotalSeats = totalSeats;
    }

    public int getBookedSeats() {
        return BookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        BookedSeats = bookedSeats;
    }

    public int getDiscount() {
        return Discount;
    }

    public void setDiscount(int discount) {
        Discount = discount;
    }

    public ParseGeoPoint getDriverLocation() {
        return DriverLocation;
    }

    public void setDriverLocation(ParseGeoPoint driverLocation) {
        DriverLocation = driverLocation;
    }
}
