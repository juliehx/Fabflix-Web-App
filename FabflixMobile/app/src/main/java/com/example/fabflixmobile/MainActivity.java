package com.example.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class MainActivity extends AppCompatActivity {
    private CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void userLogin(View view) {
        //String username = ((EditText) findViewById(R.id.userText)).getText().toString();
        //String password = ((EditText) findViewById(R.id.passText)).getText().toString();
        String url = "https://ec2-18-191-196-56.us-east-2.compute.amazonaws.com:8443/project1/api/login";

        final Intent goToSearchPage = new Intent(this, SearchActivity.class);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("login.info", response);
                        try {
                            JSONObject jsn = new JSONObject(response);
//                            Log.d("jsn", jsn.toString());
                            String verify = jsn.getString("status");
                            Log.d("verify",verify);
                            if(verify.equals("success")){
                                startActivity(goToSearchPage);
                            }else{
                                Log.d("msg","wrong info");
                                Toast.makeText(getApplicationContext(),jsn.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("login.error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<String, String>();
                String username = ((EditText) findViewById(R.id.userText)).getText().toString();
                String password = ((EditText) findViewById(R.id.passText)).getText().toString();


                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };

        queue.add(loginRequest);
    }
}
