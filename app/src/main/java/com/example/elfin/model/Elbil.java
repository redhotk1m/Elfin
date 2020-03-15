package com.example.elfin.model;

public class Elbil {

    private String merke, lader, batteri;


    public Elbil(){
        //public no-arg constructor needed for firestore database
    }

    public Elbil(String merke, String lader, String batteri) {
        this.merke = merke;
        this.lader = lader;
        this.batteri = batteri;
    }

    public String getMerke() {
        return merke;
    }

    public String getLader() {
        return lader;
    }

    public String getBatteri() {
        return batteri;
    }
}
