package edu.duke.ece651.group4.RISK.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import edu.duke.ece651.group4.RISK.client.R;

public class TerritoriesFragment extends Fragment {
    private ScrollView mTerritoriesScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_territories, container, false);

        mTerritoriesScrollView = v.findViewById(R.id.territories_scroll_view);

        return v;
    }
}
