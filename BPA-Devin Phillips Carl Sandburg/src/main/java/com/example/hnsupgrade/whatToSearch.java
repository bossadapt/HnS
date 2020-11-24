package com.example.hnsupgrade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class whatToSearch extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whattosearchfor);
        Button b = findViewById(R.id.startSearch);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("whatToSearch","clicked");

                checkSwitches();
            }
        });
    }
    private void checkSwitches() {
        Log.e("whatToSearch","checkSwitches");

        String locationPassed = "emptyLocation";
        //initialize all of the switches
        Switch park = findViewById(R.id.parkSwitch);
        Switch tourist = findViewById(R.id.touristSpotSwitch);
        Switch restaurant = findViewById(R.id.restaurantSwitch);
        Switch theater = findViewById(R.id.movieTheaterSwitch);
        Switch bar = findViewById(R.id.barSwitch);
        Switch casino = findViewById(R.id.casinoSwitch);
        Switch nightClub = findViewById(R.id.nightClubSwitch);
        Switch all = findViewById(R.id.allSwitch);
        //if the all switch is on then pick randomly between them all and send it over to game
        //if any switches are chosen besides the all then add them to a list and pick one and send it over to game
        //else ask for them to check at least one
        if (all.isChecked()) {
            Log.e("whatToSearch","all checked");

            int ran = (int) (Math.random() * 7);

            switch (ran) {
                case 0:
                    locationPassed = "park";
                    break;
                case 1:
                    locationPassed = "tourist_attraction";
                    break;
                case 2:
                    locationPassed = "restaurant";
                    break;
                case 3:
                    locationPassed = "movie_theater";
                    break;
                case 4:
                    locationPassed = "bar";
                    break;
                case 5:
                    locationPassed = "casino";
                    break;
                case 6:
                    locationPassed = "night_club";
            }
            sendToNextActivity(locationPassed);
        } else if (park.isChecked() || tourist.isChecked() || restaurant.isChecked() || theater.isChecked() ||  bar.isChecked() || casino.isChecked() || nightClub.isChecked()) {
            Log.e("whatToSearch","one of the switches checked besides all");

            //check how many are switched to make string array with length
            int amountSwitched = 0;
            if (park.isChecked()) {
                amountSwitched++;
            }
            if (tourist.isChecked()) {
                amountSwitched++;
            }
            if (restaurant.isChecked()) {
                amountSwitched++;
            }
            if (theater.isChecked()) {
                amountSwitched++;
            }

            if (bar.isChecked()) {
                amountSwitched++;
            }
            if (casino.isChecked()) {
                amountSwitched++;
            }
            if (nightClub.isChecked()) {
                amountSwitched++;
            }


            String[] choices = new String[amountSwitched];


            int howManyUsed = 0;
            if (park.isChecked()) {
                choices[howManyUsed] = "park";
                howManyUsed++;
            }
            if (tourist.isChecked()) {
                choices[howManyUsed] = "tourist_attraction";
                howManyUsed++;
            }
            if (restaurant.isChecked()) {
                choices[howManyUsed] = "restaurant";
                howManyUsed++;
            }
            if (theater.isChecked()) {
                choices[howManyUsed] = "movie_theater";
                howManyUsed++;
            }
            if (bar.isChecked()) {
                choices[howManyUsed] = "bar";
                howManyUsed++;
            }
            if (casino.isChecked()) {
                choices[howManyUsed] = "casino";
                howManyUsed++;
            }
            if (nightClub.isChecked()) {
                choices[howManyUsed] = "night_club";

            }
            int ran = (int) (Math.random() * choices.length);
            locationPassed = choices[ran];
            sendToNextActivity(locationPassed);

        }else {
            Log.e("whatToSearch","none switched");

            Toast.makeText(whatToSearch.this, "Please Switch on one of the locations", Toast.LENGTH_SHORT).show();
            }
        }

        private void sendToNextActivity(String string){
            Intent intent = new Intent(whatToSearch.this,singlePlayerGame.class);
              Log.e("whatToSearch","sendToNextActivity");
            Log.e("whatToSearch","type:" + string);
            intent.putExtra("type",string);
            startActivity(intent);

        }

    }
