package ru.ifmo.rss;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
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
    LayoutInflater inflater;

    public FeedItemsAdapter(List<FeedItem> items, Context context) {
        this.items = items;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = inflater.inflate(R.layout.item_row, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView description = (TextView) convertView.findViewById(R.id.description);

        FeedItem item = items.get(position);
        title.setText(item.getTitle());
        description.setText(Html.fromHtml(item.getDescription()));

        return convertView;
    }
}
