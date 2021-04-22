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
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.JOB_NAMES;
import static edu.duke.ece651.group4.RISK.shared.Constant.UNIT_NAMES;


// todo: refactor some code & comment
public class UpgradeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private String terrName;
    private String type;
    private int levelBefore;
    private int levelAfter;
    private int nUnit;

    private ImageView worldImageView;
    private Spinner terrSpinner;
    private Spinner levelBeforeSpinner;
    private Spinner typeSpinner;
    private Spinner levelAfterSpinner;
    private EditText nUnitET;
    private Button commitBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Upgrade");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        this.terrName = "";
        this.type = "";
        this.levelBefore = 0;
        this.levelAfter = 0;
        this.nUnit = -1;

        Log.e(TAG, "Upgrade Activity: set up successfully and will enter UI");
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // type spinner
        List<String> jobNames = JOB_NAMES;

        typeSpinner = findViewById(R.id.unit_type_choices);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(
                UpgradeActivity.this, R.layout.item_choice,
                jobNames);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = JOB_NAMES.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // unit level before spinner
        List<String> levelNames = UNIT_NAMES.subList(0, getTechLevel() + 1);

        levelBeforeSpinner = findViewById(R.id.unit_before_choices);
        SpinnerAdapter levelBeforeAdapter = new ArrayAdapter<>(
                UpgradeActivity.this, R.layout.item_choice,
                levelNames);
        levelBeforeSpinner.setAdapter(levelBeforeAdapter);
        levelBeforeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                levelBefore = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        levelAfterSpinner = findViewById(R.id.unit_after_choices);
        SpinnerAdapter levelAfterAdapter = new ArrayAdapter<>(
                UpgradeActivity.this, R.layout.item_choice,
                levelNames);
        levelAfterSpinner.setAdapter(levelAfterAdapter);
        levelAfterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                levelAfter = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // number of unit
        nUnitET = findViewById(R.id.inputNum);

        // commit button
        Button commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v -> {

            // get number
            Editable text = nUnitET.getText();
            if (text == null) {
                Log.e(TAG,LOG_FUNC_FAIL+"input text null");
                return;
            } else if (text.toString().equals("")) {
                showByToast(UpgradeActivity.this, "Please input the number.");
                return;
            }
            nUnit = Integer.parseInt(text.toString());

            doSoldierUpgrade(buildUpOrder(terrName, levelBefore, levelAfter, nUnit, type),
                    new onResultListener() {
                @Override
                public void onSuccess() {
                    finish();
                }

                @Override
                public void onFailure(String errMsg) {
                    showByToast(UpgradeActivity.this, errMsg);
                }
            });
        });
    }
}