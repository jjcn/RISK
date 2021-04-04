package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.adapter.RoomAdapter;
import edu.duke.ece651.group4.RISK.client.listener.onItemClickListener;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.shared.World;

import static edu.duke.ece651.group4.RISK.client.Constant.SUCCESS_CREATE;
import static edu.duke.ece651.group4.RISK.client.Constant.SUCCESS_JOIN;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.sendAndReceive;
import static edu.duke.ece651.group4.RISK.client.utility.Instruction.showByToast;

public class RoomActivity extends AppCompatActivity {
    private final static String TAG = RoomActivity.class.getSimpleName();

    private Button createBT;
    private RecyclerView roomsRC;
    private Button freshBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        impUI();
    }

    // back button at toolbar
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
        impFreshBT();
        impRoomList();
    }

    // TODO:refresh / ondatachanged
    private void impFreshBT() {
    }

    //TODO
    private void impRoomList() {
        roomsRC = findViewById(R.id.roomList);
        RoomAdapter roomsAdapt = new RoomAdapter();
        roomsAdapt.setItemClickListener(new onItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                int ID = roomsAdapt.getItemId(position);
//                JoinGame();

//                sendAndReceive(numUser, new onReceiveListener() {
//                    @Override
//                    public void onSuccess(Object o) {
//                        if (o instanceof World) {
//                            // TODO: call function in RISK?
//                            Intent gameIntent = new Intent(RoomActivity.this, GameActivity.class);
//                            showByToast(RoomActivity.this, SUCCESS_JOIN);
//                            startActivity(gameIntent);
//                            finish();
//                        } else {
//                            // showByToast(RoomActivity.this, result);
//                            createBT.setClickable(false);
//                            return;
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String errMsg) {
//                        Log.e(TAG, "create room: " + errMsg.toString());
//                    }
//                });
            }
        });
        roomsRC.setAdapter(roomsAdapt);
    }

    private void impCreateBT() {
        createBT = findViewById(R.id.createButton);
        createBT.setOnClickListener(v -> {
            createBT.setClickable(false);
            // TODO: choose number activity
            int numUser = 2;
            runOnUiThread(() -> {
                createGame(numUser,
                        new onReceiveListener() {
                            @Override
                            public void onSuccess(Object o) { // receive a String if creating game failed.
                                if (o instanceof String) {
                                    String result = (String) o;
                                    showByToast(RoomActivity.this, result);// show account err message
                                    createBT.setClickable(true);
                                    return;
                                } else{
                                }
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                Log.e(TAG, "create room:try create game: " + errMsg.toString());
                            }
                        },

                        new onReceiveListener() { // receive a World if successfully join created game otherwise null
                            @Override
                            public void onSuccess(Object o) {
                                if (o instanceof World) {
                                    Intent gameIntent = new Intent(RoomActivity.this, GameActivity.class);
                                    showByToast(RoomActivity.this, SUCCESS_CREATE);
                                    startActivity(gameIntent);
                                    finish();
                                }
                            }
                            @Override
                            public void onFailure(String errMsg) {
                                Log.e(TAG, "create room:receive world: " + errMsg.toString());
                            }
                        });
            });
        });
    }
}