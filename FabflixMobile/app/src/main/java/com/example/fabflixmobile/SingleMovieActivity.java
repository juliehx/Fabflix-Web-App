package com.example.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
//    private String movieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovieview);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> movieInfo = bundle.getStringArrayList("movie");

        TextView title = (TextView) findViewById(R.id.movieTitle);
        TextView year = (TextView) findViewById(R.id.movieYear);
        TextView director = (TextView) findViewById(R.id.movieDirector);
        TextView genres = (TextView) findViewById(R.id.movieGenres);
        TextView stars = (TextView) findViewById(R.id.movieStars);

        title.setText(movieInfo.get(0));
        year.setText(movieInfo.get(2));
        director.setText(movieInfo.get(1));
        genres.setText(movieInfo.get(4)); //4
        stars.setText(movieInfo.get(5)); //5
//        movieId = bundle.getString("movieId");
    }

//    public void movieSearch(View view) {
//        //String username = ((EditText) findViewById(R.id.userText)).getText().toString();
////        String title = ((EditText) findViewById(R.id.searchText)).getText().toString();
////        final int pageNum = 1;
//
////        String url = "https://10.0.2.2:8443/project1/api/single-movie?id=" + movieId;
//
////        final Intent goToMovieListPage = new Intent(this, MovieListActivity.class);
//
//        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
//
//        final StringRequest movieInfoRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.d("movieInfo.success", response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("movieInfo.error", error.toString());
//                    }
//                });
//
//        queue.add(movieInfoRequest);
//    }
}
