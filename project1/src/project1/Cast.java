package project1;
import java.util.ArrayList;

public class Cast {
	private String id;
	
	private String title;
	
	private String actors;
	
	private String star_id;
	
	public Cast() {
		
	}
	
	public Cast(String id, String title, String actor, String star_id) {
		this.id = id;
		this.title = title;
		this.actors = actor;
		this.star_id = star_id;
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
	
	public String getStarId() {
		return this.star_id;
	}
	
	//toString method
	
	public String toString() {
		return "Film ID: " + this.id + "\n" + 
				"Film Title: " + this.title + "\n" + 
				"Actor: " + this.actors;
	}
}
