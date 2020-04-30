package com.example.elfin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TimingLogger;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.API.NobilAPIHandler;
import com.example.elfin.API.RetrieveJSON;
import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.Utils.App;
import com.example.elfin.Utils.AsyncResponse;
import com.example.elfin.Utils.ChargersForRoute;
import com.example.elfin.Utils.EditTextFunctions;
import com.example.elfin.Utils.GPSTracker;
import com.example.elfin.adapter.CarAdapter;
import com.example.elfin.car.CarInfoActivity;
import com.example.elfin.car.CarSearchActivity;
import com.example.elfin.car.Elbil;
import com.example.elfin.car.SharedCarPreferences;
import com.example.elfin.listener.SpinnerInteractionListener;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Method;
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
    public ArrayList<ChargerItem> updatedCharginingStations;
    public ArrayList<ChargerItem> chargersForCar;

    TextView headerText;

    TimingLogger logger;
    public ImageButton imageButton;
    GPSTracker gpsTracker;

    private Elbil mSelectedCar;
    private ArrayList<Elbil> mCarList;
    private CarAdapter mCarAdapter;
    private Spinner dropdown;
    private SpinnerInteractionListener interactionListener;
    private SharedPreferences sharedPreferences;
    private SharedCarPreferences sharedCarPreferences;
    private boolean initDropDown, onLongClicked, currentSpinnerItem;

    Handler mHandler;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("KJØRER ONCREATE I MAINACTIVITY__@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // startActivity(new Intent(this, CarSearchActivity.class));

        dropdown = findViewById(R.id.chooseCar);
        headerText = findViewById(R.id.headerText);
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
        gpsTracker = new GPSTracker(this);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logger.addSplit("finished");
            logger.dumpToLog();
            String message = intent.getStringExtra("case");
            if ("error".equals(message))
                System.out.println("error i main receiver");
                //TODO: Error message to user
            else {
                System.out.println("MOTATT I MAIN");
                allChargingStations = ((App) getApplication()).getChargerItems();
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
            }
        }
    };


    public void closeKeyboard(View view) {
        //Brukes ofr å luke keyboardet
        InputMethodManager keyboardManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboardManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        listViewSuggest.setVisibility(View.INVISIBLE);
    }

    private void getSharedCarPreferences(SharedPreferences sharedPreferences) {
        sharedCarPreferences = new SharedCarPreferences();
        mCarList = sharedCarPreferences.getSavedCars(sharedPreferences);
        if (initDropDown) initCarSpinner();
        else System.out.println("INITIALIZING CAR SPINNER ; " + initDropDown);
    }


    private void initCarSpinner() {
        // if (mCarList.size() == 0) mCarList.add(new Elbil(R.drawable.ic_car_black_24dp, getString(R.string.choosenCar)));
        // mCarList.add(new Elbil(R.drawable.ic_add_box_black_24dp, getString(R.string.add_car)));
        mCarAdapter = new CarAdapter(this, mCarList);
      //  mCarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setSelection(0, false);
        dropdown.setAdapter(mCarAdapter);

        interactionListener = new SpinnerInteractionListener(this);
        dropdown.setOnTouchListener(interactionListener);
        dropdown.setOnItemSelectedListener(interactionListener);

        mSelectedCar = (Elbil) dropdown.getSelectedItem();


        System.out.println("(ON CREATE) SELECTED ELBIL: " + mSelectedCar.toString());

        //((App) getApplication()).setElbil(mSelectedCar);



        initDropDown = true;
    }

    public void performSpinnerClick(final Elbil clickedElbil, int click) {
        if (mCarList.size() == 1) {
            String display = mSelectedCar.getSpinnerDisplay();
            checkAddCarDisplay(display);
        } else if (click == 0) {
            System.out.println("(MAIN ACTIVITY) PERFORMING CLICK ON DROP DOWN");
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dropdown.performClick();
                }
            }, 500);
        } else if (click == 1) {
            System.out.println("(MAIN ACTIVITY) SELECT SPINNER CLICKED ELBIL: " + clickedElbil.toString());
            selectSpinnerItemByValue(dropdown, clickedElbil);
            if (currentSpinnerItem) getSelectedCar(dropdown);
            // registerForContextMenu(dropdown);
        }
    }

    private void selectSpinnerItemByValue(Spinner spinner, Elbil selectedElbil) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(selectedElbil)) {
                if (dropdown.getSelectedItemPosition() == spinner.getItemIdAtPosition(i)) {
                    System.out.println("SELECTED ITEM [@ CURRENT] POSITION: " + dropdown.getSelectedItemPosition() + " == " + spinner.getItemIdAtPosition(i));
                    currentSpinnerItem = true;
                } else {
                    System.out.println("SELECTED ITEM [@ DIFFERENT] POSITION: " + dropdown.getSelectedItemPosition() + " != " + spinner.getItemIdAtPosition(i));
                    currentSpinnerItem = false;
                }
                spinner.setSelection(i);
                interactionListener.setSelection(true);
                hideSpinnerDropDown(spinner);
                break;
            }
        }
    }

    private void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSelectedCar(View view) {
        System.out.println("(MAIN ACTIVITY) GET SELECTED CAR ; ON_LONG_CLICKED == " + onLongClicked);
        System.out.println("------------------------------------------------------------------------");
        mSelectedCar = (Elbil) dropdown.getSelectedItem();
        //((App) getApplication()).setElbil(mSelectedCar);
        System.out.println(mSelectedCar.getFastCharge());
        System.out.println(mSelectedCar.getBattery());
        System.out.println(mSelectedCar.getEffect());

        String display = mSelectedCar.getSpinnerDisplay();
        checkAddCarDisplay(display);


        if (onLongClicked && !getString(R.string.add_car).equals(display)) {
            System.out.println("ON ITEM LONG CLICKED ; " + onLongClicked);
            showPopup(view, mSelectedCar);
            onLongClicked = false;
        } else {
            System.out.println("ON ITEM LONG CLICKED ; " + onLongClicked);
            onLongClicked = false;
            System.out.println("ON ITEM LONG CLICKED ; " + onLongClicked);
        }
    }

    private void checkAddCarDisplay(String display) {
        if (getString(R.string.add_car).equals(display))
            startActivity(new Intent(this, CarSearchActivity.class));
    }

    public void setOnLongClicked(Elbil clickedElbil) {
        onLongClicked = true;
        selectSpinnerItemByValue(dropdown, clickedElbil);
        if (currentSpinnerItem) {
            System.out.println("CURRENT SPINNER ITEM ; " + currentSpinnerItem);
            getSelectedCar(dropdown);
        } else System.out.println("CURRENT SPINNER ITEM ; " + currentSpinnerItem);
    }

    private void showPopup(View view, final Elbil elbil) {
        System.out.println("(MAIN ACTIVITY) SHOWING & INFLATING POPUP CAR MENU");
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item1:
                       // Toast.makeText(MainActivity.this, "Item 1: Show Car clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, CarInfoActivity.class);
                        intent.putExtra("Elbil", elbil);
                        intent.putExtra("CarInfo", true);
                        startActivity(intent);
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
                      //  Toast.makeText(MainActivity.this, "Item 2: Delete Car clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item3:
                        sharedCarPreferences = new SharedCarPreferences();
                        sharedCarPreferences.clearSharedPreferences(sharedPreferences);
                        getSharedCarPreferences(sharedPreferences);
                      //  Toast.makeText(MainActivity.this, "Item 3: DELETE ALL CARS clicked", Toast.LENGTH_SHORT).show();
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
                //Arraylist av arraylist, en for navn og en for placeid
                for (int i = 0; i < lists.size(); i++) {
                    for (int j = 0; j < lists.get(i).size(); j++) {
                        if (i == 0) {
                            list.add(lists.get(i).get(j));
                        } else {
                            placeIdList.add(lists.get(i).get(j));
                        }
                    }
                }
                if (!isSelected) {
                    listViewSuggest.setVisibility(View.VISIBLE);
                } else {
                    listViewSuggest.setVisibility(View.INVISIBLE);
                    isSelected = false;
                }
                arrayAdapterSuggestions = new ArrayAdapter<>(getApplication().getBaseContext(), android.R.layout.simple_list_item_1, list);
                listViewSuggest.setAdapter(arrayAdapterSuggestions);
                if (listViewSuggest.getAdapter().getCount() <= 1) {
                    listViewSuggest.setVisibility(View.INVISIBLE);
                }

            }
        });
        displaySuggestions.execute();
        listViewSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                editText.setText(listViewSuggest.getItemAtPosition(position).toString());
                isSelected = true;
                setDestinationID(placeIdList.get(position));
                destionacionValidacion = editText.getText().toString();
                arrayAdapterSuggestions.clear();
                closeKeyboard(view);

            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            //Hvis man trykker enter lages det øverste valget i displaySuggestion listen.
            //Og viewene lukkes.
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    editText.setText(listViewSuggest.getItemAtPosition(0).toString());
                    destionacionValidacion = editText.getText().toString();
                    setDestinationID(placeIdList.get(0));
                    isSelected = true;
                    closeKeyboard(view);

                }
                return false;
            }
        });


    }

    public void setAdapterOnClickState(int state) {
        mCarAdapter.setOnClick(state);
    }


    public void setDestinationID(String destinationID) {
        this.destinationID = destinationID;
    }


    public void nextActivity(View view) {
        if (editText.getText().toString().equals(destionacionValidacion)) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                        , 0);
            } else {
                Intent intent = new Intent(this, ChargingStations.class);
                Bundle bundle = new Bundle();
                gpsTracker.getLocation();
                if (gpsTracker.canGetLocation()) {
                    ChargersForRoute chargersForRoute = new ChargersForRoute();
                    System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
                    ((App) getApplication()).setChargerItems(chargersForRoute.setChargerForCar(allChargingStations, mSelectedCar));
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
            if(editText.getText().toString().length()>0){
                destinacionTextView.setText("Velg en gyldig adresse");
            }
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
        int savedCarsSize = sharedCarPreferences.getSavedCars(sharedPreferences).size();
        if (mCarList.size() == savedCarsSize) {
            System.out.println("(ON RESUME MAIN ACTIVITY) NO NEW CHANGES TO SHARED CAR PREFERENCES: "
                    + mCarList.size() + " == " + savedCarsSize);
        } else {
            System.out.println("(ON RESUME MAIN ACTIVITY) CHANGES MADE TO SHARED CAR PREFERENCES: "
                    + mCarList.size() + " == " + savedCarsSize);
            getSharedCarPreferences(sharedPreferences);
        }
        System.out.println("(ON RESUME) CURRENT SELECTED CAR: " + mSelectedCar.toString() + " ; " +
                "\nSIZE [ " + savedCarsSize + " ]");
    }
}
