package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.adapter.WorldInfoAdapter;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.getWorld;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

/**
 * implement game with text input
 */
public class TurnActivity extends AppCompatActivity {

    private Spinner chooseActionSP;
    private Button commitBT;
    private RecyclerView worldInfoRC;
    private WorldInfoAdapter worldInfoAdapter;

    private boolean activeStatus = true; // turn to false after lose game.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        impUI();

        //TODO: initialize the information after player join the game.
        // initAll();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            //TODO: switch rooms
            case R.id.menu_rooms:
                return true;
            case R.id.menu_devinfo:
                showByToast(TurnActivity.this, "Group4 in ECE651\n The best TA in the world: Kewei Xia");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    private void impUI() {
        chooseActionSP = findViewById(R.id.actions_spinner);
        commitBT = findViewById(R.id.chooseAction);
        worldInfoRC = findViewById(R.id.terrInfo);
        impActionSpinner();
        impCommitBT();
    }

    private void impActionSpinner() {


    }

    private void impCommitBT() {
        commitBT.setOnClickListener(v -> {
            Intent intent = new Intent();

        });
    }


    // TODO:
    private void updateAfterTurn() {
        runOnUiThread(() -> {
            if (activeStatus) { // playing game
                updateForWatch();
            } else { // lose game

            }
        });
    }

    // TODO
    private void updateForWatch() {
        commitBT.setVisibility(View.GONE);
        chooseActionSP.setVisibility(View.GONE);

    }

    private void updateTerrInfo() {
        worldInfoAdapter = new WorldInfoAdapter(getWorld());
        // worldInfoAdapter
    }

    private void updatePlayerInfo() {

    }

    private void updateNotice() {

    }


}