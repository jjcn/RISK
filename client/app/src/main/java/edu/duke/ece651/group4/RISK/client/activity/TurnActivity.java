package edu.duke.ece651.group4.RISK.client.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.client.utility.SimpleSelector;
import edu.duke.ece651.group4.RISK.client.utility.WaitDialog;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByReport;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.TECH_LEVEL_UPGRADE_COSTS;

/**
 * implement game with text input
 */
public class TurnActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    // todo: expendable list view
    // origin
    private ImageButton commitBT;
    private List<String> worldInfo;
    private ListView worldInfoRC;
    private ArrayAdapter<String> worldInfoAdapter;

    private String actionType;
    private List<String> actions;
    private Spinner chooseActionSP;
    private ArrayAdapter<String> actionAdapter;

    private List<String> noticeInfo;
    private ListView noticeInfoRC;
    private ArrayAdapter<String> noticesAdapter;

    private boolean isWatch; // turn to true after lose game.
    private WaitDialog waitDG;

    // added
    ArrayList<String> reportInfo;
    TextView playerInfo;
    ImageButton allyBT;
    ImageButton upTechBT;
    ImageButton reportBT;
    ImageButton doneBT;
    FloatingActionButton chatBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("RISK Room " + getWorld().getRoomID());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        actions = new ArrayList<>(Arrays.asList(UI_MOVE, UI_ATK, UI_UPTROOP, UI_UNLOCKTYPE, UI_CHANGETYPE));
        if (getCurrentRoomSize() < 3) {
            actions.remove(UI_ALLIANCE);
        }
        actionType = UI_MOVE; // default: move
        noticeInfo = new ArrayList<>();
        reportInfo = new ArrayList<>();
        isWatch = false;
        waitDG = new WaitDialog(TurnActivity.this);

        // added
        impUI();
        updateAllInfo();
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }

    /**
     * overwrite the functions to have chat and menu button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switchOut();
                finish();
                return true;
            case R.id.menu_chat:
                goChat();
                return true;
            case R.id.menu_devinfo:
                showColorEgg();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void impUI() {
        ImageView mapIV = findViewById(R.id.world_image_view);
        mapIV.setImageResource(MAPS.get(getCurrentRoomSize()));

        // text and button UI
        playerInfo = findViewById(R.id.playerInfo);
        allyBT = findViewById(R.id.ally);
        upTechBT = findViewById(R.id.upgradeTech);
        reportBT = findViewById(R.id.report);
        doneBT = findViewById(R.id.done);

        impReportBT();
        impUpTechBT();
        impAllyBT();
        impDoneButton();

        // list and spinner UI
        worldInfoRC = findViewById(R.id.worldInfo);
        impWorldInfoRC();
        chooseActionSP = findViewById(R.id.actions_spinner);
        commitBT = findViewById(R.id.commitBT);
        noticeInfoRC = findViewById(R.id.noticeInfo);

        impActionSpinner();
        impNoticeInfoRC();
        impCommitBT();
        impNEWUIBT();
    }

    private void impReportBT() {
        reportBT.setOnClickListener(v -> {
            StringBuilder report = new StringBuilder();
            for (String item : reportInfo) {
                report.append(item);
                report.append("\n");
            }
            showByReport(TurnActivity.this, "Battle report", report.toString());
        });
    }

    // todo: alert to confirm actions.
    private void impUpTechBT() {
        if(getTechLevel() == TECH_LEVEL_UPGRADE_COSTS.size()){
            upTechBT.setEnabled(false);
        }
        upTechBT.setOnClickListener(v -> {
            upTechBT.setEnabled(false); // can only upgrade tech once in a turn
            String msg = "(Upgrade will take effect next turn.)\n" + "To upgrade you will consume: " + TECH_LEVEL_UPGRADE_COSTS.get(getTechLevel());
            showUpTechConfirmDialog(UPTECH_CONFIRM, msg);
        });
    }

    private void showUpTechConfirmDialog(String title, String msg) {
        Log.i(TAG, LOG_FUNC_RUN + "enter up confirm");
        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, LOG_FUNC_RUN + "click yes");
            doOneUpgrade(new onResultListener() {
                @Override
                public void onSuccess() {
                    playerInfo.setText(getPlayerInfo());
                }

                @Override
                public void onFailure(String errMsg) {
                    showByToast(TurnActivity.this, errMsg);
                    upTechBT.setEnabled(true);
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> upTechBT.setEnabled(true));
        builder.show();
    }

    private void impAllyBT() {
        if (getCurrentRoomSize() < 3) {
            allyBT.setVisibility(View.GONE);
        } else {
            allyBT.setOnClickListener(v -> selectAlliance());
        }
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
        SimpleSelector selector = new SimpleSelector(TurnActivity.this, CHOOSE_USER_INSTR, choices, new onReceiveListener() {
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
                Log.e(TAG, LOG_FUNC_FAIL + errMsg);
            }
        });
        selector.show();
    }

    private void goChat() {
        Intent intent = new Intent(TurnActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    // todo --: passing method in show dialog
    private void impDoneButton() {
        doneBT.setOnClickListener(v -> {
            doneBT.setEnabled(false);
            if (isWatch) {
                showStayDialog();
            }
            else {
                showDoneDialog(CONFIRM, CONFIRM_ACTION);
            }
        });
    }

    private void showStayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
        builder.setTitle(LOSE_MSG)
                .setMessage(STAY_INSTR)
                .setPositiveButton("Yes", (dialog, which) -> {
                    waitNextTurn();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    switchOut();
                });
        builder.show();
    }


//    private void showStayDialog() {
//        Log.i(TAG,LOG_FUNC_RUN+"show stay dialog");
//        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
//        builder.setTitle(STAY_INSTR)
//                .setPositiveButton("Yes", (dialog, which) -> stayInGame(new onReceiveListener() {
//                    @Override
//                    public void onSuccess(Object o) {
//                        World world = (World) o;
//                        Log.i(TAG,LOG_FUNC_RUN+"stay dialog receive world");
//                        showByReport(TurnActivity.this, TURN_END, world.getReport());
//                        updateAllInfo();
//                        doneBT.performClick();
//                    }
//
//                    @Override
//                    public void onFailure(String errMsg) {
//                        Log.e(TAG, LOG_FUNC_FAIL + "watchGame should not fail");
//                    }
//                }))
//                .setNegativeButton("No", (dialog, which) -> switchOut());
//        builder.show();
//    }

    private void showDoneDialog(String title, String msg) {
        Log.i(TAG, LOG_FUNC_RUN + "enter done");
        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, LOG_FUNC_RUN + "click yes");
            waitNextTurn();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            if (isWatch) {
                switchOut();
            }
            doneBT.setEnabled(true);
        });
        builder.show();
    }

    private void waitNextTurn() {
        if (!isWatch) {
            waitDG.show();
        }
        doneBT.setEnabled(false);
        doDone(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                World world = (World) o;
                Log.i(TAG, LOG_FUNC_RUN + "Check end game result: "+ world.isGameEnd());
                if (world.isGameEnd()) {
                    // todo: can stay after finish
                    showByReport(TurnActivity.this, "Game end!", world.getWinner() + " won the game!");
                    Intent backRoom = new Intent(TurnActivity.this, RoomActivity.class);
                    startActivity(backRoom);
                    finish();
                }
                else if (world.checkLost(getUserName())) {
                    Log.i(TAG, LOG_FUNC_RUN + "Lose game.");
                    showByToast(TurnActivity.this, LOSE_MSG);
                    shutDownBTinWatch();
                    doneBT.performClick();
                }else {
                    showByToast(TurnActivity.this, TURN_END);
                    updateAfterTurn();
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_RUN + "Should not receive error message after done.");
            }
        });
    }

    private void shutDownBTinWatch() {
        doneBT.setVisibility(View.GONE);
        upTechBT.setVisibility(View.GONE);
        allyBT.setVisibility(View.GONE);
        commitBT.setVisibility(View.GONE);
    }

    /**
     * send null to server and waiting for receive World.
     */
    private void watchGame() {
        Log.i(TAG,LOG_FUNC_RUN+"watchGame called");
        waitDG.cancel();
        shutDownBTinWatch();
        showStayDialog();
    }

    private void updateAfterTurn() {
        runOnUiThread(() -> {
            updateAllInfo();
            doneBT.setEnabled(true);
            upTechBT.setEnabled(true);
            if (getAllianceName().equals(NO_ALLY)) {
                allyBT.setEnabled(true);
            }
            reportInfo.add("Turn " + getWorld().getTurnNumber() + ": \n" + getWorld().getReport() + "\n\n");
            waitDG.cancel();

            commitBT.setClickable(true);
            Log.i(TAG, LOG_FUNC_RUN + "updateInfo after turn Done");
        });
    }

    private void updateAllInfo() {
        runOnUiThread(() -> {
                    Log.i(TAG, LOG_FUNC_RUN + "call update");
                    playerInfo.setText(getPlayerInfo());

                    worldInfo.clear();
                    worldInfo.addAll(getWorldInfo());
                    worldInfoAdapter.notifyDataSetChanged();

                    noticeInfo.clear();
                    int currTurn = getWorld().getTurnNumber();
                    String notice = "Turn " + (currTurn - 1) + ":\n" + getWorld().getReport() + "\nTurn: " + currTurn + " in progress";
                    noticeInfo.add(notice);
                    noticesAdapter.notifyDataSetChanged();
                }
        );
    }

    private void showColorEgg() {
        showByReport(TurnActivity.this, "\\^^/", COLOR_EGG);
//        soundPool = new SoundPool.Builder().build();
//        soundID = soundPool.load(this, R.raw.qipao, 1);
    }

    /*************************************************************************************************/

    private void impWorldInfoRC() {
        worldInfo = getWorldInfo();
        worldInfoAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, worldInfo);
        worldInfoRC.setAdapter(worldInfoAdapter);
    }

    private void switchOut() {
        switchGame();
        Intent intent = new Intent(TurnActivity.this, RoomActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    private void impNEWUIBT() {
        Button newUIBT = findViewById(R.id.newUIFeature);
        newUIBT.setOnClickListener(v -> {
            Intent intent = new Intent(TurnActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void impNoticeInfoRC() {
        noticeInfo = new ArrayList<>();
        noticesAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, noticeInfo);
        noticeInfoRC.setDividerHeight(0);
        noticeInfoRC.setAdapter(noticesAdapter);
    }

    private void impActionSpinner() {
        actionAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, actions);
        chooseActionSP.setAdapter(actionAdapter);
        chooseActionSP.setSelection(0, false);
        chooseActionSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actionType = actionAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void impCommitBT() {
        commitBT.setOnClickListener(v -> {
            commitBT.setClickable(false);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            switch (actionType) {
                case UI_MOVE:
                case UI_ATK:
                    intent.setComponent(new ComponentName(TurnActivity.this, BasicOrderActivity.class));
                    bundle.putSerializable("actionType", actionType);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case UI_UPTROOP:
                    intent.setComponent(new ComponentName(TurnActivity.this, UpgradeActivity.class));
                    startActivity(intent);
                    break;
                case UI_UNLOCKTYPE:
                    selectType();
                    break;
                case UI_CHANGETYPE:
                    intent.setComponent(new ComponentName(TurnActivity.this, TransferActivity.class));
                    startActivity(intent);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + actionType);
            }
            commitBT.setClickable(true);
        });
    }

    /**
     * Select a unit type from an alert dialog.
     */
    private void selectType() {
        List<String> types = new ArrayList<>(getUnLockableTypes());
        SimpleSelector selector = new SimpleSelector(TurnActivity.this,
                CHOOSE_TYPE_INSTR, types, new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof String) {
                    String type = (String) o;
                    Log.d(TAG, LOG_FUNC_RUN + "User selected in type dialog: " + type);
                    unlockType(type, new onResultListener() {
                        @Override
                        public void onSuccess() {
                            if (getUnLockedTypesWithoutSoldier().size() == 0) {
                                actions.add(UI_CHANGETYPE);
                                actionAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            showByToast(TurnActivity.this, errMsg);
                        }
                    });
                } else {
                    onFailure("not String name");
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_FAIL + errMsg);
            }
        });
        selector.show();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, LOG_FUNC_RUN + "call on resume");
        super.onResume();
        updateAllInfo();
    }
}