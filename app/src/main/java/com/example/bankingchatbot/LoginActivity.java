package com.example.bankingchatbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private FirebaseAuth mAuth;//@verify- constant should be complete UpperCase
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            // User is already logged in, navigate to HomeActivity
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        // Load saved email and password if "Remember Me" was checked
        loadSavedCredentials();

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        loginButton.setOnClickListener(view -> loginUser());
        signupButton.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        forgotPasswordTextView.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;//for single statement curly brackets are optional
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Save credentials if "Remember Me" is checked
                        if (rememberMeCheckBox.isChecked()) {
                            saveCredentials(email, password);
                        } else {
                            clearCredentials();
                        }
                        // Sign in success
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return false;
        }

        if (!isValidPassword(password)) {
            passwordEditText.setError("Password must be more than 8 characters, contain a capital letter, a lowercase letter, a number, and an underscore.");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        return password.length() > 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*_.*");
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    private void clearCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loadSavedCredentials() {
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            emailEditText.setText(sharedPreferences.getString("email", ""));
            passwordEditText.setText(sharedPreferences.getString("password", ""));
            rememberMeCheckBox.setChecked(true);
        }
    }
}
