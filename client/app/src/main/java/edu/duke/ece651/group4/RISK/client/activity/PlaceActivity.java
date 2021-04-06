package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.shared.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class PlaceActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();

    int numTerrA = -1;
    int numTerrB = -1;
    int numTerrC = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        impUI();
        Log.i(TAG,LOG_CREATE_SUCCESS);
    }

    private void impUI() {
        EditText terrAETInput = findViewById(R.id.terrA).findViewById(R.id.inputNum);
        EditText terrBETInput = findViewById(R.id.terrB).findViewById(R.id.inputNum);
        EditText terrCETInput = findViewById(R.id.terrC).findViewById(R.id.inputNum);
        Button commitBT = findViewById(R.id.buttonCommitPlace);

        commitBT.setOnClickListener(v -> {
            commitBT.setClickable(false);

            List<String> myTerrNames = RISKApplication.getMyTerrNames();
            terrAETInput.append(myTerrNames.get(0));
            terrBETInput.append(myTerrNames.get(1));
            terrCETInput.append(myTerrNames.get(2));
            // check total number
            numTerrA = Integer.parseInt(String.valueOf(terrAETInput.getText()));
            numTerrB = Integer.parseInt(String.valueOf(terrBETInput.getText()));
            numTerrC = Integer.parseInt(String.valueOf(terrCETInput.getText()));
            int total = numTerrA+numTerrB+numTerrC;

            if(total == PLACE_TOTAL){
                List<PlaceOrder> placements = new ArrayList<>();
                placements.add(new PlaceOrder(myTerrNames.get(0), new Troop(numTerrA,new TextPlayer(getUserName()))));
                placements.add(new PlaceOrder(myTerrNames.get(0),new Troop(numTerrB,new TextPlayer(getUserName()))));
                placements.add(new PlaceOrder(myTerrNames.get(0),new Troop(numTerrC,new TextPlayer(getUserName()))));

                doPlacement(placements, new onReceiveListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.i(TAG,LOG_FUNC_RUN+"try to receive World");
                        if (o instanceof World) {
                            Log.i(TAG,LOG_FUNC_RUN+"receive a World");
                            runOnUiThread(() -> {
                                showByToast(PlaceActivity.this, PLACEMENT_DONE);
                                Intent intent = new Intent(PlaceActivity.this, TurnActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }else{
                            Log.i(TAG,LOG_FUNC_FAIL+"receive not World");
                        }
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        Log.e(TAG, "create room:receive world: " + errMsg);
                    }
                });
            }else if (total > PLACE_TOTAL){
                showByToast(PlaceActivity.this,PLACEMENT_MORE);
                commitBT.setClickable(true);
            }else {
                showByToast(PlaceActivity.this,PLACEMENT_LESS);
                commitBT.setClickable(true);
            }
        });
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

//    /**
//     * set up UI for placing the territory list with number of players to choose.
//     */
//    private void impPlaceList() {
//
//    }

}