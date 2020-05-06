package com.elfin.elfin.car;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.Map;

public class Elbil implements Parcelable {

    private int iconImage;
    private String spinnerDisplay;
    private String documentId, brand, model, modelYear, battery, fastCharge, effect;
    private Map<String, Double> specs;

    public Elbil() {
        //public no-arg constructor needed for firestore database
    }

    public Elbil(int iconImage, String spinnerDisplay) {
        this.iconImage = iconImage;
        this.spinnerDisplay = spinnerDisplay;
    }

    public Elbil(String brand) {
        this.brand = brand;
    }

    public Elbil(String model, String modelYear) {
        this.model = model;
        this.modelYear = modelYear;
    }

    public Elbil(String brand, String model, String modelYear, String battery, String fastCharge, String effect) {
        this.brand = brand;
        this.model = model;
        this.modelYear = modelYear;
        this.battery = battery;
        this.fastCharge = fastCharge;
        this.effect = effect;
    }

    public Elbil(Parcel in) {
        documentId = in.readString();
        brand = in.readString();
        model = in.readString();
        modelYear = in.readString();
        battery = in.readString();
        fastCharge = in.readString();
        effect = in.readString();
        //specs = in.readHashMap();
    }

    public boolean[] exists() {
        boolean[] exists = new boolean[5];
        if (!this.brand.equals("")) {
            exists[0] = true;
        }
        if (!this.model.equals("")) {
            exists[1] = true;
        }
        if (!this.modelYear.equals("")) {
            exists[2] = true;
        }
        if (!this.battery.equals("")) {
            exists[3] = true;
        }
        if (!this.fastCharge.equals("")) {
            exists[4] = true;
        }

        return exists;
    }

    public static final Creator<Elbil> CREATOR = new Creator<Elbil>() {
        @Override
        public Elbil createFromParcel(Parcel in) {
            return new Elbil(in);
        }

        @Override
        public Elbil[] newArray(int size) {
            return new Elbil[size];
        }
    };

    /*
    Boolean isError = false;

    public Elbil(String error) {
        isError = true;
    }
     */


    @Exclude
    public int getIconImage() {
        return iconImage;
    }

    public void setIconImage(int iconImage) {
        this.iconImage = iconImage;
    }

    @Exclude
    public String getSpinnerDisplay() {
        return spinnerDisplay;
    }

    public void setSpinnerDisplay(String spinnerDisplay) {
        this.spinnerDisplay = spinnerDisplay;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getFastCharge() {
        return fastCharge;
    }

    public void setFastCharge(String fastCharge) {
        this.fastCharge = fastCharge;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public Map<String, Double> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, Double> specs) {
        this.specs = specs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(documentId);
        parcel.writeString(brand);
        parcel.writeString(model);
        parcel.writeString(modelYear);
        parcel.writeString(battery);
        parcel.writeString(fastCharge);
        parcel.writeString(effect);
        //parcel.writeMap(specs);
    }

    @Override
    public String toString() {
        return "Elbil{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", modelYear='" + modelYear + '\'' +
                ", battery='" + battery + '\'' +
                ", fastCharge='" + fastCharge + '\'' +
                ", effect='" + effect + '\'' +
                '}';
    }

    /*
    @NonNull
    @Override
    public String toString() {
        if (documentId == null) return spinnerDisplay;
        spinnerDisplay = brand + " " + model + " ( " + modelYear + ")";
        return spinnerDisplay;
        //return super.toString();
    }
    */
}