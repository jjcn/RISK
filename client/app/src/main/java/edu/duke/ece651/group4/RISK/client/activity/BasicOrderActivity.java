package edu.duke.ece651.group4.RISK.client.activity;

import android.util.Log;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.Territory;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.UI_ATK;
import static edu.duke.ece651.group4.RISK.client.Constant.UI_MOVE;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class BasicOrderActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private String srcName; // source territory name
    private String desName; // destination territory name
    private String typeName; // unit type name
    private int nUnit; // number of units in action
    private String actionType; // type of action, move or attack

    private static final String EXTRA_ACTION_TYPE = "actionType"; // intent extra key

    private Spinner srcSpinner;
    private Spinner desSpinner;
    private Spinner typeSpinner;
    private EditText nUnitET;
    private Button commitBT;
    private SpinnerAdapter desAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_order);

        srcName = "";
        desName = "";
        typeName = "";
        nUnit = -1;
        actionType = getIntent().getStringExtra(EXTRA_ACTION_TYPE);

        String actionType = getIntent().getStringExtra("actionType");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        List<String> myTerrNames = getMyTerrNames();
        List<String> enemyTerrNames = getEnemyTerrNames();

        srcSpinner = findViewById(R.id.terrSrc);
        SpinnerAdapter srcAdapter = new ArrayAdapter<>(
                BasicOrderActivity.this,
                R.layout.item_choice,
                myTerrNames);
        srcSpinner.setAdapter(srcAdapter);


        desSpinner = findViewById(R.id.terrDes);
        if(actionType.equals(UI_MOVE)) {
            desAdapter = new ArrayAdapter<>(
                    BasicOrderActivity.this,
                    R.layout.item_choice,
                    myTerrNames);
        }else {
            desAdapter = new ArrayAdapter<>(
                    BasicOrderActivity.this,
                    R.layout.item_choice,
                    enemyTerrNames);
        }
        desSpinner.setAdapter(desAdapter);

        List<String> typeNames = getLevelNames();
        typeSpinner = findViewById(R.id.soldierType);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(
                BasicOrderActivity.this,
                R.layout.item_choice,
                typeNames);
        typeSpinner.setAdapter(typeAdapter);

        nUnitET = findViewById(R.id.numUnit).findViewById(R.id.inputNum);

        commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v -> {
            srcSpinner.setOnItemClickListener((parent, view, position, id) -> {
                srcName = (String) srcAdapter.getItem(position);
            });
            desSpinner.setOnItemClickListener((parent, view, position, id) -> {
                desName = (String) desAdapter.getItem(position);
            });
            typeSpinner.setOnItemClickListener((parent, view, position, id) -> {
                typeName = (String) typeAdapter.getItem(position);
            });

            nUnit = Integer.parseInt(nUnitET.getText().toString());

            if (actionType.equals(UI_ATK)) {
                buildAttackOrder(srcName, desName, nUnit, typeName);
            }

            String result = null;
            if (actionType.equals(UI_MOVE)) {
                result = doOneMove(buildMoveOrder(srcName, desName, nUnit, typeName),
                        new onResultListener() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                Log.e(TAG, errMsg);
                            }
                        });
            }
            else if (actionType.equals(UI_ATK)) {
                result = doOneAttack(buildAttackOrder(srcName, desName, nUnit, typeName),
                        new onResultListener() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                Log.e(TAG, errMsg);
                            }
                        });
            }

            if (result == null) {
                finish();
            } else {
                showByToast(this, result);
            }
        });

    }
}