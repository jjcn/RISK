package edu.duke.ece651.group4.RISK.client.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import androidx.annotation.NonNull;
import edu.duke.ece651.group4.RISK.client.R;

public class WaitDialog extends Dialog {
    private TextView waitText;

    public WaitDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Dialog);
        setCanceledOnTouchOutside(false);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_wait);
        waitText = (TextView) findViewById(R.id.waitText);
    }
}
