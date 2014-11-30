package ru.ifmo.rss.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import ru.ifmo.rss.db.DatabaseHandler;
import ru.ifmo.rss.feed.DownloadFeed;
import ru.ifmo.rss.feed.FeedItem;
import ru.ifmo.rss.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView view = (ListView)findViewById(R.id.feedList);
        new DownloadFeed(view, this).execute("http://echo.msk.ru/interview/rss-fulltext.xml");
        final Intent intent = new Intent(this, PreviewActivity.class);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
                Cursor cursor = (Cursor) adapter.getItem(position);
                intent.putExtra(PreviewActivity.PREVIEW_URL,
                        cursor.getString(cursor.getColumnIndex(DatabaseHandler.LINK_KEY)));
                startActivity(intent);
            }
        });
    }
}
