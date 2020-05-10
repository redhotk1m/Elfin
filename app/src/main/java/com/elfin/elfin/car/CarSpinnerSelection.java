package com.elfin.elfin.car;

import android.view.View;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CarSpinnerSelection {

    private CarSelectionActivity carSelectionActivity;
    private CarFilteredList carFilteredList;

    private List<Elbil> allElbilList;

    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";


    private HashMap<String, String> selectedMap;
    private String selectedBrand, selectedModel, selectedModelYear, selectedBattery, selectedEffect;

    private String[] spinnerPrompts = {"Velg bilmerke", "Velg bilmodell", "Velg årsmodell",
            "Velg batterikapasitet", "Velg ladehastighet"};
    private String[] spinnerFields = {"(INGEN VALGT)", "---VET IKKE---"};


    public CarSpinnerSelection(CarSelectionActivity carSelectionActivity) {
        this.carSelectionActivity = carSelectionActivity;
        allElbilList = carSelectionActivity.getAllCars();

        if (allElbilList == null) {
            allElbilList = new ArrayList<>();
        }

        selectedMap = carSelectionActivity.getFieldMap();

        if (selectedMap != null) {
            selectedBrand = selectedMap.get(BRAND);
            selectedModel = selectedMap.get(MODEL);
            //todo: handle in CarSerachActivity to check if selected model year exists in database
            // selectedModelYear = selectedMap.get(MODELYEAR);
            selectedModelYear = "";
            selectedBattery = selectedMap.get(BATTERY);
            selectedEffect = "";
        } else {
            selectedBrand = "";
            selectedModel = "";
            selectedModelYear = "";
            selectedBattery = "";
            selectedEffect = "";
        }

        //Todo: use carFilteredList to get filtered lists
        carFilteredList = new CarFilteredList();
    }

    protected void filteredCarsSelection(Spinner spinner, String dataField, List<String> filteredList) {
        ArrayList<Elbil> filteredCars = new ArrayList<>();
        filteredList.clear();
        switch (dataField) {
            case BRAND:
                for (Elbil elbil : allElbilList) {
                    if (!filteredList.contains(elbil.getBrand()))
                        filteredList.add(elbil.getBrand());
                }
                break;
            case MODEL:
                for (Elbil elbil : allElbilList) {
                    if (elbil.getBrand().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getModel()))
                        filteredList.add(elbil.getModel());
                }
                break;
            case MODELYEAR:
                for (Elbil elbil : allElbilList) {
                    if (elbil.getModel().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getModelYear()))
                        filteredList.add(elbil.getModelYear());
                }
                filteredList.add(spinnerFields[1]);
                break;
            case BATTERY:
                for (Elbil elbil : allElbilList) {
                    if (elbil.getModelYear().equals(spinner.getSelectedItem()))
                        filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getBattery()))
                        filteredList.add(elbil.getBattery());
                }
                break;
            case FASTCHARGE:
                for (Elbil elbil : allElbilList) {
                    if (elbil.getBattery().equals(spinner.getSelectedItem()))
                        filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    filteredList.add(elbil.getFastCharge() + " " + elbil.getEffect());
                }
                break;
            default:
        }
        Collections.sort(filteredList);
        if (filteredList.size() > 0) filteredList.add(spinnerFields[0]);
        setSpinnerSelection(spinner, filteredList);
    }


    protected void setSpinnerSelection(Spinner spinner, List<String> spinnerList) {
        if (spinnerList.size() <= 2) spinner.setSelection(0);
        else spinner.setSelection(spinnerList.size() - 1); // - 1
        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
    }

    protected void spinnerOnItemSelected(String dataField, Spinner thisSpinner, Spinner nextSpinenr,
                                         List<String> spinnerList, boolean manualSelection) {
        switch (dataField) {
            case BRAND:
                if (thisSpinner.getSelectedItem().equals(spinnerPrompts[0])
                        || thisSpinner.getSelectedItem().equals(spinnerFields[0])) {
                    carSelectionActivity.disableSpinner(MODEL);
                } else {
                    getFilteredCars(thisSpinner, MODEL, spinnerList, manualSelection);
                    setSpinnerSelection(nextSpinenr, spinnerList);
                    carSelectionActivity.disableSpinner(MODELYEAR);
                }

                break;
            case MODEL:

                if (thisSpinner.getSelectedItem().equals(spinnerPrompts[1])
                        || thisSpinner.getSelectedItem().equals(spinnerFields[0])) {
                    carSelectionActivity.disableSpinner(MODELYEAR);
                } else {
                    getFilteredCars(thisSpinner, MODELYEAR, spinnerList, manualSelection);
                    setSpinnerSelection(nextSpinenr, spinnerList);
                    carSelectionActivity.disableSpinner(BATTERY);
                }

                break;
            case MODELYEAR:

                if (thisSpinner.getSelectedItem().equals(spinnerPrompts[2])
                        || thisSpinner.getSelectedItem().equals(spinnerFields[0])) {
                    carSelectionActivity.disableSpinner(BATTERY);
                } else {
                    getFilteredCars(thisSpinner, BATTERY, spinnerList, manualSelection);
                    setSpinnerSelection(nextSpinenr, spinnerList);
                    carSelectionActivity.disableSpinner(FASTCHARGE);
                }

                break;
            case BATTERY:
                if (thisSpinner.getSelectedItem().equals(spinnerPrompts[3])
                        || thisSpinner.getSelectedItem().equals(spinnerFields[0])) {
                    carSelectionActivity.disableSpinner(FASTCHARGE);
                } else {
                    getFilteredCars(thisSpinner, FASTCHARGE, spinnerList, manualSelection);
                    setSpinnerSelection(nextSpinenr, spinnerList);
                }

                break;
            case FASTCHARGE:

               break;
            default:

        }
    }

    protected void getFilteredCars(Spinner spinner, String dataField, List<String> filteredList, boolean manualSelection) {
        ArrayList<Elbil> filteredCars = new ArrayList<>();
        filteredList.clear();
        switch (dataField) {
            case BRAND:
                if (!selectedBrand.isEmpty()) filteredList.add(selectedBrand);
                else {
                    for (Elbil elbil : allElbilList) {
                        if (!filteredList.contains(elbil.getBrand()))
                            filteredList.add(elbil.getBrand());
                    }
                    setSpinnerSelection(spinner, filteredList);
                }
                break;
            case MODEL:
                if (!selectedModel.isEmpty()) filteredList.add(selectedModel);
                else {
                    for (Elbil elbil : allElbilList) {
                        if (elbil.getBrand().equals(spinner.getSelectedItem()))
                            filteredCars.add(elbil);
                    }
                    for (Elbil elbil : filteredCars) {
                        if (!filteredList.contains(elbil.getModel()))
                            filteredList.add(elbil.getModel());
                    }
                }
                break;
            case MODELYEAR:
                if (!selectedModelYear.isEmpty()) filteredList.add(selectedModelYear);
                else {
                    for (Elbil elbil : allElbilList) {
                        if (elbil.getModel().equals(spinner.getSelectedItem()))
                            filteredCars.add(elbil);
                    }
                    for (Elbil elbil : filteredCars) {
                        if (!filteredList.contains(elbil.getModelYear()))
                            filteredList.add(elbil.getModelYear());
                    }
                }
                break;
            case BATTERY:
                if (!selectedBattery.isEmpty()) filteredList.add(selectedBattery);
                else {
                    //todo: check if equals brand and model too
                    for (Elbil elbil : allElbilList) {
                        if (elbil.getModelYear().equals(spinner.getSelectedItem()))
                            filteredCars.add(elbil);
                    }
                    for (Elbil elbil : filteredCars) {
                        if (!filteredList.contains(elbil.getBattery()))
                            filteredList.add(elbil.getBattery());
                    }
                }
                break;
            case FASTCHARGE:
                if (!selectedEffect.isEmpty()) filteredList.add(selectedEffect);
                else {
                    for (Elbil elbil : allElbilList) {
                        if (elbil.getBattery().equals(spinner.getSelectedItem()))
                            filteredCars.add(elbil);
                    }
                    for (Elbil elbil : filteredCars) {
                        if (!filteredList.contains(elbil.getFastCharge() + " " + elbil.getEffect()))
                            filteredList.add(elbil.getFastCharge() + " " + elbil.getEffect());
                    }
                }
                break;
            default:
        }
        Collections.sort(filteredList);
        if (filteredList.size() > 0) filteredList.add(spinnerFields[0]);
    }
}
