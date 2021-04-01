package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.RISKApplication;

import static edu.duke.ece651.group4.RISK.client.RISKApplication.*;

public class LoginActivity extends AppCompatActivity {
    private EditText nameET;
    private EditText passwordET;
    private Button logInButton;
    private Button signupButton;

    String name = "A";
    String password = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        impUI();
    }

    private void impUI() {
        impAccountInput();
        impLoginBt();
        impSignInBt();
    }

    private void impAccountInput() {
        nameET = findViewById(R.id.editTextTextAccount);
        passwordET = findViewById(R.id.editTextTextPassword);
        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void impLoginBt(){
        logInButton = findViewById(R.id.buttonLogin);
        logInButton.setOnClickListener(v -> {
            CharSequence nameIn = nameET.getText();
            CharSequence passwordIn = passwordET.getText();
            if (nameIn == null || passwordIn == null){
                return;
            }

            logInButton.setClickable(false);
            String result = sendSignIn(nameIn.toString(),passwordIn.toString());// checkNamePwdMatch(nameIn.toString(), passwordIn.toString());

            if( result == null) { //match
                Intent gameIntent = new Intent(LoginActivity.this, RoomActivity.class);
                startActivity(gameIntent);
                finish();
            }
            else{
                // show password not valid.
                logInButton.setClickable(true);
                return;
            }
        });
    }

    private void impSignInBt() {
        signupButton = findViewById(R.id.buttonSignUp);
        signupButton.setOnClickListener(v -> {
            Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signupIntent);
            finish();
        });
    }

    private void saveAccount() {
        SharedPreferences sp = getSharedPreferences("Accounts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", name);
        editor.putString("password", password);
        editor.commit();
    }
}