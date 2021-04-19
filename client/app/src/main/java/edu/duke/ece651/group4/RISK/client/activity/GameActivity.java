package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.fragment.ActionsFragment;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.client.utility.WaitDialog;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.*;

public class GameActivity extends AppCompatActivity {
    private final String TAG = GameActivity.class.getSimpleName();
    private boolean isWatch;
    private WaitDialog waitDG;

    TextView playerInfo;
    Button allyBT;
    Button upTechBT;
    ListView worldInfoRC;
    ArrayAdapter worldInfoAdapter;
    List<String> worldInfo;
    Button doneBT;
    FloatingActionButton chatBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, LOG_FUNC_RUN + "begin create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        isWatch = false;
        waitDG = new WaitDialog(GameActivity.this);
        impUI();
        updateAllInfo();
        Log.i(TAG,LOG_CREATE_SUCCESS);
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

        FragmentManager fm = getSupportFragmentManager();
        addFragment(fm, R.id.fragment_myTerr_container);
        addFragment(fm, R.id.fragment_enemyTerr_container);

        impUpTechBT();
        impAllyBT();
        impDoneButton();
        impWorldInfoRC();
        impChatBT();
    }

    private void impUpTechBT() {
        upTechBT.setOnClickListener(v -> {
            upTechBT.setClickable(false);
            doOneUpgrade(new onResultListener() {
                @Override
                public void onSuccess() {
                    playerInfo.setText(getPlayerInfo());
                }

                @Override
                public void onFailure(String errMsg) {
                    showByToast(GameActivity.this,errMsg);
                }
            });
        });
    }

    // TODO: untested feature
    private void impAllyBT() {
        allyBT.setOnClickListener(v -> {
            allyBT.setClickable(false);
            String choice = showSelector(GameActivity.this, CHOOSE_USER_INSTR, getMyTerrNames());
            Log.i(TAG, LOG_FUNC_RUN + "get choice: " + choice);
            if (choice != "") {
                requireAlliance(choice);
            }
        });
    }

    // TODO: incomplete feature in ChatActivity (should start activity at start and keep running)
    private void impChatBT() {
        chatBT.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }

    protected void addFragment(FragmentManager fm, int container_id) {
        Fragment fragment = fm.findFragmentById(container_id);
        if (fragment == null) {
            fragment = new ActionsFragment();
            fm.beginTransaction()
                    .add(container_id, fragment)
                    .commit();
        }
    }

    private void impWorldInfoRC() {
        worldInfo = getWorldInfo();
        worldInfoAdapter = new ArrayAdapter<>(GameActivity.this, R.layout.item_choice, worldInfo);
        worldInfoRC.setAdapter(worldInfoAdapter);
    }

    // todo --: passing method in show dialog
    private void impDoneButton() {
        doneBT.setOnClickListener(v -> {
            doneBT.setClickable(false);
            if (isWatch) {
                showDoneDialog(LOSE_MSG, STAY_INSTR);
            } else {
                showDoneDialog(CONFIRM, CONFIRM_ACTION);
            }
        });
    }

    private void showDoneDialog(String title, String msg) {
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
        if (!isWatch) {
            waitDG.show();
        }
        doneBT.setClickable(false);
        doDone(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                World world = (World) o;
                showByReport(GameActivity.this, TURN_END, world.getReport());
                updateAfterTurn();
                if (world.isGameEnd()) {
                    showByToast(GameActivity.this, world.getWinner() + " won the game!");
                    return;
                }
                if (world.checkLost(getUserName())) {
                    watchGame();
                }
                isWatch = world.checkLost(getUserName());
                if (isWatch) {
                    runOnUiThread(() -> {
                        doneBT.performClick();
                    });
                }
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_RUN + "Should not receive error message after done.");
            }
        });
    }

    private void watchGame() {
        doneBT.setVisibility(View.GONE);
        upTechBT.setVisibility(View.GONE);
        allyBT.setVisibility(View.GONE);
        receive(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                World world = (World) o;
                setWorld(world);
                showByReport(GameActivity.this, TURN_END, world.getReport());
                updateAllInfo();
                watchGame();
            }

            @Override
            public void onFailure(String errMsg) {
                Log.e(TAG, LOG_FUNC_FAIL + "watchGame should not fail");
            }
        });
    }

    private void updateAfterTurn() {
        runOnUiThread(() -> {
            updateAllInfo();
            doneBT.setClickable(true);
            upTechBT.setClickable(true);
            if (getAllianceName().equals(NO_ALLY)) {
                allyBT.setClickable(true);
            }
            waitDG.cancel();
        });
    }

    private void updateAllInfo() {
        runOnUiThread(() -> {
                    if (isWatch) {
                        doneBT.setVisibility(View.GONE);
                        upTechBT.setVisibility(View.GONE);
                        allyBT.setVisibility(View.GONE);
                    }
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
