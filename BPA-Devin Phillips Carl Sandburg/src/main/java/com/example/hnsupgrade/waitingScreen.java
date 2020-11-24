package com.example.hnsupgrade;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.gson.Gson;

import java.io.Serializable;

public class waitingScreen extends AppCompatActivity {
    private FirebaseFirestore mDB;
    private gameInfo games;
    final Handler mHandler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitingforjoin);
        Gson gson = new Gson();
        games = gson.fromJson(getIntent().getStringExtra("games"), gameInfo.class);
        mDB = FirebaseFirestore.getInstance();
        TextView lobname = findViewById(R.id.namewaiter);
        lobname.setText(games.getLobbyName());
        responseChecker();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    public void startGame(){
        TextView gameState = findViewById(R.id.waiting);
        gameState.setText("Opponent Found");

        Intent intent = new Intent(this, theSeek.class);
        Gson gson = new Gson();
        String myJson = gson.toJson(games);
        intent.putExtra("games", myJson);
        startActivity(intent);
    }

    private Runnable mRunnable;
    private void responseChecker(){
        Log.d("googleMap", "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                getAndCheckDatabaseInfo();
                mHandler.postDelayed(mRunnable, 5000);
            }
        }, 5000);
    }

    private void getAndCheckDatabaseInfo(){
        Log.e("responce","getAndCheckDatabaseInfo");
        DocumentReference dr;
        dr = mDB.collection("Games").document(games.getLobbyName());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Log.e("responce","onComplete");
                games = document.toObject(gameInfo.class);
                if(games.isHiderReady()){
                    Toast.makeText(waitingScreen.this,"Hider is ready",Toast.LENGTH_SHORT);
                    mHandler.removeCallbacks(mRunnable);
                    startGame();
                    Log.e("responce","hiderfound");
                }else{
                    Toast.makeText(waitingScreen.this,"Still waiting",Toast.LENGTH_SHORT);
                    Log.e("responce","hider is still lost");
                    Log.e("responce",Boolean.toString(games.isHiderReady()));
                    Log.e("responce",games.getCircleCenterPicked());

                }
            }
        });
    }
}



































