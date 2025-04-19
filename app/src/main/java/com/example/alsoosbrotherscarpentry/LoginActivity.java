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

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameField);
        passwordEditText = findViewById(R.id.passwordField);
        Button loginButton = findViewById(R.id.loginButton);
        Button backToRegisterButton = findViewById(R.id.registerButton);

        // Set click listener for back to register button
        backToRegisterButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity when button is clicked
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Finish LoginActivity to remove it from the back stack
        });

        // Set click listener for login button
        loginButton.setOnClickListener(v -> {
            // Get username and password from EditText
            String username = usernameEditText.getText().toString().trim(); // Trim spaces
            String password = passwordEditText.getText().toString().trim(); // Trim spaces

            // Get stored user data from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

            try {
                Gson gson = new Gson();
                String json = sharedPreferences.getString("accountsList", "");
                Type type = new TypeToken<ArrayList<Account>>() {}.getType();
                ArrayList<Account> accountsList = gson.fromJson(json, type);

                // Check if accountsList is null or empty
                if (accountsList == null) {
                    accountsList = new ArrayList<>();
                }

                // Validate login
                boolean isValidUser = false;
                for (Account account : accountsList) {
                    if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                        isValidUser = true;
                        break;  // Stop searching once the user is found
                    }
                }

                if (isValidUser) {
                    // Save the logged-in username to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loggedInUsername", username);  // Save the username of the logged-in user
                    editor.apply();

                    // Show success message
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to MainActivity after successful login
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finish LoginActivity to remove it from the back stack
                } else {
                    // Show error message
                    Toast.makeText(LoginActivity.this, "Invalid username or password! Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Log the exception and show an error message
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "An error occurred while processing your login. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
