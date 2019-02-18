package project1;

public class Actor {
	private String name;
	private int birthYear;
	
	public Actor() {
		
	}
	
	public Actor(String name, int birthYear) {
		this.name = name;
		this.birthYear = birthYear;
	}
	
	// GETTER METHODS
	
	public String getName() {
		return this.name;
	}
	
	public int getBirthYear() {
		return this.birthYear;
	}
	
	// SETTER METHODS
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public void setBirthYear(int newYear) {
		this.birthYear = newYear;
	}
	
	public String toString() {
		return "Actor: " + this.name + "(DOB: " + 
				this.birthYear + ")\n";
	}
}
