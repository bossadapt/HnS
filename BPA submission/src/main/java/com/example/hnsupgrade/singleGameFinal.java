package com.example.hnsupgrade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class singleGameFinal extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlefinal);
        TextView nameS = findViewById(R.id.nameText);
        TextView infor = findViewById(R.id.infoSection);
        TextView hintz = findViewById(R.id.hintAmnt);
        TextView guessz = findViewById(R.id.guessAmnt);

        Gson gson = new Gson();
        placeInfo pi = gson.fromJson(getIntent().getStringExtra("pi"), placeInfo.class);
        String hintTotal= "Total Hints Used "+getIntent().getIntExtra("hintTotal",0);
        String guessTotal= "Total Guesses Used "+getIntent().getIntExtra("guessTotal",0);

        String hh = spaceRemover(pi.getOpenHours());
        String h2 = fixHours(hh);

        nameS.setText("You Found: " + pi.getName());
        infor.setText("Hours:\n" + h2 + "\n Website:" + spaceRemover(pi.getWebsite()) + "\n Phone Number:" + spaceRemover(pi.getFormatPhone()));
        hintz.setText(hintTotal);
        guessz.setText(guessTotal);
        Button b = findViewById(R.id.playSingleAgain);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(singleGameFinal.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private String spaceRemover(String string) {
        string = string.replaceAll("\\s", "");
        return string;
    }

    private String fixHours(String hours) {
        String tag = "fixHours";
        String monday = "";
        String tuesday = "";
        String wednesday = "";
        String thursday = "";
        String friday = "";
        String sat = "";
        String sunday = "";

        if (hours.contains("Monday")) {
            Log.e(tag, hours);
            int spot = hours.indexOf(":");
            int spot2 = hours.indexOf("T");
            monday = hours.substring(spot, spot2 - 1);
            hours = hours.substring(spot2);
        }
        if(hours.contains("Tuesday")) {
            Log.e(tag, hours);
            int spot3 = hours.indexOf(":");
            int spot4 = hours.indexOf("W");
            tuesday = hours.substring(spot3, spot4 - 1);
            hours = hours.substring(spot4);
        }
        if(hours.contains("Wednesday")) {
            Log.e(tag, hours);
            int spot5 = hours.indexOf(":");
            int spot6 = hours.indexOf("T");
             wednesday = hours.substring(spot5, spot6 - 1);
            hours = hours.substring(spot6);
        }
        if(hours.contains("Thursday")) {
            Log.e(tag, hours);
            int spot7 = hours.indexOf(":");
            int spot8 = hours.indexOf("F");
             thursday = hours.substring(spot7, spot8 - 1);
            hours = hours.substring(spot8);
        }
        if(hours.contains("Friday")) {
            Log.e(tag, hours);
            int spot9 = hours.indexOf(":");
            int spot10 = hours.indexOf("S");
             friday = hours.substring(spot9, spot10 - 1);
            hours = hours.substring(spot10);
        }
        if(hours.contains("Saturday")) {
            Log.e(tag, hours);
            int spot11 = hours.indexOf(":");
            int spot12 = hours.indexOf("Sun");
             sat = hours.substring(spot11, spot12 - 1);
            hours = hours.substring(spot12);
        }
        if(hours.contains("Sunday")) {
            Log.e(tag, hours);
            int spot13 = hours.indexOf(":");
             sunday = hours.substring(spot13);
            Log.e(tag, hours);
        }

        String output = "Monday: " + monday + "\n Tuesday: " + tuesday + "\n Wednesday: "  + wednesday + "\n Thursday: " +thursday + "\n Friday: " + friday + "\n Saturday: " + sat + "\n Sunday: "+ sunday;
        return output;
    }
}
