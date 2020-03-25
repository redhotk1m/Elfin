package com.example.elfin.Activities.Station.StationList;

public class ChargerItem {

    private String stationName;
    private String description;
    private String chargeTimeFast;
    private String availableFast;
    private String chargeTimeSlow;
    private String availableSlow;
    private String distanceKm;

    private int imageSlow;
    private int imageFast;

    private int photo;


    public ChargerItem() {
    }

    public ChargerItem(String stationName, String description, String chargeTimeFast,
                String availableFast, String chargeTimeSlow, String availableSlow,
                String distanceKm, int imageFast, int imageSlow) {

        this.stationName=stationName;
        this.description=description;
        this.chargeTimeFast=chargeTimeFast;
        this.availableFast=availableFast;
        this.chargeTimeSlow=chargeTimeSlow;
        this.availableSlow=availableSlow;
        this.distanceKm=distanceKm;
        this.imageFast=imageFast;
        this.imageSlow=imageSlow;

    }


    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChargeTimeFast() {
        return chargeTimeFast;
    }

    public void setChargeTimeFast(String chargeTimeFast) {
        this.chargeTimeFast = chargeTimeFast;
    }

    public String getAvailableFast() {
        return availableFast;
    }

    public void setAvailableFast(String availableFast) {
        this.availableFast = availableFast;
    }

    public String getChargeTimeSlow() {
        return chargeTimeSlow;
    }

    public void setChargeTimeSlow(String chargeTimeSlow) {
        this.chargeTimeSlow = chargeTimeSlow;
    }

    public String getAvailableSlow() {
        return availableSlow;
    }

    public void setAvailableSlow(String availableSlow) {
        this.availableSlow = availableSlow;
    }

    public String getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(String distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getImageSlow() {
        return imageSlow;
    }

    public void setImageSlow(int imageSlow) {
        this.imageSlow = imageSlow;
    }

    public int getImageFast() {
        return imageFast;
    }

    public void setImageFast(int imageFast) {
        this.imageFast = imageFast;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
