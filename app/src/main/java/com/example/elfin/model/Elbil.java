package com.example.elfin.model;

import com.google.firebase.firestore.Exclude;

import java.util.Map;

public class Elbil {

    private String documentId;
    private String brand, model, modelYear, fastCharge,
            effekt, batteri;
    Map<String, Double> specs;


    public Elbil(){
        //public no-arg constructor needed for firestore database
    }

    public Elbil(String brand, String model, String fastCharge, Map<String, Double> specs) {
        this.brand = brand;
        this.model = model;
        this.fastCharge = fastCharge;
        this.specs = specs;
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

    public String getFastCharge() {
        return fastCharge;
    }

    public void setFastCharge(String fastCharge) {
        this.fastCharge = fastCharge;
    }

    public Map<String, Double> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, Double> specs) {
        this.specs = specs;
    }
}
