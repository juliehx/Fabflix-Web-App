package com.example.fabflixmobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Movie {
    private String id;
    private String title;
    private String year;
    private String director;
    private String rating;
    private JSONArray jsonGenres;
    private JSONArray jsonStars;
    private HashMap<String,String> genres;
    private HashMap<String,String> stars;

    public Movie(JSONObject movieData){
        try {
            this.id = movieData.getString("id");
            this.title = movieData.getString("title");
            this.year = movieData.getString("year");
            this.director = movieData.getString("director");
            this.rating = movieData.getString("rating");
            this.jsonGenres = movieData.getJSONArray("genres");
            this.jsonStars = movieData.getJSONArray("stars");
            this.genres = new HashMap<String, String>();
            this.stars = new HashMap<String, String>();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle(){
        return this.title;
    }

    public String getYear(){
        return this.year;
    }

    public String getId(){
        return this.id;
    }

    public String getDirector(){
        return this.director;
    }

    public String getRating(){
        return this.rating;
    }

    public String getGenres(){
        return this.genres.values().toString();
    }

    public String getStars(){
        return this.stars.values().toString();
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<String>();
        info.add(this.title);
        info.add(this.director);
        info.add(this.year);
        info.add(this.id);
        info.add(this.getGenres());
        info.add(this.getStars());

        return info;
    }

//    public String toString(){
//
//        return this.title;
//    }

    public void setStars() throws JSONException{
        for(int i = 0; i < jsonStars.length();i++){
            JSONObject gen = jsonStars.getJSONObject(i);
            String id = gen.getString("star_id");
            String name = gen.getString("star_name");
            stars.put(id,name);
        }
    }

    public void setGenres() throws JSONException {
        for(int i = 0; i < jsonGenres.length();i++){
            JSONObject gen = jsonGenres.getJSONObject(i);
            String id = gen.getString("genre_id");
            String name = gen.getString("genre_name");
            genres.put(id,name);
        }
    }
}
