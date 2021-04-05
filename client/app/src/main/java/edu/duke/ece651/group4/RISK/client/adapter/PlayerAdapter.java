package edu.duke.ece651.group4.RISK.client.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
// TODO
public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<String> players;

    public PlayerAdapter(World world){

    }

    public void setPlayers(World world){

    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder{
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
