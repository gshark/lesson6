package ru.itmo.rss;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class RSSAdapter extends CursorAdapter {
    private final LayoutInflater inflater;

    public RSSAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View row = inflater.inflate(R.layout.item_row, null);
        bindView(row, context, cursor);
        return row;
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor) {
        TextView titleView = (TextView) row.findViewById(R.id.title);
        TextView descView = (TextView) row.findViewById(R.id.description);
        String title = cursor.getString(cursor.getColumnIndex(SQLiteHelper.TITLE_KEY));
        String description = cursor.getString(cursor.getColumnIndex(SQLiteHelper.DESCRIPTION_KEY));
        titleView.setText(Html.fromHtml(title));
        descView.setText(Html.fromHtml(description));
    }
}