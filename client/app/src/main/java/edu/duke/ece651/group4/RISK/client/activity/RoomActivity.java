package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.shared.World;

import static edu.duke.ece651.group4.RISK.client.Constant.SUCCESS_JOIN;
import static edu.duke.ece651.group4.RISK.client.Constant.SUCCESS_SIGNUP;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.sendAndReceive;
import static edu.duke.ece651.group4.RISK.client.utility.Instruction.showByToast;

public class RoomActivity extends AppCompatActivity {
    private final static String TAG = RoomActivity.class.getSimpleName();

    private Button createBT;
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

    private void impFreshBT() {
    }

    private void impRoomList() {

    }

    private void impCreateBT() {
        createBT = findViewById(R.id.createButton);
        createBT.setOnClickListener(v -> {
            createBT.setClickable(false);
            // TODO: choose number activity
            int numUser = 2;
            sendAndReceive(numUser, new onReceiveListener() {
                @Override
                public void onSuccess(Object o) {
                    if (o instanceof World) {
                        // TODO: call function in RISK?
                        Intent gameIntent = new Intent(RoomActivity.this, GameActivity.class);
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
                    Log.e(TAG, "create room: " + errMsg.toString());
                }
            });
        });
    }
}