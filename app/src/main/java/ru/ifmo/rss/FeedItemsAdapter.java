package ru.ifmo.rss;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class FeedItemsAdapter extends BaseAdapter {
    List<FeedItem> items;
    Context context;

    public FeedItemsAdapter(List<FeedItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(context);
        }
        ((TextView)convertView).setText(items.get(position).getDescription());

        return convertView;
    }
}
