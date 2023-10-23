package com.example.hnsupgrade;

import java.io.Serializable;

public class placeInfo implements Serializable {
    private String name;
    private String latitude;
    private String longitude;
    private String placeID;
    private String vicinity;
    private String rating;
    private String userRating;
    private String website;
    private String formatPhone;
    private String openHours;
//website,international_phone_number,formatted_phone_number,opening_hours


    public placeInfo(String name, String latitude, String longitude, String placeID, String vicinity, String rating, String userRating, String website, String formatPhone, String openHours) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeID = placeID;
        this.vicinity = vicinity;
        this.rating = rating;
        this.userRating = userRating;
        this.website = website;
        this.formatPhone = formatPhone;
        this.openHours = openHours;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFormatPhone() {
        return formatPhone;
    }

    public void setFormatPhone(String formatPhone) {
        this.formatPhone = formatPhone;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getplaceID() {
        return placeID;
    }

    public void setplaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }
}
