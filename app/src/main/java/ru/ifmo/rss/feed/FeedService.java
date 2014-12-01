package ru.ifmo.rss.feed;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.rss.db.DatabaseHelper;
import ru.ifmo.rss.db.MyContentProvider;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class FeedService extends IntentService {
    public FeedService() {
        super("FeedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("INTENT", "Write intent");
        try {
            List<FeedItem> feedItems = new ArrayList<FeedItem>();

            String[] projection = {DatabaseHelper.SUBSCRIPTION_KEY};
            Cursor cursor = getContentResolver().query(MyContentProvider.SUBSCRIPTIONS_URI,
                    projection, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBSCRIPTION_KEY));
                    feedItems.addAll(FeedParser.parse(url));
                } while (cursor.moveToNext());
            }

            getContentResolver().delete(MyContentProvider.FEEDS_URI, null, null);
            for (FeedItem item : feedItems) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.TITLE_KEY, item.getTitle());
                values.put(DatabaseHelper.LINK_KEY, item.getLink());
                values.put(DatabaseHelper.DESCRIPTION_KEY, item.getDescription());
                getContentResolver().insert(MyContentProvider.FEEDS_URI, values);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
