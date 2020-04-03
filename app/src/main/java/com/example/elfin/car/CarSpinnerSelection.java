package com.example.elfin.car;

import android.content.res.Resources;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.elfin.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarSpinnerSelection {

    private AddCarActivity addCarActivity;

    private List<Elbil> allElbilList;

    private List<String> spinnerList;

    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";

    private String[] spinnerPromt = {"Velg bilmerke", "Velg bilmodell", "Velg årsmodell", "---VET IKKE---"};

    public CarSpinnerSelection(AddCarActivity addCarActivity) {
        this.addCarActivity = addCarActivity;
        allElbilList = addCarActivity.getAllCars();
    }

    protected List<String> initSpinnerList(String dataField) {
        spinnerList = new ArrayList<>();
        switch (dataField) {
            case BRAND:
                System.out.println("INIT BRANDS");
                spinnerList.add(spinnerPromt[0]);
                break;
            case MODEL:
                System.out.println("INIT MODELS");
                spinnerList.add(spinnerPromt[1]);
                break;
            case MODELYEAR:
                System.out.println("INIT MODEL YEARS");
                spinnerList.add(spinnerPromt[2]);
                break;
            default:
                System.out.println("NO SPINNER LIST");
        }
        return spinnerList;
    }

    protected void disableSpinner(String dataField, Spinner spinner, List<String> list) {
        switch (dataField) {
            case BRAND:
                list.clear();
                list.add(spinnerPromt[0]);
                spinner.setEnabled(false);
            case MODEL:
                list.clear();
                list.add(spinnerPromt[1]);
                spinner.setEnabled(false);
                break;
            case MODELYEAR:
                list.clear();
                list.add(spinnerPromt[2]);
                spinner.setEnabled(false);
            default:
                System.out.println("NO SPINNERS TO DISABLE");
        }
    }

    protected void setSpinnerSelection(Spinner spinner, List<String> list) {
        spinner.setSelection(list.size());
        spinner.setEnabled(true);
    }


    protected void spinnerBrandOnItemSelected(Spinner spinnerBrands, Spinner spinnerModels, Spinner spinnerModelYears,
                                              List<String> models, List<String> modelYears) {
        if (spinnerBrands.getSelectedItem().equals(spinnerPromt[0])
                || spinnerBrands.getSelectedItem().equals(spinnerPromt[3])) {
            //disableSpinner(MODEL);
            disableSpinner(MODEL, spinnerModels, models);
            disableSpinner(MODELYEAR, spinnerModelYears, modelYears);
        } else {
            getFilteredCars(spinnerBrands, MODEL, models);
            setSpinnerSelection(spinnerModels, models);
        }
    }

    protected void spinnerModelsOnItemSelected(Spinner spinnerModels, Spinner spinnerModelYears, List<String> modelYears) {
        if (spinnerModels.getSelectedItem().equals(spinnerPromt[1])
                || spinnerModels.getSelectedItem().equals(spinnerPromt[3])) {
            disableSpinner(MODELYEAR, spinnerModelYears, modelYears);
        } else {
            getFilteredCars(spinnerModels, MODELYEAR, modelYears);
            setSpinnerSelection(spinnerModelYears, modelYears);
        }
    }

    protected void spinnerModelYearsOnItemSelected(Spinner spinnerModelYears, List<String> modelYears) {
        /*
        if (spinnerModelYears.getSelectedItem().equals(spinnerPromt[2])
                || spinnerModelYears.getSelectedItem().equals(spinnerPromt[3])) {
            disableSpinner(MODELYEAR, spinnerModelYears, modelYears);
        }
        else {
            getFilteredCars(spinnerModelYears, MODELYEAR, modelYears);
            setSpinnerSelection(spinnerModelYears, modelYears);
        }

         */

    }






    protected void spinnerOnItemSelected(String dataField,
                                         Spinner spinnerBrands, Spinner spinnerModels, Spinner spinnerModelYears,
                                         List<String> models, List<String> modelYears) {
        switch (dataField) {
            case BRAND:
                if (spinnerBrands.getSelectedItem().equals(spinnerPromt[0])
                        || spinnerBrands.getSelectedItem().equals(spinnerPromt[3])) {
                    //disableSpinner(MODEL);
                    disableSpinner(MODEL, spinnerModels, models);
                    disableSpinner(MODELYEAR, spinnerModelYears, modelYears);
                } else {
                    getFilteredCars(spinnerBrands, MODEL, models);
                    setSpinnerSelection(spinnerModels, models);
                }
                // adapterModels.notifyDataSetChanged();
                break;
            case MODEL:
                if (spinnerModels.getSelectedItem().equals(spinnerPromt[1])
                        || spinnerModels.getSelectedItem().equals(spinnerPromt[3])) {
                    disableSpinner(MODELYEAR, spinnerModelYears, modelYears);
                } else {
                    getFilteredCars(spinnerModels, MODELYEAR, modelYears);
                    setSpinnerSelection(spinnerModelYears, modelYears);
                }
                // adapterModelYears.notifyDataSetChanged();
                break;
            case MODELYEAR:
                break;
            default:
                System.out.println("NO SPINNER ITEM SELECTED");
        }
    }


    protected void getFilteredCars(Spinner spinner, String dataField, List<String> filteredList) {
        ArrayList<Elbil> filteredCars = new ArrayList<>();
        filteredList.clear();
        switch (dataField) {
            case BRAND:
                // filteredList.add(getString(R.string.choose_brand));
                for (Elbil elbil : allElbilList) {
                    if (!filteredList.contains(elbil.getBrand()))
                        filteredList.add(elbil.getBrand());
                }
                spinner.setSelection(filteredList.size());
                spinner.setEnabled(true);
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
                Collections.sort(filteredList);
                break;
            default:
                System.out.println("NOTHING TO FILTER..");
                // filteredList.add(getString(R.string.choose_nothing));
        }
        filteredList.add(spinnerPromt[3]);
    }

}
