package ru.ifmo.rss;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class DownloadFeed extends AsyncTask<String, Void, List<FeedItem>> {
    private final ListView view;
    private final Context context;

    public DownloadFeed(ListView view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<FeedItem> doInBackground(String... params) {
        try {
            return FeedParser.parseFeed(params[0]);
        } catch (IOException e) {
            Log.d("APP", e.getMessage());
        } catch (XmlPullParserException e) {
            Log.d("APP", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<FeedItem> feedItems) {
        super.onPostExecute(feedItems);

        view.setAdapter(new FeedItemsAdapter(feedItems, context));
    }
}
