package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.duke.ece651.group4.RISK.client.R;

public class LoginActivity extends AppCompatActivity {
    private EditText nameET;
    private EditText passwordET;
    private Button logInButton;

    String name = "A";
    String password = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        impUI();
    }

    private void impUI() {
        nameET = findViewById(R.id.editTextTextAccount);
        passwordET = findViewById(R.id.editTextTextPassword);

        logInButton = findViewById(R.id.buttonLogin);
        logInButton.setOnClickListener(loginListener);
    }

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            login();
        }
    };

    private void login() {
        String nameIn = nameET.getText().toString().trim();
        String passwordIn = passwordET.getText().toString().trim();
        checkNamePwdMatch(nameIn, passwordIn);
        Intent gameIntent = new Intent(LoginActivity.this,RoomActivity.class);
        startActivity(gameIntent);
    }

    private boolean checkNamePwdMatch(String nameIn, String passwordIn) {
        if(name.equals(nameIn) && password.equals(passwordIn)){
            return true;
        }
        return false;
    }

    private void saveAccount() {
        SharedPreferences sp = getSharedPreferences("Accounts", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", name);
        editor.putString("password", password);
        editor.commit();
    }
}