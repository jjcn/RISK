package edu.duke.ece651.group4.RISK.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import edu.duke.ece651.group4.RISK.client.R;
import edu.duke.ece651.group4.RISK.client.listener.onReceiveListener;
import edu.duke.ece651.group4.RISK.client.listener.onResultListener;

import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.Constant.*;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.sendSignUp;
import static edu.duke.ece651.group4.RISK.client.utility.Notice.showByToast;


public class SignupActivity extends AppCompatActivity {
    private final String TAG = SignupActivity.class.getSimpleName();
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
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        impUI();
        Log.i(TAG,SUCCESS_CREATE);
    }

    // Back button at toolbar: normal back to login activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void impUI() {
        nameET = findViewById(R.id.editTextTextAccount);
        passwordET = findViewById(R.id.editTextTextPassword);
        pwdCheckET = findViewById(R.id.editTextTextPasswordCheck);
        signupButton = findViewById(R.id.buttonSignUp);

        impAccountInput();
        impSignUpBt();
    }

    /**
     * Set up input name and two password.
     * Check if input two password are the same. Sign up button been activated if true.
     */
    private void impAccountInput() {
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = Objects.requireNonNull(s).toString();
                signupButton.setEnabled(password.equals(pwdCheck));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        pwdCheckET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwdCheck = Objects.requireNonNull(s).toString();
                signupButton.setEnabled(password.equals(pwdCheck));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * send name and password to remote server and receive message if signed up a new account.
     * back to login activity if new account created.
     * remain in this activity if account exits or input empty name or password.
     */
    private void impSignUpBt() {
        signupButton.setOnClickListener(v -> {
            signupButton.setClickable(false);
            // read input.
            name = Objects.requireNonNull(nameET.getText()).toString().trim();
            password = Objects.requireNonNull(pwdCheckET.getText()).toString().trim();
            if (name.isEmpty() || password.isEmpty()) {
                showByToast(SignupActivity.this, EMPTY_INPUT);
                signupButton.setClickable(true);
                return;
            }

            //connect to server and try sign up account
            sendSignUp(name, password, new onResultListener() {
                @Override
                public void onSuccess() {
                    showByToast(SignupActivity.this, SUCCESS_SIGNUP);
                    Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }

                @Override
                public void onFailure(String errMsg) {
                    showByToast(SignupActivity.this, errMsg);// show account err message
                    signupButton.setClickable(true);
                    return;
                }
            });
        });
    }

}