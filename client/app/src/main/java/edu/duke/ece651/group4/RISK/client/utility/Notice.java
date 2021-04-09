package edu.duke.ece651.group4.RISK.client.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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

    public static String showSelector(@NonNull Context context, List<String> choices) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_choice,
                choices);
        final String[] chosen = new String[1];
        chosen[0] = "";
        builder.setTitle(R.string.CHOOSE_USER_INSTR)
                .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosen[0] = choices.get(which);
                    }
                })
                .setPositiveButton("Confirm", (dialog, which) -> {
                })
                .setNegativeButton("Cancel", ((dialog, which) -> {
                }));
        Log.i(Context.class.getSimpleName(),LOG_FUNC_RUN+"show selector");
        builder.show();
        return chosen[0];
    }
}
