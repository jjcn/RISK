package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;

import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.sendLogIn;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

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
        EditText nameET = findViewById(R.id.editTextTextAccount);
        EditText passwordET = findViewById(R.id.editTextTextPassword);
        Button logInButton = findViewById(R.id.buttonLogin);

        // TODO-corner case: name checking
        logInButton.setOnClickListener(v -> {
            logInButton.setClickable(false);
            // rend in input
            name = Objects.requireNonNull(nameET.getText()).toString();
            password = Objects.requireNonNull(passwordET.getText()).toString();

            sendLogIn(name, password, new onResultListener() {
                @Override
                public void onSuccess() {
                    Intent roomIntent = new Intent(LoginActivity.this, RoomActivity.class);
                    logInButton.setClickable(true);
                    startActivity(roomIntent);
                }

                @Override
                public void onFailure(String errMsg) {
                    showByToast(LoginActivity.this, errMsg);// show account err message_menu
                    logInButton.setClickable(true);
                    return;
                }
            });
        });
    }

    // Sign up button: change to new Signup Activity
    private void impSignUpBt() {
        Button signupButton = findViewById(R.id.buttonSignUp);
        signupButton.setOnClickListener(v -> {
            Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signupIntent);
        });
    }
}