package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Instruction.showByToast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText nameET;
    private EditText passwordET;
    private Button logInButton;
    private Button signupButton;

    String name = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        impUI();
    }

    private void impUI() {
        impAccountInput();
        impLoginBt();
        impSignUpBt();
    }

    private void impAccountInput() {
        nameET = findViewById(R.id.editTextTextAccount);
        passwordET = findViewById(R.id.editTextTextPassword);
        // read input
        CharSequence nameIn = nameET.getText();
        CharSequence passwordIn = passwordET.getText();
        if (nameIn != null && passwordIn != null){
            name = nameIn.toString();
            password = passwordIn.toString();
        }
    }

    private void impLoginBt(){
        logInButton = findViewById(R.id.buttonLogin);
        logInButton.setOnClickListener(v -> {
            logInButton.setClickable(false);
            String result = "no account";//sendSignIn(name,password);// checkNamePwdMatch

            if( result == null) { //match
                Log.i(TAG,"test result exists");
                Intent roomIntent = new Intent(LoginActivity.this, RoomActivity.class);
                startActivity(roomIntent);
                finish();
            }
            else{
                showByToast(this,result);// show account err message
                logInButton.setClickable(true);
                return;
            }
        });
    }

    private void impSignUpBt() {
        signupButton = findViewById(R.id.buttonSignUp);
        signupButton.setOnClickListener(v -> {
            Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signupIntent);
            finish();
        });
    }

}