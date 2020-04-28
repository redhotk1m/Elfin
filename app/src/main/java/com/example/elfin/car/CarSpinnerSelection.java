package com.example.elfin.car;

import android.view.View;
import android.widget.Spinner;

import com.example.elfin.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CarSpinnerSelection {

    private CarSelectionActivity carSelectionActivity;
    private CarFilteredList carFilteredList;

    private List<Elbil> allElbilList;

    private List<String> filteredList;

    private List<String> spinnerBrands, spinnerModels, spinnerModelYears;

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
            System.out.println("ALL ELBIL LIST IS NULL!");
            allElbilList = new ArrayList<>();
        } else {
            System.out.println("#####################################################################");
            System.out.println("(CAR SPINNER SELECTION CLASS) SELECTION ELBILS LIST SIZE: " + allElbilList.size());
            System.out.println("#####################################################################");
        }

        selectedMap = carSelectionActivity.getFieldMap();
        System.out.println("SELECTED MAP: " + selectedMap);

        if (selectedMap != null) {
            selectedBrand = selectedMap.get(BRAND);
            selectedModel = selectedMap.get(MODEL);
            //todo: handle in CarSerachActivity to check if selected model year exists in database
            // selectedModelYear = selectedMap.get(MODELYEAR);
            selectedModelYear = "";
            selectedBattery = selectedMap.get(BATTERY);
            // selectedEffect = selectedMap.get(FASTCHARGE);
            selectedEffect = "";
        } else {
            selectedBrand = "";
            selectedModel = "";
            selectedModelYear = "";
            selectedBattery = "";
            selectedEffect = "";
        }

        System.out.println("SELECTED FIELDS: "
                + "\nSELECTED BRAND: " + selectedBrand
                + "\nSELECTED MODEL: " + selectedModel
                + "\nSELECTED MODEL YEAR: " + selectedModelYear
                + "\nSELECTED BATTERY: " + selectedBattery
                + "\nSELECTED FAST CHARGE: " + selectedEffect);

        System.out.println("-----------------------------------------------------------------------"
                + "\nEMPTY FIELD: ");
        if (selectedBrand.isEmpty()) System.out.println("BRAND FIELD IS EMPTY!");
        if (selectedModel.isEmpty()) System.out.println("MODEL FIELD IS EMPTY");
        if (selectedModelYear.isEmpty()) System.out.println("MODEL_YEAR FIELD IS EMPTY");
        if (selectedBattery.isEmpty()) System.out.println("BATTERY FIELD IS EMPTY!");
        if (selectedEffect.isEmpty()) System.out.println("EFFECT FIELD IS EMPTY!");

        // selectedBrand = carSelectionActivity.get


        //Todo: use carFilteredList to get filtered lists
        carFilteredList = new CarFilteredList();
    }

    protected void missingFieldSelection(Spinner spinner, String dataField, List<String> filteredList) {
        switch (dataField) {

        }
    }

    protected void filteredCarsSelection(Spinner spinner, String dataField, List<String> filteredList) {
        // allElbilList = addCarActivity.getAllCars();
        ArrayList<Elbil> filteredCars = new ArrayList<>();
        filteredList.clear();
        switch (dataField) {
            case BRAND:
                // filteredList.add(getString(R.string.choose_brand));
                System.out.println("SPINNER CARS LIST SIZE: " + allElbilList.size());
                for (Elbil elbil : allElbilList) {
                    if (!filteredList.contains(elbil.getBrand()))
                        filteredList.add(elbil.getBrand());
                }
                System.out.println("SPINNER FILTERED LIST: " + filteredList.size());

                // addCarActivity.setBrands(filteredList);

                break;
            case MODEL:
                for (Elbil elbil : allElbilList) {
                    if (elbil.getBrand().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getModel()))
                        filteredList.add(elbil.getModel());
                }
                // addCarActivity.setModels(filteredList);
                break;
            case MODELYEAR:
                for (Elbil elbil : allElbilList) {
                    if (elbil.getModel().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getModelYear()))
                        filteredList.add(elbil.getModelYear());
                }
                //  Collections.sort(filteredList);
                filteredList.add(spinnerFields[1]);
                // addCarActivity.setModelYears(filteredList);
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
                // Collections.sort(filteredList);
                // filteredList.add(spinnerFields[1]);
                break;
            case FASTCHARGE:
                // filteredList.add(getString(R.string.choose_model_year));
                for (Elbil elbil : allElbilList) {
                    if (elbil.getBattery().equals(spinner.getSelectedItem()))
                        filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    //if (!filteredList.contains(elbil.getEffect()))
                    filteredList.add(elbil.getFastCharge() + " " + elbil.getEffect());
                }
                // Collections.sort(filteredList);
                // filteredList.add(spinnerFields[1]);
                break;
            default:
                System.out.println("NOTHING TO FILTER..");
                // filteredList.add(getString(R.string.choose_nothing));
        }
        Collections.sort(filteredList);
        if (filteredList.size() > 0) filteredList.add(spinnerFields[0]);
        setSpinnerSelection(spinner, filteredList);
    }


    protected void setSpinnerSelection(Spinner spinner, List<String> spinnerList) {
        // spinner.setSelection(list.size());
        spinner.setSelection(spinnerList.size() - 1); // - 1
        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
    }

    protected void spinnerOnItemSelected(String dataField, Spinner thisSpinner, Spinner nextSpinenr,
                                         List<String> spinnerList, boolean manualSelection) {
        switch (dataField) {
            case BRAND:
                System.out.println("SpinnerBrands OnItemSelected!");

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
                   // carSelectionActivity.disableSpinner(FASTCHARGE);
                }


                break;
            case FASTCHARGE:
                System.out.println("(CAR SPINNER SELECTION CLASS) FAST CHARGE SPINNER ON ITEM SELECTED");
                break;
            default:
                System.out.println("No SPINNER OnItemSelected");

        }
    }


    /*
    private void matchingCarsFound(String dataField, List<String> foundMatches) {
        switch (dataField) {
            case BRAND:
                if (!brand.isEmpty()) {
                    System.out.println("FOUND EXACT BRAND: " + brand);      //exact brand found
                    //todo: return found brand
                } else if (!foundMatches.isEmpty()) {
                    System.out.println("FOUND MATCHING BRANDS: " + foundMatches);    //matching models found
                    //todo: return list of possible brands for selection
                } else System.out.println("NO MATCHING BRANDS FOUND...");
                break;
            case MODEL:
                if (!model.isEmpty()) {
                    //todo: return found model
                    System.out.println("FOUND EXACT MODEL: " + model);    //exact model found
                } else if (!foundMatches.isEmpty()) {
                    //todo: return list of possible models for selection
                    System.out.println("FOUND MATCHING MODELS: " + foundMatches);    //matching models found
                } else System.out.println("NO MATCHING MODELS FOUND...");
                break;
            case BATTERY:
                if (!battery.isEmpty()) {
                    //todo: return found brand
                    System.out.println("FOUND EXACT BATTERY: " + battery);  //exact battery found
                } else if (!foundMatches.isEmpty()) {
                    //todo: return list of possible brands for selection
                    System.out.println("FOUND MATCHING BATTERIES: " + foundMatches); //matching batteries found
                } else System.out.println("NO MATCHING BATTERIES FOUND...");
                break;
            default:
                System.out.println("NO SUCH DATA FIELD TO MATCH");
        }
    }
    */


    protected void getFilteredCars(Spinner spinner, String dataField, List<String> filteredList, boolean manualSelection) {
//        allElbilList = addCarActivity.getAllCars();
        ArrayList<Elbil> filteredCars = new ArrayList<>();
        filteredList.clear();
        switch (dataField) {
            case BRAND:
                if (!selectedBrand.isEmpty()) filteredList.add(selectedBrand);
                else {
                    // filteredList.add(getString(R.string.choose_brand));
                    System.out.println("SPINNER CARS LIST SIZE: " + allElbilList.size());
                    for (Elbil elbil : allElbilList) {
                        if (!filteredList.contains(elbil.getBrand()))
                            filteredList.add(elbil.getBrand());
                    }
                    System.out.println("SPINNER FILTERED LIST: " + filteredList.size());
                    setSpinnerSelection(spinner, filteredList);
                }
                break;
            case MODEL:
                if (!selectedModel.isEmpty()) filteredList.add(selectedModel);
                else {
                    // filteredList.add(getString(R.string.choose_model));
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
                    // filteredList.add(getString(R.string.choose_model_year));
                    for (Elbil elbil : allElbilList) {
                        if (elbil.getModel().equals(spinner.getSelectedItem()))
                            filteredCars.add(elbil);
                    }
                    for (Elbil elbil : filteredCars) {
                        if (!filteredList.contains(elbil.getModelYear()))
                            filteredList.add(elbil.getModelYear());
                    }
                }
                // Collections.sort(filteredList);
                // filteredList.add(spinnerFields[1]);
                break;
            case BATTERY:
                if (!selectedBattery.isEmpty()) filteredList.add(selectedBattery);
                else {
                    // filteredList.add(getString(R.string.choose_model_year));
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
                // Collections.sort(filteredList);
                // filteredList.add(spinnerFields[1]);
                break;
            case FASTCHARGE:
                if (!selectedEffect.isEmpty()) filteredList.add(selectedEffect);
                else {
                    // filteredList.add(getString(R.string.choose_model_year));
                    for (Elbil elbil : allElbilList) {
                        if (elbil.getBattery().equals(spinner.getSelectedItem()))
                            filteredCars.add(elbil);
                    }
                    for (Elbil elbil : filteredCars) {
                        if (!filteredList.contains(elbil.getEffect()))
                            filteredList.add(elbil.getFastCharge() + " " + elbil.getEffect());
                    }
                }
                // Collections.sort(filteredList);
                // filteredList.add(spinnerFields[1]);
                break;
            default:
                System.out.println("NOTHING TO FILTER..");
                // filteredList.add(getString(R.string.choose_nothing));
        }
        Collections.sort(filteredList);
        // if (manualSelection && filteredList.size() > 0) filteredList.add(spinnerFields[0]);
        if (filteredList.size() > 0) filteredList.add(spinnerFields[0]);
    }
}
