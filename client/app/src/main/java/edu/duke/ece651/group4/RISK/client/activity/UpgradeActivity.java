package edu.duke.ece651.group4.RISK.client.activity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_CREATE_SUCCESS;
import static edu.duke.ece651.group4.RISK.client.Constant.MAPS;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class UpgradeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private String terrName;
    private String typeNameBefore;
    private String typeNameAfter;
    private int nUnit;

    private ImageView worldImageView;
    private Spinner terrSpinner;
    private Spinner levelBeforeSpinner;
    private Spinner levelAfterSpinner;
    private EditText nUnitET;
    private Button commitBT;

    private Map<String, Integer> levels;

    protected void initMapping() {
        for (int i = 0; i <= 6; i++) {
            levels.put("Soldier LV" + i, i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.terrName = "";
        this.typeNameBefore = "";
        this.typeNameAfter = "";
        this.nUnit = -1;
        this.levels = new HashMap<>();
        initMapping();

        Log.e(TAG, "Upgrade Activity: set up successfully and will enter UI" );
        impUI();
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }

    // back button at toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void impUI() {
        worldImageView = findViewById(R.id.world_image_view);
        worldImageView.setImageResource(MAPS.get(getCurrentRoomSize()));
        
        // territory spinner
        List<String> myTerrNames = getMyTerrNames();

        terrSpinner = findViewById(R.id.terr_choices);
        SpinnerAdapter terrAdapter = new ArrayAdapter<>(
                UpgradeActivity.this, R.layout.item_choice,
                myTerrNames);
        terrSpinner.setAdapter(terrAdapter);
        terrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                terrName = (String) terrAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // unit level before spinner
        List<String> levelNamesBefore = RISKApplication.getLevelNames(); // TODO

        levelBeforeSpinner = findViewById(R.id.unit_before_choices);
        SpinnerAdapter levelBeforeAdapter = new ArrayAdapter<>(
                UpgradeActivity.this, R.layout.item_choice,
                levelNamesBefore);
        levelBeforeSpinner.setAdapter(levelBeforeAdapter);
        levelBeforeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeNameBefore = (String) levelBeforeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // unit level after spinner
        List<String> levelNamesAfter = RISKApplication.getLevelNames(); // TODO

        levelAfterSpinner = findViewById(R.id.unit_after_choices);
        SpinnerAdapter levelAfterAdapter = new ArrayAdapter<>(
                UpgradeActivity.this, R.layout.item_choice,
                levelNamesAfter);
        levelAfterSpinner.setAdapter(levelAfterAdapter);
        levelAfterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeNameAfter = (String) levelAfterAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // number of unit
        nUnitET = findViewById(R.id.inputNum);

        // commit button
        Button commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v -> {

            Editable text = nUnitET.getText();
            if(text == null){
                return;
            }else if(text.toString()==""){
                showByToast(UpgradeActivity.this,"Please input the number.");
                return;
            }
            nUnit = Integer.parseInt(text.toString());
            Log.d(TAG, String.format("User selected: upgrade %d units from \"%s\" to \"%s\".",
                    nUnit, typeNameBefore, typeNameAfter));
            Log.d(TAG, String.format("Upgrade order to be created: upgrade %d units from LV%d to LV%d.",
                    nUnit, levels.get(typeNameBefore), levels.get(typeNameAfter)));

            // TODO: need mapping from levelNames -> level integer, hard-coded for now
            String result = doSoldierUpgrade(
                    buildUpOrder(terrName,
                            levels.get(typeNameBefore),
                            levels.get(typeNameAfter),
                            nUnit));
            if (result == null) {
                finish();
            } else {
                showByToast(this, result);
            }
        });
    }
}