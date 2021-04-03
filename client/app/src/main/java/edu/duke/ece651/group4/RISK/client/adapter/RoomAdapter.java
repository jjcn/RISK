package edu.duke.ece651.group4.RISK.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.shared.message.GameMessage;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private List<GameMessage> rooms;

    // create new views invoked by layout manager
    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup roomGroup, int viewType) {
        View view = LayoutInflater.from(roomGroup.getContext()).inflate(R.layout.item_game, roomGroup, false);
        return new ViewHolder(view);
    }

    // replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.ViewHolder holder, int position) {
        holder.roomIDView.setText(rooms.get(position).getGameID());
        holder.playersView.setText(rooms.get(position).getNumPlayers());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // refer to the type of views used
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView roomIDView;
        private TextView playersView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIDView = itemView.findViewById(R.id.roomIDTV);
            playersView = itemView.findViewById(R.id.playerTV);
        }
    }
}