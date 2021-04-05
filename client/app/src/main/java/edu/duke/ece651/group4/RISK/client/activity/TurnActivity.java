package edu.duke.ece651.group4.RISK.client.activity;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;

/**
 * implement game with text input
 */
public class TurnActivity extends AppCompatActivity {

    private Spinner chooseActionSP;
    private Button commitBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        impUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: initialize the information after player join the game.
    private void impUI() {
        chooseActionSP = findViewById(R.id.terrInfo);
        commitBT = findViewById(R.id.chooseAction);
        impTerrInfoSpinner();
        impCommitBT();
    }

    private void impTerrInfoSpinner() {

    }

    private void impCommitBT() {

    }


    private void updateAfterTurn(){

    }
}