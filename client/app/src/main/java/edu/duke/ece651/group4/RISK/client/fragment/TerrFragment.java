package edu.duke.ece651.group4.RISK.client.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.activity.GameActivity;
import edu.duke.ece651.group4.RISK.client.model.TerrModel;
import edu.duke.ece651.group4.RISK.shared.Soldier;
import edu.duke.ece651.group4.RISK.shared.Territory;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_FUNC_RUN;
import static edu.duke.ece651.group4.RISK.client.Constant.TERR;
import static edu.duke.ece651.group4.RISK.shared.Constant.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TerrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TerrFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private Territory terr;

    //UI
    private TextView terrInfo;
    private TextView soldierInfo;
    private ImageButton transferBT;
    private RecyclerView jobInfo;

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
            Log.i(TAG,LOG_FUNC_RUN+"getArg not null");
            terr = (Territory) getArguments().getSerializable(TERR);
        }
    }

    /**
     * ~ onCreate in Activity
     * @param inflater pass in layout source ID
     * @param container parent view
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_terr, container, false);
        terrInfo = v.findViewById(R.id.terrInfo);
        soldierInfo = v.findViewById(R.id.soldierInfo);
        transferBT = v.findViewById(R.id.transferBT);
        jobInfo = v.findViewById(R.id.jobInfo);
        if(getArguments()!=null) {
            terr = (Territory) getArguments().getSerializable(TERR);
            terrInfo.setText(terr.getInfo());
            soldierInfo.setText(getTroopInfo(SOLDIER));
        }

        impTransferBT();
        return v;
    }

    private void impTransferBT() {
        transferBT.setOnClickListener(v->{
//            Intent intent = new Intent(getActivity(),Transfer);
//            startActivity(intent);
        });
    }

    private String getTroopInfo(String type){
        StringBuilder info = new StringBuilder();
        info.append("Type: ").append(type).append("\n");

        Map<String, Integer> troopInfo = terr.checkTypeNum(type);
        for(Map.Entry entry: troopInfo.entrySet()){
            info.append(entry.getKey()+": "+entry.getValue()+"\n");
        }
//        for (String jobName : dict.keySet()) {
//            int nUnit = dict.get(jobName) == null ? 0 : dict.get(jobName);
//            info.append(jobName + " : " + nUnit + "\n");
//        }
        return info.toString();
    }
}