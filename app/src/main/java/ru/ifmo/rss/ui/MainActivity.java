package ru.ifmo.rss.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import ru.ifmo.rss.db.DatabaseHelper;
import ru.ifmo.rss.db.MyContentProvider;
import ru.ifmo.rss.R;
import ru.ifmo.rss.feed.FeedCursorAdapter;
import ru.ifmo.rss.feed.FeedService;
import ru.ifmo.rss.subsriptions.SubscriptionCursorAdapter;

public class MainActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private FeedCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(0, null, this);
        adapter = new FeedCursorAdapter(this, null, 0);
        getList().setAdapter(adapter);

        showFeedList();
    }

    private void showFeedList() {
        ListView view = getList();
        final Intent intent = new Intent(this, PreviewActivity.class);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CursorAdapter adapter = (CursorAdapter) parent.getAdapter();
                Cursor cursor = (Cursor) adapter.getItem(position);
                intent.putExtra(PreviewActivity.PREVIEW_URL,
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINK_KEY)));
                startActivity(intent);
            }
        });
    }

    private ListView getList() {
        return (ListView) findViewById(R.id.feedList);
    }

    public void onAddSubscriptionClick(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_subscriptions_dialog);

        final Context context = this;
        final ListView listView = (ListView) dialog.findViewById(R.id.subscriptions_list);
        final CursorAdapter subscriptionsAdapter = new SubscriptionCursorAdapter(this, null, 0);
        listView.setAdapter(subscriptionsAdapter);
        final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                String[] projection = {
                        DatabaseHelper.ID_KEY,
                        DatabaseHelper.SUBSCRIPTION_KEY};
                return new CursorLoader(context, MyContentProvider.SUBSCRIPTIONS_URI, projection, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
                subscriptionsAdapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {
                subscriptionsAdapter.swapCursor(null);
            }
        };
        getLoaderManager().initLoader(1, null, loaderCallbacks);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                context.getContentResolver().delete(MyContentProvider.SUBSCRIPTIONS_URI,
                        DatabaseHelper.ID_KEY + "=?", new String[]{Long.toString(l)});

                return true;
            }
        });

        dialog.findViewById(R.id.add_subscription_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText urlEditText = (EditText) dialog.findViewById(R.id.new_subscription_url);
                String url = urlEditText
                        .getText().toString();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.SUBSCRIPTION_KEY, url);
                context.getContentResolver().insert(MyContentProvider.SUBSCRIPTIONS_URI, values);
                urlEditText.setText("");
            }
        });

        dialog.findViewById(R.id.edit_subscriptions_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onRefresh(View view) {
        Log.d("REFRESH", "pressed");
        Intent intent = new Intent(this, FeedService.class);
        startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                DatabaseHelper.ID_KEY,
                DatabaseHelper.TITLE_KEY,
                DatabaseHelper.LINK_KEY,
                DatabaseHelper.DESCRIPTION_KEY};
        return new CursorLoader(this, MyContentProvider.FEEDS_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
