package com.example.hnsupgrade;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.Serializable;

public class create extends AppCompatActivity implements Serializable {
    private FirebaseFirestore mDB;

    private gameInfo games = new gameInfo(
            "empty",
            false,
            0.0,
            "0.0,0.0",
            "empty",
            0,
            false,
            false,
            "0.0,0.0",
            false,
            "empty"
    );
    private String lobbyName;
    private boolean hints;
    private double circleSize;
    private static final int REQUEST_CODE = 101;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamecreation);
        mDB = FirebaseFirestore.getInstance();
        Button finalized = findViewById(R.id.finalCreate);
        finalized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectingVarToLayout();
            }
        });


    }

    //creates variables out of all the layouts and makes sure none is blank

    private void connectingVarToLayout(){
        TextView errorFiller = findViewById(R.id.errorText);
        EditText lobn = findViewById(R.id.lobbyName);
        EditText circleS = findViewById(R.id.circleSize);
        Switch h =  findViewById(R.id.hints);
        hints = h.isChecked();
        lobbyName = lobn.getText().toString();
        try {
            circleSize = Double.parseDouble(circleS.getText().toString());
        }
        catch(Exception e) {
            Toast.makeText(create.this,"Decimal",Toast.LENGTH_SHORT).show();
            return;
        }
        if(circleSize <= 0){
            Toast.makeText(create.this,"The Circle Size is less than or equal to zero",Toast.LENGTH_SHORT).show();
        }
        else if(lobbyName.equals("")||lobbyName==null){
            Toast.makeText(create.this,"Lobby name is empty",Toast.LENGTH_SHORT).show();


        }
        else if(circleSize> 5){
            Toast.makeText(create.this,"Keep it under 5 miles",Toast.LENGTH_SHORT).show();

        }
        else {



            mDB.collection("Games").document(lobbyName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.e("On Complete", "Called");
                    if(task.getResult().exists()==true){
                        Toast.makeText(create.this,"Lobby name already taken",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(create.this, waitingScreen.class);
                        DocumentReference dr = mDB.collection("Games").document(lobbyName);
                        games.setLobbyName(lobbyName);
                        games.setHints(hints);
                        games.setCircleSize(circleSize);
                        dr.set(games);
                        Gson gson = new Gson();
                        String myJson = gson.toJson(games);
                        intent.putExtra("games", myJson);
                        startActivity(intent);
                    }
                }
            });

        }
    }
}





