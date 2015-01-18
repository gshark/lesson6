package ru.itmo.rss;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RSSParser extends DefaultHandler {
    private final List<ItemRSS> items = new ArrayList<>();
    private final ItemRSS.Builder itemBuilder = new ItemRSS.Builder();
    private StringBuilder currentText;

    public static List<ItemRSS> parse(String url) throws IOException, ParserConfigurationException, SAXException {
        InputStream stream = null;
        try {
            stream = new URL(url).openStream();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            RSSParser RSSParser = new RSSParser();
            reader.setContentHandler(RSSParser);
            reader.parse(new InputSource(stream));
            return RSSParser.items;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        currentText = new StringBuilder();

        if (qName.equals("item")) {
            itemBuilder.clear();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        switch (qName) {
            case "item":
                items.add(itemBuilder.createItem());
                break;
            case "title":
                itemBuilder.setTitle(currentText.toString());
                break;
            case "link":
                itemBuilder.setLink(currentText.toString());
                break;
            case "description":
                itemBuilder.setDescription(currentText.toString());
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        currentText.append(ch, start, length);
    }
}