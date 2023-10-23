package com.example.hnsupgrade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class finalScreen extends AppCompatActivity {
    private FirebaseFirestore mDB;
    String lobbyName;
    gameInfo games;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finalsc);
        lobbyName = getIntent().getStringExtra("lobbyName");
        mDB = FirebaseFirestore.getInstance();
        getDatabaseInfo();

    }
    //sets text then deletes dataBaseSpot
    private void setText(){
        TextView timeTaken = findViewById(R.id.timeTakenText);
        TextView hintsTaken = findViewById(R.id.hintsUsed);
        timeTaken.setText(games.getTimeTaken());
        hintsTaken.setText("Hints Taken:" + games.getTotalHintsUsed());
        mDB.collection("Games").document(games.getLobbyName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    mDB.collection("Games").document(games.getLobbyName()).delete();
//                    games.setHiderFound(false);
//                    games.setTotalHintsUsed(0);
//                    games.setTimeTaken(null);
//                    games.setLobbyName(null);
//                    games.setHiderReady(false);
//                    games.setCircleCenterPicked(null);
//                    games.setSeekerLocation(null);
//                    games.setLastHintUsed(null);
//                    games.setHasSeekerReachedCircle(false);
//                    games.setCircleSize(0);
//                    games.setHints(false);


                }


            }
        });
    }
    private void playAgain(){
        Button b = findViewById(R.id.playAgain);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(finalScreen.this, join.class);
                Log.e("logger","Intent went through");
                startActivity(intent);
            }
        });

    }
    private void getDatabaseInfo(){
        DocumentReference dr = mDB.collection("Games").document(lobbyName);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                games = document.toObject(gameInfo.class) ;
                setText();
                playAgain();
            }
        });
    }
}
