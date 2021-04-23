package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
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
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.JOB_NAMES;
import static edu.duke.ece651.group4.RISK.shared.Constant.SOLDIER;

public class BasicOrderActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private String srcName; // source territory name
    private String desName; // destination territory name
    private String level; // unit type name
    private int nUnit; // number of units in action
    private String actionType; // type of action, move or attack
    private String type;

    private static final String EXTRA_ACTION_TYPE = "actionType"; // intent extra key

    private ImageView worldImageView;
    private Spinner srcSpinner;
    private Spinner desSpinner;
    private Spinner typeSpinner;
    private Spinner levelSpinner;
    private EditText nUnitET;
    private Button commitBT;
    private SpinnerAdapter desAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Basic Order");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        srcName = "";
        desName = "";
        type = "";
        level = "";
        nUnit = -1;

        // read action type from activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            actionType = bundle.getString(EXTRA_ACTION_TYPE);
        }
        String actionType = getIntent().getStringExtra("actionType");
        Log.i(TAG, LOG_FUNC_RUN + "ACT TYPE: " + actionType);

        impUI();
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
        //List<String> myTerrNames = getMyTerrNames();
        List<String> terrNamesWithMyTroop = getTerrNamesWithMyTroop();
        List<String> myAndAllyTerrNames = getMyAndAllyTerrNames();
        List<String> enemyTerrNames = getEnemyTerrNames();

        // source terr spinner
        srcSpinner = findViewById(R.id.terrSrc);
        SpinnerAdapter srcAdapter = new ArrayAdapter<>(
                BasicOrderActivity.this,
                R.layout.item_choice,
                terrNamesWithMyTroop);
        srcSpinner.setAdapter(srcAdapter);
        srcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                srcName = (String) srcAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // des terr spinner
        desSpinner = findViewById(R.id.terrDes);
        if (actionType.equals(UI_MOVE)) {
            desAdapter = new ArrayAdapter<>(
                    BasicOrderActivity.this,
                    R.layout.item_choice,
                    myAndAllyTerrNames);
        } else {
            desAdapter = new ArrayAdapter<>(
                    BasicOrderActivity.this,
                    R.layout.item_choice,
                    enemyTerrNames);
        }
        desSpinner.setAdapter(desAdapter);
        desSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                desName = (String) desAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // typeNames == level spinner
        List<String> levelNames = getLevelNames();
        levelSpinner = findViewById(R.id.soldierType);
        SpinnerAdapter levelAdapter = new ArrayAdapter<>(
                BasicOrderActivity.this,
                R.layout.item_choice,
                levelNames);
        levelSpinner.setAdapter(levelAdapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = (String) levelAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // type spinner
        List<String> jobNames = new ArrayList<>();
        jobNames.add(SOLDIER);
        jobNames.addAll(JOB_NAMES);

        typeSpinner = findViewById(R.id.unit_type_choices);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(
                BasicOrderActivity.this, R.layout.item_choice,
                jobNames);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = (String) typeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        nUnitET = findViewById(R.id.numUnit);

        /**
         * commit Button
         */
        commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v -> {
            Editable text = nUnitET.getText();
            if (text == null) {
                return;
            } else if (text.toString() == "") {
                showByToast(BasicOrderActivity.this, "Please input number.");
                return;
            }
            nUnit = Integer.parseInt(text.toString());
            Log.d(TAG, LOG_FUNC_RUN + "User selected: from " + srcName + " to " + desName
                    + " move " + nUnit + " " + level);
            String result = "";
            // move action
            if (actionType.equals(UI_MOVE)) {
                doOneMove(buildMoveOrder(srcName, desName, nUnit, level),
                        new onResultListener() {
                            @Override
                            public void onSuccess() {
                                finish();
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                showByToast(BasicOrderActivity.this, errMsg);
                                Log.e(TAG, errMsg);
                            }
                        });
            }
            // attack action
            else if (actionType.equals(UI_ATK)) {
                doOneAttack(buildAttackOrder(srcName, desName, nUnit, level),
                        new onResultListener() {
                            @Override
                            public void onSuccess() {
                                finish();
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                showByToast(BasicOrderActivity.this, errMsg);
                                Log.e(TAG, errMsg);
                            }
                        });
            }
        });
    }
}
