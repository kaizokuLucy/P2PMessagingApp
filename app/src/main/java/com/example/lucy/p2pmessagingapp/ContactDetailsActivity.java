package com.example.lucy.p2pmessagingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.lucy.p2pmessagingapp.Models.Contact;
import com.example.lucy.p2pmessagingapp.Networking.AppSingleton;
import com.example.lucy.p2pmessagingapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactDetailsActivity extends AppCompatActivity {

    private TextView nameSurname;
    private TextView number;
    private Contact contact;
    private String userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        nameSurname = (TextView) findViewById(R.id.imePrezime);
        number = (TextView) findViewById(R.id.brojKontakta);

        contact = (Contact) getIntent().getSerializableExtra("Contact");
        userNumber = (String) getIntent().getSerializableExtra("UserNumber");

        nameSurname.setText(contact.getFirstName() + " " + contact.getLastName());
        number.setText(contact.getNumber());
    }

    public void addFriend(View view) {
        final ProgressDialog dialog = ProgressDialog.show(ContactDetailsActivity.this, "", "Adding to friend list...", true);

        String url = "http://p2pmessenger.azurewebsites.net/api/users/addfriend";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Toast.makeText(ContactDetailsActivity.this, "Friend is added to list", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                // Toast.makeText(ContactDetailsActivity.this, "Error" + error.networkResponse.toString(), Toast.LENGTH_LONG).show();

                //Toast.makeText(ContactDetailsActivity.this, "ERROR\n" + error.networkResponse.toString(), Toast.LENGTH_LONG).show();

                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    switch (response.statusCode) {
                        case 400:
                            json = new String(response.data);
                            json = trimMessage(json, "Message");
                            Toast.makeText(ContactDetailsActivity.this, json, Toast.LENGTH_LONG).show();
                            if (json != null) displayMessage(json);
                            // Toast.makeText(ContactDetailsActivity.this, "bla" + json, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Log.d("ERROR", "" + response.statusCode);
                            break;
                    }
                    dialog.dismiss();
                }
            }

            private String trimMessage(String json, String key) {
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

            private void displayMessage(String toastString) {
                Toast.makeText(ContactDetailsActivity.this, toastString, Toast.LENGTH_LONG).show();
            }

        }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //number of current user on this device
                params.put("user_number", userNumber);
                //friend number
                params.put("friend_number", contact.getNumber());
                return params;
            }


        };
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, "add friend");
        /** Intent intent = new Intent(ContactDetailsActivity.this, HomeActivity.class);
         startActivity(intent);**/
        // this.onBackPressed();
    }

    public void startConversation(View view) {
        if (!contact.getStatus().equals("1")) {
            Toast.makeText(ContactDetailsActivity.this, contact.getFirstName() + " is not online", Toast.LENGTH_LONG).show();
            finish();
            //Intent intent = new Intent(ContactDetailsActivity.this, HomeActivity.class);
            //startActivity(intent);
        }

        final Intent intent = new Intent(ContactDetailsActivity.this, ChatActivity.class);
        intent.putExtra("Contact", contact);
        intent.putExtra("userNumber", userNumber);
        intent.putExtra("port","15000");
        Log.d("Contact IP", contact.getIp());

        String url = "placeholder";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        intent.putExtra("port", response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                String json = null;
//                NetworkResponse response = error.networkResponse;
//                if (response != null && response.data != null) {
//
//                    switch (response.statusCode) {
//                        case 400:
//                            json = new String(response.data);
//                            json = trimMessage(json, "Message");
//                            Toast.makeText(ContactDetailsActivity.this, json, Toast.LENGTH_LONG).show();
//                            break;
//                        default:
//                            Log.d("ERROR", "" + response.statusCode);
//                            break;
//                    }
//                }
//            }
//
//            private String trimMessage(String json, String key) {
//                String trimmedString = null;
//                try {
//                    JSONObject obj = new JSONObject(json);
//                    trimmedString = obj.getString(key);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//                return trimmedString;
//            }
//        }) {
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("contactNumber", contact.getNumber());
//
//                return params;
//            }
//        };
//
//        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, "begin_chat");
        startActivity(intent);
    }
}
