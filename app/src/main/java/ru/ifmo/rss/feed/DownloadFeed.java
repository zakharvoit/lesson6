package ru.ifmo.rss.feed;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ru.ifmo.rss.db.DatabaseHandler;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class DownloadFeed extends AsyncTask<String, Void, List<FeedItem>> {
    private final ListView view;
    private final Context context;
    private ProgressDialog dialog;

    public DownloadFeed(ListView view, Context context) {
        this.view = view;
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
    protected List<FeedItem> doInBackground(String... params) {
        try {
            return FeedParser.parse(params[0]);
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
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseHandler handler = new DatabaseHandler(context);

            handler.clearFeeds();
            for (FeedItem item : feedItems) {
                handler.addItem(item);
            }

            Cursor cursor = handler.getItems();

            String[] from = { "title", "link", "description" };
            int[] to = { android.R.id.text1, android.R.id.text2, android.R.id.text2 };
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
                    android.R.layout.simple_list_item_2, cursor,
                    from, to, 0);
            view.setAdapter(adapter);
        }
    }
}
