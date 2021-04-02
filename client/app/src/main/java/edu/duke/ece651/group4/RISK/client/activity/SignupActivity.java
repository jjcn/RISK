package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import edu.duke.ece651.group4.RISK.client.R;

import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.utility.Instruction.showByToast;

public class SignupActivity extends AppCompatActivity {
    private EditText nameET;
    private EditText passwordET;
    private EditText pwdCheckET;
    private Button signupButton;

    String name = "";
    String password = "";
    String pwdCheck = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        impUI();
    }

    private void impUI() {
        impAccountInput();
        impSignUpBt();
    }

    private void impAccountInput() {
        // read input
        nameET = findViewById(R.id.editTextTextAccount);
        passwordET = findViewById(R.id.editTextTextPassword);
        pwdCheckET = findViewById(R.id.editTextTextPasswordCheck);

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = Objects.requireNonNull(s).toString();
                signupButton.setEnabled(password.equals(pwdCheck));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pwdCheckET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwdCheck = Objects.requireNonNull(s).toString();
                signupButton.setEnabled(password.equals(pwdCheck));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void impSignUpBt() {
        signupButton = findViewById(R.id.buttonSignUp);
        signupButton.setOnClickListener(v -> {
            signupButton.setClickable(false);
            name = Objects.requireNonNull(nameET.getText()).toString().trim();
            password = Objects.requireNonNull(pwdCheckET.getText()).toString().trim();
            if (name.isEmpty() || password.isEmpty()) {
                showByToast(this,EMPTY_INPUT);
                signupButton.setClickable(true);
                return;
            }
            // TODO: result to be replaced with true func.
            String result = null; //sendSignUp(name,password);
            if (result == null) {
                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                showByToast(this, SUCCESS_SIGNUP);
                startActivity(loginIntent);
                finish();
            } else {
                showByToast(this, result);// show account err message
                signupButton.setClickable(true);
                return;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent loginIntent = new Intent(this,LoginActivity.class);
            startActivity(loginIntent);
            this.finish();
        }
        return false;
    }
}