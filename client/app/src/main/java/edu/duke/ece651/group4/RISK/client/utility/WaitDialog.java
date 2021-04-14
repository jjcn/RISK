package edu.duke.ece651.group4.RISK.client.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import androidx.annotation.NonNull;
import edu.duke.ece651.group4.RISK.client.R;

public class WaitDialog extends Dialog {
    private TextView waitText;
    private Activity a;

    public WaitDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Dialog);
        a = (Activity) context;
        waitText = findViewById(R.id.waitTV);
        setCanceledOnTouchOutside(false);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_wait);
    }

    /**
     * If want to change default waiting message_menu.
     */
    public WaitDialog(@NonNull Context context,String text) {
        super(context, R.style.Theme_AppCompat_Dialog);
        a = (Activity) context;
        waitText = findViewById(R.id.waitTV);
        waitText.setText(text);
        setCanceledOnTouchOutside(false);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_wait);
    }

    @Override
    public void show() {
        a.runOnUiThread(super::show);
    }
}
