package eu.elqet.BlueCapa;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Matej Baran on 9.4.2016.
 */
public class LogListAdapter extends CursorAdapter {
    public LogListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.log_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP));
        TextView textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        textViewTime.setText(Utils.getOnlyTimeFromStamp(timeStamp));
        TextView textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewDate.setText(Utils.getOnlyDateFromStamp(timeStamp));
    }
}
