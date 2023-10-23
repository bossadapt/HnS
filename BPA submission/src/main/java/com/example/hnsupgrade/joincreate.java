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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class joincreate extends AppCompatActivity implements OnMapReadyCallback {
    private gameInfo games;
    MarkerOptions markerOptions2;
    private FirebaseFirestore mDB;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private boolean onThisScreen = false;
    String circleCenterPicked;
    boolean circlePicked;
    String lobbyID;
    private boolean hiderReady;
    private final static long UPDATE_INTERVAL = 2000;
    private final static long FASTEST_INTERVAL = 1000;
    private String lobbyName;
    private LocationCallback locationCallback;
    DocumentReference dr;
    GoogleMap googleMapOuter;
    final Handler mHandler = new Handler();
    private GoogleMap googleMap;
    CircleOptions cn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joincreator);

        mDB = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        lobbyID = getIntent().getStringExtra("lobbyName");
        getDatabaseInfo();
        lobbyName = getIntent().getStringExtra("lobbyName");
        onThisScreen = true;
        startMap();
        startGameButton();
        circleCenterPickerButton();
    }

    private void circleCenterPickerButton() {
        Button b = findViewById(R.id.circleMaker);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleCenterPicked = currentLocation.getLatitude() +"," + currentLocation.getLongitude();
                circlePicked = true;
            }
        });
    }

    private void startGameButton() {
        Button b = findViewById(R.id.startGame);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(circlePicked = false){
                    Toast.makeText(joincreate.this, "Circle Center Not Picked Yet", Toast.LENGTH_SHORT).show();
                }else{
                    hiderReady = true;
                    games.setCircleCenterPicked(circleCenterPicked);
                    games.setHiderReady(hiderReady);
                    DocumentReference docRef = mDB.collection("Games").document(lobbyName);
                    docRef.set(games);
                    mHandler.removeCallbacks(mRunnable);
                    Intent intent = new Intent(joincreate.this, theHide.class);
                    intent.putExtra("lobbyName",lobbyName);
                    startActivity(intent);
                }
            }
        });


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
                    supportMapFragment.getMapAsync(joincreate.this);
                }


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

    @Override
    public void onMapReady(final GoogleMap googleMap) {
//current location latlng
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//animate camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        //options for overlays
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You");
        CircleOptions cs = new CircleOptions().center(latLng).radius(1609.34 * games.getCircleSize()).strokeWidth(3f).strokeColor(Color.RED);
        //add overlays
        googleMap.addCircle(cs);
        googleMap.addMarker(markerOptions);
        final Button b = findViewById(R.id.locationUpdater);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(joincreate.this,"Location Updater activated",Toast.LENGTH_SHORT);
                startUserLocationsRunnable(googleMap);
                b.setVisibility(View.INVISIBLE);
            }
        });
    }
    private Runnable mRunnable;
    private void startUserLocationsRunnable(final GoogleMap googleMap){
        Log.d("googleMap", "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                updateTillOffScreen(googleMap);
                mHandler.postDelayed(mRunnable, 3000);
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateTillOffScreen(GoogleMap googleMap){
        getLastLocation();

        googleMap.clear();
        Log.e("googleMap","mapclear");
        //options for overlays
        markerOptions2 = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).title("You");
        Log.e("googleMap","optionsadding");

        if(!circlePicked) {
            cn = new CircleOptions().center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).radius(1609.34 * games.getCircleSize()).strokeWidth(3f).strokeColor(Color.RED).fillColor(Color.GREEN);
        }
        else{
            cn = new CircleOptions().center(toLatLng(circleCenterPicked)).radius(1609.34 * games.getCircleSize()).strokeWidth(3f).strokeColor(Color.RED).fillColor(Color.GREEN);

        }//add overlays
        Log.e("googleMap","overlayadding");
        googleMap.addCircle(cn);
        googleMap.addMarker(markerOptions2);
        //check if things are updating correctly
        Log.e("currentLocation",new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()).toString());
        Log.e("circleOptions",cn.getCenter().toString());
        Log.e("markerOptions",markerOptions2.getPosition().toString());
        Log.e("googleMap","updateTillOffScreen");

    }


    private LatLng toLatLng(String coordinates) {
        String[] data = coordinates.split(",");
        return new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
    }

    private void getDatabaseInfo(){
        dr = mDB.collection("Games").document(lobbyID);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                games = document.toObject(gameInfo.class);
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

}
