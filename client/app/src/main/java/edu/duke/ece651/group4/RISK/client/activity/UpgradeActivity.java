package edu.duke.ece651.group4.RISK.client.activity;

import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.Territory;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.UNIT_NAMES;

public class UpgradeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private String terr;
    private int from;
    private int num;
    private int to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        this.terr = "";
        this.from = -1;
        this.to = -1;
        this.num = -1;
        impUI();
    }

    private void impUI() {
        List<Territory> terrChoices = getMyTerritory();
        Spinner srcChoices = findViewById(R.id.terrSrc);
        SpinnerAdapter srcAdapter = new ArrayAdapter<>(UpgradeActivity.this, R.layout.item_choice, terrChoices);
        srcChoices.setAdapter(srcAdapter);

        Spinner fromChoices = findViewById(R.id.terrDes);
        SpinnerAdapter desAdapter = new ArrayAdapter<>(UpgradeActivity.this, R.layout.item_choice, UNIT_NAMES);
        fromChoices.setAdapter(desAdapter);

        Spinner toChoices = findViewById(R.id.soldierType);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(UpgradeActivity.this, R.layout.item_choice, UNIT_NAMES);
        toChoices.setAdapter(typeAdapter);

        Button commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v->{
            commitBT.setClickable(false);
            srcChoices.setOnItemClickListener((parent, view, position, id) -> {
                terr = (String) srcAdapter.getItem(position);
            });
            fromChoices.setOnItemClickListener((parent, view, position, id) -> {
                from = position;
            });
            toChoices.setOnItemClickListener((parent, view, position, id) -> {
                to = position;
            });

            EditText numIn = findViewById(R.id.numUnit);
            num = Integer.parseInt(numIn.toString());

            String result = doSoliderUpgrade(buildUpOrder(terr, from, to, num), new onResultListener() {
                @Override
                public void onSuccess() { }
                @Override
                public void onFailure(String errMsg) {
                    Log.e(TAG,errMsg);
                }
            });
            if(result == null){
                finish();
            }else{
                showByToast(this,result);
                commitBT.setClickable(true);
            }
        });
    }
}