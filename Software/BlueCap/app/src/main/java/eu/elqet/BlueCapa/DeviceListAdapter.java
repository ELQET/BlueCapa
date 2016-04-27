package eu.elqet.BlueCapa;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Matej Baran on 8.3.2016.
 */
public class DeviceListAdapter extends CursorAdapter {

    public DeviceListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.device_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE)));
        TextView textViewRssi = (TextView) view.findViewById(R.id.textViewRssi);
        TextView textViewVoltage = (TextView) view.findViewById(R.id.textViewVoltage);
        String[] statesArray = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_STATE)).split(",");
        textViewRssi.setText(statesArray[0]);
        textViewRssi.setTextColor(Utils.setColorFromRSSI(statesArray[0]));
        textViewVoltage.setText(statesArray[1]);
        textViewVoltage.setTextColor(Utils.setColorFromVoltage(statesArray[1]));
        TextView textViewMac = (TextView) view.findViewById(R.id.textViewMac);
        textViewMac.setText(cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_MAC)));
    }
}
