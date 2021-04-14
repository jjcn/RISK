package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.utility.WaitDialog;
import edu.duke.ece651.group4.RISK.shared.*;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class PlaceActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();

    private TextView instr;
    private ImageView mapIV;
    int numTerrA = -1;
    int numTerrB = -1;
    private WaitDialog waitDG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Placement");
        }
        waitDG = new WaitDialog(PlaceActivity.this,WAIT_PLACEMENT);
        impUI();
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }


    private void impUI() {
        instr = findViewById(R.id.placeInstru);
        instr.append("You have total of " + PLACE_TOTAL + " soldiers.");
        mapIV = findViewById(R.id.worldFG).findViewById(R.id.world_image_view);
        mapIV.setImageResource(MAPS.get(getCurrentRoomSize()));
        List<Territory> myTerr = getMyTerritory(); // two for each player

        LinearLayout terrA = findViewById(R.id.terrA);
        LinearLayout terrB = findViewById(R.id.terrB);

        EditText terrAETInput = terrA.findViewById(R.id.inputNum);
        EditText terrBETInput = terrB.findViewById(R.id.inputNum);

        TextView terrATV = terrA.findViewById(R.id.placeinstrTV);
        terrATV.append(myTerr.get(0).getName());
        TextView terrBTV = terrB.findViewById(R.id.placeinstrTV);
        terrBTV.append(myTerr.get(1).getName());

        Button commitBT = findViewById(R.id.buttonCommitPlace);
        commitBT.setOnClickListener(v -> {
            commitBT.setClickable(false);

            // check total number
            numTerrA = Integer.parseInt(String.valueOf(terrAETInput.getText()));
            numTerrB = Integer.parseInt(String.valueOf(terrBETInput.getText()));
            int total = numTerrA + numTerrB;

            if (total == PLACE_TOTAL) {
                List<PlaceOrder> placements = new ArrayList<>();
                placements.add(new PlaceOrder(myTerr.get(0).getName(), new Troop(numTerrA, new TextPlayer(getUserName()))));
                placements.add(new PlaceOrder(myTerr.get(1).getName(), new Troop(numTerrB, new TextPlayer(getUserName()))));

                doPlacement(placements, new onReceiveListener() {
                    @Override
                    public void onSuccess(Object o) {
                        showByToast(PlaceActivity.this, PLACEMENT_DONE);
                        Intent intent = new Intent(PlaceActivity.this, TurnActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        showByToast(PlaceActivity.this, errMsg);
                    }
                });
            } else if (total > PLACE_TOTAL) {
                showByToast(PlaceActivity.this, PLACEMENT_MORE);
                commitBT.setClickable(true);
            } else {
                showByToast(PlaceActivity.this, PLACEMENT_LESS);
                commitBT.setClickable(true);
            }
        });
    }

//    /**
//     * set up UI for placing the territory list with number of players to choose.
//     */
//    private void impPlaceList() {
//
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK) {
            showByToast(PlaceActivity.this, "You cannot return during placement.");
        }
        return true;
    }
}