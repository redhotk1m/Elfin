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
        // spinnerBrands = addCarActivity.getBrands();
        // spinnerModels = addCarActivity.getModels();
        // spinnerModelYears = addCarActivity.getModelYears();
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

    protected List<String> initSpinnerList(Spinner spinner, List<String> spinnerList, String dataField) {
        if (spinnerList == null) spinnerList = new ArrayList<>();
        System.out.println("INIT SPINNERLIST" + spinnerList + " ; " + spinnerList.size());
        spinnerList.clear();
        switch (dataField) {
            case BRAND:
                System.out.println("INIT BRANDS");
                // spinnerList.add(spinnerPrompts[0]);
                // spinnerList.add(spinnerFields[1]);
             //   getFilteredCars(spinner, dataField, spinnerList);
                // setSpinnerSelection(spinner, spinnerList);
                addCarActivity.setBrands(spinnerList);
                break;
            case MODEL:
                System.out.println("INIT MODELS");
                // spinnerList.add(spinnerPrompts[1]);
             //   getFilteredCars(spinner, dataField, spinnerList);
                addCarActivity.setModels(spinnerList);
                break;
            case MODELYEAR:
                System.out.println("INIT MODEL YEARS");
                //  spinnerList.add(spinnerPrompts[2]);
            //    getFilteredCars(spinner, dataField, spinnerList);
                addCarActivity.setModelYears(spinnerList);
                break;
            case BATTERY:
                System.out.println("INIT BATTERIES");
                // spinnerList.add(spinnerPrompts[3]);
              //  getFilteredCars(spinner, dataField, spinnerList);
                addCarActivity.setBatteries(spinnerList);
                break;
            case FASTCHARGE:
                System.out.println("INIT FASTCHARGE EFFECTS");
                // spinnerList.add(spinnerPrompts[4]);
                addCarActivity.setFastCharges(spinnerList);
                break;
            default:
                System.out.println("NO SPINNER LIST");
        }
        return spinnerList;
    }


    protected void disableSpinner(Spinner spinner, List<String> list, String dataField) {
        switch (dataField) {
            case BRAND:
               // initSpinnerList(spinner, list, BRAND);
               // spinner.setVisibility(View.VISIBLE);
               // spinner.setEnabled(false);
                break;
            case MODEL:
                initSpinnerList(spinner, list, MODEL);
               // spinner.setEnabled(false);
                spinner.setVisibility(View.GONE);
                break;
            case MODELYEAR:
                initSpinnerList(spinner, list, MODELYEAR);
               // spinner.setEnabled(false);
                spinner.setVisibility(View.GONE);
                break;
            case BATTERY:
                initSpinnerList(spinner, list, BATTERY);
               // spinner.setEnabled(false);
                spinner.setVisibility(View.GONE);
                break;
            case FASTCHARGE:
                initSpinnerList(spinner, list, FASTCHARGE);
               // spinner.setEnabled(false);
                spinner.setVisibility(View.GONE);
                break;
            default:
                System.out.println("NO SPINNERS TO DISABLE");
        }
    }

    protected void setSpinnerSelection(Spinner spinner, List<String> spinnerList) {
        // spinner.setSelection(list.size());
        spinner.setSelection(spinnerList.size() - 1); // - 1
        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
    }


    protected void spinnerBrandOnItemSelected(Spinner spinnerBrands, Spinner spinnerModels, Spinner spinnerModelYears, Spinner spinnerBatteries, Spinner spinnerCharges,
                                              List<String> brands, List<String> models, List<String> modelYears, List<String> batteries, List<String> fastCharges) {
        if (spinnerBrands.getSelectedItem().equals(spinnerPrompts[0])
                || spinnerBrands.getSelectedItem().equals(spinnerFields[0])) {
            //disableSpinner(MODEL);
            setSpinnerSelection(spinnerBrands, brands);
            disableSpinner(spinnerModels, models, MODEL);
            disableSpinner(spinnerModelYears, modelYears, MODELYEAR);
            disableSpinner(spinnerBatteries, batteries, BATTERY);
            disableSpinner(spinnerCharges, fastCharges, FASTCHARGE);
        } else {
           // filteredCarsSelection(spinnerBrands, MODEL, models);
            getFilteredCars(spinnerBrands, MODEL, models);
            setSpinnerSelection(spinnerModels, models);
        }
    }

    protected void spinnerModelsOnItemSelected(Spinner spinnerModels, Spinner spinnerModelYears, Spinner spinnerBatteries, Spinner spinnerCharges,
                                               List<String> modelYears, List<String> batteries, List<String> fastCharges) {
        if (spinnerModels.getSelectedItem().equals(spinnerPrompts[1])
                || spinnerModels.getSelectedItem().equals(spinnerFields[0])) {
            disableSpinner(spinnerModelYears, modelYears, MODELYEAR);
            disableSpinner(spinnerBatteries, batteries, BATTERY);
            disableSpinner(spinnerCharges, fastCharges, FASTCHARGE);
        } else {
           // filteredCarsSelection(spinnerModels, MODELYEAR, modelYears);
            getFilteredCars(spinnerModels, MODELYEAR, modelYears);
            setSpinnerSelection(spinnerModelYears, modelYears);
        }
    }

    protected void spinnerModelYearsOnItemSelected(Spinner spinnerModelYears, Spinner spinnerBatteries, Spinner spinnerCharges,
                                                   List<String> batteries, List<String> fastCharges) {
        if (spinnerModelYears.getSelectedItem().equals(spinnerPrompts[2])
                || spinnerModelYears.getSelectedItem().equals(spinnerFields[0])) {
            disableSpinner(spinnerBatteries, batteries, BATTERY);
            disableSpinner(spinnerCharges, fastCharges, FASTCHARGE);
        } else {
           // filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
            getFilteredCars(spinnerModelYears, BATTERY, batteries);
            setSpinnerSelection(spinnerBatteries, batteries);
        }
    }

    protected void spinnerBatteriesOnItemSelected(Spinner spinnerBatteries, Spinner spinnerCharges,
                                                  List<String> charges) {
        if (spinnerBatteries.getSelectedItem().equals(spinnerPrompts[3])
                || spinnerBatteries.getSelectedItem().equals(spinnerFields[0])) {
            disableSpinner(spinnerCharges, charges, FASTCHARGE);
        } else {
          //  filteredCarsSelection(spinnerBatteries, FASTCHARGE, charges);
            getFilteredCars(spinnerBatteries, FASTCHARGE, charges);
            setSpinnerSelection(spinnerCharges, charges);
        }
    }

    protected void spinnerChargesOnItemSelected(Spinner spinnerCharges, List<String> charges) {

        if (spinnerCharges.getSelectedItem().equals(spinnerPrompts[4])
                || spinnerCharges.getSelectedItem().equals(spinnerFields[0])) {
            disableSpinner(spinnerCharges, charges, FASTCHARGE);
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
                // spinner.setSelection(filteredList.size());
                // spinner.setEnabled(true);
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
                    if (elbil.getBattery().equals(spinner.getSelectedItem())) filteredCars.add(elbil);
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
    }
}
