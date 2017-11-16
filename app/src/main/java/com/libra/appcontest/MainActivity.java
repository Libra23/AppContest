package com.libra.appcontest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private final static String TAG = "MainActivity";

    // for http connection
    private HttpResponsAsync httpResponsAsync;
    // for fused gps
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private final static int REQUEST_CODE_LOCATION_PERMISSION = 1111;
    private final static int REQUEST_CODE_LOCATION_PERMISSION2 = 1112;
    private TextView latitudeText, longitudeText;
    private double longitude, latitude;
    private Button checkButton, gpsButton;
    private Switch gpsSwitch;

    private TextView idText;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //httpResponsAsync = new HttpResponsAsync();

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        }

        locationRequest = new LocationRequest()
                .setInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);;


        longitudeText = findViewById(R.id.longitude_text);
        latitudeText = findViewById(R.id.latitude_text);
        longitude = 0f;
        latitude = 0f;
        longitudeText.setText(String.valueOf(longitude));
        latitudeText.setText(String.valueOf(latitude));
        checkButton = findViewById(R.id.check_button);
        checkButton.setOnClickListener(this);
        gpsButton = findViewById(R.id.gps_button);
        gpsButton.setOnClickListener(this);
        gpsSwitch = findViewById(R.id.gps_switch);
        gpsSwitch.setOnCheckedChangeListener(this);
        idText = findViewById(R.id.id_text);
        id = 4;
        idText.setText(String.valueOf(id));
        //sendData(3, 100f,100f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.check_button:
                sendData();
                break;
            case R.id.gps_button:
                getGPS();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()) {
            case R.id.gps_switch:
                Toast.makeText(this, "Auto GPS is " + (b ? "on" : "off"),
                        Toast.LENGTH_SHORT).show();
                autoUpdate(b);
                break;
        }
    }

    private void sendData() {
        JSONObject jsonObject = new JSONObject();
        id = Integer.parseInt(idText.getText().toString());
        longitude = Double.parseDouble(longitudeText.getText().toString());
        latitude = Double.parseDouble(latitudeText.getText().toString());
        try{
            jsonObject.put("pole_id",id);
            jsonObject.put("longitude",longitude);
            jsonObject.put("latitude",latitude);

            httpResponsAsync = new HttpResponsAsync();
            httpResponsAsync.execute(jsonObject);

            Log.d(TAG,"JSON FINISH");
            Log.d(TAG,jsonObject.toString());
            //jsonView.setText(jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //sendLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "location is changed");
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            longitudeText.setText(String.valueOf(longitude));
            latitudeText.setText(String.valueOf(latitude));
            sendData();
            //sendData(id, longitude,latitude);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getGPS();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
            case REQUEST_CODE_LOCATION_PERMISSION2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    autoUpdate(gpsSwitch.isChecked());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    void getGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            longitude = lastLocation.getLongitude();
            latitude = lastLocation.getLatitude();
            longitudeText.setText(String.valueOf(longitude));
            latitudeText.setText(String.valueOf(latitude));
            //sendData(id, longitude,latitude);
        }
    }

    void autoUpdate(boolean b) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION2);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(b) {
            Log.d(TAG, "startLocationUpdate");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            Log.d(TAG, "stopLocationUpdate");
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }
}
