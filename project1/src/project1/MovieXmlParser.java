package project1;
import project1.Movie;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MovieXmlParser extends DefaultHandler {
//	static ArrayList<Movie> movieList;
	
	private String tempVal;
	private Movie tempMovie;
	
	private Connection conn;
	private String jdbcURL; 

	private HashMap<Movie,String> movieMap;
	
	PreparedStatement psInsertMovies;
	PreparedStatement psInsertGenres;
	
	String sqlInsertMovies;
	String sqlInsertGenres;
	
	
	public MovieXmlParser() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
//		movieList = new ArrayList<Movie>();
//		movieMap = new HashMap<Movie,String>();
		
		sqlInsertMovies = "call moviedb.add_mains(?,?,?,?)";
		sqlInsertGenres = "call moviedb.add_mains_genre(?,?)";
		
		
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		jdbcURL = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
		try {
			conn = DriverManager.getConnection(jdbcURL, "mytestuser", "mypassword");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			psInsertMovies = conn.prepareStatement(sqlInsertMovies);
			psInsertGenres = conn.prepareStatement(sqlInsertGenres);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private void parseDocument() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		try {
			SAXParser sp = spf.newSAXParser();
			
			sp.parse("mains243.xml", this);
			
		} catch (SAXException se) {
			se.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}
	
//	public int getArraySize() {
//		return movieList.size();
//	}
//	
//	public ArrayList<Movie> getArray(){
//		return movieList;
//	}
	
//	public void initialize() {
//		
//	}
	
	public void runMovieParser() {
		parseDocument();
		try {
			conn.commit();
			psInsertMovies.close();
			psInsertGenres.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("Done");
//		printData();
	}
	
//	private void printData() {
//		System.out.println(movieList.size() + " movies added\n\n");
//		for(int i = 0; i < movieList.size(); i++) {
//			System.out.println(movieList.get(i) + "\n\n");
//		}
//	}
	
	// EVENT HANDLERS
	
	public void startElement(String url, String localName,
							String qName, Attributes attributes) 
							throws SAXException {
		tempVal = "";
		if(qName.equalsIgnoreCase("film")) {
			tempMovie = new Movie();
		}
		
	}
	
	public boolean checkMovieDetails(Movie m) {
		return m.getId() != null && !m.getId().equals("") &&
				m.getTitle() != null && !m.getTitle().equals("") &&
				m.getDirector() != null && !m.getDirector().equals("");
				
	}
	
	
	public void characters(char[] ch, int start, int length)throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName)
						throws SAXException {
		System.out.print("Adding movie...");
		
		if(qName.equalsIgnoreCase("film")) {
			if(checkMovieDetails(tempMovie)) {
				
				try {
					psInsertMovies.setString(1, tempMovie.getId());
					psInsertMovies.setString(2, tempMovie.getTitle());
					psInsertMovies.setInt(3, tempMovie.getYear());
					psInsertMovies.setString(4, tempMovie.getDirector());
					psInsertMovies.addBatch();
					
					for(int k = 0; k < tempMovie.getGenres().size();k++) {
						psInsertGenres.setString(1, tempMovie.getId());
						psInsertGenres.setString(2, tempMovie.getGenres().get(k));
					}
					psInsertGenres.addBatch();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			else {
				System.out.println(String.format("Failed To Insert Movie: M_ID: %s\n Title:%s\n Year:%d\n Director:%s",
						tempMovie.getId(),
						tempMovie.getTitle(),
						tempMovie.getYear(),
						tempMovie.getDirector()));
			}
		} else if(qName.equalsIgnoreCase("fid")) {
			tempMovie.setId(tempVal);
		} else if(qName.equalsIgnoreCase("t")) {
			tempMovie.setTitle(tempVal);
		} else if(qName.equalsIgnoreCase("year")) {
			int year;
			try {
				year = Integer.parseInt(tempVal.trim());
			} catch(NumberFormatException e) {
				year = 0;
			}
			tempMovie.setYear(year);
		} else if(qName.equalsIgnoreCase("dirn")) {
			if(tempMovie.getDirector() == null || tempMovie.getDirector().equals("")) {
				tempMovie.setDirector(tempVal);
			}
		} else if(qName.equalsIgnoreCase("cat")) {
			if(tempMovie.getGenres() == null || tempMovie.getGenres().size() == 0) {
				tempMovie.addGenre(tempVal);
			}
		}
		
		if(qName.equalsIgnoreCase("movies")) {
			try {
				psInsertMovies.executeBatch();
				psInsertGenres.executeBatch();
//				conn.close();
//				psInsertMovies.close();
//				psInsertGenres.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.print("Done\n");
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		MovieXmlParser mxp = new MovieXmlParser();
		long startTime = System.currentTimeMillis();
		mxp.runMovieParser();
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println(elapsedTime);		
	}
}
