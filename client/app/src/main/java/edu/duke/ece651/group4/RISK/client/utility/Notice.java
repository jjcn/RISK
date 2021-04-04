package edu.duke.ece651.group4.RISK.client.utility;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

public class Notice {
        public static void showByToast(Activity activity, String instr) {
            activity.runOnUiThread(() -> {
                        Toast toast = Toast.makeText(activity, instr, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
            );
        }
    }
