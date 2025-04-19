package com.example.alsoosbrotherscarpentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton, backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        backToLoginButton = findViewById(R.id.backToLoginButton);
        registerButton = findViewById(R.id.registerButton);

        // Back to login
        backToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Register logic
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Check if fields are empty
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // SharedPreferences to load and save accounts
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("accountsList", "");
            Type type = new TypeToken<ArrayList<Account>>() {}.getType();
            ArrayList<Account> accountsList;

            // Load existing accounts list from SharedPreferences
            try {
                if (json.isEmpty()) {
                    accountsList = new ArrayList<>();  // If there's no existing data, create a new list
                } else {
                    accountsList = gson.fromJson(json, type);  // Deserialize existing accounts
                    if (accountsList == null) {
                        accountsList = new ArrayList<>();  // In case of null, initialize a new list
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                Toast.makeText(this, "Error loading accounts list. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if username already exists
            for (Account account : accountsList) {
                if (account.getUsername() != null && account.getUsername().equals(username)) {
                    Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Save the new account
            Account newAccount = new Account(username, password);
            accountsList.add(newAccount);  // Add the new account to the list

            // Save the updated accounts list back to SharedPreferences
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("accountsList", gson.toJson(accountsList));  // Save the updated list
                editor.apply();  // Apply changes
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                Toast.makeText(this, "Error saving account. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Account Registered Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}
