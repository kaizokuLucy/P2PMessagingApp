package com.example.lucy.p2pmessagingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.lucy.p2pmessagingapp.Models.Contact;
import com.example.lucy.p2pmessagingapp.Networking.AppSingleton;
import com.example.lucy.p2pmessagingapp.TCPCommunication.TcpClient;
import com.example.lucy.p2pmessagingapp.TCPCommunication.TcpClientThread;
import com.example.lucy.p2pmessagingapp.TCPCommunication.TcpServer;
import com.example.lucy.p2pmessagingapp.TCPCommunication.TcpServerThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ListView contactList;
    private List<Contact> contacts_data;
    private ContactAdapter adapter;
    private EditText numberSearch;
    private String userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        numberSearch = (EditText) findViewById(R.id.editText2);
        userNumber = (String) getIntent().getSerializableExtra("UserNumber");

        contactList = (ListView)findViewById(R.id.contactsListView);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Contact selectedItem = (Contact)parent.getItemAtPosition(position);

                Intent intent = new Intent(HomeActivity.this, ContactDetailsActivity.class);
                intent.putExtra("Contact", selectedItem);
                startActivity(intent);
            }
        });

        contacts_data = new ArrayList<>();
        adapter = new ContactAdapter(this, R.layout.listview_item_row, contacts_data);
        contactList.setAdapter(adapter);
        updateContacts(null);

        //start tcp server
        TcpServer server = new TcpServer(this);

        Runnable serverThread = new TcpServerThread(server);
        new Thread(serverThread).start();


    }

    public void updateContacts(View view){
        final ProgressDialog dialog = ProgressDialog.show(HomeActivity.this, "", "Refreshing...", true);
        //number - provide users number to find his friends
        String url ="http://p2pmessenger.azurewebsites.net/api/users/friends?number=" + userNumber;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        //deserialize
                        contacts_data.clear();
                        for (int i=0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                contacts_data.add(new Contact(obj.getString("first_name"), obj.getString("last_name"), obj.getString("number"), obj.getString("ip"), obj.getString("status")));
                            } catch (JSONException e) {
                                Toast.makeText(HomeActivity.this, "Serialization error", Toast.LENGTH_LONG).show();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(getRequest, "friend list");
    }

    public void findContact(View view){
        if(numberSearch.getText().toString().isEmpty()) return;
        if(numberSearch.getText().toString().equals(userNumber)) {
            Toast.makeText(HomeActivity.this, "You cannot be your own friend", Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(HomeActivity.this, "", "Finding user...", true);
        //number - provide users number to find
        String url ="http://p2pmessenger.azurewebsites.net/api/users/findbynumber?number=" + numberSearch.getText().toString().trim();

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        //deserialize
                        dialog.dismiss();
                        try {
                            Contact contact = new Contact(response.getString("first_name"), response.getString("last_name"), response.getString("number"), response.getString("ip"), response.getString("status"));
                            Intent intent = new Intent(HomeActivity.this, ContactDetailsActivity.class);
                            intent.putExtra("Contact", contact);
                            intent.putExtra("UserNumber", userNumber);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Toast.makeText(HomeActivity.this, "Serialization error", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Korisnik ne postoji", Toast.LENGTH_LONG).show();
                    }
                }
        );

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(getRequest, "find user");
    }

    public void createChat(){
        //Intent intent = new Intent(this, ChatActivity.class);
        //startActivity(intent);
    }

    public void LogOff() {

        String url = "http://p2pmessenger.azurewebsites.net/api/Users/LogOff";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String response = error.getMessage();
                Toast.makeText(HomeActivity.this, response, Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("number", "0911917843");
                return params;
            }
        };

        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request, "Log off");
    }


    @Override
    public void onDestroy()
    {
        LogOff();
        Toast.makeText(HomeActivity.this, "You are logged off", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // this.updateContacts(null);
    }
}
