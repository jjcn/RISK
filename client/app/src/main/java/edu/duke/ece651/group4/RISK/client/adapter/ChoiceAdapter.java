package edu.duke.ece651.group4.RISK.client.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChoiceAdapter extends RecyclerView.Adapter<ChoiceAdapter.ChoiceViewHolder> {
    private List<String> choices;

    @NonNull
    @Override
    public ChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceAdapter.ChoiceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChoiceViewHolder extends RecyclerView.ViewHolder{
        public ChoiceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
