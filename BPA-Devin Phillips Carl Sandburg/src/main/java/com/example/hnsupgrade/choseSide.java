package com.example.hnsupgrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class choseSide extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_side);
        creatorButton();
        joinButton();
    }
    private void creatorButton() {
        Button named = findViewById(R.id.creator);
        named.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreatorActivity();
            }
        });
    }

    private void openCreatorActivity() {
        Intent pest = new Intent(this, create.class);
        startActivity(pest);
    }
    private void joinButton() {
        Button named = findViewById(R.id.follower);
        named.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinActivity();
            }
        });
    }

    private void openJoinActivity() {
        Intent mest = new Intent(this, join.class);
        startActivity(mest);
    }
}
