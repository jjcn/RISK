package edu.duke.ece651.group4.RISK.client.activity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_CREATE_SUCCESS;
import static edu.duke.ece651.group4.RISK.client.Constant.MAPS;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.JOB_NAMES;
import static edu.duke.ece651.group4.RISK.shared.Constant.UNIT_NAMES;

public class TransferActivity extends AppCompatActivity {
    
    private final String TAG = this.getClass().getSimpleName();
    private String terrName;
    private String typeAfter;
    private int level;
    private int nUnit;

    private ImageView worldImageView;
    private Spinner terrSpinner;
    private Spinner typeAfterSpinner;
    private Spinner levelSpinner;
    private EditText nUnitET;
    private Button commitBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Transfer");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        this.terrName = "";
        this.typeAfter = "";
        this.level = 0;
        this.nUnit = -1;

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
                TransferActivity.this, R.layout.item_choice,
                myTerrNames);
        terrSpinner.setAdapter(terrAdapter);
        terrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                terrName = (String) terrAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // type spinner
        List<String> jobNames = JOB_NAMES;

        typeAfterSpinner = findViewById(R.id.type_after_choices);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(
                TransferActivity.this, R.layout.item_choice,
                jobNames);
        typeAfterSpinner.setAdapter(typeAdapter);
        typeAfterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeAfter = JOB_NAMES.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // unit level spinner
        List<String> levelNames = UNIT_NAMES;

        levelSpinner = findViewById(R.id.level_choices);
        SpinnerAdapter levelAfterAdapter = new ArrayAdapter<>(
                TransferActivity.this, R.layout.item_choice,
                levelNames);
        levelSpinner.setAdapter(levelAfterAdapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // number of unit
        nUnitET = findViewById(R.id.inputNum);

        // commit button
        commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v -> {
            Editable text = nUnitET.getText();
            if (text == null) {
                return;
            } else if (text.toString().equals("")) {
                showByToast(TransferActivity.this, "Please input the number.");
                return;
            }
            nUnit = Integer.parseInt(text.toString());

            String result = doSoldierTransfer(
                    buildTransferTroopOrder(terrName, typeAfter, level, nUnit));
            if (result == null) {
                finish();
            } else {
                showByToast(this, result);
            }
        });
    }
}