package com.example.hnsupgrade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class join extends AppCompatActivity {
    private String lobbyID;
    private FirebaseFirestore mDB;
    private EditText lob;
    private Button b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamejoin);
        mDB = FirebaseFirestore.getInstance();
        lob = findViewById(R.id.LobbyId);
        b = findViewById(R.id.joinGame);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nullChecker();
            }

        });
    }
    private void nullChecker(){
        lobbyID = lob.getText().toString();
        Log.e("join",lobbyID);
        if (lobbyID.equals("")  || lobbyID ==null) {
            Toast.makeText(join.this, "Lobby name is empty", Toast.LENGTH_SHORT).show();

        }
        else{
            joinGameButton();
        }


    }

    private void joinGameButton() {
        mDB.collection("Games").document(lobbyID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    Intent intent = new Intent(join.this, joincreate.class);
                    Toast.makeText(join.this, "Game Found", Toast.LENGTH_SHORT).show();
                    intent.putExtra("lobbyName", lobbyID);
                    startActivity(intent);
                } else {

                    Toast.makeText(join.this, "NO Game Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
