package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.adapter.RoomAdapter;
import edu.duke.ece651.group4.RISK.client.utility.WaitDialog;
import edu.duke.ece651.group4.RISK.client.listener.onJoinRoomListener;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;

import java.util.ArrayList;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class RoomActivity extends AppCompatActivity {
    private final static String TAG = RoomActivity.class.getSimpleName();

    private Button createBT;
    private RecyclerView roomsRC;
    private RoomAdapter roomsAdapt;
    private SwipeRefreshLayout refreshGS;
    private WaitDialog waitDG;
    private int numUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Game Rooms");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        waitDG = new WaitDialog(RoomActivity.this);
        numUser = 0;
        impUI();
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }

    /**
     * Back button: normal return to login page and kill current one
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void impUI() {
        createBT = findViewById(R.id.createButton);
        roomsRC = findViewById(R.id.roomList);
        refreshGS = findViewById(R.id.refresh);
        impCreateBT();
        impRoomList();
        impSwipeFresh();
    }

    private void impSwipeFresh() {
        refreshGS.setOnRefreshListener(this::refreshRoom);
    }

    private void refreshRoom() {
        refreshGameInfo(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                roomsAdapt.setRooms((ArrayList<RoomInfo>) o);
            }

            @Override
            public void onFailure(String errMsg) {
                showByToast(RoomActivity.this, errMsg);
            }
        });
        refreshGS.setRefreshing(false);
    }

    /**
     * Listen on which room user choose to join. Receive result from server: null if success
     * fail if room is full and player is not already in the room.
     * If join successfully: call waitGameStart to receive World form server.
     */
    private void impRoomList() {
        roomsAdapt = new RoomAdapter();
        roomsAdapt.setItemClickListener(position -> { // user click on one item in the room list.
            int ID = roomsAdapt.getRoomId(position);
            JoinGame(ID, new onResultListener() {
                @Override
                public void onSuccess() {
                    showByToast(RoomActivity.this, SUCCESS_JOIN);
                    waitDG.show();
                    waitGameStart(new onJoinRoomListener() {
                        @Override
                        public void onJoinNew() {
                            enterNewGame();
                        }

                        @Override
                        public void onBack() {
                            waitDG.cancel();
                            Intent gameIntent = new Intent(RoomActivity.this, TurnActivity.class);
                            showByToast(RoomActivity.this, SUCCESS_START);
                            startActivity(gameIntent);
                        }
                    });
                }

                @Override
                public void onFailure(String errMsg) {
                    showByToast(RoomActivity.this, errMsg); // error message_menu if can not join
                    return;
                }
            });
        });
        roomsRC.setLayoutManager(new LinearLayoutManager(this));
        roomsRC.setAdapter(roomsAdapt);
    }

    private void impCreateBT() {
        createBT.setOnClickListener(v -> {
            createBT.setClickable(false);
            chooseNumUser();
            if (SINGLE_TEST) {
                numUser = TEST_NUM_USER;
            }
        });
    }

    private void chooseNumUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
        builder.setTitle(CHOOSE_MAP);
        final String[] numbers = new String[]{"1", "2", "3", "4", "5"};
        builder.setItems(numbers, (dialog, which) -> {
            numUser = Integer.parseInt(numbers[which]);
            // numUser = 1;
            create();
        });
        builder.show();
    }

    private void create() {
        createGame(numUser, new onResultListener() {
            @Override
            public void onSuccess() {
                showByToast(RoomActivity.this, SUCCESS_CREATE);
                waitDG.show();
                waitGameStart(new onJoinRoomListener() {
                    @Override
                    public void onJoinNew() {
                        enterNewGame();
                    }

                    @Override
                    public void onBack() {
                        Log.e(TAG, LOG_FUNC_FAIL + "Creating room should not join back");
                    }
                });
            }

            @Override
            public void onFailure(String errMsg) {
                showByToast(RoomActivity.this, errMsg);
                createBT.setClickable(true);
                return;
            }
        });
    }

    private void enterNewGame() {
        waitDG.cancel();
        showByToast(RoomActivity.this, SUCCESS_START);
        Intent placeIntent = new Intent(RoomActivity.this, PlaceActivity.class);
        startActivity(placeIntent);
    }
}