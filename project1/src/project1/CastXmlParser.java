package project1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastXmlParser extends DefaultHandler {
//	static ArrayList<Cast> castList;
	
	private static String dbUrl = "jdbc:mysql://localhost:3306/moviedb";
	private static String user = "mytestuser";
	private static String password = "mypassword";
	
	private Connection conn;
	
	private String tempVal;
	private Cast tempCast;
	
	private HashMap<String,String>stars;
	private HashMap<String,Movie>movies;
	
	static HashMap<String, Cast>cList;
	
	private String star_id;
	
	
	
	public CastXmlParser() {
//		castList = new ArrayList<Cast>();
		stars =  new HashMap<String,String>();
		movies =  new HashMap<String,Movie>();
		cList = new HashMap<String, Cast>();
		star_id = "am0";
	}
	
	public void prefetch() throws Exception {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String jdbcUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
		
		Connection dbcon = DriverManager.getConnection(jdbcUrl, "mytestuser", "mypassword");
		
		String movieQuery = "select * from movies";
		String starQuery = "select * from stars";
		
		PreparedStatement statement = dbcon.prepareStatement(movieQuery);
		ResultSet movieResult = statement.executeQuery();
		
		while(movieResult.next()) {
			String mov_id = movieResult.getString("id");
			String mov_title = movieResult.getString("title");
			int mov_year = movieResult.getInt("year");
			String mov_dir = movieResult.getString("director");
			
			movies.put(mov_id, new Movie(mov_id, mov_title, mov_year, mov_dir));
		}
		movieResult.close();
		
		statement = dbcon.prepareStatement(starQuery);
		ResultSet starResult = statement.executeQuery();
		
		while(starResult.next()) {
			String star_id = starResult.getString("id");
			String star_nm = starResult.getString("name");
			
			stars.put(star_nm, star_id);
		}
		
		starResult.close();
		
//		String starIdQuery = "select max(id) as s_id from stars where stars.id like 'nm0%'";
//		ResultSet starIdRes = statement.executeQuery();
//		
//		if(starIdRes.next()) {
//			star_id = starIdRes.getString("s_id");
//		}
//		
//		starIdRes.close();
		statement.close();
		dbcon.close();
	}
	
	private void parseDocument() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		try {
			SAXParser sp = spf.newSAXParser();
			
			sp.parse("casts124.xml", this);
			
		} catch (SAXException se) {
			se.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void runCastParser() throws Exception {
		prefetch();
		parseDocument();
//		printData();
	}
	
//	private void printData() {
//		System.out.println(castList.size() + " casts added\n\n");
//		for(int i = 0; i < castList.size(); i++) {
//			System.out.println(castList.get(i) + "\n\n");
//		}
//	}
	
	public void startElement(String url, String localName,String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		if(qName.equalsIgnoreCase("m")) {
			tempCast = new Cast();
		}
	}

	public void characters(char[] ch, int start, int length)throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	public boolean isValid(String arg) {
		return arg != null && !arg.equals("");
	}
	
	private void addToCastList(Cast c) {
		String m_id = null;
		String a_id = null;
//		String s_id = null;
		
		String actor = c.getActors();
		String title = c.getTitle();
		
		if(movies.containsKey(c.getId())) {
			m_id = c.getId();
		}
		
		if(stars.containsKey(c.getActors())) {
			a_id = stars.get(c.getActors());
		}
		
//		if(a_id == null) {
//			String currId = star_id.replace("am", "");
//			int newIdNum = Integer.parseInt(currId) + 1;
//			a_id = "am" + Integer.toString(newIdNum);
//			star_id = a_id;
//		}
//			System.out.println("Actor does not exist in database");
		if(m_id == null)
			System.out.println("Movie does not exist in database");
		else
			cList.put(a_id, new Cast(m_id, title, actor, a_id));
	}
	
	public void endElement(String uri, String localName, String qName)throws SAXException{
//		System.out.print("Curating Cast..");
		if(qName.equalsIgnoreCase("m")) {
			if(isValid(tempCast.getActors()) && isValid(tempCast.getId())
					&& isValid(tempCast.getTitle())) {
				addToCastList(tempCast);
			}
//				castList.add(tempCast);
		}
		if(qName.equalsIgnoreCase("f")) {
			tempCast.setId(tempVal);
		}
		else if(qName.equalsIgnoreCase("t")){
			tempCast.setTitle(tempVal);
		}
		else if(qName.equalsIgnoreCase("a")) {
			tempCast.addActor(tempVal);
		}
//		System.out.print("Done curating cast!\n");
	}
	
//	public ArrayList<Cast> getArray(){
//		return castList;
//	}
//	
//	public int getArraySize(){
//		return castList.size();
//	}
	
	public static void main(String[] args) throws Exception, InstantiationException, IllegalAccessException, ClassNotFoundException {
		CastXmlParser fxp = new CastXmlParser();
		long startTime = System.currentTimeMillis();
		System.out.print("Parsing casts...");
		fxp.runCastParser();
		System.out.print("Done!\n");
		
		Connection conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String jdbcURL = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
		
		try {
			conn = DriverManager.getConnection(jdbcURL, "mytestuser", "mypassword");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		Statement statement = conn.createStatement();
//		String query = "select * from stars;";
//		ResultSet allStars = statement.executeQuery(query);
		PreparedStatement psInsertSim = null;
		String sqlInsertSim = null;
		
		PreparedStatement psInsertStar = null;
		String sqlInsertStar = null;
		
		int[] numRows = null;
		
		sqlInsertSim = "call moviedb.add_to_sim(?,?,?)";
		sqlInsertStar = "call moviedb.add_actor(?,?)";
		
		try {
			conn.setAutoCommit(false);
			
			psInsertSim = conn.prepareStatement(sqlInsertSim);
			psInsertStar = conn.prepareStatement(sqlInsertStar);
			
//			for(int i = 0; i < fxp.getArraySize(); i++) {
//				Cast c = fxp.getArray().get(i);
//				String m_id = c.getId();
////				String m_title = c.getTitle();
//				String actor = c.getActors(); //only one actor
//				
//				psInsertSim.setString(1, actor);
//				psInsertSim.setString(2, m_id);
//				
//				psInsertSim.addBatch();
//			}
			
			for(String key: cList.keySet()) {
				psInsertSim.setString(1, key);
				psInsertSim.setString(2, cList.get(key).getId());
				psInsertSim.setString(3, cList.get(key).getActors());
				
				psInsertSim.addBatch();
			}
			try {
				System.out.print("Adding casts to database...");
			numRows = psInsertSim.executeBatch();
			}catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			conn.commit();
			System.out.print("Done!\n");
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			System.out.println("Finished in: " + elapsedTime + " ms");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(psInsertSim!= null) psInsertSim.close();
			if(psInsertStar != null) psInsertStar.close();
			if(conn != null) conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
