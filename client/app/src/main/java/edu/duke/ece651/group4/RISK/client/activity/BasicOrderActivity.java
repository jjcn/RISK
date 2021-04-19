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

import static edu.duke.ece651.group4.RISK.client.Constant.*;
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

    private ImageView worldImageView;
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
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        worldImageView = findViewById(R.id.world_image_view);
        worldImageView.setImageResource(MAPS.get(getCurrentRoomSize()));

        srcName = "";
        desName = "";
        typeName = "";
        nUnit = -1;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            actionType = bundle.getString(EXTRA_ACTION_TYPE);
        }

        String actionType = getIntent().getStringExtra("actionType");

        Log.i(TAG, LOG_FUNC_RUN + "ACT TYPE" + actionType);
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

        // source terr spinner
        srcSpinner = findViewById(R.id.terrSrc);
        SpinnerAdapter srcAdapter = new ArrayAdapter<>(
                BasicOrderActivity.this,
                R.layout.item_choice,
                myTerrNames);
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
                    myTerrNames);
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

        // typeNames spinner
        List<String> typeNames = getLevelNames();
        typeSpinner = findViewById(R.id.soldierType);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(
                BasicOrderActivity.this,
                R.layout.item_choice,
                typeNames);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeName = (String) typeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        nUnitET = findViewById(R.id.numUnit);

        commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v -> {
            Editable text = nUnitET.getText();
            if(text == null){
                return;
            }else if(text.toString()==""){
                showByToast(BasicOrderActivity.this,"Please input number.");
                return;
            }
            nUnit = Integer.parseInt(text.toString());
            Log.d(TAG, LOG_FUNC_RUN + "User selected: from " + srcName + " to " + desName
                    + " move " + nUnit + " " + typeName);
            String result = "";
            if (actionType.equals(UI_MOVE)) {
                result = doOneMove(buildMoveOrder(srcName, desName, nUnit, typeName)
                );
            } else if (actionType.equals(UI_ATK)) {
                result = doOneAttack(buildAttackOrder(srcName, desName, nUnit, typeName)
                );
            }

            if (result == null) {
                finish();
            } else {
                showByToast(this, result);
            }
        });

    }
}