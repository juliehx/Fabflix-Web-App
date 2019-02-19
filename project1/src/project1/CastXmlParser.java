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
	static ArrayList<Cast> castList;
	
	private static String dbUrl = "jdbc:mysql://localhost:3306/moviedb";
	private static String user = "mytestuser";
	private static String password = "mypassword";
	
	private Connection conn;
	
	private String tempVal;
	private Cast tempCast;
	
	private HashMap<String,String>stars;
	private HashMap<String,String>movies;
	
	
	
	public CastXmlParser() {
		castList = new ArrayList<Cast>();
		stars =  new HashMap<String,String>();
		movies =  new HashMap<String,String>();
		
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
	
	public void runCastParser() {
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
	
	public void endElement(String uri, String localName, String qName)throws SAXException{
		System.out.print("Curating Cast..");
		if(qName.equalsIgnoreCase("m")) {
			if(isValid(tempCast.getActors()) && isValid(tempCast.getId())
					&& isValid(tempCast.getTitle()))
				castList.add(tempCast);
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
		System.out.print("Done curating cast!\n");
	}
	
	public ArrayList<Cast> getArray(){
		return castList;
	}
	
	public int getArraySize(){
		return castList.size();
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		CastXmlParser fxp = new CastXmlParser();
		fxp.runCastParser();
		
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
		
		sqlInsertSim = "call moviedb.add_to_sim(?,?)";
		sqlInsertStar = "call moviedb.add_actor(?,?)";
		
		try {
			conn.setAutoCommit(false);
			
			psInsertSim = conn.prepareStatement(sqlInsertSim);
			psInsertStar = conn.prepareStatement(sqlInsertStar);
			
			for(int i = 0; i < fxp.getArraySize(); i++) {
				Cast c = fxp.getArray().get(i);
				String m_id = c.getId();
				String m_title = c.getTitle();
				String actor = c.getActors(); //only one actor
				
				psInsertSim.setString(1, actor);
				psInsertSim.setString(2, m_id);
				
				psInsertSim.addBatch();
			}
			try {
			numRows = psInsertSim.executeBatch();
			}catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			conn.commit();
			System.out.println("Done");
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
