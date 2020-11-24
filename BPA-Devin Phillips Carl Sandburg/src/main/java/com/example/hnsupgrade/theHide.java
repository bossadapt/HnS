package com.example.hnsupgrade;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class theHide extends AppCompatActivity {
    private int hintCounter;
    private FirebaseFirestore mDB;
    DocumentSnapshot document;
    private DocumentReference dr;
    private gameInfo games;
    private Chronometer Chronometer;

    private boolean gameNotFinished;
    private boolean hasEnteredArea;
    private String timeCurrent;
    private static final int REQUEST_CODE = 101;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    TextView ruhRoh;
    TextView hintUsed;
    String lobbyName;
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hiding);
        gameNotFinished = true;
        mDB = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        lobbyName = getIntent().getStringExtra("lobbyName");
        getDatabaseInfo();
        Chronometer = findViewById(R.id.chronometer);
        ruhRoh = findViewById(R.id.seekerHasReachedCircle);
        hintUsed = findViewById(R.id.hintsUsed);
        ruhRoh.setText("Seeker Is on the way to the circle");
        hintUsed.setText("No hint used yet");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataChecker.run();
                locationChecker.run();
                iveBeenFoundButton();

            }
        }, 500);


    }




    private void iveBeenFoundButton() {
        Button b = findViewById(R.id.foundButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                games.setHiderFound(true);
                Intent intent = new Intent(theHide.this, finalScreen.class);
                games.setTotalHintsUsed(hintCounter);
                if(timeCurrent !=null) {
                    games.setTimeTaken(timeCurrent);
                }else{
                    games.setTimeTaken("The Hider hasn't Even Entered the area");
                }
                gameNotFinished = false;
                dr.set(games);
                intent.putExtra("lobbyName",lobbyName);
                startActivity(intent);
                mHandler.removeCallbacks(dataChecker);
                mHandler.removeCallbacks(locationChecker);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(dataChecker);
        mHandler.removeCallbacks(locationChecker);
    }

    private void setTimer(){
        Chronometer timeElapsed  = findViewById(R.id.chronometer);
        //code from https://stackoverflow.com/questions/4152569/how-to-change-format-of-chronometer/25391944
        timeElapsed.setOnChronometerTickListener(new android.widget.Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(hh+":"+mm+":"+ss);
                timeCurrent = hh+":"+mm+":"+ss;
            }
        });
        timeElapsed.setBase(SystemClock.elapsedRealtime());
        timeElapsed.start();

    }
    public double rad(double x){
        return x * Math.PI / 180;
    }
    private void getLocation() {
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
    private void checkIfHiderIsInCircle() {



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    currentLocation = location;
                    String circleLatLng = games.getCircleCenterPicked();
                    String[] latlong =  circleLatLng.split(",");
                    double circleLatitude = Double.parseDouble(latlong[0]);
                    double circleLongitude = Double.parseDouble(latlong[1]);
                    getLocation();
                    //p1 = circle center
                    //p2 = current location
                    double R = 6378137; // Earth’s mean radius in meter
                    double dLat = rad(currentLocation.getLatitude() - circleLatitude);
                    double dLong = rad(currentLocation.getLongitude() - circleLongitude);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(circleLatitude)) * Math.cos(rad(currentLocation.getLongitude())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    double d = R * c;
                    double dInMiles = d /1609.34;
                    if(dInMiles>games.getCircleSize()){
                        games.setHiderFound(true);
                        Intent intent = new Intent(theHide.this, finalScreen.class);
                        games.setTotalHintsUsed(hintCounter);
                        games.setTimeTaken("LEFT THE CIRCLE GAME OVER");
                        gameNotFinished = false;
                        dr.set(games);
                        startActivity(intent);
                    }
                }
            }
        });

    }
    public void getAndSendDistanceFromCenter(){
        //converting LatLng circle to doubles
        String circleLatLng = games.getCircleCenterPicked();
        String[] latlong =  circleLatLng.split(",");
        double circleLatitude = Double.parseDouble(latlong[0]);
        double circleLongitude = Double.parseDouble(latlong[1]);
        getLocation();
        //p1 = circle center
        //p2 = current location
        double R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(currentLocation.getLatitude() - circleLatitude);
        double dLong = rad(currentLocation.getLongitude() - circleLongitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(circleLatitude)) * Math.cos(rad(currentLocation.getLongitude())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        double dInFeet = d * 3.28084;
        games.setLastHintUsed((Double.toString(dInFeet)));
        dr.set(games);
        hintUsed.setText("The Seeker used a hint to see how far you are from the center of the circle");

    }
    public void LatitudeGetterAndSender(){
        getLocation();
        double lat = currentLocation.getLatitude();
        games.setLastHintUsed((Double.toString(lat)));
        dr.set(games);
        hintUsed.setText("The Seeker used a hint to see your Latitude");

    }
    public void LongitudeGetterAndSender(){
        getLocation();
        double lon = currentLocation.getLongitude();
        games.setLastHintUsed((Double.toString(lon)));
        dr.set(games);
        hintUsed.setText("The Seeker used a hint to see your Longitude");
    }
    public void hotOrCold(){
        //converting LatLng circle to doubles
        String seekerLatLng = games.getSeekerLocation().toString();
        String[] latlong =  seekerLatLng.split(",");
        double circleLatitude = Double.parseDouble(latlong[0]);
        double circleLongitude = Double.parseDouble(latlong[1]);
        getLocation();
        //p1 = circle center
        //p2 = current location
        double R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(currentLocation.getLatitude() - circleLatitude);
        double dLong = rad(currentLocation.getLongitude() - circleLongitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(circleLatitude)) * Math.cos(rad(currentLocation.getLongitude())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        double dInMiles = d * 0.000621371;
        String output = "COLD";
        if(dInMiles<=(1/4*games.getCircleSize())){
            output = "HOT";
        }
        if(dInMiles<=(1/2*games.getCircleSize())){
            output = "WARM";
        }

        games.setLastHintUsed(output);
        dr.set(games);
        hintUsed.setText("The Seeker used a hint to see if he is Hot, Warm or cold");

    }
    private void hintsAreDisabled(){

        hintUsed.setText("Hints are disabled");

    }

    Runnable locationChecker = new Runnable() {
        @Override
        public void run() {
            try {

                Log.e("response","circle+response");
                checkIfHiderIsInCircle();
                Log.e("response","checkIfHiderIsInCircle");
            } finally {
                mHandler.postDelayed(locationChecker, 3000);
            }
        }
    };
    Runnable dataChecker = new Runnable() {
        @Override
        public void run() {
            try {

                Log.e("response","circle+response");
                getDatabaseInfo();
                Log.e("response","getDatabaseInfo");
            } finally {
                mHandler.postDelayed(dataChecker, 3000);
            }
        }
    };

    public void whatHintRequested () {

        if(games.isHasSeekerReachedCircle()&&!hasEnteredArea){
            ruhRoh.setText("Seeker Is In The Circle");
            setTimer();
            Log.e("hint","Seeker has reached the circle");
            hasEnteredArea = true;
        }
        if(!games.isHints()){
            hintsAreDisabled();
        }
        if(games.getLastHintUsed().equals("DistanceFromCenter")){
            getAndSendDistanceFromCenter();
            hintCounter++;
        }
        if(games.getLastHintUsed().equals("Latitude")){
            LatitudeGetterAndSender();
            hintCounter++;
        }
        if(games.getLastHintUsed().equals("Longitude")){
            LongitudeGetterAndSender();
            hintCounter++;
        }
        if(games.getLastHintUsed().equals("HotOrCold")){
            hotOrCold();
            hintCounter++;
        }
        Log.e("response","finished whatHintRequested");
    }

    private void getDatabaseInfo(){
        dr = mDB.collection("Games").document(lobbyName);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                games = document.toObject(gameInfo.class) ;
                Log.e("response","whatHintRequested");
                whatHintRequested();



            }
        });
    }
}