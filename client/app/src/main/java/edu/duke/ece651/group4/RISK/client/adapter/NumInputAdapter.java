package edu.duke.ece651.group4.RISK.client.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NumInputAdapter extends RecyclerView.Adapter<NumInputAdapter.NumInputViewHolder> {
    private List<String> numbers;

    @NonNull
    @Override
    public NumInputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NumInputViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NumInputViewHolder extends RecyclerView.ViewHolder{
        public NumInputViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
