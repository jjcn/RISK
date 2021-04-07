package edu.duke.ece651.group4.RISK.client.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.BasicOrder;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;
import edu.duke.ece651.group4.RISK.shared.World;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;
import static edu.duke.ece651.group4.RISK.shared.Constant.SWITCH_OUT_ACTION;

/**
 * implement game with text input
 */
public class TurnActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    // TODO:expendable list view
    private Button commitBT;
    private ListView worldInfoRC;
    private ArrayAdapter<String> worldInfoAdapter;
    private Spinner chooseActionSP;
    private ArrayAdapter<String> actionAdapter;
    private ListView noticeInfoRC;
    private ArrayAdapter<String> noticesAdapter;
    private TextView userInfoTV;
    private ImageView mapIV;
    private SwipeRefreshLayout refreshGS;
    private List<String> worldInfo;
    private List<String> noticeInfo;


    private String actionType;
    private boolean isWatch; // turn to true after lose game.
    private WaitDialog waitDG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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

    private void switchOut() {
        send(new BasicOrder(null, null, null, SWITCH_OUT_ACTION), new onResultListener() {
            @Override
            public void onSuccess() {
                Intent joinIntent = new Intent(TurnActivity.this, RoomActivity.class);
                startActivity(joinIntent);
                finish();
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, errMsg);
            }
        });
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
        userInfoTV = findViewById(R.id.playerInfo);
        noticeInfoRC = findViewById(R.id.noticeInfo);
        mapIV = findViewById(R.id.world_image_view);
        refreshGS = findViewById(R.id.refreshInfo);
        Log.i(TAG, LOG_FUNC_RUN + refreshGS);

        mapIV.setImageResource(MAPS.get(getCurrentRoomSize()));
        impActionSpinner();
        impWorldInfoRC();
        impNoticeInfoRC();
        impCommitBT();
        impSwipeFresh();
        userInfoTV.setText(getPlayerInfo());
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
            }
        });
    }

    private void impCommitBT() {
        commitBT.setOnClickListener(v -> {
            commitBT.setClickable(false);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            Log.i(TAG, LOG_FUNC_RUN + "commitBT");
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
                    if (isWatch) {
                        showStayDialog();
                    }
                    showConfirmDialog();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + actionType);
            }
            commitBT.setClickable(true);
            updateAfterTurn();
        });
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TurnActivity.this);
        builder.setTitle(CONFIRM);
        builder.setMessage(CONFIRM_ACTION);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            waitNextTurn();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.show();
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
        builder.show();
    }

    private void upgradeTech() {
        doOneUpgrade(new onResultListener() {
            @Override
            public void onSuccess() {
                userInfoTV.setText(getPlayerInfo());
                actionAdapter.remove(UI_UPTECH); // can only upgrade once in one turn
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
                            commitBT.performClick();
                        });
                    } else {
                        updateAfterTurn();
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

    private void updateAfterTurn() {
        runOnUiThread(() -> {
                    if (isWatch) {
                        chooseActionSP.setVisibility(View.GONE);
                        userInfoTV.setVisibility(View.GONE);
                    }
                    Log.i(TAG, LOG_FUNC_RUN + "call update after turn");
                    userInfoTV.setText(getPlayerInfo());
                    noticeInfo.clear();
                    noticeInfo.add(getWorld().getReport());
                    noticesAdapter.notifyDataSetChanged();
                    worldInfo.clear();
                    worldInfo.addAll(getWorldInfo());
                    worldInfoAdapter.notifyDataSetChanged();
                    waitDG.cancel();
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