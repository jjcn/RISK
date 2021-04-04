package edu.duke.ece651.group4.RISK.client.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.List;

// TODO: have not implement
public abstract class ChoiceAdapter<T> extends RecyclerView.Adapter{
    private List<T> choices;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomIDView;
        TextView usersView;
        View itemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.roomIDView = itemView.findViewById(R.id.roomIDTV);
            this.usersView = itemView.findViewById(R.id.playerTV);
            this.itemView = itemView;
        }
    }
}
