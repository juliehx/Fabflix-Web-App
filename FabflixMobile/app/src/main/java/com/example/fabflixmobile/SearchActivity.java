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

        final String title = ((EditText) findViewById(R.id.searchText)).getText().toString();
        final int pageNum = 1;
        String url = "https://ec2-18-191-196-56.us-east-2.compute.amazonaws.com:8443/project1/api/movies?title=" + title +
                "&mode=search&order=rating&limit=10&page=" + pageNum;

        final Intent goToMovieListPage = new Intent(this, MovieListActivity.class);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest movieSearchRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("search.success", response);

                        goToMovieListPage.putExtra("title",title);
                        goToMovieListPage.putExtra("data",response);
                        goToMovieListPage.putExtra("page",pageNum);
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
        queue.add(movieSearchRequest);
    }

}
