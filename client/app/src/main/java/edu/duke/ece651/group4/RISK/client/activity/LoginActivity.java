package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.*;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;

import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.Constant.DEBUG_MODE;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;
import static edu.duke.ece651.group4.RISK.client.utility.Instruction.showByToast;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
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
        impLoginBt();
        impSignUpBt();
    }

    private void impLoginBt() {
        nameET = findViewById(R.id.editTextTextAccount);
        passwordET = findViewById(R.id.editTextTextPassword);
        logInButton = findViewById(R.id.buttonLogin);

        logInButton.setOnClickListener(v -> {
            logInButton.setClickable(false);
            name = Objects.requireNonNull(nameET.getText()).toString();
            password = Objects.requireNonNull(passwordET.getText()).toString();

            sendLogIn(name, password, new onReceiveListener() {
                @Override
                public void onSuccess(Object o) {
                    String result = (String) o;
                    if (DEBUG_MODE) {
                        result = null;
                    }
                    runOnUiThread(() -> {
                        if (result == null) { //match
                            Intent roomIntent = new Intent(LoginActivity.this, RoomActivity.class);
                            startActivity(roomIntent);
                        } else {
                            showByToast(LoginActivity.this, result);// show account err message
                            logInButton.setClickable(true);
                            return;
                        }
                    });
                }

                @Override
                public void onFailure(String errMsg) {
                    Log.e(TAG, "login: " + errMsg);
                }
            });
        });
    }

    private void impSignUpBt() {
        signupButton = findViewById(R.id.buttonSignUp);
        signupButton.setOnClickListener(v -> {
            Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signupIntent);
        });
    }

}