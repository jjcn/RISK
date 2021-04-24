package edu.duke.ece651.group4.RISK.client.activity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(UI_CHANGETYPE);
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
        List<String> jobNames = new ArrayList<>(getUnLockedTypesWithoutSoldier());

        typeAfterSpinner = findViewById(R.id.type_choices);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(
                TransferActivity.this, R.layout.item_choice, jobNames);
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
        List<Integer> levels = LEVELS;
        levelSpinner = findViewById(R.id.level_choices);
        SpinnerAdapter levelAdapter = new ArrayAdapter<>(
                TransferActivity.this, R.layout.item_choice,
                levels);
        levelSpinner.setAdapter(levelAdapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = (int) levelAdapter.getItem(position);
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
                Log.e(TAG,LOG_FUNC_FAIL + "input text null");
                return;
            } else if (text.toString().equals("")) {
                showByToast(TransferActivity.this, "Please input the number.");
                return;
            }
            nUnit = Integer.parseInt(text.toString());

            Log.d(TAG, LOG_FUNC_RUN + "user selected: change " + nUnit + " soldier of level " +
                    level + " to " + typeAfter);
            doSoldierTransfer(
                    buildTransferTroopOrder(terrName, typeAfter, level, nUnit), new onResultListener() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            showByToast(TransferActivity.this, errMsg);
                        }
                    });
        });
    }
}