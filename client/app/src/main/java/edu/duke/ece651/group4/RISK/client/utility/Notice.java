package edu.duke.ece651.group4.RISK.client.utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.List;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_FUNC_RUN;

public class Notice {
    public static void showByToast(Activity activity, String instr) {
        activity.runOnUiThread(() -> {
                    Toast toast = Toast.makeText(activity, instr, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
        );
    }

    public static void showByReport(Context context, String title, String report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(report);
        builder.setPositiveButton("OK", (dialog, which) -> {
        });
        builder.show();
    }

    public static String showSelector(@NonNull Context context, String title, List<String> choices) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_choice,
                choices);
        final String[] chosen = new String[1];
        chosen[0] = "";
        builder.setTitle(title)
                .setSingleChoiceItems(adapter, 0, (dialog, which) -> {
                    chosen[0] = choices.get(which);
                    showByToast((Activity) context,"You have choose: "+chosen[0]);
                })
                .setPositiveButton("Confirm", (dialog, which)->{
                    Log.i(Context.class.getSimpleName(),LOG_FUNC_RUN+"confirm");
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which)->{
                    chosen[0] = "";
                    dialog.dismiss();
                });
        builder.show();
        Log.i(Context.class.getSimpleName(),LOG_FUNC_RUN+"show selector done");
        return chosen[0];
    }
}
