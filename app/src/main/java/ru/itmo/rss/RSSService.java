package ru.itmo.rss;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RSSService extends IntentService {
    public RSSService() {
        super("FeedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("INTENT", "Write intent");
        try {
            List<ItemRSS> items = new ArrayList<>();

            String[] projection = {SQLiteHelper.SUBSCRIPTION_KEY};
            Cursor cursor = getContentResolver().query(RSSProvider.SUBSCRIPTIONS_URI,
                    projection, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(cursor.getColumnIndex(SQLiteHelper.SUBSCRIPTION_KEY));
                    items.addAll(RSSParser.parse(url));
                } while (cursor.moveToNext());
            }

            getContentResolver().delete(RSSProvider.FEEDS_URI, null, null);
            for (ItemRSS item : items) {
                ContentValues values = new ContentValues();
                values.put(SQLiteHelper.TITLE_KEY, item.getTitle());
                values.put(SQLiteHelper.LINK_KEY, item.getLink());
                values.put(SQLiteHelper.DESCRIPTION_KEY, item.getDescription());
                getContentResolver().insert(RSSProvider.FEEDS_URI, values);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}