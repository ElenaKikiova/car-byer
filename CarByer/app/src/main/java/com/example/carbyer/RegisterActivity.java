package com.example.carbyer;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameET;
    private EditText lastNameET;
    private EditText emailET;
    private EditText phoneET;
    private EditText passwordET;
    private EditText repeatPasswordET;
    private CheckBox isSellerCB;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        firstNameET = findViewById(R.id.registerFirstNameET);
        lastNameET = findViewById(R.id.registerLastNameET);
        emailET = findViewById(R.id.registerEmailET);
        phoneET = findViewById(R.id.registerPhoneET);
        passwordET = findViewById(R.id.registerPasswordET);
        repeatPasswordET = findViewById(R.id.registerRepeatPasswordET);
        isSellerCB = findViewById(R.id.isSellerCB);

        session = new SessionManager(this);
    }

    public void onCancelClick(View view) {
        finish();
    }

    public void onRegisterClick(View view) {

        String firstName = firstNameET.getText().toString().trim();
        String lastName = lastNameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString();
        String repeatPassword = repeatPasswordET.getText().toString();
        String phone = phoneET.getText().toString();
        String role = isSellerCB.isChecked() ? "seller" : "buyer";

        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.error_message_empty_fields,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!password.equals(repeatPassword)) {
            Toast.makeText(
                    this,
                    R.string.error_message_passwords_dont_match,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        JSONObject body = new JSONObject();

        try {
            body.put("firstName", firstName);
            body.put("lastName", lastName);
            body.put("email", email);
            body.put("phone", phone);
            body.put("password", password);
            body.put("role", role);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        ApiClient.post("auth/register", body, null, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                String token = body.optString("token");
                JSONObject user = body.optJSONObject("user");

                if (token == null || token.isEmpty() || user == null) {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration failed",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                session.save(token, user.toString());

                Toast.makeText(
                        RegisterActivity.this,
                        "Registration successful",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }

            @Override
            public void onError(int httpCode, String message) {
                Toast.makeText(
                        RegisterActivity.this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}