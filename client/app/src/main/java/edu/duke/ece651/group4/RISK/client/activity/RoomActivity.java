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
    private RoomAdapter roomsAdapt;
    private SwipeRefreshLayout refreshGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        impUI();
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
        impCreateBT();
        impRoomList();
        impSwipeFresh();
    }

    private void impSwipeFresh() {
        refreshGS = findViewById(R.id.refresh);
        refreshGS.setOnRefreshListener(this::refreshRoom);
    }

    private void refreshRoom() {
        refreshGameInfo(new onReceiveListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof List) {
                    List<RoomInfo> rooms = (List) o;
                    roomsAdapt.setRooms(rooms);
                }
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
        refreshGS.setRefreshing(false);
    }

    // for debugging
    private void pseudoRefreshGameInfo() {
        ArrayList<String> l = new ArrayList<>();
        l.add("123");
        RoomInfo t1 = new RoomInfo(1,l , 2);
        List<RoomInfo> rooms = new ArrayList<>();
        rooms.add(t1);

        roomsAdapt.setRooms(rooms);
        Log.i(TAG, LOG_FUNC_RUN + rooms.size());
        refreshGS.setRefreshing(false);
    }

    private void impRoomList() {
        RecyclerView roomsRC = findViewById(R.id.roomList);
        roomsAdapt = new RoomAdapter();
        roomsAdapt.setItemClickListener(position -> { // user click on one item in the room list.
            int ID = roomsAdapt.getRoomId(position);
            runOnUiThread(() -> {
                        JoinGame(ID, new onReceiveListener() {  // check if the room can add a user
                                    @Override
                                    public void onSuccess(Object o) {
                                        if (o instanceof String) {   // error message if can not join
                                            String result = (String) o;
                                            showByToast(RoomActivity.this, result);// show creating err message
                                            return;
                                        } else { // null if successfully join, show a dialog and disable further action before game start
                                            showByToast(RoomActivity.this, SUCCESS_JOIN);
                                            WaitDialog waitDG = new WaitDialog(RoomActivity.this);
                                            waitDG.show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String errMsg) {
                                        Log.e(TAG, "join room:try join: " + errMsg);
                                    }
                                },
                                new onReceiveListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        if (o instanceof World) {
                                            Intent gameIntent = new Intent(RoomActivity.this, PlaceActivity.class);
                                            showByToast(RoomActivity.this, SUCCESS_JOIN);
                                            startActivity(gameIntent);
                                            finish();
                                        } else {
                                            // showByToast(RoomActivity.this, result);
                                            createBT.setClickable(false);
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
        });
        roomsRC.setLayoutManager(new LinearLayoutManager(this));
        // roomsRC.setHasFixedSize(true);
        roomsRC.setAdapter(roomsAdapt);
    }

    private void impCreateBT() {
        createBT = findViewById(R.id.createButton);
        createBT.setOnClickListener(v -> {
            createBT.setClickable(false);
            // TODO: choose number diag
            int numUser = 2;
            runOnUiThread(() -> {
                createGame(numUser,
                        new onReceiveListener() {
                            @Override
                            public void onSuccess(Object o) { // receive a String if creating game failed.
                                if (o instanceof String) {
                                    String result = (String) o;
                                    showByToast(RoomActivity.this, result);// show creating err message
                                    createBT.setClickable(true);
                                    return;
                                } else {
                                    showByToast(RoomActivity.this, SUCCESS_CREATE);
                                    runOnUiThread(() -> {
                                        WaitDialog waitDG = new WaitDialog(RoomActivity.this);
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
                                if (o instanceof World) {
                                    showByToast(RoomActivity.this, SUCCESS_CREATE);
                                    Intent placeIntent = new Intent(RoomActivity.this, PlaceActivity.class);
                                    startActivity(placeIntent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                Log.e(TAG, "create room:receive world: " + errMsg);
                            }
                        });
            });
        });
    }
}