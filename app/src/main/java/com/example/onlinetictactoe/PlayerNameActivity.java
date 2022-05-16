package com.example.onlinetictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PlayerNameActivity extends AppCompatActivity {

    private EditText playerName;
    private AppCompatButton startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);

        playerName = findViewById(R.id.playerName);
        startGame = findViewById(R.id.gameStart);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String getPlayerName = playerName.getText().toString();

                if (getPlayerName.isEmpty()){
                    Toast.makeText(PlayerNameActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(PlayerNameActivity.this, MainActivity.class);
                    intent.putExtra("playername", getPlayerName);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }
}