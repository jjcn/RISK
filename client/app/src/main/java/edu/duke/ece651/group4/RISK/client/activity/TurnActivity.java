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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.utility.WaitDialog;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.BasicOrder;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showSelector;
import static edu.duke.ece651.group4.RISK.shared.Constant.SWITCH_OUT_ACTION;

/**
 * implement game with text input
 */
public class TurnActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    // TODO--: expendable list view
    private Button commitBT;
    private ListView worldInfoRC;
    private ArrayAdapter<String> worldInfoAdapter;
    private Spinner chooseActionSP;
    private ArrayAdapter<String> actionAdapter;
    private ListView noticeInfoRC;
    private ArrayAdapter<String> noticesAdapter;
    private ListView userInfoRC;
    private ArrayAdapter<String> userInfoAdapter;
    private ImageView mapIV;
    private SwipeRefreshLayout refreshGS;
    private List<String> worldInfo;
    private List<String> noticeInfo;
    private List<String> userInfo;

    private String actionType;
    private boolean isWatch; // turn to true after lose game.
    private WaitDialog waitDG;

    List<String> actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        actions = new ArrayList<>(Arrays.asList(UI_MOVE, UI_ATK, UI_UPTECH, UI_UPTROOP, UI_DONE));
        actionType = UI_MOVE; // default: move
        isWatch = false;
        waitDG = new WaitDialog(TurnActivity.this);
        Log.i(TAG, LOG_CREATE_SUCCESS + "start");
        impUI();
        updateAfterTurn();
    }

    /**
     * overwrite the functions to have switch room and back and menu button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_chat:
                goChat();
            case R.id.menu_rooms:
                switchOut();
                return true;
            case R.id.menu_devinfo:
                showByToast(TurnActivity.this, COLOR_EGG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goChat() {
        Intent intent = new Intent(TurnActivity.this,ChatActivity.class);
        startActivity(intent);
    }

    private void switchOut() {
        exitGame();
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

    private void impUI() {
        chooseActionSP = findViewById(R.id.actions_spinner);
        commitBT = findViewById(R.id.commitBT);
        worldInfoRC = findViewById(R.id.terrInfo);
        userInfoRC = findViewById(R.id.playerInfo);
        noticeInfoRC = findViewById(R.id.noticeInfo);
        mapIV = findViewById(R.id.world_image_view);
        refreshGS = findViewById(R.id.refreshInfo);
        Log.i(TAG, LOG_FUNC_RUN + refreshGS);

        mapIV.setImageResource(MAPS.get(getCurrentRoomSize()));
        impActionSpinner();
        impWorldInfoRC();
        impNoticeInfoRC();
        impUserInfoRC();
        impCommitBT();
        impSwipeFresh();
    }

    private void impUserInfoRC() {
        userInfo = new ArrayList<>();
        userInfo.add(getPlayerInfo());
        userInfoAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, userInfo);
        userInfoRC.setAdapter(userInfoAdapter);
    }

    private void impSwipeFresh() {
        Log.i(TAG, LOG_FUNC_RUN + "start swipe fresh");
        refreshGS.setOnRefreshListener(this::updateAfterTurn);
    }

    private void impWorldInfoRC() {
        worldInfo = getWorldInfo();
        worldInfoAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, worldInfo);
        worldInfoRC.setAdapter(worldInfoAdapter);
    }

    private void impNoticeInfoRC() {
        noticeInfo = new ArrayList<>();
        noticesAdapter = new ArrayAdapter<>(TurnActivity.this, R.layout.item_choice, noticeInfo);
        noticeInfoRC.setAdapter(noticesAdapter);
    }

    private void impActionSpinner() {
        List<String> actions = new ArrayList<>(Arrays.asList(UI_MOVE, UI_ATK, UI_UPTECH, UI_UPTROOP, UI_DONE));
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
                // nothing yet
            }
        });
    }

    // TODO: alert to confirm actions.
    private void impCommitBT() {
        commitBT.setOnClickListener(v -> {
            commitBT.setClickable(false);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            Log.e(TAG, LOG_FUNC_RUN + "commitBT");
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
                case UI_UPTECH:
                    upgradeTech();
                    break;
                case UI_DONE:
                    Log.i(TAG, LOG_FUNC_RUN + "is watch" + isWatch);
                    if (isWatch) {
                        showStayDialog();
                    } else {
                        showConfirmDialog();
                    }
                    break;
                case UI_ALLIANCE:
                    String choice = showSelector(TurnActivity.this, getMyTerrNames());
                    requireAlliance(choice);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + actionType);
            }
            commitBT.setClickable(true);
            updateAfterTurn();
        });
    }

    private void showConfirmDialog() {
        Log.i(TAG, LOG_FUNC_RUN + "enter confirm");
        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
        builder.setTitle(CONFIRM);
        builder.setMessage(CONFIRM_ACTION);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, LOG_FUNC_RUN + "click yes");
            waitNextTurn();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.show();
    }


    private void showStayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
        builder.setTitle(LOSE_MSG)
                .setMessage(STAY_INSTR)
                .setPositiveButton("Yes", (dialog, which) -> {
                    waitNextTurn();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    exitGame();
                });
        builder.show();
    }

    private void upgradeTech() {
        doOneUpgrade(new onResultListener() {
            @Override
            public void onSuccess() {
                userInfo.clear();
                userInfo.add(getPlayerInfo());
                userInfoAdapter.notifyDataSetChanged();
                actions.remove(UI_UPTECH); // can only upgrade once in one turn
                actionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, errMsg);
            }
        });
    }

    private void waitNextTurn() {
        if (!isWatch) {
            waitDG.show();
        }
        commitBT.setClickable(false);
        doDone(new BasicOrder(null, null, null, 'D'), new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                World world = (World) o;
                if (world.isGameEnd()) {
                    showByToast(TurnActivity.this, world.getWinner() + " won the game!");
                    Intent joinGame = new Intent(TurnActivity.this, RoomActivity.class);
                    startActivity(joinGame);
                    finish();
                }
                isWatch = world.checkLost(getUserName());
                if (isWatch) {
                    actionType = UI_DONE;
                    runOnUiThread(() -> {
                        commitBT.performClick();
                    });
                } else {
                    updateAfterTurn();
                    showByToast(TurnActivity.this, TURN_END);
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_RUN + "Should not receive error message after done.");
            }
        });
    }

    private void updateAfterTurn() {
        runOnUiThread(() -> {
                    if (isWatch) {
                        chooseActionSP.setVisibility(View.GONE);
                        userInfoRC.setVisibility(View.GONE);
                        commitBT.setVisibility(View.GONE);
                    }
                    Log.i(TAG, LOG_FUNC_RUN + "call update after turn");
                    noticeInfo.clear();
                    noticeInfo.add(getPlayerInfo());
                    noticeInfo.add(getWorld().getReport());
                    noticesAdapter.notifyDataSetChanged();
                    worldInfo.clear();
                    worldInfo.addAll(getWorldInfo());
                    worldInfoAdapter.notifyDataSetChanged();
                    Log.i(TAG, LOG_FUNC_RUN + "start dismiss");
                    waitDG.dismiss();
                    commitBT.setClickable(true);
                    refreshGS.setRefreshing(false);
                }
        );
    }

    @Override
    protected void onResume() {
        Log.i(TAG, LOG_FUNC_RUN + "call on resume");
        super.onResume();
        updateAfterTurn();
    }
}