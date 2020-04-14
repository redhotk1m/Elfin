package com.example.elfin.car;

import android.view.View;
import android.widget.Spinner;

import com.example.elfin.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarSpinnerSelection {

    private AddCarActivity addCarActivity;

    private List<Elbil> allElbilList;

    private List<String> filteredList;

    private List<String> spinnerBrands, spinnerModels, spinnerModelYears;

    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private String[] spinnerPrompts = {"Velg bilmerke", "Velg bilmodell", "Velg årsmodell",
            "Velg batterikapasitet", "Velg ladehastighet"};
    private String[] spinnerFields = {"(INGEN VALGT)", "---VET IKKE---"};

    public CarSpinnerSelection(AddCarActivity addCarActivity) {
        this.addCarActivity = addCarActivity;
        allElbilList = addCarActivity.getAllCars();
    }

    protected void filteredCarsSelection(Spinner spinner, String dataField, List<String> filteredList) {
        allElbilList = addCarActivity.getAllCars();
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

                addCarActivity.setBrands(filteredList);

                break;
            case MODEL:
                for (Elbil elbil : allElbilList) {
                    if (elbil.getBrand().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getModel()))
                        filteredList.add(elbil.getModel());
                }
                addCarActivity.setModels(filteredList);
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
                addCarActivity.setModelYears(filteredList);
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




    protected void spinnerBrandOnItemSelected(Spinner spinnerBrands, Spinner spinnerModels,
                                              List<String> brands, List<String> models) {
        if (spinnerBrands.getSelectedItem().equals(spinnerPrompts[0])) {
            setSpinnerSelection(spinnerBrands, brands);
            addCarActivity.disableSpinner(MODEL);
        } else {
            // filteredCarsSelection(spinnerBrands, MODEL, models);
            getFilteredCars(spinnerBrands, MODEL, models);
            setSpinnerSelection(spinnerModels, models);
            addCarActivity.disableSpinner(MODELYEAR);
        }
    }

    protected void spinnerModelsOnItemSelected(Spinner spinnerModels, Spinner spinnerModelYears,
                                               List<String> modelYears) {
        if (spinnerModels.getSelectedItem().equals(spinnerPrompts[1])
                || spinnerModels.getSelectedItem().equals(spinnerFields[0])) {
            addCarActivity.disableSpinner(MODELYEAR);
        } else {
            // filteredCarsSelection(spinnerModels, MODELYEAR, modelYears);
            getFilteredCars(spinnerModels, MODELYEAR, modelYears);
            setSpinnerSelection(spinnerModelYears, modelYears);
            addCarActivity.disableSpinner(BATTERY);
        }
    }

    protected void spinnerModelYearsOnItemSelected(Spinner spinnerModelYears, Spinner spinnerBatteries,
                                                   List<String> batteries) {
        if (spinnerModelYears.getSelectedItem().equals(spinnerPrompts[2])
                || spinnerModelYears.getSelectedItem().equals(spinnerFields[0])) {
            addCarActivity.disableSpinner(BATTERY);
        } else {
            // filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
            getFilteredCars(spinnerModelYears, BATTERY, batteries);
            setSpinnerSelection(spinnerBatteries, batteries);
            addCarActivity.disableSpinner(FASTCHARGE);
        }
    }

    protected void spinnerBatteriesOnItemSelected(Spinner spinnerBatteries, Spinner spinnerCharges,
                                                  List<String> charges) {
        if (spinnerBatteries.getSelectedItem().equals(spinnerPrompts[3])
                || spinnerBatteries.getSelectedItem().equals(spinnerFields[0])) {

            // disableSpinner(spinnerCharges, charges, FASTCHARGE);
        } else {
            //  filteredCarsSelection(spinnerBatteries, FASTCHARGE, charges);
            getFilteredCars(spinnerBatteries, FASTCHARGE, charges);
            setSpinnerSelection(spinnerCharges, charges);
        }
    }

    protected void spinnerChargesOnItemSelected(Spinner spinnerCharges) {
        if (spinnerCharges.getSelectedItem().equals(spinnerPrompts[4])
                || spinnerCharges.getSelectedItem().equals(spinnerFields[0])) {
            addCarActivity.disableSpinner("spinner");
        } else {
            // getFilteredCars(spinnerBatteries, FASTCHARGE, charges);
            // setSpinnerSelection(spinnerCharges, charges);
        }
    }

    protected void getFilteredCars(Spinner spinner, String dataField, List<String> filteredList) {
        allElbilList = addCarActivity.getAllCars();
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
                setSpinnerSelection(spinner, filteredList);
                break;
            case MODEL:
                // filteredList.add(getString(R.string.choose_model));
                for (Elbil elbil : allElbilList) {
                    if (elbil.getBrand().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getModel()))
                        filteredList.add(elbil.getModel());
                }
                break;
            case MODELYEAR:
                // filteredList.add(getString(R.string.choose_model_year));
                for (Elbil elbil : allElbilList) {
                    if (elbil.getModel().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
                }
                for (Elbil elbil : filteredCars) {
                    if (!filteredList.contains(elbil.getModelYear()))
                        filteredList.add(elbil.getModelYear());
                }
                // Collections.sort(filteredList);
                // filteredList.add(spinnerFields[1]);
                break;
            case BATTERY:
                // filteredList.add(getString(R.string.choose_model_year));
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
                    if (!filteredList.contains(elbil.getEffect()))
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
    }
}
