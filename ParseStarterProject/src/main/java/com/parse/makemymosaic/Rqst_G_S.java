package com.parse.makemymosaic;

import android.location.Location;

/**
 * Created by Lenovo on 27-10-2017.
 */

class Rqst_G_S {

    String ListViewContent, Distance, Duration;
    String Usernames;
    Double Lat;
    Double Lng;
    Location Location;

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getListViewContent() {
        return ListViewContent;
    }

    public void setListViewContent(String listViewContent) {
        ListViewContent = listViewContent;
    }

    public String getUsernames() {
        return Usernames;
    }

    public void setUsernames(String usernames) {
        Usernames = usernames;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lat = lng;
    }

    public Location getLocation() {
        return Location;
    }

    public void setLocation(Location location) {
        Location = location;
    }


}
