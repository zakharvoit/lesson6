package ru.ifmo.rss.feed;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ru.ifmo.rss.db.DatabaseHandler;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class DownloadFeed extends AsyncTask<Void, Void, List<FeedItem>> {
    private final ListView view;
    private final Context context;
    private ProgressDialog dialog;
    private DatabaseHandler handler;

    public DownloadFeed(ListView view, Context context) {
        this.view = view;
        this.context = context;
        this.handler = new DatabaseHandler(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected List<FeedItem> doInBackground(Void... params) {
        try {
            List<FeedItem> result = new ArrayList<FeedItem>();
            Cursor cursor = handler.getSubscriptions();

            if (cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(cursor.getColumnIndex(DatabaseHandler.SUBSCRIPTION_KEY));
                    result.addAll(FeedParser.parse(url));
                } while (cursor.moveToNext());
            }

            return result;
        } catch (ParserConfigurationException e) {
            Log.d("APP", e.getMessage());
        } catch (SAXException e) {
            Log.d("APP", e.getMessage());
        } catch (IOException e) {
            Log.d("APP", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<FeedItem> feedItems) {
        super.onPostExecute(feedItems);

        dialog.dismiss();
        if (feedItems == null) {
            Toast.makeText(context, "Feed loading error", Toast.LENGTH_SHORT).show();
        } else {
            handler.clearFeeds();
            for (FeedItem item : feedItems) {
                handler.addItem(item);
            }

            Cursor cursor = handler.getItems();

            FeedCursorAdapter adapter = new FeedCursorAdapter(context,
                    cursor, 0);
            view.setAdapter(adapter);
        }
    }
}
