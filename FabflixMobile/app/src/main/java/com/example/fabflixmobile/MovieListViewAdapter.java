package com.example.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movieList;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.layout_movielistview_row, movies);
        this.movieList = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_movielistview_row, parent, false);

        Movie m = movieList.get(position);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView year = (TextView) view.findViewById(R.id.year);
        TextView director = (TextView) view.findViewById(R.id.director);
        TextView genres = (TextView) view.findViewById(R.id.genres);
        TextView stars = (TextView) view.findViewById(R.id.stars);

        title.setText(m.getTitle());
        year.setText(m.getYear());
        director.setText(m.getDirector());
        genres.setText(m.getGenres());
        stars.setText(m.getStars());

        return view;
    }

    public void updateItems(ArrayList<Movie> mList){
        movieList.clear();
        movieList.addAll(mList);
        this.notifyDataSetChanged();
    }
}