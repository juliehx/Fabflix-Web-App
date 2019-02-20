package project1;

import java.util.ArrayList;

public class Movie {
	
	// PRIVATE VARIABLES

	private String id;
	private String title;
	private int year;
	private String director;
	private ArrayList<String> genres;
	
	// CONSTRUCTORS
	
	public Movie() {
		this.id = null;
		this.title = null;
		this.year = 0;
		this.director = null;
		this.genres = new ArrayList<String>();
	}
	
	public Movie(String id, String title, 
			int year, String director) {
		this.id = id;
		this.title = title;
		this.year = year;
		this.director = director;
	}
	
	// GETTER METHODS
	
	public String getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public String getDirector() {
		return this.director;
	}
	
	public ArrayList<String> getGenres() {
		return this.genres;
	}
	
	// SETTER METHODS
	
	public void setId(String newId) {
		this.id = newId;
	}
	
	public void setTitle(String newTitle) {
		this.title = newTitle;
	}
	
	public void setYear(int newYear) {
		this.year = newYear;
	}
	
	public void setDirector(String newDirector) {
		this.director = newDirector;
	}
	
	public void addGenre(String genre) {
		this.genres.add(genre);
	}
	
	
	// toString
	
	public String toString() {
		return "Movie: " + this.title + 
				" (ID: " + this.id + ")\n==================\n" +
				"Release Year: " + this.year + "\n" +
				"Director: " + this.director + "\n" +
				"Genres: " + this.genres.toString();
	}
}
