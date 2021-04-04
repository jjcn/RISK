package edu.duke.ece651.group4.RISK.client.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onItemClickListener;
import edu.duke.ece651.group4.RISK.shared.RoomInfo;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.*;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private List<RoomInfo> rooms;
    private onItemClickListener itemListener = null;

    public RoomAdapter() {
        rooms = new ArrayList<>();
        if(DEBUG_MODE) {
            RoomInfo t1 = new RoomInfo(0, null, 2);
            List<RoomInfo> rooms = new ArrayList<>();
            rooms.add(t1);
        }
        Log.i(TAG,LOG_CREATE_SUCCESS+rooms.size());
        System.out.println(LOG_CREATE_SUCCESS+rooms.size());
    }

    // create new views invoked by layout manager
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup roomGroup, int viewType) {
        View view = LayoutInflater.from(roomGroup.getContext()).inflate(R.layout.item_room, roomGroup, false);
        return new RoomViewHolder(view);
    }

    // replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomInfo room = rooms.get(position);
        String idNum = Integer.toString(room.getRoomID());
        String totalUserNum = Integer.toString(room.getMaxNumPlayers());
        String usersInfo = "";
        String sep = "";
        for (String userName : room.getUserNames()) {
            usersInfo.concat(sep + userName);
            sep = ", ";
        }

        holder.roomIDView.append(idNum);
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
    public class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomIDView;
        TextView usersView;
        View itemView;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.roomIDView = itemView.findViewById(R.id.roomIDTV);
            this.usersView = itemView.findViewById(R.id.playerTV);
            this.itemView = itemView;
        }
    }

    //TODO: check pointer assign or copy
    public void setRooms(List<RoomInfo> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    public void setItemClickListener(onItemClickListener listener) {
        this.itemListener = listener;
    }

    public int getRoomId(int position){
        return rooms.get(position).getRoomID();
    };
}