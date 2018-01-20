package com.example.lucy.p2pmessagingapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lucy.p2pmessagingapp.Networking.AppSingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText number;
    EditText password;
    private String UserIP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        number = (EditText) findViewById(R.id.login_numberText);
        password = (EditText) findViewById(R.id.login_passwordText);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        UserIP = wm.getDhcpInfo().toString().split(" ")[1];
        //Log.d("IP: ", wm.getDhcpInfo().toString().split(" ")[1]);

    }

    public void Register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void Login(View v) {
        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", "Molimo pričekajte", true);

        String url = "http://p2pmessenger.azurewebsites.net/api/Users/Login";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(getParams()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("UserNumber", number.getText().toString());
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    switch (response.statusCode) {
                        case 400:
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "I got " + response.statusCode, Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                dialog.dismiss();
            }
        });

        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request, "login");
    }

    public Map<String, String> getParams() {
        Map<java.lang.String, java.lang.String> params = new HashMap<>();
//                params.put("number", number.getText().toString());
        params.put("number", number.getText().toString());
        params.put("password", password.getText().toString());
        params.put("ip", UserIP);
        return params;
    }


    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        super.onBackPressed();
    }
}
