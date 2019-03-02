package com.example.fabflixmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Bundle bundle = getIntent().getExtras();

        ArrayList<Movie> movieList = new ArrayList<Movie>();


        String data = bundle.getString("data");
        Log.d("data", data);
        try {
            JSONArray jsn = new JSONArray(data);
            Log.d("jsonArray",jsn.toString());
            for(int i = 0; i < jsn.length(); i++){
                JSONObject movie = jsn.getJSONObject(i);
                Log.d("movie",movie.toString());
                Movie m = new Movie(movie);
                Log.d("movie-title",m.toString());

                m.setGenres();
                m.setStars();

                movieList.add(m);

            }

            Log.d("list",movieList.toString());

        }catch(Exception e){
            Log.d("error", e.getMessage());
        }
        MovieListViewAdapter adapter = new MovieListViewAdapter(movieList,this);

        ListView listView = (ListView)findViewById(R.id.movie_list);
        listView.setAdapter(adapter);


    }
}
