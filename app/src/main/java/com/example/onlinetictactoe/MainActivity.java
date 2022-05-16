package com.example.onlinetictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout playerOneLayout, playerTwoLayout;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9;
    private TextView playerOneTV, playerTwoTV;

    private final List<int[]> combinationlist = new ArrayList<>();
    private final List<String> doneBoxes = new ArrayList<>();

    private String playerUniqueID = "0";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://tictactoe-b7a9d-default-rtdb.firebaseio.com/");

    private boolean opponentfound = false;
    private String opponentUniqueID = "0";
    private String status = "matching";
    private String playerturn = "";
    private String connectionID = "";

    ValueEventListener turnsEventListener, wonEventListener;

    private final String[] BoxesSelectedBy= {"", "", "", "", "", "", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerOneLayout = findViewById(R.id.playerOneLayout);
        playerTwoLayout = findViewById(R.id.playerTwoLayout);

        playerOneTV = findViewById(R.id.playerOneTV);
        playerTwoTV = findViewById(R.id.playerTwoTV);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image7 = findViewById(R.id.image7);
        image8 = findViewById(R.id.image8);
        image9 = findViewById(R.id.image9);

        final String getplayername = getIntent().getStringExtra("playername");

        combinationlist.add(new int[]{0,1,2});
        combinationlist.add(new int[]{3,4,5});
        combinationlist.add(new int[]{6,7,8});
        combinationlist.add(new int[]{0,3,6});
        combinationlist.add(new int[]{1,4,7});
        combinationlist.add(new int[]{2,5,8});
        combinationlist.add(new int[]{2,4,6});
        combinationlist.add(new int[]{0,4,8});


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Waiting for Opponent");
        progressDialog.show();

        playerUniqueID = String.valueOf(System.currentTimeMillis());

        playerOneTV.setText(getplayername);

        databaseReference.child("connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!opponentfound){
                    if (snapshot.hasChildren()){
                        for (DataSnapshot connections : snapshot.getChildren()){
                            String conID = connections.getKey();

                            int getPlayersCount = (int) connections.getChildrenCount();

                            if (status.equals("waiting")){
                                if (getPlayersCount == 2){

                                    playerturn = playerUniqueID;
                                    applyPlayerTurn(playerturn);

                                    boolean playerFound = false;

                                    for (DataSnapshot players : connections.getChildren()){

                                        String getPlayerUniqueid = players.getKey();

                                        if(getPlayerUniqueid.equals(playerUniqueID)){

                                            playerFound = true;

                                        }
                                        else if (playerFound){
                                            String getOpponentPlayerName = players.child("player_name").getValue(String.class);
                                            opponentUniqueID = players.getKey();

                                            playerTwoTV.setText(getOpponentPlayerName);
                                            connectionID = conID;
                                            opponentfound = true;

                                            databaseReference.child("turns").child(connectionID).addValueEventListener(turnsEventListener);
                                            databaseReference.child("won").child(connectionID).addValueEventListener(wonEventListener);

                                            if (progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }

                                            databaseReference.child("connections").removeEventListener(this);
                                        }

                                    }

                                }

                            }

                            else {

                                if(getPlayersCount == 1){
                                    connections.child(playerUniqueID).child("player_name").getRef().setValue(getplayername);

                                    for (DataSnapshot players : connections.getChildren()){
                                        String getOpponentName = players.child("player_name").getValue(String.class);
                                        playerturn = opponentUniqueID;

                                        applyPlayerTurn(playerturn);

                                        playerTwoTV.setText(getOpponentName);
                                        connectionID = conID;
                                        opponentfound = true;

                                        databaseReference.child("turns").child(connectionID).addValueEventListener(turnsEventListener);
                                        databaseReference.child("won").child(connectionID).addValueEventListener(wonEventListener);

                                        if (progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }

                                        databaseReference.child("connections").removeEventListener(this);

                                        break;

                                    }
                                }

                            }

                        }

                        if (!opponentfound && !status.equals("waiting")){
                            String connectionUniqueID = String.valueOf(System.currentTimeMillis());

                            snapshot.child(connectionUniqueID).child(playerUniqueID).child("player_name").getRef().setValue(getplayername);

                            status = "waiting";
                        }

                    }
                    else {
                        String connectionUniqueID = String.valueOf(System.currentTimeMillis());

                        snapshot.child(connectionUniqueID).child(playerUniqueID).child("player_name").getRef().setValue(getplayername);

                        status = "waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        turnsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getChildrenCount() == 2){
                        final int getBoxPosition = Integer.parseInt(dataSnapshot.child("box_position").getValue(String.class));

                        final String getPlayerId = dataSnapshot.child("player_id").getValue(String.class);

                        if (!doneBoxes.contains(String.valueOf(getBoxPosition))){
                            doneBoxes.add(String.valueOf(getBoxPosition));

                            if (getBoxPosition == 1){
                                selectBox(image1, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 2){
                                selectBox(image2, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 3){
                                selectBox(image3, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 4){
                                selectBox(image4, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 5){
                                selectBox(image5, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 6){
                                selectBox(image6, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 7){
                                selectBox(image7, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 8){
                                selectBox(image8, getBoxPosition, getPlayerId);
                            }
                            else if (getBoxPosition == 9){
                                selectBox(image9, getBoxPosition, getPlayerId);
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        wonEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("player_id")){
                    String getWinPlayerId = snapshot.child("player_id").getValue(String.class);
                    final WinDialog winDialog;

                    if (getWinPlayerId.equals(playerUniqueID)){
                        winDialog = new WinDialog(MainActivity.this, "You won the Game!");
                    }
                    else {
                        winDialog = new WinDialog(MainActivity.this, "Opponent won the Game!");
                    }
                    winDialog.setCancelable(false);
                    winDialog.show();

                    databaseReference.child("turns").child(connectionID).removeEventListener(turnsEventListener);
                    databaseReference.child("won").child(connectionID).removeEventListener(wonEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        image1.setOnClickListener(v -> {
            if (!doneBoxes.contains("1") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("1");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image2.setOnClickListener(v -> {
            if (!doneBoxes.contains("2") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("2");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image3.setOnClickListener(v -> {
            if (!doneBoxes.contains("3") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("3");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image4.setOnClickListener(v -> {
            if (!doneBoxes.contains("4") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("4");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image5.setOnClickListener(v -> {
            if (!doneBoxes.contains("5") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("5");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image6.setOnClickListener(v -> {
            if (!doneBoxes.contains("6") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("6");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image7.setOnClickListener(v -> {
            if (!doneBoxes.contains("7") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("7");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image8.setOnClickListener(v -> {
            if (!doneBoxes.contains("8") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("8");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

        image9.setOnClickListener(v -> {
            if (!doneBoxes.contains("9") && playerturn.equals(playerUniqueID)){
                ((ImageView)v).setImageResource(R.drawable.x);

                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue("9");
                databaseReference.child("turns").child(connectionID).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueID);

                playerturn = opponentUniqueID;
            }
        });

    }

    private void applyPlayerTurn(String playerUniqueID2) {
        if (playerUniqueID2.equals(playerUniqueID)){
            playerOneLayout.setBackgroundResource(R.drawable.round_back);
            playerTwoLayout.setBackgroundResource(R.drawable.round_box);
        }
        else {
            playerTwoLayout.setBackgroundResource(R.drawable.round_back);
            playerOneLayout.setBackgroundResource(R.drawable.round_box);
        }
    }
    private void selectBox(ImageView imageView, int selectionBoxPosition, String selectedByPlayer){

        BoxesSelectedBy[selectionBoxPosition - 1] = selectedByPlayer;

        if (selectedByPlayer.equals(playerUniqueID)){
            imageView.setImageResource(R.drawable.x);
            playerturn = playerUniqueID;
        }
        applyPlayerTurn(playerturn);
        if (checkPlayerWin(selectedByPlayer)){
            databaseReference.child("won").child(connectionID).child("player_id").setValue(selectedByPlayer);
        }

        if (doneBoxes.size() == 9){
            final WinDialog winDialog = new WinDialog(MainActivity.this, "It is a DRAW");
            winDialog.setCancelable(false);
            winDialog.show();
        }

    }

    private boolean checkPlayerWin(String playerid){
        boolean isPlayerWin = false;

        for (int i = 0; i < combinationlist.size(); i++){
            final int[] combination = combinationlist.get(i);

            if (BoxesSelectedBy[combination[0]].equals(playerid) && BoxesSelectedBy[combination[1]].equals(playerid) &&
                    BoxesSelectedBy[combination[2]].equals(playerid)){
                isPlayerWin = true;
            }
        }
        return isPlayerWin;
    }
}