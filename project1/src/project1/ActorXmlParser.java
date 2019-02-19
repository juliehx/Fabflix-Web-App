package project1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ActorXmlParser extends DefaultHandler {
	static ArrayList<Actor> actorList;
	
	private String tempVal;
	private Actor tempAct;
	
	public ActorXmlParser() {
		actorList = new ArrayList<Actor>();
	}
	
	private void parseDocument() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		try {
			SAXParser sp = spf.newSAXParser();
			
			sp.parse("actors63.xml", this);
		} catch(SAXException se) {
			se.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void runActorParser() {
		parseDocument();
//		printData();
	}
	
	private void printData() {
		System.out.println(actorList.size() + " actors added\n\n");
		for(int i = 0; i < actorList.size(); i++) {
			System.out.println(actorList.get(i) + "\n\n");
		}
	}
	
	// EVENT HANDLERS
	
	public void startElement(String url, String localName,
							String qName, Attributes attributes) 
							throws SAXException {
		tempVal = "";
		if(qName.equalsIgnoreCase("actor")) {
			tempAct = new Actor();
		}
	}
	
	public void characters(char[] ch, int start, int length)
							throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName)
							throws SAXException {
		System.out.print("Adding actor...");
		
		if(qName.equalsIgnoreCase("actor")) {
			actorList.add(tempAct);
		} else if(qName.equalsIgnoreCase("stagename")) {
			tempAct.setName(tempVal);
		} else if(qName.equalsIgnoreCase("dob")) {
			int year;
			try {
				year = Integer.parseInt(tempVal.trim());
			} catch(NumberFormatException e) {
				year = 0;
			}
			tempAct.setBirthYear(year);
		}
		System.out.print("Done\n");
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ActorXmlParser axp = new ActorXmlParser();
		axp.runActorParser();
		
		Connection conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String jdbcURL = "jdbc:mysql://localhost:3306/moviedb";
		
		try {
			conn = DriverManager.getConnection(jdbcURL, "mytestuser", "mypassword");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PreparedStatement psInsertActor = null;
		String sqlInsertActors = null;
		int[] numRows = null;
		
		sqlInsertActors = "call moviedb.add_actor(?,?)";
		
		try {
			conn.setAutoCommit(false);
			
			psInsertActor = conn.prepareStatement(sqlInsertActors);	
			
			for(int i = 0; i < actorList.size();i++) {
				Actor a = axp.actorList.get(i);
				String fullName = a.getName();
				int birthYear = a.getBirthYear();
				psInsertActor.setString(1, fullName);
				psInsertActor.setInt(2, birthYear);
				
				psInsertActor.addBatch();
			}
			numRows = psInsertActor.executeBatch();
			conn.commit();
			System.out.println("Done");
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if(psInsertActor != null) psInsertActor.close();
			if(conn != null) conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
