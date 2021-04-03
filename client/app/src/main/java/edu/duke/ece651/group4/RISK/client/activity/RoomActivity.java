package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;

public class RoomActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        impUI();
    }

    private void impUI() {
        impCreateBT();
        impJoinList();
    }

    private void impJoinList() {

    }

    private void impCreateBT() {
        Button createBT = findViewById(R.id.createButton);
        createBT.setOnClickListener(v -> {
            Intent newRoom = new Intent(RoomActivity.this, LoginActivity.class);
        });
    }


}