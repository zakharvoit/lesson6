package ru.ifmo.rss.feed;

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

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class FeedParser extends DefaultHandler {
    private final List<FeedItem> items = new ArrayList<FeedItem>();
    private final FeedItem.Builder itemBuilder = new FeedItem.Builder();
    private StringBuilder currentText;

    public static List<FeedItem> parse(String url) throws IOException, ParserConfigurationException, SAXException {
        InputStream stream = null;
        try {
            stream = new URL(url).openStream();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            FeedParser feedParser = new FeedParser();
            reader.setContentHandler(feedParser);

            reader.parse(new InputSource(stream));

            return feedParser.items;
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

        if (qName.equals("item")) {
            items.add(itemBuilder.createFeedItem());
        } else if (qName.equals("title")) {
            itemBuilder.setTitle(currentText.toString());
        } else if (qName.equals("link")) {
            itemBuilder.setLink(currentText.toString());
        } else if (qName.equals("description")) {
            itemBuilder.setDescription(currentText.toString());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        currentText.append(ch, start, length);
    }
}
