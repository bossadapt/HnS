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
import android.widget.EditText;
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
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

public class singleGameGame extends AppCompatActivity implements OnMapReadyCallback {
    placeInfo pi;
    TextView hints;
    EditText guessPortion;
    Button guessPortionButton;
    Button setLiveLocationButton;
    Button getHintButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    MarkerOptions markerOptions2;
    CircleOptions cn;
    boolean latUsed=false;
    boolean lonUsed=false;
    boolean rateUsed=false;
    boolean totalRateUsed=false;
    boolean hoursUsed=false;
    int hintTotal;
    int guessTotal;
    int min;
    double raz;
    double raz2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamegame);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Gson gson = new Gson();
        pi = gson.fromJson(getIntent().getStringExtra("pi"), placeInfo.class);
        hints = findViewById(R.id.hintTextField);
        guessPortion = findViewById(R.id.guessPortionText);
        guessPortionButton = findViewById(R.id.tryPortion);
        setLiveLocationButton = findViewById(R.id.setLiveLocation);
        getHintButton = findViewById(R.id.hintButton);
        String temp = spaceRemover(pi.getName());
        if(temp.length()/2>10){
            min = 10;
        }
        else if (temp.length()>8) {
         min = temp.length()/2;
        }else if(temp.length()>4) {
             min = 4;
        }else{
           min = temp.length();

        }

        guessPortion.setHint("Guess The Place(min"+min+")");
        guessPortionButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               comparePortionToAnswer();
           }
       });
       getHintButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            hints.setText(hintGiver());
           }
       });

        startMap();
    }
    private String spaceRemover(String string){
        string = string.replaceAll("\\s","");
        return string;
    }
    private void comparePortionToAnswer(){
String guess = guessPortion.getText().toString();
if(guess.length()>min) {
    guess = guess.substring(0, min);
    if (containsIgnoreCase(pi.getName(),guess)) {
        Intent intent = new Intent(singleGameGame.this, singleGameFinal.class);
        Gson gson = new Gson();
        String myJson = gson.toJson(pi);
        intent.putExtra("pi", myJson);
        intent.putExtra("hintTotal",hintTotal);
        intent.putExtra("guessTotal",guessTotal);
        startActivity(intent);
    }
    else{
        guessTotal++;
        Toast.makeText(singleGameGame.this,"Incorrect Answer",Toast.LENGTH_SHORT).show();
    }
}
else{
    Toast.makeText(singleGameGame.this,"Guess has to be at least "+min+" characters long",Toast.LENGTH_SHORT).show();
}

    }
    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

