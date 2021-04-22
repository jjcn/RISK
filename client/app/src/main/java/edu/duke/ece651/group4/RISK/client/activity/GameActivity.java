package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.fragment.ActionsFragment;
import edu.duke.ece651.group4.RISK.client.fragment.TerrFragment;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.client.utility.SimpleSelector;
import edu.duke.ece651.group4.RISK.client.utility.WaitDialog;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.*;

public class GameActivity extends AppCompatActivity {
    private final String TAG = GameActivity.class.getSimpleName();
    private boolean isWatch;
    private WaitDialog waitDG;

    TextView playerInfo;
    ImageButton allyBT;
    ImageButton upTechBT;
    ImageButton reportBT;
    ListView worldInfoRC;
    ArrayAdapter worldInfoAdapter;
    List<String> worldInfo;
    Button doneBT;
    FloatingActionButton chatBT;
    ArrayList<String> noticeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, LOG_FUNC_RUN + "begin create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("RISK Room "+getWorld().getRoomID());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        isWatch = false;
        waitDG = new WaitDialog(GameActivity.this);
        noticeInfo = new ArrayList<>();
        impUI();
        updateAllInfo();
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }

    /**
     * overwrite the functions to have switch room and back and menu button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitGame();
                return true;
            case R.id.menu_rooms:
                exitGame();
                finish();
                return true;
            case R.id.menu_devinfo:
                showByToast(GameActivity.this, COLOR_EGG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TODO: historyInfo button, upTech button
    private void impUI() {
        playerInfo = findViewById(R.id.playerInfo);
        allyBT = findViewById(R.id.ally);
        upTechBT = findViewById(R.id.upgradeTech);
        doneBT = findViewById(R.id.done);
        worldInfoRC = findViewById(R.id.worldInfo);
        chatBT = findViewById(R.id.chatButton);
        reportBT = findViewById(R.id.report);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.terrInfo_container);
        if (fragment == null) {
            fragment = new TerrFragment();
            fm.beginTransaction()  // start a new fragment task
                    .add(R.id.terrInfo_container, fragment)
                    .commit();
        }
        //addFragment(fm, R.id.terrInfo_container);
        //addFragment(fm, R.id.fragment_enemyTerr_container);

        impReportBT();
        impUpTechBT();
        impAllyBT();
        impDoneButton();
        impWorldInfoRC();
        impChatBT();
    }

    private void impReportBT() {
        reportBT.setOnClickListener(v->{
            StringBuilder report = new StringBuilder();
            for(String item: noticeInfo){
                report.append(item);
                report.append("\n");
            }
            showByReport(GameActivity.this,"Battle report", report.toString());
        });
    }

    // todo: alert to confirm actions.
    private void impUpTechBT() {
        upTechBT.setOnClickListener(v -> {
            upTechBT.setEnabled(false);
            doOneUpgrade(new onResultListener() {
                @Override
                public void onSuccess() {
                    playerInfo.setText(getPlayerInfo());
                }

                @Override
                public void onFailure(String errMsg) {
                    showByToast(GameActivity.this, errMsg);
                }
            });
        });
    }

    private void impAllyBT() {
        allyBT.setOnClickListener(v -> {
            selectAlliance();
        });
    }

    /**
     * you can only select once.
     */
    private void selectAlliance() {
        ArrayList<String> choices = new ArrayList<>();
        // you can not ally with yourself
        for (String playerName : getAllPlayersName()) {
            if (!playerName.equals(getUserName())) {
                choices.add(playerName);
            }
        }
        SimpleSelector selector = new SimpleSelector(GameActivity.this, CHOOSE_USER_INSTR, choices, new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof String) {
                    String alliance = (String) o;
                    requireAlliance(alliance);
                    allyBT.setEnabled(false);
                } else {
                    onFailure("not String name");
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, errMsg);
            }
        });
        selector.show();
    }

    private void impChatBT() {
        chatBT.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }

    private void impWorldInfoRC() {
        worldInfo = getWorldInfo();
        worldInfoAdapter = new ArrayAdapter<>(GameActivity.this, R.layout.item_choice, worldInfo);
        worldInfoRC.setAdapter(worldInfoAdapter);
    }

    // todo --: passing method in show dialog
    private void impDoneButton() {
        doneBT.setOnClickListener(v -> {
            doneBT.setEnabled(false);
            if (isWatch) {
                showDoneDialog(LOSE_MSG, STAY_INSTR);
            } else {
                showDoneDialog(CONFIRM, CONFIRM_ACTION);
            }
        });
    }

    private void showDoneDialog(String title, String msg) {
        Log.i(TAG, LOG_FUNC_RUN + "enter done");
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, LOG_FUNC_RUN + "click yes");
            waitNextTurn();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            if (isWatch) {
                exitGame();
            }
        });
        builder.show();
    }

    private void showUpConfirmDialog(String title, String msg) {
        Log.i(TAG, LOG_FUNC_RUN + "enter confirm");
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, LOG_FUNC_RUN + "click yes");
            waitNextTurn();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            if (isWatch) {
                exitGame();
            }
        });
        builder.show();
    }

    private void waitNextTurn() {
        waitDG.show();
        doneBT.setEnabled(false);
        doDone(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                World world = (World) o;
                if (world.isGameEnd()) {
                    // todo: can stay after finish
                    showByReport(GameActivity.this, "Game end!", world.getWinner() + " won the game!");
                    Intent backRoom = new Intent(GameActivity.this, RoomActivity.class);
                    startActivity(backRoom);
                    finish();
                }
                if (world.checkLost(getUserName())) {
                    showByReport(GameActivity.this, LOSE_MSG, world.getReport());
                    watchGame();
                }
                showByToast(GameActivity.this, TURN_END);
                updateAfterTurn();
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_RUN + "Should not receive error message after done.");
            }
        });
    }

    /**
     * send null to server and waiting for receive World.
     */
    private void watchGame() {
        waitDG.cancel();
        doneBT.setVisibility(View.GONE);
        upTechBT.setVisibility(View.GONE);
        allyBT.setVisibility(View.GONE);
        showStayDialog();
    }

    private void showStayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle(STAY_INSTR)
                .setPositiveButton("Yes", (dialog, which) -> {
                    stayInGame(new onReceiveListener() {
                        @Override
                        public void onSuccess(Object o) {
                            World world = (World) o;
                            showByReport(GameActivity.this, TURN_END, world.getReport());
                            updateAllInfo();
                            watchGame();
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            Log.e(TAG, LOG_FUNC_FAIL + "watchGame should not fail");
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    exitGame();
                });
        builder.show();
    }

    private void updateAfterTurn() {
        runOnUiThread(() -> {
            updateAllInfo();
            doneBT.setEnabled(true);
            upTechBT.setEnabled(true);
            if (getAllianceName().equals(NO_ALLY)) {
                allyBT.setEnabled(true);
            }
            noticeInfo.add("Turn " + getWorld().getTurnNumber() + ": \n" + getWorld().getReport());
            waitDG.cancel();
        });
    }

    private void updateAllInfo() {
        runOnUiThread(() -> {
                    Log.i(TAG, LOG_FUNC_RUN + "call update");
                    playerInfo.setText(getPlayerInfo());
                    worldInfo.clear();
                    worldInfo.addAll(getWorldInfo());
                    worldInfoAdapter.notifyDataSetChanged();
                }
        );
    }

    @Override
    protected void onResume() {
        Log.i(TAG, LOG_FUNC_RUN + "call on resume");
        super.onResume();
        updateAllInfo();
    }
}
