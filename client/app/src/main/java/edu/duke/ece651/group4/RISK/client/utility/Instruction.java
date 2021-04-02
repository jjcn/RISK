package edu.duke.ece651.group4.RISK.client.utility;

import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Instruction {
    public static void showByToast(Context activity,String instr){
        Toast toast = Toast.makeText(activity,instr,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