private String hintGiver(){
        int counter = 0;

    if(!pi.getLatitude().isEmpty()&!latUsed){
        counter ++;
    }
    if(!pi.getLongitude().isEmpty()&!lonUsed){
        counter ++;
    }

    if(!pi.getRating().isEmpty()&!rateUsed){
        counter ++;
    }
    if(!pi.getUserRating().isEmpty()&!totalRateUsed){
        counter ++;
    }

    if(!pi.getOpenHours().isEmpty()&!hoursUsed){
        counter ++;
    }
    int counter2 = 0;
    String[] hints = new String[counter];
    int latX= -1;
    int lonX= -1;
    int rateX= -1;
    int hintX= -1;
    int hourX = -1;

    if(!pi.getLatitude().isEmpty()&!latUsed){
        hints[counter2]="the place's latitude is " + spaceRemover(pi.getLatitude());
         latX = counter2;
        counter2 ++;
    }
    if(!pi.getLongitude().isEmpty()&!lonUsed){
        hints[counter2]="the place's longitude is " + spaceRemover(pi.getLongitude());
         lonX = counter2;
        counter2 ++;
    }

    if(!pi.getRating().isEmpty()&!rateUsed){
        hints[counter2]="this destination is rated: "+spaceRemover(pi.getRating());
         rateX = counter2;
        counter2 ++;
    }
    if(!pi.getUserRating().isEmpty()&!totalRateUsed){
        hints[counter2]=spaceRemover(pi.getUserRating())+" people rated this destination";
         hintX = counter2;
        counter2 ++;
    }

    if(!pi.getOpenHours().isEmpty()&!hoursUsed){
        hints[counter2]="the destination open hours are:\n"+pi.getOpenHours();
         hourX = counter2;

    }
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return "Error no location permission";
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
    //converting LatLng circle to doubles
    String seekerLatLng = (spaceRemover(pi.getLatitude()))+","+spaceRemover(pi.getLongitude());
    String[] latlong =  seekerLatLng.split(",");
    double circleLatitude = Double.parseDouble(latlong[0]);
    double circleLongitude = Double.parseDouble(latlong[1]);
    getLastLocation();
    //p1 = circle center
    //p2 = current location
    double R = 6378137; // Earth’s mean radius in meter
    double dLat = rad(currentLocation.getLatitude() - circleLatitude);
    double dLong = rad(currentLocation.getLongitude() - circleLongitude);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(circleLatitude)) * Math.cos(rad(currentLocation.getLongitude())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = R * c;
    double dInMiles = d * 0.000621371;
    String output = "you are COLD";
    double hotDouble = 5/4;
    double warmDouble = 5/2;

    if(dInMiles<=(hotDouble)){
        output = "you are HOT";
    }
    if(dInMiles<=(warmDouble)){
        output = "you are WARM";
    }


int ran = (int)(Math.random()*counter2);
hintTotal++;
    if(ran == latX){
        latUsed=true;
    }
    else if(ran == lonX){
        lonUsed=true;
    }
    else if(ran == rateX){
        rateUsed = true;
    }
    else if(ran == hintX){
        totalRateUsed = true;
    }
    else if(ran == hourX){
        hoursUsed = true;
    }

if(hints.length==0){
    return output;
}
else {
    return hints[ran];
}
}
    public double rad(double x){
        return x * Math.PI / 180;
    }
    private void startMap() {

        Log.e("Logger", "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    currentLocation = location;
                    Log.e("Logger", "onComplete: latitude: " + location.getLatitude());
                    Log.e("Logger", "onComplete: longitude: " + location.getLongitude());
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(singleGameGame.this);
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
    public void onMapReady(GoogleMap googleMap) {
//current location latlng
        final GoogleMap gmaps = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//animate camera
        gmaps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        gmaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You");
          raz = (Math.random()*10)/100;
          raz2 = (Math.random()*10)/100;
         double lat = Double.parseDouble(spaceRemover(pi.getLatitude()))+raz;
         double lon = Double.parseDouble(spaceRemover(pi.getLongitude()))+raz2;
        CircleOptions cs = new CircleOptions().center(new LatLng(lat,lon)).radius(4828).strokeWidth(3f).strokeColor(Color.RED);
        //add overlays
        gmaps.addCircle(cs);
        gmaps.addMarker(markerOptions);
        setLiveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(singleGameGame.this,"Location Updater activated",Toast.LENGTH_SHORT).show();
                startUserLocationsRunnable(gmaps);
                setLiveLocationButton.setVisibility(View.INVISIBLE);
            }
        });
    }
    Handler mHandler = new Handler();
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
    private void updateTillOffScreen(GoogleMap googleMap){
        getLastLocation();

        googleMap.clear();
        Log.e("googleMap","mapclear");
        //options for overlays
        cn = new CircleOptions().center(new LatLng((Double.parseDouble(spaceRemover(pi.getLatitude()))+raz),(Double.parseDouble(spaceRemover(pi.getLongitude())))+raz2)).radius(4828).strokeWidth(3f).strokeColor(Color.RED);
        markerOptions2 = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).title("You");
        Log.e("googleMap","optionsadding");

       //add overlays
        Log.e("googleMap","overlayadding");
        googleMap.addMarker(markerOptions2);
        googleMap.addCircle(cn);
        //check if things are updating correctly


    }
}
