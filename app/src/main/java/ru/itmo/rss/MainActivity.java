package ru.itmo.rss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
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

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RSSAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(0, null, this);
        adapter = new RSSAdapter(this, null, 0);
        getList().setAdapter(adapter);
        showFeedList();
        onRefresh(null);
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
                        cursor.getString(cursor.getColumnIndex(SQLiteHelper.LINK_KEY)));
                startActivity(intent);
            }
        });
    }

    private ListView getList() {
        return (ListView) findViewById(R.id.feedList);
    }

    public void onAddSubscriptionClick(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        final Context context = this;
        final ListView listView = (ListView) dialog.findViewById(R.id.subscriptions_list);
        final CursorAdapter subscriptionsAdapter = new RSSCursorAdapter(this, null, 0);
        listView.setAdapter(subscriptionsAdapter);
        final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                String[] projection = {
                        SQLiteHelper.ID_KEY,
                        SQLiteHelper.SUBSCRIPTION_KEY};
                return new CursorLoader(context, RSSProvider.SUBSCRIPTIONS_URI, projection, null, null, null);
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
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Warning")
                            .setMessage("Do you wanna delete?")
                            .setCancelable(false)
                            .setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            context.getContentResolver().delete(RSSProvider.SUBSCRIPTIONS_URI,
                                                    SQLiteHelper.ID_KEY + "=?", new String[]{Long.toString(l)});

                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
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
                values.put(SQLiteHelper.SUBSCRIPTION_KEY, url);
                context.getContentResolver().insert(RSSProvider.SUBSCRIPTIONS_URI, values);
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
        Intent intent = new Intent(this, RSSService.class);
        startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                SQLiteHelper.ID_KEY,
                SQLiteHelper.TITLE_KEY,
                SQLiteHelper.LINK_KEY,
                SQLiteHelper.DESCRIPTION_KEY};
        return new CursorLoader(this, RSSProvider.FEEDS_URI, projection, null, null, null);
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