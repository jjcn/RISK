package edu.duke.ece651.group4.RISK.client.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class SimpleSelector extends Dialog {
    private List<String> choices;
    private String chosen;
    private final ArrayAdapter<String> adapter;
    private Context context;

    public SimpleSelector(@NonNull Context context, String title, List<String> choices) {
        super(context);
        this.context = context;
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        this.choices = choices;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        adapter = new ArrayAdapter<>(context, R.layout.item_choice,
                choices);
        builder.setTitle(title)
                .setSingleChoiceItems(adapter, 0, (dialog, which) -> {
                    chosen = choices.get(which);
                    showByToast((Activity) context,"You have choose: "+chosen);
                })
                .setPositiveButton("Confirm", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", ((dialog, which) -> {
                    chosen = null;
                    dialog.dismiss();
                }));
    }

    public void setChoices(List<String> choices) {
        this.choices.clear();
        this.choices.addAll(choices);
        adapter.notifyDataSetChanged();
    }

    public String getChosen(){
        return chosen;
    }

    @Override
    public void show() {
        super.show();
    }
}
