package ru.ifmo.rss;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class FeedParser {
    public static List<FeedItem> parseFeed(String url) throws IOException, XmlPullParserException {
        return new FeedParser(url).parseFeed();
    }

    private final XmlPullParser parser;

    private FeedParser(String url) throws IOException, XmlPullParserException {
        InputStream stream = new URL(url).openStream();

        parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(stream, null);
        parser.nextTag();
    }

    private List<FeedItem> parseFeed() throws IOException, XmlPullParserException {
        List<FeedItem> result = new ArrayList<FeedItem>();
        parser.require(XmlPullParser.START_TAG, null, "rss");
        while (true) {
            if (parser.next() == XmlPullParser.START_TAG) break;
        }
        parser.require(XmlPullParser.START_TAG, null, "channel");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("item")) {
                result.add(parseItem());
            } else {
                skipTag();
            }
        }

        return result;
    }

    private void skipTag() throws IOException, XmlPullParserException {
        int depth = 1;
        while (depth > 0) {
            switch (parser.next()) {
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }
    }


    private FeedItem parseItem() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "item");

        String link = null;
        String title = null;
        String description = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("link")) {
                link = parseLink();
            } else if (name.equals("title")) {
                title = parseTitle();
            } else if (name.equals("description")) {
                description = parseDescription();
            } else {
                skipTag();
            }
        }

        return new FeedItem(link, title, description);
    }

    private String parseDescription() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "description");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "description");

        return result;
    }

    private String parseTitle() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");

        return result;
    }

    private String parseLink() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "link");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "link");

        return result;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
