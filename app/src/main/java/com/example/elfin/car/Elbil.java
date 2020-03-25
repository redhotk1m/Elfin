package com.example.elfin.car;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.firebase.firestore.Exclude;

import java.util.Map;

public class Elbil implements Parcelable {

    private String documentId;
    private String brand, model, modelYear;
    private Map<String, Double> specs;

    public Elbil(){
        //public no-arg constructor needed for firestore database
    }

    public Elbil(String brand, String model, String modelYear, Map<String, Double> specs) {
        this.brand = brand;
        this.model = model;
        this.modelYear = modelYear;
        this.specs = specs;
    }

    public Elbil(Parcel in) {
        documentId = in.readString();
        brand = in.readString();
        model = in.readString();
        modelYear = in.readString();
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

    Boolean isError = false;

    public Elbil(String error) {
        isError = true;
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
    }
}
