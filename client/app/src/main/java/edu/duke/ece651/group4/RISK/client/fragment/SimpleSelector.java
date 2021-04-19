package edu.duke.ece651.group4.RISK.client.fragment;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.List;

public class SimpleSelector extends Dialog {
    private List<String> choices;
    private String chosen;
    private final ArrayAdapter<String> adapter;

    public SimpleSelector(@NonNull Context context,String title, List<String> choices) {
        super(context);
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        this.choices = choices;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        adapter = new ArrayAdapter<>(context, R.layout.item_choice,
                choices);
        builder.setTitle(title)
                .setSingleChoiceItems(adapter, 0, (dialog, which) -> chosen = choices.get(which))
                .setPositiveButton("Confirm", (dialog, which) -> {
                })
                .setNegativeButton("Cancel", ((dialog, which) -> {
                }));
    }

    public void setChoices(List<String> choices) {
        this.choices.clear();
        this.choices.addAll(choices);
        adapter.notifyDataSetChanged();
    }
}
