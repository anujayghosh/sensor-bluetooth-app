package com.iisc.xyz.test13.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iisc.xyz.test13.R;
import com.iisc.xyz.test13.helper.InputValidation;
import com.iisc.xyz.test13.sql.DatabaseHelper;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private final Activity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();

    }
    @SuppressLint("WrongViewCast")
    private void initViews() {
        nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputLayoutEmail =  findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword =  findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword =  findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);
        textViewLinkRegister =  findViewById(R.id.textViewLinkRegister);
    }

    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;

            case R.id.textViewLinkRegister:
                Intent intentRegister = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intentRegister);
                break;

        }






    }

    private void verifyFromSQLite()
    {
        if(!inputValidation.isInputEditTextFailed(textInputEditTextEmail,textInputLayoutEmail,getString(R.string.error_email)))
        {
            return;
        }
        if(!inputValidation.isInputEditTextEmail(textInputEditTextEmail,textInputLayoutEmail,getString(R.string.error_email)))
        {
            return;
        }
        if(!inputValidation.isInputEditTextFailed(textInputEditTextPassword,textInputLayoutPassword,getString(R.string.error_password)))
        {
            return;
        }

        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()))
        {
            Intent accountsIntent = new Intent(activity, UsersActivity.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);
        }
        else {
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    private void emptyInputEditText(){
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

    public void btMakeDiscoverable(View view) {
    }
}

