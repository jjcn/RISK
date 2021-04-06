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

public class BasicOrderActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private String src;
    private String des;
    private int num;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_order);

        src = "";
        des = "";
        type = "";
        num = -1;
        impUI();
    }

    private void impUI() {
        List<Territory> terrChoices = getMyTerritory();
        Spinner srcChoices = findViewById(R.id.terrSrc);
        SpinnerAdapter srcAdapter = new ArrayAdapter<>(BasicOrderActivity.this, R.layout.item_choice, terrChoices);
        srcChoices.setAdapter(srcAdapter);

        Spinner desChoices = findViewById(R.id.terrDes);
        SpinnerAdapter desAdapter = new ArrayAdapter<>(BasicOrderActivity.this, R.layout.item_choice, terrChoices);
        desChoices.setAdapter(desAdapter);

        Spinner typeChoices = findViewById(R.id.soldierType);
        SpinnerAdapter typeAdapter = new ArrayAdapter<>(BasicOrderActivity.this, R.layout.item_choice, terrChoices);
        typeChoices.setAdapter(typeAdapter);

        Button commitBT = findViewById(R.id.commit_button);
        commitBT.setOnClickListener(v->{
            srcChoices.setOnItemClickListener((parent, view, position, id) -> {
                src = (String) srcAdapter.getItem(position);
            });
            desChoices.setOnItemClickListener((parent, view, position, id) -> {
                des = (String) desAdapter.getItem(position);
            });
            typeChoices.setOnItemClickListener((parent, view, position, id) -> {
                type = (String) typeAdapter.getItem(position);
            });

            EditText numIn = findViewById(R.id.numUnit);
            num = Integer.parseInt(numIn.toString());

            String result = doOneMove(buildMoveOrder(src, des, num, type), new onResultListener() {
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
            }
        });
    }
}