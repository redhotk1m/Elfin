package com.example.elfin.car;

import java.util.ArrayList;
import java.util.List;

public class CarFilteredList {
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private List<Elbil> filteredElbils;
    private List<String> filteredFieldsList;


    public List<String> filterFields(String dataField, String matchingField, List<String> filteredList) {
        if (dataField.contains(matchingField) && !filteredList.contains(matchingField))
            filteredList.add(matchingField);

        return filteredList;
    }

    public String filterExactMatch(String dataField, String matchingField) {
        if (dataField.equals(matchingField)) { //&& dataField.length() == matchingField.length()
            //  System.out.println("DATA FIELD: " + dataField + " EQUALS " + matchingField +
            //        " ; LENGTH: " + dataField.length() + " == " + matchingField.length());
            dataField = matchingField;
            // missing[0] = true;
        }

        return dataField;
    }

    public List<Elbil> filteredCars(List<Elbil> elbils, List<String> fields) {
        filteredElbils = new ArrayList<>();
        // filteredFieldsList = new ArrayList<>();

        //  System.out.println("ELBILS LIST SIZE; " + elbils.size());
        //  System.out.println("FIELDS LIST SIZE; " + fields.size());
        for (Elbil elbil : elbils) {
            switchFields(fields, elbil);
        }

        //  System.out.println("FILTERED ELBILS LIST SIZE; " + filteredElbils.size());
        // return filteredFieldsList;
        return filteredElbils;
    }

    private void switchFields(List<String> fields, Elbil elbil) {
        switch (fields.size()) {
            case 1:
                if (fields.get(0).equals(elbil.getBrand())) {
                    filteredElbils.add(elbil);
                }
                break;
            case 2:
                if (fields.get(0).equals(elbil.getBrand()) && fields.get(1).equals(elbil.getModel())) {
                    filteredElbils.add(elbil);
                }
                break;
            case 3:
                if (fields.get(0).equals(elbil.getBrand()) && fields.get(1).equals(elbil.getModel())
                        && fields.get(2).equals(elbil.getModelYear())) {
                    filteredElbils.add(elbil);
                }
                break;
            case 4:
                if (fields.get(0).equals(elbil.getBrand()) && fields.get(1).equals(elbil.getModel())
                        && fields.get(2).equals(elbil.getModelYear()) && fields.get(3).equals(elbil.getBattery())) {
                    filteredElbils.add(elbil);
                }
                break;
        }
    }
}
