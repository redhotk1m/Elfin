package com.elfin.elfin.Activities.Station.StationList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class ChargerItem implements Parcelable {

    /**
     * This class is used to keep track off all the information about a chargingStation.
     */
    public ChargerItem() {
    }

    private String street;
    private String houseNumber;
    private String zipCode;
    private String city;
    private String municipality;
    private String county;
    private String descriptionOfLocation;
    private String ownedBy;
    private String numberChargingPoints;
    private String image;
    private String availableChargingPoints;
    private String userComment;
    private String contactInfo;
    private String created;
    private String updated;
    private String stationStatus;
    private String chademo;
    private String numberOfChademo;
    private String chademoTime;
    private String ccs;
    private String numberOfCcs;
    private String ccsTime;
    private String ligtning;
    private String lightningTime;
    private String[] latLng;
    private int imageFast;
    private int imageSlow;
    private double[] pointLatLng;
    private String MFromStartLocation;
    private String mFromCar;
    private String lightningCCS;
    private String numberOflightningCCS;
    private String fastText;
    private String lightningText;
    private String everyCharger;

    public ChargerItem(String street, String houseNumber, String city,
                       String descriptionOfLocation,
                       String ownedBy, String userComment, String contactInfo,
                       String[] latlng,
                       String chademo, String numberOfChademo, String chademoTime, String ccs, String numberOfCcs,
                       String ccsTime, int imageFast, int imageSlow, String lightningCCS,
                       String numberOflightningCCS, String lightningTime, String fastText, String lightningText) {

        this.street = street;
        this.houseNumber = houseNumber;
        this.descriptionOfLocation = descriptionOfLocation;
        this.city=city;
        this.ownedBy = ownedBy;
        this.userComment = userComment;
        this.contactInfo = contactInfo;
        this.latLng = latlng;
        this.chademo = chademo;
        this.numberOfChademo = numberOfChademo;
        this.chademoTime = chademoTime;
        this.ccs = ccs;
        this.numberOfCcs = numberOfCcs;
        this.ccsTime = ccsTime;
        this.imageFast = imageFast;
        this.imageSlow = imageSlow;
        this.lightningTime = lightningTime;
        this.lightningCCS = lightningCCS;
        this.numberOflightningCCS = numberOflightningCCS;
        this.lightningTime = lightningTime;
        this.fastText=fastText;
        this.lightningText = lightningText;
    }


    public static final Creator<ChargerItem> CREATOR = new Creator<ChargerItem>() {
        @Override
        public ChargerItem createFromParcel(Parcel in) {
            return new ChargerItem(in);
        }

        @Override
        public ChargerItem[] newArray(int size) {
            return new ChargerItem[size];
        }
    };

    public void setLatLng(String[] latLng) {
        this.latLng = latLng;
    }


    public String getEveryCharger() {
        return everyCharger;
    }

    public void setEveryCharger(String everyCharger) {
        this.everyCharger = everyCharger;
    }

    public String getFastText() {
        return fastText;
    }

    public void setFastText(String fastText) {
        this.fastText = fastText;
    }

    public String getLightningText() {
        return lightningText;
    }

    public void setLightningText(String lightningText) {
        this.lightningText = lightningText;
    }

    public String getChademoTime() {
        return chademoTime;
    }

    public void setChademoTime(String chademoTime) {
        this.chademoTime = chademoTime;
    }

    public String getCcsTime() {
        return ccsTime;
    }

    public void setCcsTime(String ccsTime) {
        this.ccsTime = ccsTime;
    }

    public String getLightningCCS() {
        return lightningCCS;
    }

    public void setLightningCCS(String lightningCCS) {
        this.lightningCCS = lightningCCS;
    }

    public String getNumberOflightningCCS() {
        return numberOflightningCCS;
    }

    public void setNumberOflightningCCS(String numberOflightningCCS) {
        this.numberOflightningCCS = numberOflightningCCS;
    }

    public String getLightningTime() {
        return lightningTime;
    }

    public void setLightningTime(String lightningTime) {
        this.lightningTime = lightningTime;
    }



    public String getLigtning() {
        return ligtning;
    }

    public void setLigtning(String ligtning) {
        this.ligtning = ligtning;
    }

    public String getCcs() {
        return ccs;
    }

    public void setCcs(String ccs) {
        this.ccs = ccs;
    }

    public String getNumberOfCcs() {
        return numberOfCcs;
    }

    public void setNumberOfCcs(String numberOfCcs) {
        this.numberOfCcs = numberOfCcs;
    }

    public String getChademo() {
        return chademo;
    }

    public void setChademo(String chademo) {
        this.chademo = chademo;
    }

    public String getNumberOfChademo() {
        return numberOfChademo;
    }

    public void setNumberOfChademo(String numberOfChademo) {
        this.numberOfChademo = numberOfChademo;
    }

    public String getmFromCar() {
        return mFromCar;
    }

    public void setmFromCar(double KMFromStartLocation) {
        this.mFromCar = String.valueOf(KMFromStartLocation);
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

    public LatLng getLatLng() {
        return new LatLng(Double.valueOf(latLng[0]),Double.valueOf(latLng[1]));
    }

    public void setLatLng(LatLng latLng) {
        this.latLng[0] = Double.toString(latLng.latitude);
        this.latLng[1] = Double.toString(latLng.longitude);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDescriptionOfLocation() {
        return descriptionOfLocation;
    }

    public void setDescriptionOfLocation(String descriptionOfLocation) {
        this.descriptionOfLocation = descriptionOfLocation;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getNumberChargingPoints() {
        return numberChargingPoints;
    }

    public void setNumberChargingPoints(String numberChargingPoints) {
        this.numberChargingPoints = numberChargingPoints;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAvailableChargingPoints() {
        return availableChargingPoints;
    }

    public void setAvailableChargingPoints(String availableChargingPoints) {
        this.availableChargingPoints = availableChargingPoints;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStationStatus() {
        return stationStatus;
    }

    public void setStationStatus(String stationStatus) {
        this.stationStatus = stationStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(street);
        parcel.writeString(descriptionOfLocation);
        parcel.writeString(ownedBy);
        parcel.writeString(city);
        parcel.writeString(userComment);
        parcel.writeString(contactInfo);
        parcel.writeDouble(Double.valueOf(latLng[0]));
        parcel.writeDouble(Double.valueOf(latLng[1]));
        parcel.writeString(chademo);
        parcel.writeString(numberOfChademo);
        parcel.writeString(chademoTime);
        parcel.writeString(ccs);
        parcel.writeString(numberOfCcs);
        parcel.writeString(ccsTime);
        parcel.writeInt(imageFast);
        parcel.writeInt(imageSlow);
        parcel.writeString(ligtning);
        parcel.writeString(numberOflightningCCS);
        parcel.writeString(lightningTime);
        parcel.writeString(fastText);
        parcel.writeString(lightningText);
    }

    ChargerItem(Parcel in){
        this.street = in.readString();
        this.houseNumber = in.readString();
        this.city = in.readString();
        this.descriptionOfLocation = in.readString();
        this.ownedBy = in.readString();
        this.userComment = in.readString();
        this.contactInfo = in.readString();
        this.latLng[0] = Double.toString(in.readDouble());
        this.latLng[1] = Double.toString(in.readDouble());
        this.chademo = in.readString();
        this.numberOfChademo = in.readString();
        this.chademoTime = in.readString();
        this.ccs = in.readString();
        this.numberOfCcs = in.readString();
        this.ccsTime = in.readString();
        this.imageFast = in.readInt();
        this.imageSlow = in.readInt();
        this.ligtning = in.readString();
        this.numberOflightningCCS = in.readString();
        this.lightningTime = in.readString();
        this.fastText = in.readString();
        this.lightningText = in.readString();
        this.everyCharger = in.readString();
    }

    public void setPointLatLng(double[] pointLatLng) {
        this.pointLatLng = pointLatLng;
    }

    public void setPointLatLng(LatLng latLng) {
        this.pointLatLng[0] = latLng.latitude;
        this.pointLatLng[1] = latLng.longitude;
    }

    public double[] getPointLatLng() {
        return pointLatLng;
    }

    public String getMFromStartLocation() {
        return MFromStartLocation;
    }

    public void setMFromStartLocation(String KMFromStartLocation) {
        this.MFromStartLocation = KMFromStartLocation;
    }

    public void setMFromStartLocation(double KMFromStartLocation) {
        this.MFromStartLocation = String.valueOf(KMFromStartLocation);
        this.mFromCar = String.valueOf(KMFromStartLocation);
    }
}
