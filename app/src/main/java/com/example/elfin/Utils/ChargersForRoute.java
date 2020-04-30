package com.example.elfin.Utils;

import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.car.Elbil;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ChargersForRoute {

    public ArrayList<ChargerItem> setChargerForCar(ArrayList<ChargerItem> allChargingStations, Elbil mSelectedCar){
        ArrayList<ChargerItem>chargersForCar = new ArrayList<>();

        ArrayList<ChargerItem> chargerItemsTemp = new ArrayList<>(allChargingStations);
        double chargeTime = 0;
        int carEffect = 150;
        System.out.println(mSelectedCar.getEffect());

        if(mSelectedCar.getFastCharge() != null){
            chargeTime = Math.round(Double.parseDouble(mSelectedCar.getBattery())/50*60);
            String onlyInt = mSelectedCar.getEffect().replaceAll("\\s+","");
            onlyInt =  onlyInt.replaceAll("[^0-9]", "");

            carEffect = Integer.parseInt(onlyInt);
        }
        int chargeTimeMin = (int) chargeTime;
        String fastTime = "ca "  + chargeTimeMin + " min";
        double chargeTimeLight = 0;
        if(mSelectedCar.getFastCharge() != null){
            chargeTimeLight = Math.round(Double.parseDouble(mSelectedCar.getBattery())/150*60);
        }
        int chargeTimeMinLight = (int) chargeTimeLight;
        String fastTimeLight = "ca "  + chargeTimeMinLight + " min";

        for (ChargerItem chargerItem: chargerItemsTemp){
            LatLng latLng = chargerItem.getLatLng();
            String [] latlngArray = {""+latLng.latitude, ""+latLng.longitude};


            if("CHAdeMO".equals(mSelectedCar.getFastCharge())){
                if(chargerItem.getChademo().equals("CHAdeMO")){
                    ChargerItem chargerItem1 = new ChargerItem(chargerItem.getStreet(), chargerItem.getHouseNumber(),
                            chargerItem.getCity(), chargerItem.getDescriptionOfLocation(),
                            chargerItem.getOwnedBy(),
                            chargerItem.getUserComment(), chargerItem.getContactInfo(), latlngArray,
                            chargerItem.getChademo(), chargerItem.getNumberOfChademo(), fastTime, "",
                            "","", chargerItem.getImageFast(), 0, "", "",
                            "", chargerItem.getFastText(), "");
                    chargersForCar.add(chargerItem1);
                }
            }else if("CCS/Combo".equals(mSelectedCar.getFastCharge())){
                if(chargerItem.getCcs().equals("CCS/Combo")){
                    if(chargerItem.getLightningCCS().equals("")){
                        fastTimeLight = "";
                    } else {
                        fastTimeLight = "ca "  + chargeTimeMinLight + " min";
                    }
                    if(carEffect<150){
                        ChargerItem chargerItem1 = new ChargerItem(chargerItem.getStreet(), chargerItem.getHouseNumber(), chargerItem.getCity(),
                                chargerItem.getDescriptionOfLocation(), chargerItem.getOwnedBy(),
                                chargerItem.getUserComment(), chargerItem.getContactInfo(),latlngArray, "", "",
                                "", chargerItem.getCcs(), chargerItem.getNumberOfCcs(), fastTime,
                                chargerItem.getImageFast(), 0, "",
                                "", "", chargerItem.getFastText(), "");
                        chargersForCar.add(chargerItem1);
                    } else {
                        ChargerItem chargerItem1 = new ChargerItem(chargerItem.getStreet(), chargerItem.getHouseNumber(), chargerItem.getCity(),
                                chargerItem.getDescriptionOfLocation(), chargerItem.getOwnedBy(),
                                chargerItem.getUserComment(), chargerItem.getContactInfo(),latlngArray, "", "", "", chargerItem.getCcs(),
                                chargerItem.getNumberOfCcs(), fastTime, chargerItem.getImageFast(), chargerItem.getImageSlow(), chargerItem.getLightningCCS(),
                                chargerItem.getNumberOflightningCCS(), fastTimeLight, chargerItem.getFastText(),chargerItem.getLightningText());
                        chargersForCar.add(chargerItem1);
                    }

                }
                if(chargerItem.getCcs().equals("") && chargerItem.getLightningCCS().equals("CCS/Combo\n" + "350 kW DC") &&
                        carEffect>=350 ){
                    double chargeTimeLight2 = 0;
                    if(mSelectedCar.getFastCharge() != null){
                        chargeTimeLight2 = Math.round(Double.parseDouble(mSelectedCar.getBattery())/350*60);
                    }
                    int chargeTimeMinLight2 = (int) chargeTimeLight2;
                    String fastTimeLight2 = "ca "  + chargeTimeMinLight2 + " min";

                    ChargerItem chargerItem1 = new ChargerItem(chargerItem.getStreet(), chargerItem.getHouseNumber(), chargerItem.getCity(),
                            chargerItem.getDescriptionOfLocation(), chargerItem.getOwnedBy(),
                            chargerItem.getUserComment(), chargerItem.getContactInfo(),latlngArray, "", "", "", "",
                            chargerItem.getCcs(), chargerItem.getCcsTime(), 0, chargerItem.getImageSlow(), chargerItem.getLightningCCS(),
                            chargerItem.getNumberOflightningCCS(), fastTimeLight2, "",chargerItem.getLightningText());
                    chargersForCar.add(chargerItem1);
                }
                if(carEffect >= 150  && chargerItem.getCcs().equals("") &&
                        chargerItem.getLightningCCS().equals("CCS/Combo\n" + "150 kW DC")){
                    if(chargerItem.getLightningCCS().equals("")){
                        fastTimeLight = "";
                    } else {
                        fastTimeLight = "ca "  + chargeTimeMinLight + " min";
                    }
                    ChargerItem chargerItem1 = new ChargerItem(chargerItem.getStreet(), chargerItem.getHouseNumber(), chargerItem.getCity(),
                            chargerItem.getDescriptionOfLocation(), chargerItem.getOwnedBy(),
                            chargerItem.getUserComment(), chargerItem.getContactInfo(),latlngArray, "", "", "", "",
                            "", "", 0, chargerItem.getImageSlow(), chargerItem.getLightningCCS(),
                            chargerItem.getNumberOflightningCCS(), fastTimeLight, "",chargerItem.getLightningText());
                    chargersForCar.add(chargerItem1);
                }
                if(carEffect >= 150  && chargerItem.getCcs().equals("") &&
                        chargerItem.getLightningCCS().equals("CCS/Combo\n" + "+150 kW DC")){
                    if(chargerItem.getLightningCCS().equals("")){
                        fastTimeLight = "";
                    } else {
                        fastTimeLight = "ca "  + chargeTimeMinLight + " min";
                    }
                    ChargerItem chargerItem1 = new ChargerItem(chargerItem.getStreet(), chargerItem.getHouseNumber(), chargerItem.getCity(),
                            chargerItem.getDescriptionOfLocation(), chargerItem.getOwnedBy(),
                            chargerItem.getUserComment(), chargerItem.getContactInfo(),latlngArray, "", "", "", "",
                            "", "", 0, chargerItem.getImageSlow(), chargerItem.getLightningCCS(),
                            chargerItem.getNumberOflightningCCS(), fastTimeLight, "",chargerItem.getLightningText());
                    chargersForCar.add(chargerItem1);
                }
            }





            else {
                ChargerItem chargerItem1 = new ChargerItem(chargerItem.getStreet(), chargerItem.getHouseNumber(), chargerItem.getCity(),
                        chargerItem.getDescriptionOfLocation(), chargerItem.getOwnedBy(),
                        chargerItem.getUserComment(), chargerItem.getContactInfo(), latlngArray, chargerItem.getChademo(), chargerItem.getNumberOfChademo(), chargerItem.getChademoTime(), chargerItem.getCcs(),
                        chargerItem.getNumberOfCcs(), chargerItem.getCcsTime(), chargerItem.getImageFast(), chargerItem.getImageSlow(), chargerItem.getLightningCCS(),
                        chargerItem.getNumberOflightningCCS(), chargerItem.getLightningTime(), chargerItem.getFastText(),chargerItem.getLightningText());
                chargersForCar.add(chargerItem1);
            }



        }
        return chargersForCar;
    }
}
