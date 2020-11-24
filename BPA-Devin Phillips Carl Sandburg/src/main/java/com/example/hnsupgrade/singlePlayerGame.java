package com.example.hnsupgrade;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.type.LatLng;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class singlePlayerGame extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlegame);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final String apiString = "AIzaSyDJgx7pYcDKOHX2-DrM_lYxbn1vhyKRBnM";
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        String type = getIntent().getStringExtra("type");
        getLocation(type,apiString);
    }
    //example link search https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&rankby=distance&type=food&key=YOUR_API_KEY

    private void getLocation(final String type, final String api) {

        Log.e("WebsiteResponse","getLocation");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    location = task.getResult();
                    Log.e("WebsiteResponse","goot location");
                    Location location = task.getResult();
                    String latlng = location.getLatitude() + ","+ location.getLongitude();
                    parseHtml(latlng,type,api);
                }




            }
        });

    }

    private void parseHtml(String latlng, String type, String api){
String txt = "";
String website = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?location="+latlng+"&rankby=distance&type="+type+"&key="+api;
        try {
            Log.e("WebsiteResponse",website);
            Document doc = Jsoup.connect(website).get();
             txt = doc.outerHtml();
            Log.e("WebsiteResponse",txt);
        } catch (Exception e) {
            Log.e("name of activity","error message to show in log", e);
        }
        finally {
            placeGetter(txt,api);
        }
    }
    private void placeGetter(String websiteInfo,String api){
        Log.e("rawr","placeGetter");
        int resultAmount = 0;
        String websiteInfo2 = websiteInfo;
        for (int i =0; i<20;i++){
            if(websiteInfo2.contains("<result>")){
                int index = websiteInfo2.indexOf("<result>")+7; //7 == result>
                websiteInfo2 = websiteInfo2.substring(index);
                resultAmount++;

            }
            else{
                Toast.makeText(singlePlayerGame.this,"no close type locations nearby",Toast.LENGTH_SHORT).show();
            }

        }
        int ran = (int)(Math.random()*resultAmount+1);
        String name = "";
        String latitude = "";
        String longitude = "";
        String vicinity ="";
        String rating = "";
        String userRating= "";
        String placeID= "";
        int counter = 0;
        Log.e("rawr","ran #"+ran);
        for(int i =0; i<ran;i++){
            Log.e("rawr","loopran");
        int start = websiteInfo.indexOf("result");

            Log.e("rawr",ran +"/"+i);

            if(i == ran-1){ Log.e("rawr","if ran");
            //name get
                Log.e("rawr","ifRan");
            int startName = websiteInfo.indexOf("name");
            websiteInfo = websiteInfo.substring(startName+5);
            int endName = websiteInfo.indexOf("</")-1;
            name = websiteInfo.substring(0,endName);
            //vicinity get
                int startvicinity = websiteInfo.indexOf("vicinity");
                websiteInfo = websiteInfo.substring(startvicinity+8);
                int endVicinity = websiteInfo.indexOf("</")-1;
                vicinity = websiteInfo.substring(0,endVicinity);
                //lat
                int startlat= websiteInfo.indexOf("lat");
                websiteInfo = websiteInfo.substring(startlat+4);
                int endlat = websiteInfo.indexOf("</")-1;
                latitude = websiteInfo.substring(0,endlat);
                //long
                int startLong= websiteInfo.indexOf("lng");
                websiteInfo = websiteInfo.substring(startLong+4);
                int endLong = websiteInfo.indexOf("</")-1;
                longitude = websiteInfo.substring(0,endLong);
                //rating
                int startRate= websiteInfo.indexOf("rating");
                websiteInfo = websiteInfo.substring(startRate+7);
                int endRate = websiteInfo.indexOf("</")-1;
                rating = websiteInfo.substring(0,endRate);
                //ID
                int startID= websiteInfo.indexOf("<reference>");
                websiteInfo = websiteInfo.substring(startID+11);
                int endID = websiteInfo.indexOf("</")-1;
                placeID = websiteInfo.substring(0,endID);
                //user rating
                int startUserR= websiteInfo.indexOf("user_ratings_total");
                websiteInfo = websiteInfo.substring(startUserR+19);
                int enduserR = websiteInfo.indexOf("</")-1;
                userRating = websiteInfo.substring(0,enduserR);

            }
        else{
            //delete that portion of the string and index then delete
           websiteInfo = websiteInfo.substring(start+5);
                Log.e("elseCut","start:"+start);
           start = websiteInfo.indexOf("result");
           Log.e("elseCut","start:"+start);
           websiteInfo = websiteInfo.substring(start);

        }

    }
        Log.e("rawr","loop finished");
        Log.e("list","name; "+name);
        Log.e("list","latitude; "+latitude);
        Log.e("list","longitude; "+longitude);
        Log.e("list","placeID; "+placeID);
        Log.e("list","vicinity; "+vicinity);
        Log.e("list","rating; "+rating);
        Log.e("list","userRating; "+userRating);

    getContacts(new placeInfo(name,latitude,longitude,placeID,vicinity,rating,userRating,"","",""),api);
    }
    // request for contact https://maps/api/place/details/json?place_id=ChIJN1t_tDeuEmsRUsoyG83frY4&fields=name,rating,formatted_phone_number&key=YOUR_API_KEY
