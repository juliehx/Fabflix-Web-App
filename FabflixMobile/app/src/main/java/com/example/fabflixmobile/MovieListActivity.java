package com.example.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {
    private int pageNum;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Bundle bundle = getIntent().getExtras();

        final Intent goToSingleMovie = new Intent(this,SingleMovieActivity.class);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final ArrayList<Movie> movieList = new ArrayList<Movie>();



        String data = bundle.getString("data");
        title = bundle.getString("title");
        pageNum = getIntent().getIntExtra("pageNum",1);
        movieList.clear();
        movieList.addAll(parseData(data));

        final MovieListViewAdapter adapter = new MovieListViewAdapter(movieList,this);

        ListView listView = (ListView)findViewById(R.id.movie_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = movieList.get(position);
                goToSingleMovie.putExtra("movie", m.getInfo());
                startActivity(goToSingleMovie);
            }
        });

        Button prevPageButton = (Button) findViewById(R.id.prevButton);
        prevPageButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageNum > 1) {
                    pageNum--;
                    String url = "https://ec2-18-191-196-56.us-east-2.compute.amazonaws.com:8443/project1/api/movies?title=" + title +
                            "&mode=search&order=rating&limit=10&page=" + pageNum;

                    final StringRequest previousPageRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("search.success", response);
                                    adapter.updateItems(parseData(response));
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("search.error", error.toString());
                                }
                            });
                    queue.add(previousPageRequest);
                }else{
                    Toast.makeText(getApplicationContext(),"Smallest Page Reached",Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button nextPageButton = (Button) findViewById(R.id.nextButton);
        nextPageButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {

                ++pageNum;

                String url = "https://ec2-18-191-196-56.us-east-2.compute.amazonaws.com:8443/project1/api/movies?title=" + title +
                        "&mode=search&order=rating&limit=10&page=" + pageNum;


                final StringRequest nextPageRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("search.success", response);
                                if(!response.equals("[]")) {
                                    adapter.updateItems(parseData(response));
                                }else{
                                    Toast.makeText(getApplicationContext(),"Max Page Reached",Toast.LENGTH_SHORT).show();
                                    --pageNum;
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("search.error", error.toString());
                            }
                        });

                queue.add(nextPageRequest);

            }
        });
    }

    public ArrayList<Movie> parseData(String data){
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        try {
            JSONArray jsn = new JSONArray(data);
            for(int i = 0; i < jsn.length(); i++){
                JSONObject movie = jsn.getJSONObject(i);
                Movie m = new Movie(movie);

                m.setGenres();
                m.setStars();

                movieList.add(m);

            }

        }catch(Exception e){
            Log.d("error", e.getMessage());
        }
        return movieList;
    }
}
