package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.adapter.RoomAdapter;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        impUI();
        waitDG = new WaitDialog(RoomActivity.this);
        //TODO:auto refresh once after join
        Log.i(TAG, LOG_CREATE_SUCCESS);
    }

    // back button return to login page kill current one
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
                Log.i(TAG, LOG_FUNC_RUN + "refresh success");
                if (o instanceof List) {
                    List<RoomInfo> rooms = (List<RoomInfo>) o;
                    roomsAdapt.setRooms(rooms);
                }
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
        Log.i(TAG, LOG_FUNC_RUN + "after refresh");
        refreshGS.setRefreshing(false);
    }

    // for debugging
    private void pseudoRefreshGameInfo() {
        ArrayList<String> l = new ArrayList<>();
        l.add("refresh test");
        RoomInfo t1 = new RoomInfo(1, l, 2);
        List<RoomInfo> rooms = new ArrayList<>();
        rooms.add(t1);

        roomsAdapt.setRooms(rooms);
        Log.i(TAG, LOG_FUNC_RUN + rooms.size());
        refreshGS.setRefreshing(false);
    }

    private void impRoomList() {
        roomsAdapt = new RoomAdapter();
        roomsAdapt.setItemClickListener(position -> { // user click on one item in the room list.
                    int ID = roomsAdapt.getRoomId(position);
                    JoinGame(ID, new onReceiveListener() {  // check if the room can add a user
                                @Override
                                public void onSuccess(Object o) {
                                    if (o instanceof String) {   // error message if can not join
                                        String result = (String) o;
                                        showByToast(RoomActivity.this, result);// show creating err message
                                        return;
                                    } else { // null if successfully join, show a dialog and disable further action before game start
                                        showByToast(RoomActivity.this, SUCCESS_JOIN);
                                        waitDG.show();
                                    }
                                }

                                @Override
                                public void onFailure(String errMsg) {
                                    Log.e(TAG, "join room:try join: " + errMsg);
                                }
                            },

                            new onReceiveListener() { // wait for game starting
                                @Override
                                public void onSuccess(Object o) {
                                    Log.i(TAG, LOG_FUNC_RUN + "try to receive world");
                                    if (o instanceof World) {  // receive a world, start the game
                                        runOnUiThread(() -> {
                                            waitDG.cancel();
                                            Intent gameIntent = new Intent(RoomActivity.this, PlaceActivity.class);
                                            showByToast(RoomActivity.this, SUCCESS_START);
                                            startActivity(gameIntent);
                                            finish();
                                        });
                                    } else {
                                        // showByToast(RoomActivity.this, result);
                                        Log.e(TAG, LOG_FUNC_RUN + "not World type");
                                        createBT.setClickable(true);
                                        return;
                                    }
                                }

                                @Override
                                public void onFailure(String errMsg) {
                                    Log.e(TAG, "join room:receive world: " + errMsg);
                                }
                            });
                }
        );
        roomsRC.setLayoutManager(new LinearLayoutManager(this));
        roomsRC.setAdapter(roomsAdapt);
    }

    private void impCreateBT() {
        createBT.setOnClickListener(v -> {
            createBT.setClickable(false);
            // TODO: choose number dialog
            int numUser = TEST_NUM_USER;
            Log.i(TAG, LOG_FUNC_RUN + "before create game.");
            createGame(numUser,
                    new onReceiveListener() {
                        @Override
                        public void onSuccess(Object o) { // receive a String if creating game failed.
                            Log.i(TAG, LOG_FUNC_RUN + "check create valid");
                            if (o instanceof String) {
                                Log.i(TAG, LOG_FUNC_RUN + "not create a game");
                                String result = (String) o;
                                showByToast(RoomActivity.this, result);// show creating err message
                                createBT.setClickable(true);
                                return;
                            } else {
                                Log.i(TAG, LOG_FUNC_RUN + "wait for other players");
                                showByToast(RoomActivity.this, SUCCESS_CREATE);
                                runOnUiThread(() -> {
                                    waitDG.show();
                                });
                            }
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            Log.e(TAG, "create room:try create game: " + errMsg);
                        }
                    },

                    new onReceiveListener() { // receive a World if successfully join created game otherwise null
                        @Override
                        public void onSuccess(Object o) {
                            Log.i(TAG, LOG_FUNC_RUN + "should receive World");
                            if (o instanceof World) {
                                Log.i(TAG, LOG_FUNC_RUN + "receive a World");
                                runOnUiThread(() -> {
                                    waitDG.cancel();
                                    showByToast(RoomActivity.this, SUCCESS_START);
                                    Intent placeIntent = new Intent(RoomActivity.this, PlaceActivity.class);
                                    startActivity(placeIntent);
                                    finish();
                                });
                            } else {
                                Log.i(TAG, LOG_FUNC_FAIL + "receive not World");
                            }
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            Log.e(TAG, "create room:receive world: " + errMsg);
                        }
                    });
        });
    }
}