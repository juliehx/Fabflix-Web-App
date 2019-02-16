package project1;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FilmXmlParser extends DefaultHandler {
	static ArrayList<Film> filmList;
	
	private String tempVal;
	private Film tempFilm;
	
	public FilmXmlParser() {
		filmList = new ArrayList<Film>();
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
	
	public void runFilmParser() {
		parseDocument();
		printData();
	}
	
	private void printData() {
		System.out.println(filmList.size() + " films added\n\n");
		for(int i = 0; i < filmList.size(); i++) {
			System.out.println(filmList.get(i) + "\n\n");
		}
	}
	
	public void startElement(String url, String localName,String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		if(qName.equalsIgnoreCase("m")) {
			tempFilm = new Film();
		}
	}

	public void characters(char[] ch, int start, int length)throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName)throws SAXException{
		System.out.println("Curating Cast..");
		if(qName.equalsIgnoreCase("m")) {
			filmList.add(tempFilm);
		}
		if(qName.equalsIgnoreCase("f")) {
			tempFilm.setId(tempVal);
		}
		else if(qName.equalsIgnoreCase("t")){
			tempFilm.setTitle(tempVal);
		}
		else if(qName.equalsIgnoreCase("a")) {
			tempFilm.addActor(tempVal);
		}
		System.out.println("Done curating cast!");
	}
	
	public static void main(String[] args) {
		FilmXmlParser fxp = new FilmXmlParser();
		fxp.runFilmParser();
	}
}
