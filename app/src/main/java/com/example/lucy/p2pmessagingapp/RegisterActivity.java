package com.example.lucy.p2pmessagingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.lucy.p2pmessagingapp.Networking.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    EditText firstNameText;
    EditText lastNameText;
    EditText numberText;
    EditText passwordText;
    EditText repeatpasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameText = (EditText) findViewById(R.id.firstNameText);
        lastNameText = (EditText) findViewById(R.id.lastNameText);
        numberText = (EditText) findViewById(R.id.register_numberText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        repeatpasswordText = (EditText) findViewById(R.id.repeatpasswordText);
    }

    public void sendRegistrationForm(View view) {
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String number = numberText.getText().toString();
        String password = passwordText.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || number.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_LONG).show();
        } else {
            if (!password.equals(repeatpasswordText.getText().toString())) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show();
                passwordText.setText("");
                repeatpasswordText.setText("");
                return;
            }
            //Toast.makeText(this, firstNameText.getText() + " " + lastNameText.getText() + " " + numberText.getText(), Toast.LENGTH_LONG).show();
            final ProgressDialog dialog = ProgressDialog.show(RegisterActivity.this, "", "Molimo pričekajte", true);

            String url = "http://p2pmessenger.azurewebsites.net/api/Users/Register";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(RegisterActivity.this, "Uspješno registriran", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(RegisterActivity.this, "ERROR\n" + error.networkResponse.toString(), Toast.LENGTH_LONG).show();

                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        switch (response.statusCode) {
                            case 400:
                                json = new String(response.data);
                                json = trimMessage(json, "Message");
                                Toast.makeText(RegisterActivity.this, json, Toast.LENGTH_LONG).show();
                                if (json != null) displayMessage(json);
                                // Toast.makeText(RegisterActivity.this, "bla" + json, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Log.d("ERROR", "" + response.statusCode);
                                break;
                        }
                        dialog.dismiss();
                    }
                }

                public String trimMessage(String json, String key) {
                    String trimmedString = null;

                    try {
                        JSONObject obj = new JSONObject(json);
                        trimmedString = obj.getString(key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }

                    return trimmedString;
                }

                public void displayMessage(String toastString) {
                    Toast.makeText(RegisterActivity.this, toastString, Toast.LENGTH_LONG).show();
                }
            }) {

                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("first_name", firstNameText.getText().toString().trim());
                    params.put("last_name", lastNameText.getText().toString().trim());
                    params.put("number", numberText.getText().toString().trim());
                    params.put("password", repeatpasswordText.getText().toString());

                    return params;
                }

            };

            // Adding String request to request queue
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, "registracija");
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            //  intent.putExtra("name", nameInputText.getText().toString());
            //  intent.putExtra("number", numberInputText.getText().toString());

            startActivity(intent);
        }
    }
}

