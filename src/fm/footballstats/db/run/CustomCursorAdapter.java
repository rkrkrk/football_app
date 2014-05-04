package fm.footballstats.db.run;

import fm.footballstats.R;
import fm.footballstats.db.FreeContentProvider;
import fm.footballstats.db.PuckOutContentProvider;
import fm.footballstats.db.ShotContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends SimpleCursorAdapter {
	private Context mContext;
	private Context appContext;
	private int layout, TYPE;
	private Cursor cr;
	private final LayoutInflater inflater;

	public CustomCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int TYPE) {
		super(context, layout, c, from, to);
		this.layout = layout;
		this.mContext = context;
		this.inflater = LayoutInflater.from(context);
		this.cr = c;
		this.TYPE = TYPE;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(layout, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor c1) {
		super.bindView(view, context, c1);
		TextView tv1, tv2, tv3, tv4, tv5, tv6;
		String name, number;
		switch (TYPE) {
		case 1:
			tv1 = (TextView) view.findViewById(R.id.filtertext1);
			tv2 = (TextView) view.findViewById(R.id.stext2);
			tv3 = (TextView) view.findViewById(R.id.stext3);
			tv4 = (TextView) view.findViewById(R.id.stext4);
			tv5 = (TextView) view.findViewById(R.id.stext5);
			tv6 = (TextView) view.findViewById(R.id.stext6);
			tv1.setText(c1.getString(c1
					.getColumnIndexOrThrow(ShotContentProvider.TIME)));
			tv2.setText(c1.getString(c1
					.getColumnIndexOrThrow(ShotContentProvider.TEAM)));
			tv3.setText(c1.getString(c1
					.getColumnIndexOrThrow(ShotContentProvider.OUTCOME)));
			tv4.setText(c1.getString(c1
					.getColumnIndexOrThrow(ShotContentProvider.TYPE)));
			tv6.setText(c1.getString(c1
					.getColumnIndexOrThrow(ShotContentProvider.POSITION)));

			 name = c1.getString(c1
					.getColumnIndexOrThrow("panel_nickname"));
			 number = c1.getString(c1
					.getColumnIndexOrThrow(ShotContentProvider.PLAYER_ID));

			if ((name != null) && !name.equals("")) {
				tv5.setText(name);
			} else if ((number != null) && !number.equals("")) {
				int playerNo = 0;
				try {
					playerNo = -Integer.parseInt(number);
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "player input error #01");
				}
				if (playerNo > 0) {
					tv5.setText(Integer.toString(playerNo));
				} else {
					tv5.setText("");
				}

			}
			break;
		case 2:
			tv1 = (TextView) view.findViewById(R.id.ftext1);
			tv2 = (TextView) view.findViewById(R.id.ftext2);
			tv3 = (TextView) view.findViewById(R.id.ftext3);
			tv5 = (TextView) view.findViewById(R.id.ftext5);
			tv6 = (TextView) view.findViewById(R.id.ftext6);
			tv1.setText(c1.getString(c1
					.getColumnIndexOrThrow(FreeContentProvider.TIME)));
			tv2.setText(c1.getString(c1
					.getColumnIndexOrThrow(FreeContentProvider.TEAM)));
			tv3.setText(c1.getString(c1
					.getColumnIndexOrThrow(FreeContentProvider.REASON)));
			tv6.setText(c1.getString(c1
					.getColumnIndexOrThrow(FreeContentProvider.POSITION)));

			 name = c1.getString(c1
					.getColumnIndexOrThrow("panel_nickname"));
			 number = c1.getString(c1
					.getColumnIndexOrThrow(FreeContentProvider.PLAYER_ID));

			if ((name != null) && !name.equals("")) {
				tv5.setText(name);
			} else if ((number != null) && !number.equals("")) {
				int playerNo = 0;
				try {
					playerNo = -Integer.parseInt(number);
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "player input error #01");
				}
				if (playerNo > 0) {
					tv5.setText(Integer.toString(playerNo));
				} else {
					tv5.setText("");
				}

			}
			break;
		case 3:
			tv1 = (TextView) view.findViewById(R.id.ftext1);
			tv2 = (TextView) view.findViewById(R.id.ftext2);
			tv3 = (TextView) view.findViewById(R.id.ftext3);
			tv5 = (TextView) view.findViewById(R.id.ftext5);
			tv6 = (TextView) view.findViewById(R.id.ftext6);
			tv1.setText(c1.getString(c1
					.getColumnIndexOrThrow(PuckOutContentProvider.TIME)));
			tv2.setText(c1.getString(c1
					.getColumnIndexOrThrow(PuckOutContentProvider.TEAM)));
			tv3.setText(c1.getString(c1
					.getColumnIndexOrThrow(PuckOutContentProvider.OUTCOME)));
			tv6.setText(c1.getString(c1
					.getColumnIndexOrThrow(PuckOutContentProvider.POSITION)));

			 name = c1.getString(c1
					.getColumnIndexOrThrow("panel_nickname"));
			 number = c1.getString(c1
					.getColumnIndexOrThrow(PuckOutContentProvider.PLAYER_ID));

			if ((name != null) && !name.equals("")) {
				tv5.setText(name);
			} else if ((number != null) && !number.equals("")) {
				int playerNo = 0;
				try {
					playerNo = -Integer.parseInt(number);
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "player input error #01");
				}
				if (playerNo > 0) {
					tv5.setText(Integer.toString(playerNo));
				} else {
					tv5.setText("");
				}

			}
			break;
		}
	}

}
