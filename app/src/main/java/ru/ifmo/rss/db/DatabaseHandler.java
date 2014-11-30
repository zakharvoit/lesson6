package ru.ifmo.rss.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.rss.feed.FeedItem;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 3;
    private static final String DB_NAME = "rssDb";
    private static final String FEED_TABLE = "feed";
    private static final String ID_KEY = "_id";
    private static final String ID_KEY_TYPE = "integer primary key";
    private static final String TITLE_KEY = "title";
    private static final String TITLE_KEY_TYPE = "text";
    public static final String LINK_KEY = "link";
    private static final String LINK_KEY_TYPE = "text";
    private static final String DESCRIPTION_KEY = "description";
    private static final String DESCRIPTION_KEY_TYPE = "text";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createFeedsTable(sqLiteDatabase);
    }

    private void createFeedsTable(SQLiteDatabase sqLiteDatabase) {
        String createFeedTable = "create table " + FEED_TABLE + " ("
                + ID_KEY + " " + ID_KEY_TYPE + ", "
                + TITLE_KEY + " " + TITLE_KEY_TYPE + ", "
                + LINK_KEY + " " + LINK_KEY_TYPE + ", "
                + DESCRIPTION_KEY + " " + DESCRIPTION_KEY_TYPE
                + ")";
        sqLiteDatabase.execSQL(createFeedTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        dropFeedsTable(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    private void dropFeedsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table if exists " + FEED_TABLE);
    }

    private static ContentValues itemToValues(FeedItem item) {
        ContentValues values = new ContentValues();
        values.put(TITLE_KEY, item.getTitle());
        values.put(LINK_KEY, item.getLink());
        values.put(DESCRIPTION_KEY, item.getDescription());

        return values;
    }

    public void addItem(FeedItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(FEED_TABLE, null, itemToValues(item));
        db.close();
    }

    public Cursor getItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FEED_TABLE,
                new String[] { ID_KEY, TITLE_KEY, LINK_KEY, DESCRIPTION_KEY },
                null, null, null, null, null, null);

        cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public int getItemsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + FEED_TABLE, null);
        int count = cursor.getCount();

        db.close();
        cursor.close();

        return count;
    }

    public void clearFeeds() {
        SQLiteDatabase db = this.getWritableDatabase();
        dropFeedsTable(db);
        createFeedsTable(db);

        db.close();
    }
}
