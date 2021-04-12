package edu.duke.ece651.group4.RISK.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import edu.duke.ece651.group4.RISK.client.R;

public class ActionsFragment extends Fragment {
    private Spinner actionSpinner;
    private Button commitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_actions, container, false);
        actionSpinner = v.findViewById(R.id.actions_spinner);
        commitButton = v.findViewById(R.id.commit_button);
        return v;
    }
}
