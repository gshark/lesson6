package ru.itmo.rss;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class RSSCursorAdapter extends CursorAdapter {
    public RSSCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        TextView view = new TextView(context);
        bindView(view, context, cursor);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String string = cursor.getString(cursor.getColumnIndex(SQLiteHelper.SUBSCRIPTION_KEY));
        ((TextView) view).setText(Html.fromHtml(string));
    }
}