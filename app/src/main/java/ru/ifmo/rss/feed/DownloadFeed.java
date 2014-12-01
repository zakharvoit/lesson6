package ru.ifmo.rss.feed;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ru.ifmo.rss.db.DatabaseHelper;
import ru.ifmo.rss.db.MyContentProvider;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class DownloadFeed extends AsyncTask<Void, Void, List<FeedItem>> {
    private final Context context;
    private ProgressDialog dialog;

    public DownloadFeed(Context context) {
        this.context = context;
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
            String[] projection = { DatabaseHelper.SUBSCRIPTION_KEY };
            Cursor cursor = context.getContentResolver().query(MyContentProvider.SUBSCRIPTIONS_URI,
                    projection, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBSCRIPTION_KEY));
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
            context.getContentResolver().delete(MyContentProvider.FEEDS_URI, null, null);
            for (FeedItem item : feedItems) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.TITLE_KEY, item.getTitle());
                values.put(DatabaseHelper.LINK_KEY, item.getLink());
                values.put(DatabaseHelper.DESCRIPTION_KEY, item.getDescription());
                context.getContentResolver().insert(MyContentProvider.FEEDS_URI, values);
            }
        }
    }
}
