package com.example.hnsupgrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    //created by Devin Phillips from Carl Sandburg HighSchool
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mForegroundGranted = false;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS =9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final int ERROR_DIALOG_REQUEST= 9001;
    public static final String TAG = "HnS; ";
    //checking permissions and downloads - https://gist.github.com/mitchtabian/2b9a3dffbfdc565b81f8d26b25d059bf
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    private void startLocationService(){
        Log.e(TAG,"StartlocationService");
        if(!isLocationServiceRunning()){
            Log.e(TAG,"location service is not running");
            Intent serviceIntent = new Intent(MainActivity.this, locationGetter.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                Log.e(TAG,"new boi ran");
                MainActivity.this.startForegroundService(serviceIntent);
            }else{
                Log.e(TAG,"old boi ran");
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        boolean output=false;
        Log.e(TAG,"isLocationServiceRunning?");
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.hnsupgrade.test.locationGetter".equals(service.service.getClassName())) {
                Log.e(TAG, "isLocationServiceRunning: location service is already running.");
                output= true;
            }
        }
        Log.e(TAG,"isLocationServiceRunning: "+ output);
        return output;
    }


    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    private void getForegroundService(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.FOREGROUND_SERVICE)
                == PackageManager.PERMISSION_GRANTED){
            mForegroundGranted = true;
        }else{
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.FOREGROUND_SERVICE)
                    != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(MainActivity.this, "You need to run FOREGROUND ACTIVITIES", Toast.LENGTH_LONG).show();

        }
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()) {
            choseButton();
            startLocationService();
        }
        else{
            getLocationPermission();
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    choseButton();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }
    private void choseButton() {
        Button named = findViewById(R.id.gameStart);
        named.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChosenActivity();

            }
        });
        Button b = findViewById(R.id.singlePlayerB);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChosenActivity2();
            }
        });
    }
    //opens map spot picker
    private void openChosenActivity() {
        Intent intent = new Intent(this, choseSide.class);
        startActivity(intent);
    }
    private void openChosenActivity2() {
        Intent intent = new Intent(this, whatToSearch.class);
        startActivity(intent);
    }

}
