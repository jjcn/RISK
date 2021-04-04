package edu.duke.ece651.group4.RISK.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import edu.duke.ece651.group4.RISK.client.R;

public class WorldFragment extends Fragment {
    private ImageView mWorldImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_world, container, false);

        mWorldImageView = v.findViewById(R.id.world_image_view);

        return v;
    }
}
