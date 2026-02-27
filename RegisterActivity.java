package com.example.tadawix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText nameInput = findViewById(R.id.register_name);
        EditText idInput = findViewById(R.id.register_id);
        EditText emailInput = findViewById(R.id.register_email);
        EditText phoneInput = findViewById(R.id.register_phone);
        EditText passwordInput = findViewById(R.id.register_password);
        Spinner roleSpinner = findViewById(R.id.register_role);
        Button registerButton = findViewById(R.id.register_button);
        Button backToLogin = findViewById(R.id.back_to_login);

        registerButton.setOnClickListener(v -> {
            if (!isConnected()) {
                Toast.makeText(RegisterActivity.this, "No internet connection. Data not saved.", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = nameInput.getText().toString().trim();
            String userId = idInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String roleKey = roleKeyFromPosition(roleSpinner.getSelectedItemPosition());

            boolean hasError = false;
            if (name.isEmpty()) {
                nameInput.setError("Required");
                hasError = true;
            }
            if (userId.isEmpty()) {
                idInput.setError("Required");
                hasError = true;
            }
            if (email.isEmpty()) {
                emailInput.setError("Required");
                hasError = true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Invalid email");
                hasError = true;
            }
            if (phone.isEmpty()) {
                phoneInput.setError("Required");
                hasError = true;
            }
            if (password.isEmpty()) {
                passwordInput.setError("Required");
                hasError = true;
            }
            if (hasError) {
                Toast.makeText(RegisterActivity.this, "Please fill required data.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveAccount(roleKey, name, userId, email, phone, password);

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("userId", userId);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("roleKey", roleKey);
            startActivity(intent);
        });

        backToLogin.setOnClickListener(v -> finish());
    }

    private String roleKeyFromPosition(int position) {
        if (position == 1) {
            return "doctor";
        } else if (position == 2) {
            return "pharmacist";
        } else if (position == 3) {
            return "lab";
        }
        return "patient";
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void saveAccount(String roleKey, String name, String userId, String email, String phone, String password) {
        SharedPreferences prefs = getSharedPreferences("tadawix_accounts", MODE_PRIVATE);
        prefs.edit()
            .putString("name_" + roleKey, name)
            .putString("id_" + roleKey, userId)
            .putString("email_" + roleKey, email)
            .putString("phone_" + roleKey, phone)
            .putString("password_" + roleKey, password)
            .apply();
    }
}
