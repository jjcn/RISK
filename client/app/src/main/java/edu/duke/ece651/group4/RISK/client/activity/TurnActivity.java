package edu.duke.ece651.group4.RISK.client.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.adapter.WorldInfoAdapter;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.BasicOrder;
import edu.duke.ece651.group4.RISK.shared.Constant;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

/**
 * implement game with text input
 */
public class TurnActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    // TODO:expendable list view
    private Button switchBT;
    private ListView worldInfoRC;
    private ArrayAdapter<String> worldInfoAdapter;
    private Spinner chooseActionSP;
    private ArrayAdapter<String> actionAdapter;
    private ListView noticeInfoRC;
    private ArrayAdapter<String> noticesAdapter;
    private TextView userInfoTV;

    private String actionType;
    private boolean isWatch; // turn to true after lose game.
    private WaitDialog waitDG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionType = UI_MOVE; // default: move
        isWatch = false;
        waitDG = new WaitDialog(TurnActivity.this);
        impUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            //TODO: switch rooms
            case R.id.menu_rooms:
                // waitDG.show();
                return true;
            case R.id.menu_devinfo:
                showByToast(TurnActivity.this, COLOR_EGG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    private void impUI() {
        chooseActionSP = findViewById(R.id.actions_spinner);
        switchBT = findViewById(R.id.switchOut);
        worldInfoRC = findViewById(R.id.terrInfo);
        userInfoTV = findViewById(R.id.playerInfo);
        noticeInfoRC = findViewById(R.id.noticeInfo);
        impActionSpinner();
        impWorldInfoRC();
        impNoticeInfoRC();
        impSwitchBT();
    }

    private void impWorldInfoRC() {
        List<String> worldInfo = getWorldInfo();
        worldInfoAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, worldInfo);
        worldInfoRC.setAdapter(worldInfoAdapter);
    }

    private void impNoticeInfoRC() {
        List<String> worldInfo = getWorldInfo();
        noticesAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, worldInfo);
        noticeInfoRC.setAdapter(noticesAdapter);
    }

    private void impSwitchBT() {
        switchBT.setOnClickListener(v -> {
            Intent joinIntent = new Intent(TurnActivity.this, RoomActivity.class);
            startActivity(joinIntent);
            finish();
        });
    }

//    private void impActionSpinner() {
//        List<String> actions = new ArrayList<>(Arrays.asList(UI_MOVE, UI_ATK, UI_UPTECH, UI_UPTROOP, UI_DONE));
//        actionAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, actions);
//        chooseActionSP.setAdapter(actionAdapter);
////        chooseActionSP.setOnItemClickListener((parent, view, position, id) -> {
//        chooseActionSP.setOnItemSelectedListener((parent, view, position, id) -> {
//            actionType = actionAdapter.getItem(position);
//            impCommit();
//        });
//    }
    private void impActionSpinner() {
        List<String> actions = new ArrayList<>(Arrays.asList(UI_MOVE, UI_ATK, UI_UPTECH, UI_UPTROOP, UI_DONE));
        actionAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, actions);
        chooseActionSP.setAdapter(actionAdapter);
        chooseActionSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actionType = actionAdapter.getItem(position);
                impCommit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing
            }
        });
            /*
            chooseActionSP.setOnItemClickListener((parent, view, position, id) -> {
                actionType = actionAdapter.getItem(position);
                impCommit();
            });
            */
    }
    private void impCommit() {
        // commitBT.setClickable(false);
        Intent intent = new Intent();
        switch (actionType) {
            case UI_MOVE:
            case UI_ATK:
                intent.setComponent(new ComponentName(TurnActivity.this, BasicOrderActivity.class));
                intent.putExtra("actionType", actionType);
                break;
            case UI_UPTROOP:
                intent.setComponent(new ComponentName(TurnActivity.this, UpgradeActivity.class));
                break;
            case UI_UPTECH:
                upgradeTech();
            case UI_DONE:
                if (isWatch) {
                    showStayDialog();
                }
                waitNextTurn();
        }
    }

    private void showStayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
        builder.setTitle(LOSE_MSG);
        builder.setMessage(STAY_INSTR);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            waitNextTurn();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            Intent joinGame = new Intent(TurnActivity.this, RoomActivity.class);
            startActivity(joinGame);
            finish();
        });
    }

    private void upgradeTech() {
        doOneUpgrade(new onResultListener() {
            @Override
            public void onSuccess() {
                userInfoTV.setText(getPlayerInfo());
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, errMsg);
            }
        });
    }

    // TODO: alert to confirm
    private void waitNextTurn() {
        waitDG.show();
        doDone(new BasicOrder(null, null, null, 'D'), new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof World) {
                    World world = (World) o;
                    if (world.isGameEnd()) {
                        runOnUiThread(() -> {
                            showByToast(TurnActivity.this, world.getWinner() + "wins the game!");
                            Intent joinGame = new Intent(TurnActivity.this, RoomActivity.class);
                            startActivity(joinGame);
                            finish();
                        });
                    }
                    isWatch = ((World) o).checkLost(getUserName());
                    if (isWatch) {
                        actionType = UI_DONE;
                        runOnUiThread(() -> {
                            impCommit();
                        });
                    } else {
                        World newWorld = (World) o;
                        updateAfterTurn(newWorld);
                        showByToast(TurnActivity.this, TURN_END);
                    }
                } else {
                    this.onFailure("receive not a World");
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, "login: " + errMsg);
            }
        });
    }

    private void updateAfterTurn(World world) {
        runOnUiThread(() -> {
            if (isWatch) {
                chooseActionSP.setVisibility(View.GONE);
                userInfoTV.setVisibility(View.GONE);
            }
            userInfoTV.setText(getPlayerInfo());
            waitDG.cancel();
        });
    }
}