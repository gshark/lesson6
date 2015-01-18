package ru.itmo.rss;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class RSSProvider extends ContentProvider {
    public static final int URI_FEEDS = 1;
    public static final int URI_FEED_ID = 2;
    public static final int URI_SUBSCRIPTIONS = 3;
    public static final int URI_SUBSCRIPTION_ID = 4;
    public static final String AUTHORITY = "ru.itmo.rss.contentprovider";
    public static final String FEEDS_PATH = "feed";
    public static final String SUBSCRIPTIONS_PATH = "subscription";
    public static final Uri FEEDS_URI = Uri.parse("content://" + AUTHORITY + "/" + FEEDS_PATH);
    public static final Uri SUBSCRIPTIONS_URI = Uri.parse("content://" + AUTHORITY + "/" + SUBSCRIPTIONS_PATH);
    public static final String FEEDS_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + FEEDS_PATH;
    public static final String FEED_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + FEEDS_PATH;
    public static final String SUBSCRIPTIONS_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + SUBSCRIPTIONS_PATH;
    public static final String SUBSCRIPTION_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + SUBSCRIPTIONS_PATH;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, FEEDS_PATH, URI_FEEDS);
        uriMatcher.addURI(AUTHORITY, FEEDS_PATH + "/#", URI_FEED_ID);
        uriMatcher.addURI(AUTHORITY, SUBSCRIPTIONS_PATH, URI_SUBSCRIPTIONS);
        uriMatcher.addURI(AUTHORITY, SUBSCRIPTIONS_PATH + "/#", URI_SUBSCRIPTION_ID);
    }

    private SQLiteHelper dbHandler;

    @Override
    public boolean onCreate() {
        dbHandler = new SQLiteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case URI_FEEDS:
                cursor = db.query(SQLiteHelper.FEED_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), FEEDS_URI);
                break;
            case URI_FEED_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = "";
                } else {
                    selection += " and ";
                }
                selection += SQLiteHelper.ID_KEY + " = " + uri.getLastPathSegment();

                cursor = db.query(SQLiteHelper.FEED_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), FEEDS_URI);
                break;
            case URI_SUBSCRIPTIONS:
                cursor = db.query(SQLiteHelper.SUBSCRIPTIONS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), SUBSCRIPTIONS_URI);
                break;
            case URI_SUBSCRIPTION_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = "";
                } else {
                    selection += " and ";
                }
                selection += SQLiteHelper.ID_KEY + " = " + uri.getLastPathSegment();

                cursor = db.query(SQLiteHelper.SUBSCRIPTIONS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), SUBSCRIPTIONS_URI);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_FEEDS:
                return FEEDS_CONTENT_TYPE;
            case URI_FEED_ID:
                return FEED_CONTENT_TYPE;
            case URI_SUBSCRIPTIONS:
                return SUBSCRIPTIONS_CONTENT_TYPE;
            case URI_SUBSCRIPTION_ID:
                return SUBSCRIPTION_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        try {
            long id;
            switch (uriType) {
                case URI_FEEDS:
                    id = db.insert(SQLiteHelper.FEED_TABLE, null, values);
                    return Uri.parse(FEEDS_PATH + "/" + id);
                case URI_SUBSCRIPTIONS:
                    id = db.insert(SQLiteHelper.SUBSCRIPTIONS_TABLE, null, values);
                    return Uri.parse(SUBSCRIPTIONS_PATH + "/" + id);
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } finally {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int rowsDeleted;
        switch (uriType) {
            case URI_FEEDS:
                rowsDeleted = db.delete(SQLiteHelper.FEED_TABLE, selection,
                        selectionArgs);
                break;
            case URI_FEED_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = "";
                } else {
                    selection += " and ";
                }
                selection += SQLiteHelper.ID_KEY + "=" + uri.getLastPathSegment();
                rowsDeleted = db.delete(SQLiteHelper.FEED_TABLE,
                        selection,
                        null);
                break;
            case URI_SUBSCRIPTION_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = "";
                } else {
                    selection += " and ";
                }
                selection += SQLiteHelper.ID_KEY + "=" + uri.getLastPathSegment();
                rowsDeleted = db.delete(SQLiteHelper.SUBSCRIPTIONS_TABLE,
                        selection,
                        null);
                break;
            case URI_SUBSCRIPTIONS:
                rowsDeleted = db.delete(SQLiteHelper.SUBSCRIPTIONS_TABLE, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case URI_FEEDS:
                rowsUpdated = db.update(SQLiteHelper.FEED_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case URI_FEED_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = "";
                } else {
                    selection += " and ";
                }
                selection += SQLiteHelper.ID_KEY + "=" + uri.getLastPathSegment();
                rowsUpdated = db.update(SQLiteHelper.FEED_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}