package com.example.elfin.car;

import java.util.ArrayList;
import java.util.List;

public class CarFilteredList {
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";


    public List<String> filterFields(String dataField, String matchingField, List<String> filteredList) {
        if (dataField.contains(matchingField) && !filteredList.contains(matchingField))
            filteredList.add(matchingField);

        return filteredList;
    }

    public String filterExactMatch(String dataField, String matchingField) {
        if (dataField.equals(matchingField) && dataField.length() == matchingField.length()) {
            System.out.println("BRAND " + dataField + " EQUALS " + matchingField +
                    " ; LENGTH: " + dataField.length() + " == " + matchingField.length());
            dataField = matchingField;
           // missing[0] = true;
        }

        return dataField;
    }

    public List<Elbil> filteredCars(List<Elbil> filteredElbils) {


        return filteredElbils;
    }
}
