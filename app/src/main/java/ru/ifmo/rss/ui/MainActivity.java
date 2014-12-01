package ru.ifmo.rss.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import ru.ifmo.rss.db.DatabaseHandler;
import ru.ifmo.rss.feed.DownloadFeed;
import ru.ifmo.rss.R;
import ru.ifmo.rss.subsriptions.SubscriptionCursorAdapter;

public class MainActivity extends Activity {

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DatabaseHandler(this);
        showFeedList();
    }

    private void showFeedList() {
        ListView view = (ListView)findViewById(R.id.feedList);
        final Intent intent = new Intent(this, PreviewActivity.class);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CursorAdapter adapter = (CursorAdapter) parent.getAdapter();
                Cursor cursor = (Cursor) adapter.getItem(position);
                intent.putExtra(PreviewActivity.PREVIEW_URL,
                        cursor.getString(cursor.getColumnIndex(DatabaseHandler.LINK_KEY)));
                startActivity(intent);
            }
        });
    }

    public void onAddSubscriptionClick(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_subscriptions_dialog);

        final ListView listView = (ListView) dialog.findViewById(R.id.subscriptions_list);
        setSubscriptionsViewAdapter(listView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("ROW", l + "");
                dbHandler.removeSubscription(l);
                setSubscriptionsViewAdapter(listView);

                return true;
            }
        });

        dialog.findViewById(R.id.add_subscription_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText urlEditText = (EditText) dialog.findViewById(R.id.new_subscription_url);
                String url = urlEditText
                        .getText().toString();
                dbHandler.addSubscription(url);
                urlEditText.setText("");

                setSubscriptionsViewAdapter(listView);
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

    private void setSubscriptionsViewAdapter(ListView listView) {
        Cursor cursor = dbHandler.getSubscriptions();
        listView.setAdapter(new SubscriptionCursorAdapter(this, cursor, 0));
    }

    public void onRefresh(View view) {
        ListView list = (ListView)findViewById(R.id.feedList);
        new DownloadFeed(list, this).execute();
    }
}
