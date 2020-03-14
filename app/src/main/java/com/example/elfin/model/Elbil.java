package com.example.elfin.model;

public class Elbil {

    private String Merke, Hurtiglader, Batterikapasitet;


    public Elbil(){

    }

    public String getMerke() {
        return Merke;
    }

    public void setMerke(String merke) {
        Merke = merke;
    }

    public String getHurtiglader() {
        return Hurtiglader;
    }

    public void setHurtiglader(String hurtiglader) {
        Hurtiglader = hurtiglader;
    }

    public String getBatterikapasitet() {
        return Batterikapasitet;
    }

    public void setBatterikapasitet(String batterikapasitet) {
        Batterikapasitet = batterikapasitet;
    }
}
