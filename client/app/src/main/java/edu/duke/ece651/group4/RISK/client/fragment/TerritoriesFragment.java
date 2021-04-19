package edu.duke.ece651.group4.RISK.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.activity.TurnActivity;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.getWorldInfo;

public class TerritoriesFragment extends FragmentActivity {
    private ListView mTerritoriesListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_territories, container, false);
//        mTerritoriesListView = v.findViewById(R.id.territories_list_view);
//
//        List<String> worldInfo = getWorldInfo();
//        ArrayAdapter<String> worldInfoAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_choice, worldInfo);
//        mTerritoriesListView.setAdapter(worldInfoAdapter);
//        return v;
//    }
}
