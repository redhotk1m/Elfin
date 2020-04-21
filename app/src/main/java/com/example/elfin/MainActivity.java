package com.example.elfin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TimingLogger;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.API.NobilAPIHandler;
import com.example.elfin.API.RetrieveJSON;
import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.Utils.App;
import com.example.elfin.Utils.AsyncResponse;
import com.example.elfin.Utils.EditTextFunctions;
import com.example.elfin.Utils.GPSTracker;
import com.example.elfin.adapter.CarAdapter;
import com.example.elfin.car.CarSearchActivity;
import com.example.elfin.car.Elbil;
import com.example.elfin.car.SharedCarPreferences;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public EditText editText;
    public DisplaySuggestions displaySuggestions;
    //TextView textView;
    public ListView listViewSuggest;
    public ArrayAdapter<String> arrayAdapterSuggestions;
    String destinationID;
    public ArrayList<ChargerItem> allChargingStations;
    private boolean chargingStationsFound = false;
    public TextView destinacionTextView;
    public String destionacionValidacion;
    public Boolean isSelected = false;


    private ArrayList<Elbil> mCarList;
    private ArrayAdapter adapter;
    private Spinner dropdown;
    TimingLogger logger;
    public ImageButton imageButton;
    GPSTracker gpsTracker;

    private CarAdapter mCarAdapter;
    private SharedPreferences sharedPreferences;
    private SharedCarPreferences sharedCarPreferences;
    private boolean initDropDown;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("KJØRER ONCREATE I MAINACTIVITY__@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // startActivity(new Intent(this, CarSearchActivity.class));

        dropdown = findViewById(R.id.chooseCar);
        //textView = findViewById(R.id.textViewSuggest);
        listViewSuggest = findViewById(R.id.listViewSuggest);
        listViewSuggest.setVisibility(View.INVISIBLE);

        imageButton = findViewById(R.id.imageButtonDriveNow);
        editText = findViewById(R.id.editTextToAPlace);
        editText.setCursorVisible(false);

        //get added cars from shared preferences to be displayed in dropdown spinner
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        getSharedCarPreferences(sharedPreferences);
        initCarSpinner();

        //Intent intent = new Intent(this,AboutCharger.class);
        //startActivity(intent);
        destinacionTextView = findViewById(R.id.textViewFyllIn);
        destinacionTextView.setVisibility(View.INVISIBLE);


        EditTextFunctions editTextFunctions = new EditTextFunctions(this, isSelected);
        editTextFunctions.setText();


        /*
        //Hvis den skal funke
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Geolocation geolocation = new Geolocation();
                geolocation.getAdress(editText.getText().toString(), getApplicationContext(), new GeoHandler());

            }
        });
        */
        //swapview

        //Henter JSON fra Nobil APIet
        logger = new TimingLogger("MyTag", "MethodA");
        RetrieveJSON a = new RetrieveJSON(this, NobilAPIHandler.class);
        logger.addSplit("Retrieve Create");
        //countrycode=NOR&
        a.execute("https://nobil.no/api/server/datadump.php?apikey=64138b17020c3ab35706a48902171429&file=false&countrycode=NOR&format=json");
        logger.addSplit("Retrieve Execute");
        a = null;
        //Lager en broadcastmanager som mottar JSON fra API ved ferdig utførelse.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("allStations"));

        //Log.d("Debug2",new MainActivity().editText.getText().toString());
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logger.addSplit("finished");
            logger.dumpToLog();
            String message = intent.getStringExtra("case");
            if ("error".equals(message))
                System.out.println("error");
                //TODO: Error message to user
            else {
                System.out.println("MOTATT I MAIN");
                allChargingStations = ((App) getApplication()).getChargerItems();
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
            }
        }
    };

    public void closeKeyboard(View view) {
        InputMethodManager keyboardManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboardManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        listViewSuggest.setVisibility(View.INVISIBLE);


    }

    private void getSharedCarPreferences(SharedPreferences sharedPreferences) {
        sharedCarPreferences = new SharedCarPreferences();
        mCarList = sharedCarPreferences.getSavedCars(sharedPreferences);
        //todo: setIconImage and setSpinnerDisplay can be done in "Legg til bil" instead of here
        for (Elbil elbil : mCarList) {
            elbil.setIconImage(R.drawable.ic_car_black_24dp);
            //todo: fix elbil.toString()
            String display = elbil.getBrand() + " " + elbil.getModel() + " (" + elbil.getModelYear() + ")";
            elbil.setSpinnerDisplay(display);
        }
        initDropDown = true;
        initCarSpinner();
    }

    private void initCarSpinner() {
        // mCarList.add(new Elbil(R.drawable.ic_car_black_24dp, getString(R.string.choosenCar)));
        mCarList.add(new Elbil(R.drawable.ic_add_box_black_24dp, getString(R.string.add_car)));
        mCarAdapter = new CarAdapter(this, mCarList);

        dropdown.setSelection(0, false);
        dropdown.setAdapter(mCarAdapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener: \n" + adapterView.getItemAtPosition(position).toString(),
                        Toast.LENGTH_LONG).show();
                if (initDropDown) System.out.println("INIT SPINNER: " + initDropDown);
                else getSelectedCar(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        initDropDown = false;
    }

    private void getSelectedCar(View view) {
        Elbil elbil = (Elbil) dropdown.getSelectedItem();
        String display = elbil.getSpinnerDisplay();
        if (getString(R.string.add_car).equals(display))
            startActivity(new Intent(this, CarSearchActivity.class));
        else showPopup(view, elbil); //todo: remove selected position instead of elbil objekt
    }

    public void showPopup(View view, final Elbil elbil) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item1:
                        Toast.makeText(MainActivity.this, "Item 1: Show Car clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item2:
                        // mCarList.remove(elbil);
                        // System.out.println("SELECTED ELBIL BRAND: " + elbil.getBrand());
                        System.out.println("this mCarList: " + mCarList);
                        mCarList.remove(elbil);
                        mCarList.remove(mCarList.size() - 1);
                        System.out.println("removed mCarList: " + mCarList);
                        sharedCarPreferences = new SharedCarPreferences();
                        sharedCarPreferences.updateSavedCars(sharedPreferences, mCarList);
                        getSharedCarPreferences(sharedPreferences);
                        Toast.makeText(MainActivity.this, "Item 2: Delete Car clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item3:
                        sharedCarPreferences = new SharedCarPreferences();
                        sharedCarPreferences.clearSharedPreferences(sharedPreferences);
                        getSharedCarPreferences(sharedPreferences);
                        Toast.makeText(MainActivity.this, "Item 3: DELETE ALL CARS clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.popup_car_menu);
        popup.show();
    }

    public void displaySuggestions(String adress) {
        final ArrayList<String> placeIdList = new ArrayList<>();


        displaySuggestions = new DisplaySuggestions(getBaseContext(), adress, new AsyncResponse() {

            ArrayList<String> list = new ArrayList<>();


            @Override
            public void processFinish(ArrayList<ArrayList<String>> lists) {
                for (int i = 0; i < lists.size(); i++) {
                    for (int j = 0; j < lists.get(i).size(); j++) {
                        if (i == 0) {
                            list.add(lists.get(i).get(j));
                        } else {
                            placeIdList.add(lists.get(i).get(j));
                        }
                    }
                }

                System.out.println(isSelected);
                if(!isSelected){
                    listViewSuggest.setVisibility(View.VISIBLE);
                } else {
                    listViewSuggest.setVisibility(View.INVISIBLE);
                    isSelected=false;
                }
                arrayAdapterSuggestions = new ArrayAdapter<>(getApplication().getBaseContext(), android.R.layout.simple_list_item_1,list);
                listViewSuggest.setAdapter(arrayAdapterSuggestions);
                if(listViewSuggest.getAdapter().getCount()<=1){
                    listViewSuggest.setVisibility(View.INVISIBLE);
                }

            }
        });
        displaySuggestions.execute();
        listViewSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                editText.setText(listViewSuggest.getItemAtPosition(position).toString());
                isSelected=true;
                setDestinationID(placeIdList.get(position));
                destionacionValidacion = editText.getText().toString();
                arrayAdapterSuggestions.clear();
                closeKeyboard(view);

            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_DPAD_CENTER)){
                    editText.setText(listViewSuggest.getItemAtPosition(0).toString());
                    destionacionValidacion = editText.getText().toString();
                    setDestinationID(placeIdList.get(0));
                    isSelected=true;
                    closeKeyboard(view);
                }
                return false;
            }
        });




    }




    public void setDestinationID(String destinationID){
        this.destinationID = destinationID;
    }


    public void nextActivity(View view) {
        if (editText.getText().toString().equals(destionacionValidacion)) {
            Intent intent = new Intent(this, ChargingStations.class);
            Bundle bundle = new Bundle();
            gpsTracker = new GPSTracker(this);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                        , 0);
            } else {
                gpsTracker.getLocation();
                if (gpsTracker.canGetLocation()) {
                    ((App) getApplication()).setChargerItems(allChargingStations);
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                    bundle.putDouble("longditude", gpsTracker.getLongitude());
                    bundle.putDouble("latitude", gpsTracker.getLatitude());
                    bundle.putString("destinationID", destinationID);
                    bundle.putString("destinatinasjon", editText.getText().toString());
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
            }
        } else {
            destinacionTextView.setVisibility(View.VISIBLE);
        }

    }


    public ArrayList<ChargerItem> getAllChargingStations() {
        return allChargingStations;
    }


    public void setAllChargingStations(ArrayList<ChargerItem> allChargingStations) {
        this.allChargingStations = allChargingStations;
    }

    public void setChargingStationsFound(boolean found) {
        this.chargingStationsFound = found;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            gpsTracker.getLocation();
        } else {
            gpsTracker.popupMessageNeedPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedCarPreferences(sharedPreferences);
    }
}
