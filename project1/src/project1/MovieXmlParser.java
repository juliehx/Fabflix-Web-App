package project1;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MovieXmlParser extends DefaultHandler {
	static ArrayList<Movie> movieList;
	
	private String tempVal;
	private Movie tempMovie;
	
	
	public MovieXmlParser() {
		movieList = new ArrayList<Movie>();
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
	
	
	public void runMovieParser() {
		parseDocument();
		printData();
	}
	
	private void printData() {
		System.out.println(movieList.size() + " movies added\n\n");
		for(int i = 0; i < movieList.size(); i++) {
			System.out.println(movieList.get(i) + "\n\n");
		}
	}
	
	// EVENT HANDLERS
	
	public void startElement(String url, String localName,
							String qName, Attributes attributes) 
							throws SAXException {
		tempVal = "";
		if(qName.equalsIgnoreCase("film")) {
			tempMovie = new Movie();
		}
	}
	
	public void characters(char[] ch, int start, int length)
						throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName)
						throws SAXException {
		System.out.print("Adding movie...");
		if(qName.equalsIgnoreCase("film")) {
			movieList.add(tempMovie);
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
		System.out.print("Done\n");
	}
	
	public static void main(String[] args) {
		MovieXmlParser mxp = new MovieXmlParser();
		mxp.runMovieParser();
	}
}
