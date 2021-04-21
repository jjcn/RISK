package edu.duke.ece651.group4.RISK.client.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.shared.Territory;

import static edu.duke.ece651.group4.RISK.client.Constant.TERR;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TerrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TerrFragment extends Fragment {
    private Territory terr;

    public TerrFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param terr territory
     * @return A new instance of fragment TerrFragment.
     */
    public static TerrFragment newInstance(Territory terr) {
        TerrFragment fragment = new TerrFragment();
        Bundle args = new Bundle();
        args.putSerializable(TERR,terr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            terr = (Territory) getArguments().getSerializable(TERR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terr, container, false);
    }
}