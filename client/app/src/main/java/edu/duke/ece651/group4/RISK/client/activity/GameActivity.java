package edu.duke.ece651.group4.RISK.client.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.fragment.ActionsFragment;
import edu.duke.ece651.group4.RISK.client.fragment.TerritoriesFragment;
import edu.duke.ece651.group4.RISK.client.fragment.WorldFragment;

public class GameActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FragmentManager fm = getSupportFragmentManager();

        addFragment(fm, R.id.fragment_world_container);
        addFragment(fm, R.id.fragment_territories_container);
        addFragment(fm, R.id.fragment_actions_container);
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

    
}
