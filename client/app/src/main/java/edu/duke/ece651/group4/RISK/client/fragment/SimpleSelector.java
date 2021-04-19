package edu.duke.ece651.group4.RISK.client.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class SimpleSelector extends Dialog {
    private List<String> choices;
    private String chosen;
    private final ArrayAdapter<String> adapter;
    AlertDialog.Builder builder;

    public SimpleSelector(@NonNull Context context, String title, List<String> choices, onReceiveListener listener) {
        super(context);
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        this.choices = choices;
        builder = new AlertDialog.Builder(context);
        adapter = new ArrayAdapter<>(context, R.layout.item_choice,
                choices);
        builder.setTitle(title)
                .setSingleChoiceItems(adapter, 0, (dialog, which) -> {
                    chosen = choices.get(which);
                    showByToast((Activity) context,"You have choose: "+chosen);
                })
                .setPositiveButton("Confirm", (dialog, which) -> {
                    listener.onSuccess(chosen);
                })
                .setNegativeButton("Cancel", ((dialog, which) -> {
                    dialog.cancel();
                }));
    }

    @Override
    public void show() {
        builder.show();
    }
}
