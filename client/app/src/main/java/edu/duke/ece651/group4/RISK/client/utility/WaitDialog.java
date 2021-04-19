package edu.duke.ece651.group4.RISK.client.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import androidx.annotation.NonNull;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.Constant.LOG_FUNC_FAIL;

public class WaitDialog extends Dialog {
    private final String TAG = WaitDialog.class.getSimpleName();
    private TextView waitText;
    private Activity a;

    public WaitDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Dialog);
        a = (Activity) context;
        setCanceledOnTouchOutside(false);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_wait);
        waitText = findViewById(R.id.waitTV);
    }

    /**
     * If want to change default waiting message_menu.
     */
    public void setWaitText(CharSequence text){
        waitText.setText(text);
    }

    @Override
    public void show() {
        a.runOnUiThread(super::show);
    }
}
