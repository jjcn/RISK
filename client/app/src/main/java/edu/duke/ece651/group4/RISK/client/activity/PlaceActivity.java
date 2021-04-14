package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.shared.*;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class PlaceActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();

    private TextView instr;
    private ImageView mapIV;
    int numTerrA = -1;
    int numTerrB = -1;
    int numTerrC = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Placement");
        }
        impUI();
        Log.i(TAG,LOG_CREATE_SUCCESS);
    }


    private void impUI() {
        instr = findViewById(R.id.placeInstru);
        instr.append("You have total of "+PLACE_TOTAL+" soldiers.");
        mapIV = findViewById(R.id.worldFG).findViewById(R.id.world_image_view);
        mapIV.setImageResource(MAPS.get(getCurrentRoomSize()));

        LinearLayout terrA = findViewById(R.id.terrA);
        LinearLayout terrB = findViewById(R.id.terrB);

        EditText terrAETInput = terrA.findViewById(R.id.inputNum);
        EditText terrBETInput = terrB.findViewById(R.id.inputNum);

        List<Territory> myTerr = getMyTerritory();
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
            int total = numTerrA+numTerrB;

            if(total == PLACE_TOTAL){
                List<PlaceOrder> placements = new ArrayList<>();
                placements.add(new PlaceOrder(myTerr.get(0).getName(),new Troop(numTerrA,new TextPlayer(getUserName()))));
                placements.add(new PlaceOrder(myTerr.get(1).getName(),new Troop(numTerrB,new TextPlayer(getUserName()))));

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


//    /**
//     * set up UI for placing the territory list with number of players to choose.
//     */
//    private void impPlaceList() {
//
//    }

}