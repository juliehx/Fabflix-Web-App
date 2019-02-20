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
//	static ArrayList<Actor> actorList;
	private String jdbcURL = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";

	private String tempVal;
	private Actor tempAct;
	
	private Connection conn;
	
	PreparedStatement psInsertActors;
	String sqlInsertActors; 

	
	public ActorXmlParser() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//		actorList = new ArrayList<Actor>();
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		try {
			conn = DriverManager.getConnection(jdbcURL, "mytestuser", "mypassword");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sqlInsertActors = "call moviedb.add_actor(?,?)";
		
		try {
			psInsertActors = conn.prepareStatement(sqlInsertActors);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		try {
			conn.commit();
			psInsertActors.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
//		printData();
	}
	
//	private void printData() {
//		System.out.println(actorList.size() + " actors added\n\n");
//		for(int i = 0; i < actorList.size(); i++) {
//			System.out.println(actorList.get(i) + "\n\n");
//		}
//	}
	
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
//			actorList.add(tempAct);
			try {
				psInsertActors.setString(1, tempAct.getName());
				psInsertActors.setInt(2, tempAct.getBirthYear());
				psInsertActors.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		
		if(qName.equalsIgnoreCase("actors")) {
			try {
				psInsertActors.executeBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.print("Done\n");
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ActorXmlParser axp = new ActorXmlParser();
		long startTime = System.currentTimeMillis();
		axp.runActorParser();
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println(elapsedTime);
	}
}
