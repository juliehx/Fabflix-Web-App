package project1;

public class Movie {
	
	// PRIVATE VARIABLES

	private String id;
	private String title;
	private int year;
	private String director;
	
	// CONSTRUCTORS
	
	public Movie() {
		
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
	
	// toString
	
	public String toString() {
		return "Movie: " + this.title + 
				" (ID: " + this.id + ")\n==================\n" +
				"Release Year: " + this.year + "\n" +
				"Director: " + this.director;
	}
}