private void getContacts(placeInfo pi,String api){
        String txt = "";
//String website = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?location="+latlng+"&rankby=distance&type="+type+"&key="+api;
    String website = "https://maps.googleapis.com/maps/api/place/details/xml?placeid="+pi.getplaceID()+"&fields=website,opening_hours,formatted_phone_number&key="+api;
    website = spaceRemover(website);
    System.out.print(website);
    final Handler handler = new Handler();
    final String finalWebsite = website;
            try {
                Log.e("WebsiteResponse2", finalWebsite);
                Document doc = Jsoup.connect(finalWebsite).get();
                txt = doc.outerHtml();
                Log.e("WebsiteResponse2", txt);
            } catch (Exception e) {
                Log.e("name of activity",e.toString());
            }
            finally

        {
            Log.e("WebsiteResponse2", "ready for next activity");
        }
    String websiteobj= "";
    String phone= "";
    String hours= "";
    Log.e("zoo","finally");

    String webweb = txt;
    Log.e("infoMachine",webweb);//phone
    if(webweb.contains("<formatted_phone_number>")) {
        Log.e("infoMachine","there is phone");
        int startPhone = webweb.indexOf("<formatted_phone_number>");
        webweb = webweb.substring(startPhone + 24);
        int endPhone = webweb.indexOf("</") - 1;
        phone = spaceRemover(webweb.substring(0, endPhone));
    }
    //websiteobj
    if(webweb.contains("<website>")) {
        Log.e("infoMachine","there is website");
        int startName = webweb.indexOf("<website>");
        webweb = webweb.substring(startName + 9);
        int endName = webweb.indexOf("</") - 1;
        websiteobj = spaceRemover(webweb.substring(0, endName));
    }
    //open time
    if(webweb.contains("<weekday_text>")) {
Log.e("infoMachine","there is hours");
        int startOpen = webweb.indexOf("<weekday_text>");
        webweb = webweb.substring(startOpen + 14);
        int endOpen = webweb.indexOf("</opening_hours>");
        hours = webweb.substring(0, endOpen - 1);
        pi.setOpenHours(openTimeCleaner(hours));
        hours = openTimeCleaner(hours);
    }
    placeInfo ri = new placeInfo(pi.getName(),pi.getLatitude(),pi.getLongitude(),pi.getPlaceID(),pi.getVicinity(),pi.getRating(),pi.getUserRating(),websiteobj,phone,hours);
    Intent intent = new Intent(singlePlayerGame.this,singleGameGame.class);
    Gson gson = new Gson();
    String myJson = gson.toJson(ri);
    intent.putExtra("pi", myJson);
    startActivity(intent);

}
private String openTimeCleaner(String str){
        str = spaceRemover(str);
        str = str.replaceAll("<weekday_text>","");
    str = str.replaceAll("</weekday_text>","\n");
    return str;
}
private String spaceRemover(String string){
        string = string.replaceAll("\\s","");
        return string;
}

}

