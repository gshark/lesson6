package ru.itmo.rss;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int VERSION = 7;
    private static final String DB_NAME = "rssDb";
    public static final String ID_KEY = "_id";
    public static final String ID_KEY_TYPE = "integer primary key";
    public static final String FEED_TABLE = "feed";
    public static final String TITLE_KEY = "title";
    public static final String TITLE_KEY_TYPE = "text";
    public static final String LINK_KEY = "link";
    public static final String LINK_KEY_TYPE = "text";
    public static final String DESCRIPTION_KEY = "description";
    public static final String DESCRIPTION_KEY_TYPE = "text";
    public static final String SUBSCRIPTIONS_TABLE = "subscriptions";
    public static final String SUBSCRIPTION_KEY = "subscription";
    public static final String SUBSCRIPTION_KEY_TYPE = "text";


    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createFeedsTable(sqLiteDatabase);
        createSubscriptionsTable(sqLiteDatabase);

        preinstallSubscriptions(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        dropFeedsTable(sqLiteDatabase);
        dropSubscriptionsTable(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    private void createFeedsTable(SQLiteDatabase db) {
        String createFeedTable = String.format("create table %s (%s %s, %s %s, %s %s, %s %s)", FEED_TABLE, ID_KEY, ID_KEY_TYPE, TITLE_KEY, TITLE_KEY_TYPE, LINK_KEY, LINK_KEY_TYPE, DESCRIPTION_KEY, DESCRIPTION_KEY_TYPE);
        db.execSQL(createFeedTable);
    }

    private void createSubscriptionsTable(SQLiteDatabase db) {
        String createTable = String.format("create table %s (%s %s, %s %s)", SUBSCRIPTIONS_TABLE, ID_KEY, ID_KEY_TYPE, SUBSCRIPTION_KEY, SUBSCRIPTION_KEY_TYPE);
        db.execSQL(createTable);

    }

    private void preinstallSubscriptions(SQLiteDatabase db) {
        addSubscription(db, "http://echo.msk.ru/interview/rss-fulltext.xml");
        addSubscription(db, "http://www.championat.com/xml/rss-article.xml");
        addSubscription(db, "http://feeds.bbci.co.uk/news/rss.xml");
    }

    private void dropFeedsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table if exists " + FEED_TABLE);
    }

    private void dropSubscriptionsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table if exists " + SUBSCRIPTIONS_TABLE);
    }

    private void addSubscription(SQLiteDatabase db, String url) {
        ContentValues values = new ContentValues();
        values.put("subscription", url);
        db.insert(SUBSCRIPTIONS_TABLE, null, values);
    }
}