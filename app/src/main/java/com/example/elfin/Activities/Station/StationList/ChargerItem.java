package com.example.elfin.Activities.Station.StationList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class ChargerItem implements Parcelable {

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
    private String[] latLng;

    public ChargerItem(String street, String houseNumber, String zipCode, String city,
                       String municipality, String county, String descriptionOfLocation,
                       String ownedBy, String numberChargingPoints, String image,
                       String availableChargingPoints, String userComment, String contactInfo,
                       String created, String updated, String stationStatus, String[] latlng) {

        this.street = street;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.municipality = municipality;
        this.county = county;
        this.descriptionOfLocation = descriptionOfLocation;
        this.ownedBy = ownedBy;
        this.numberChargingPoints = numberChargingPoints;
        this.image = image;
        this.availableChargingPoints = availableChargingPoints;
        this.userComment = userComment;
        this.contactInfo = contactInfo;
        this.created = created;
        this.updated = updated;
        this.stationStatus = stationStatus;
        this.latLng = latlng;
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
        parcel.writeString(houseNumber);
        parcel.writeString(zipCode);
        parcel.writeString(city);
        parcel.writeString(municipality);
        parcel.writeString(county);
        parcel.writeString(descriptionOfLocation);
        parcel.writeString(ownedBy);
        parcel.writeString(numberChargingPoints);
        parcel.writeString(image);
        parcel.writeString(availableChargingPoints);
        parcel.writeString(userComment);
        parcel.writeString(contactInfo);
        parcel.writeString(created);
        parcel.writeString(updated);
        parcel.writeString(stationStatus);
        parcel.writeDouble(Double.valueOf(latLng[0]));
        parcel.writeDouble(Double.valueOf(latLng[1]));
    }

    ChargerItem(Parcel in){
        this.street = in.readString();
        this.houseNumber = in.readString();
        this.zipCode = in.readString();
        this.city = in.readString();
        this.municipality = in.readString();
        this.county = in.readString();
        this.descriptionOfLocation = in.readString();
        this.ownedBy = in.readString();
        this.numberChargingPoints = in.readString();
        this.image = in.readString();
        this.availableChargingPoints = in.readString();
        this.userComment = in.readString();
        this.contactInfo = in.readString();
        this.created = in.readString();
        this.updated = in.readString();
        this.stationStatus = in.readString();
        this.latLng[0] = Double.toString(in.readDouble());
        this.latLng[1] = Double.toString(in.readDouble());
    }
}
