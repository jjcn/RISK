package edu.duke.ece651.group4.RISK.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;
import edu.duke.ece651.group4.RISK.client.listener.onItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private List<RoomInfo> rooms;
    private onItemClickListener itemListener = null;

    public RoomAdapter() {
        rooms = new ArrayList<>();
    }

    public void setRooms(List<RoomInfo> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

//    public List<RoomInfo> getRooms(){
//        return rooms;
//    };

    public void setItemClickListener(onItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    // create new views invoked by layout manager
    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup roomGroup, int viewType) {
        View view = LayoutInflater.from(roomGroup.getContext()).inflate(R.layout.item_room, roomGroup, false);
        return new ViewHolder(view);
    }

    // replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.ViewHolder holder, int position) {
        RoomInfo room = rooms.get(position);
        String idNum = Integer.toString(room.getRoomID());
        String totalUserNum = Integer.toString(room.getMaxNumPlayers());
        holder.roomIDView.append(idNum);

        String usersInfo = "";
        String sep = "";
        for (String userName : room.getUserNames()) {
            usersInfo.concat(sep + userName);
            sep = ", ";
        }
        holder.usersView.append(usersInfo);
        holder.usersView.append("(need " + totalUserNum + "in total)");
        if (itemListener != null) {
            holder.itemView.setOnClickListener(v -> {
                itemListener.onItemClick(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    // refer to the type of views used
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView roomIDView;
        private TextView usersView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIDView = itemView.findViewById(R.id.roomIDTV);
            usersView = itemView.findViewById(R.id.playerTV);
        }
    }
}