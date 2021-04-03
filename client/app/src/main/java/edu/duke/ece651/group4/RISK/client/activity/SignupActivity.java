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

import java.util.Objects;

import static edu.duke.ece651.group4.RISK.client.Constant.EMPTY_INPUT;
import static edu.duke.ece651.group4.RISK.client.Constant.SUCCESS_SIGNUP;
import static edu.duke.ece651.group4.RISK.client.RISKApplication.sendSignUp;
import static edu.duke.ece651.group4.RISK.client.utility.Instruction.showByToast;


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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        impUI();
    }

    // back button at toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void impUI() {
        impAccountInput();
        impSignUpBt();
    }

    /**
     * Set up Check if input
     */
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
                showByToast(SignupActivity.this, EMPTY_INPUT);
                signupButton.setClickable(true);
                return;
            }

            sendSignUp(name, password, new onReceiveListener() {
                @Override
                public void onSuccess(Object o) {
                    String result = (String) o;
                    if (result == null) {
                        Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                        showByToast(SignupActivity.this, SUCCESS_SIGNUP);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        showByToast(SignupActivity.this, result);// show account err message
                        signupButton.setClickable(true);
                        return;
                    }
                }

                @Override
                public void onFailure(String errMsg) {
                    Log.e(TAG, "sign up: " + errMsg.toString());
                }
            });

        });
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            Intent loginIntent = new Intent(this,LoginActivity.class);
//            startActivity(loginIntent);
//            this.finish();
//        }
//        return false;
//    }

}