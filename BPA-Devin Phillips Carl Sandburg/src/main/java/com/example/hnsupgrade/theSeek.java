package com.example.hnsupgrade;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.gson.Gson;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.lang.Math.random;

public class theSeek extends AppCompatActivity implements OnMapReadyCallback {
    private boolean hintsUnlocked = false;
    private FirebaseFirestore mDB;
    private boolean notInCircle = true;
    Location currentLocation;
    private DocumentReference dr;
    private boolean hotAndColdUsed= false;
    private boolean getDistanceFromCenter= false;
    final Handler mHandler = new Handler();
    private boolean getLatitude= false;
    private boolean getLongitude= false;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private boolean onThisScreen = true;
    private gameInfo games;
    TextView hint;
    MarkerOptions markerOptions2;
    CircleOptions cn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seeking);
        mDB = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Gson gson = new Gson();
        games = gson.fromJson(getIntent().getStringExtra("games"), gameInfo.class);
        DocumentReference dr = mDB.collection("Games").document(games.getLobbyName());
        dr.set(games);
        getDatabaseInfo();
        circleCheckChecker();
        getHintButton();
        startMap();
        checkIfGameIsOver();
        hint = findViewById(R.id.hintsText);
    }

    private void checkIfGameIsOver(){
        DocumentReference dr = mDB.collection("Games").document(games.getLobbyName());
        dr.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                getDatabaseInfo();
                if(games.isHiderFound()){
                    Intent intent = new Intent(theSeek.this, finalScreen.class);
                    intent.putExtra("games",games);
                    startActivity(intent);

                }


            }


        });




    }



    private void returnDatabaseHint(final String hintName){
        //sets delay(3sec) to check database then sets a delay(1sec) to make sure that all the info was waited for
        getDatabaseInfo();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getDatabaseInfo();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        hint.setText(hintName +" : "+ games.getLastHintUsed());
                    }
                }, 3000);
            }
        }, 1000);
    }

    private void getHintButton() {
        Button b = findViewById(R.id.getHintsButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hintsUnlocked) {
                    int RandomNum = (int) (Math.floor(Math.random() * 4));
                    if (RandomNum == 0 && !hotAndColdUsed) {
                        getLastLocation();
                        games.setLastHintUsed("HotOrCold");
                        games.setSeekerLocation(currentLocation.getLatitude() + ","+ currentLocation.getLongitude());
                        DocumentReference dr = mDB.collection("Games").document(games.getLobbyName());
                        dr.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {

                                hotAndColdUsed = true;
                                returnDatabaseHint("Hot Or Cold");

                            }


                        });
                    }
                    if (RandomNum == 1 && !getDistanceFromCenter) {
                        games.setLastHintUsed("DistanceFromCenter");
                        DocumentReference dr = mDB.collection("Games").document(games.getLobbyName());
                        dr.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {

                                hotAndColdUsed = true;
                                returnDatabaseHint("Distance From Center");

                            }


                        });
                        getDistanceFromCenter = true;
                    }

                    if (RandomNum == 2 && !getLatitude) {
//Latitude
                        games.setLastHintUsed("Latitude");
                        DocumentReference dr = mDB.collection("Games").document(games.getLobbyName());
                        dr.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {

                                hotAndColdUsed = true;
                                returnDatabaseHint("Latitude");

                            }


                        });
                        getLatitude = true;
                    }
                    if (RandomNum == 3 && !getLongitude) {
//Longitude
                        games.setLastHintUsed("Longitude");
                        DocumentReference dr = mDB.collection("Games").document(games.getLobbyName());
                        dr.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {

                                hotAndColdUsed = true;
                                returnDatabaseHint("Longitude");

                            }


                        });
                        getLongitude = true;
                    }


                }
                else {
                    Toast.makeText(theSeek.this, "Not Able To Use Hints(may have used all the hints or have hints disabled)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //ready with new location catcher
    public double rad(double x){
        return x * Math.PI / 180;
    }

    private Runnable mRunnable;
    private void circleCheckChecker(){
        Log.d("googleMap", "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                inCircleChecker();
                mHandler.postDelayed(mRunnable, 5000);
            }
        }, 5000);
    }
    private void inCircleChecker(){
        Log.e("circleChecker","inCircleChecker");
        //check every one sec
        getLastLocation();
        if (currentLocation != null){

            String circleLatLng = games.getCircleCenterPicked();
            Log.e("circleChecker",circleLatLng);
            String[] latlong =  circleLatLng.split(",");
            double circleLatitude = Double.parseDouble(latlong[0]);
            double circleLongitude = Double.parseDouble(latlong[1]);
            double R = 6378137; // Earth’s mean radius in meter
            double dLat = rad(currentLocation.getLatitude() - circleLatitude);
            double dLong = rad(currentLocation.getLongitude() - circleLongitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(circleLatitude)) * Math.cos(rad(currentLocation.getLongitude())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c;
            double dInMiles = d /1609.34;
            if(dInMiles<games.getCircleSize()){
                games.setHasSeekerReachedCircle(true);
                notInCircle = false;
                // if settings say that hints are allowed then let them use hints
                if(games.isHints()) {
                    hintsUnlocked = true;
                }
                dr.set(games);
            }
        }



    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        getLastLocation();
        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You");
        MarkerOptions searchZoneCenter = new MarkerOptions().position(toLatLng(games.getCircleCenterPicked())).title("Search Zone Center");
        CircleOptions searchZone = new CircleOptions().center(toLatLng(games.getCircleCenterPicked())).radius(1609.34*games.getCircleSize()).strokeColor(Color.RED).fillColor(Color.GREEN);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        googleMap.addMarker(markerOptions);
        googleMap.addMarker(searchZoneCenter);
        googleMap.addCircle(searchZone);
        final Button b = findViewById(R.id.locationMachine);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(theSeek.this,"Location Updater activated",Toast.LENGTH_SHORT);
                startUserLocationsRunnable(googleMap);
                b.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void getLastLocation() {
        Log.d("googleMap", "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    currentLocation = location;
                }
            }
        });

    }
    private Runnable zRunnable;
    private void startUserLocationsRunnable(final GoogleMap googleMap){
        Log.d("googleMap", "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(zRunnable = new Runnable() {
            @Override
            public void run() {
                updateTillOffScreen(googleMap);
                mHandler.postDelayed(zRunnable, 3000);
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(zRunnable);
    }

    private void updateTillOffScreen(GoogleMap googleMap){
        getLastLocation();

        googleMap.clear();
        Log.e("googleMap","mapclear");
        //options for overlays
        markerOptions2 = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).title("You");
        Log.e("googleMap","optionsadding");
        cn = new CircleOptions().center(toLatLng(games.getCircleCenterPicked())).radius(1609.34 * games.getCircleSize()).strokeWidth(3f).strokeColor(Color.RED);
        Log.e("googleMap","overlayadding");
        googleMap.addCircle(cn);
        googleMap.addMarker(markerOptions2);
        //check if things are updating correctly
        Log.e("currentLocation",new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()).toString());
        Log.e("circleOptions",cn.getCenter().toString());
        Log.e("markerOptions",markerOptions2.getPosition().toString());
        Log.e("googleMap","updateTillOffScreen");

    }
    //gets location sets it to map after checking permissions
    private void startMap() {

        Log.e("Logger", "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    currentLocation = location;
                    Log.e("Logger", "onComplete: latitude: " + location.getLatitude());
                    Log.e("Logger", "onComplete: longitude: " + location.getLongitude());
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(theSeek.this);
                }


            }
        });

    }
    private LatLng toLatLng(String coordinates){
        String[] data = coordinates.split(",");
        return new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
    }
    private void getDatabaseInfo(){
        dr = mDB.collection("Games").document(games.getLobbyName());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                document.toObject(gameInfo.class);

            }
        });
    }
}
