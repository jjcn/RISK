package edu.duke.ece651.group4.RISK.client.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.shared.Player;
import edu.duke.ece651.group4.RISK.shared.Territory;
import edu.duke.ece651.group4.RISK.shared.World;

import java.util.List;

/**
 * For displaying the world info.
 *
 * EX.
 * Player A:
 * ----------------
 * Terr A: 3 Soldier Level 1, 2 Soldier Level 2.
 * Terr B: 1 Soldier Level 1, 3 Soldier Level 2.
 */
// TODO: not implemented yet
public class WorldInfoAdapter extends RecyclerView.Adapter<WorldInfoAdapter.WorldInfoViewHolder> {
    private World world;
    private List<Territory> allTerrs;

    public WorldInfoAdapter(World world){
        this.world = world;
        List<Territory> allTerrs = world.getAllTerritories();
    }

    public void setPlayers(World world){

    }

    @NonNull
    @Override
    public WorldInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WorldInfoViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return allTerrs.size();
    }

    public class WorldInfoViewHolder extends RecyclerView.ViewHolder{
        public WorldInfoViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
