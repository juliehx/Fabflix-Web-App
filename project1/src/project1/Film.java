package project1;
import java.util.ArrayList;

public class Film {
	private String id;
	
	private String title;
	
	private String actors;
	
	public Film() {
		
	}
	
	public Film(String id, String title, String actor) {
		this.id = id;
		this.title = title;
		this.actors = actor;
	}
	
	//SET METHODS
	
	public void addActor(String actor) {
		this.actors = actor;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	//GET METHODS
	
	public String getActors(){
		return this.actors;
	}
	public String getTitle() {
		return this.title;
	}
	
	public String getId() {
		return this.id;
	}
	
	//toString method
	
	public String toString() {
		return "Film ID: " + this.id + "\n" + 
				"Film Title: " + this.title + "\n" + 
				"Actor: " + this.actors;
	}
}
