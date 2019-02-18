package project1;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastXmlParser extends DefaultHandler {
	static ArrayList<Cast> castList;
	
	private String tempVal;
	private Cast tempCast;
	
	public CastXmlParser() {
		castList = new ArrayList<Cast>();
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
		printData();
	}
	
	private void printData() {
		System.out.println(castList.size() + " casts added\n\n");
		for(int i = 0; i < castList.size(); i++) {
			System.out.println(castList.get(i) + "\n\n");
		}
	}
	
	public void startElement(String url, String localName,String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		if(qName.equalsIgnoreCase("m")) {
			tempCast = new Cast();
		}
	}

	public void characters(char[] ch, int start, int length)throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName)throws SAXException{
		System.out.print("Curating Cast..");
		if(qName.equalsIgnoreCase("m")) {
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
	
	public static void main(String[] args) {
		CastXmlParser fxp = new CastXmlParser();
		fxp.runCastParser();
	}
}
