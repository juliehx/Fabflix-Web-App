package com.example.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void movieSearch(View view) {
        //String username = ((EditText) findViewById(R.id.userText)).getText().toString();
        String title = ((EditText) findViewById(R.id.searchText)).getText().toString();
        String url = "https://10.0.2.2:8443/project1/api/movies?title=" + title +
                "&mode=search&order=rating&limit=10&page=1";

        final Intent goToMovieListPage = new Intent(this, MovieListActivity.class);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("search.success", response);
//                        ((TextView) findViewById(R.id.httpResponse)).setText(response);
                        // Add the request to the RequestQueue.
//                        queue.add(afterLoginRequest);
                        startActivity(goToMovieListPage);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("search.error", error.toString());
                    }
                });
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                // Post request form data
//                final Map<String, String> params = new HashMap<String, String>();
//                String title = ((EditText) findViewById(R.id.searchText)).getText().toString();
//
//                params.put("title", title);
//                params.put("mode", "search");
//                params.put("order", "rating");
//                params.put("limit", "10");
//                params.put("page", "1");
//
//                return params;
//            }
//        };

        queue.add(loginRequest);
    }

}
