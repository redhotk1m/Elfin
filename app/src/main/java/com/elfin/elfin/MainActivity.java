package com.elfin.elfin;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.API.NobilAPIHandler;
import com.elfin.elfin.API.RetrieveJSON;
import com.elfin.elfin.Activities.Station.ChargingStations;
import com.elfin.elfin.Activities.Station.StationList.ChargerItem;
import com.elfin.elfin.Utils.App;
import com.elfin.elfin.Utils.AsyncResponse;
import com.elfin.elfin.Utils.ChargersForRoute;
import com.elfin.elfin.Utils.EditTextFunctions;
import com.elfin.elfin.Utils.GPSTracker;
import com.elfin.elfin.adapter.CarAdapter;
import com.elfin.elfin.car.CarInfoActivity;
import com.elfin.elfin.car.CarSearchActivity;
import com.elfin.elfin.car.Elbil;
import com.elfin.elfin.car.SharedCarPreferences;
import com.elfin.elfin.listener.SpinnerInteractionListener;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public EditText editText;
    public DisplaySuggestions displaySuggestions;
    public ListView listViewSuggest;
    public ArrayAdapter<String> arrayAdapterSuggestions;
    String destinationID;
    public ArrayList<ChargerItem> allChargingStations;
    private boolean chargingStationsFound = false;
    public TextView destinacionTextView;
    public String destionacionValidacion;
    public Boolean isSelected = false;

    TextView headerText;

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

    /**
     * This is the main class, where the app starts.
     * Here we immediately fetch all the chargingStations from the NobilAPI to save time,
     * since calculating all the charging stations along a route, and calculation the route
     * takes enough time on it's own.
     * Generally lots of code here to setup how the start activity works, and how it handles
     * certain events.
     * Gets all the cars the users have added, and puts them into the spinner.
     * Handles creating the correct classes and etc for when a user searches for a location
     * Handles when a user wants to drive to that location, or when the user wants
     * to add / change the current car.
     * @param savedInstanceState
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dropdown = findViewById(R.id.chooseCar);
        headerText = findViewById(R.id.headerText);
        listViewSuggest = findViewById(R.id.listViewSuggest);
        listViewSuggest.setVisibility(View.INVISIBLE);

        imageButton = findViewById(R.id.imageButtonDriveNow);
        editText = findViewById(R.id.editTextToAPlace);
        editText.setCursorVisible(false);

        //get added cars from shared preferences to be displayed in dropdown spinner
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        getSharedCarPreferences(sharedPreferences);
        initCarSpinner();

        destinacionTextView = findViewById(R.id.textViewFyllIn);
        destinacionTextView.setVisibility(View.INVISIBLE);


        EditTextFunctions editTextFunctions = new EditTextFunctions(this, isSelected);
        editTextFunctions.setText();

        //Henter JSON fra Nobil APIet
        RetrieveJSON a = new RetrieveJSON(this, NobilAPIHandler.class);
        String key = getString(R.string.nobil_use);
        a.execute("https://nobil.no/api/server/datadump.php?apikey=" + key + "&file=false&countrycode=NOR&format=json");
        a = null;
        //Lager en broadcastmanager som mottar JSON fra API ved ferdig utførelse.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("allStations"));

        gpsTracker = new GPSTracker(this);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("case");
            if ("error".equals(message))
                System.out.println("error i main receiver");
                //TODO: Error message to user
            else {
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
    }


    private void initCarSpinner() {
        mCarAdapter = new CarAdapter(this, mCarList);

        dropdown.setSelection(0, false);
        dropdown.setAdapter(mCarAdapter);

        interactionListener = new SpinnerInteractionListener(this);
        dropdown.setOnTouchListener(interactionListener);
        dropdown.setOnItemSelectedListener(interactionListener);

        mSelectedCar = (Elbil) dropdown.getSelectedItem();
        initDropDown = true;
    }

    public void performSpinnerClick(final Elbil clickedElbil, int click) {
        if (mCarList.size() == 1) {
            String display = mSelectedCar.getSpinnerDisplay();
            checkAddCarDisplay(display);
        } else if (click == 0) {
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dropdown.performClick();
                }
            }, 500);
        } else if (click == 1) {
            selectSpinnerItemByValue(dropdown, clickedElbil);
            if (currentSpinnerItem) getSelectedCar(dropdown);
        }
    }

    private void selectSpinnerItemByValue(Spinner spinner, Elbil selectedElbil) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(selectedElbil)) {
                currentSpinnerItem = dropdown.getSelectedItemPosition() == spinner.getItemIdAtPosition(i);
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
        mSelectedCar = (Elbil) dropdown.getSelectedItem();

        String display = mSelectedCar.getSpinnerDisplay();
        checkAddCarDisplay(display);


        if (onLongClicked && !getString(R.string.add_car).equals(display)) {
            showPopup(view, mSelectedCar);
            onLongClicked = false;
        } else {
            onLongClicked = false;
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
            getSelectedCar(dropdown);
        }
    }

    private void showPopup(View view, final Elbil elbil) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item1:
                        Intent intent = new Intent(MainActivity.this, CarInfoActivity.class);
                        intent.putExtra("Elbil", elbil);
                        intent.putExtra("CarInfo", true);
                        startActivity(intent);
                        return true;
                    case R.id.item2:
                        mCarList.remove(elbil);
                        mCarList.remove(mCarList.size() - 1);
                        sharedCarPreferences = new SharedCarPreferences();
                        sharedCarPreferences.updateSavedCars(sharedPreferences, mCarList);
                        getSharedCarPreferences(sharedPreferences);
                        return true;
                    case R.id.item3:
                        sharedCarPreferences = new SharedCarPreferences();
                        sharedCarPreferences.clearSharedPreferences(sharedPreferences);
                        getSharedCarPreferences(sharedPreferences);
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
            if (editText.getText().toString().length() > 0) {
                destinacionTextView.setText("Velg en gyldig adresse");
            }
        }

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

        if (mCarList.size() != savedCarsSize) getSharedCarPreferences(sharedPreferences);
    }
}
