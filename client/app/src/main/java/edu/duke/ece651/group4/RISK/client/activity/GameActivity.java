package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;
import edu.duke.ece651.group4.RISK.client.fragment.ActionsFragment;
import edu.duke.ece651.group4.RISK.client.fragment.TerritoriesFragment;
import edu.duke.ece651.group4.RISK.client.fragment.WorldFragment;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.utility.WaitDialog;
import edu.duke.ece651.group4.RISK.shared.BasicOrder;
import edu.duke.ece651.group4.RISK.shared.World;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.Constant.LOG_FUNC_RUN;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByReport;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

import static edu.duke.ece651.group4.RISK.shared.Constant.DONE_ACTION;

public class GameActivity extends FragmentActivity {
    private final String TAG = GameActivity.class.getSimpleName();
    private boolean isWatch;
    private WaitDialog waitDG;

    TextView playerInfo;
    Button doneBT;
    ListView worldInfoRC;
    ArrayAdapter worldInfoAdapter;
    List<String> worldInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        isWatch = false;
        waitDG = new WaitDialog(GameActivity.this);
        impUI();
        updateAllInfo();
    }

    // TODO: history button, upTech button
    private void impUI() {
        playerInfo = findViewById(R.id.playerInfo);
        doneBT = findViewById(R.id.done);
        worldInfoRC = findViewById(R.id.worldInfo);

        FragmentManager fm = getSupportFragmentManager();
        addFragment(fm, R.id.fragment_myTerr_container);
        addFragment(fm, R.id.fragment_enemyTerr_container);

        impDoneButton();
        impWorldInfoRC();
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

    private void impDoneButton() {
        doneBT.setOnClickListener(v -> {
            doneBT.setClickable(false);
//            try {
//                Method yesFunc = GameActivity.class.getDeclaredMethod("waitNextTurn");
//                Method noFunc = RISKApplication.class.getDeclaredMethod("exitGame");
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
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
                updateAllInfo();
                doneBT.setClickable(true);
                if (world.isGameEnd()) {
                    showByToast(GameActivity.this, world.getWinner() + " won the game!");
                    Intent joinGame = new Intent(GameActivity.this, RoomActivity.class);
                    startActivity(joinGame);
                    finish();
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

    private void updateAllInfo() {
        runOnUiThread(() -> {
                    if (isWatch) {
                        doneBT.setVisibility(View.GONE);
                    }
                    Log.i(TAG, LOG_FUNC_RUN + "call update");
                    playerInfo.setText(getPlayerInfo());
                    worldInfo.clear();
                    worldInfo.addAll(getWorldInfo());
                    worldInfoAdapter.notifyDataSetChanged();
                    waitDG.dismiss();
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
