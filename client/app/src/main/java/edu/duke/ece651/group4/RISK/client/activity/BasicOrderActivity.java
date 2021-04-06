package edu.duke.ece651.group4.RISK.client.activity;

import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.shared.Territory;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;

public class BasicOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_order);
        impUI();
    }

    private void impUI() {
        List<Territory> terrChoices = getMyTerritory();

//        terrsAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, worldInfo);
//        worldInfoRC.setAdapter(worldInfoAdapter);
//
//        List<String> worldInfo = getWorldInfo();
//        worldInfoAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, worldInfo);
//        worldInfoRC.setAdapter(worldInfoAdapter);
//
//        List<String> worldInfo = getWorldInfo();
//        worldInfoAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, worldInfo);
//        worldInfoRC.setAdapter(worldInfoAdapter);

        Button commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v->{
           // String result = doOneMove(buildMoveOrder(src,des,num,job));
        });
    }
}