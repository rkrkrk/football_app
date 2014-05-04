package fm.footballstats.db.run;

import java.util.ArrayList;

import fm.footballstats.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ShotAdapter extends ArrayAdapter<Shot> {
	private final Context context;
	private final ArrayList<Shot> values;

	public ShotAdapter(Context context, ArrayList<Shot> values) {
		super(context, R.layout.shot_row_layout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.shotplayer_row_layout, parent,
				false);
		TextView textView1 = (TextView) rowView.findViewById(R.id.stext1);
		TextView textView2 = (TextView) rowView.findViewById(R.id.stext2);
		TextView textView3 = (TextView) rowView.findViewById(R.id.stext3);
		TextView textView4 = (TextView) rowView.findViewById(R.id.stext4);
		TextView textView5 = (TextView) rowView.findViewById(R.id.stext5);
		TextView textView6 = (TextView) rowView.findViewById(R.id.stext6);
		TextView textView7 = (TextView) rowView.findViewById(R.id.stext7);

		textView1.setText(values.get(position).getTeam());
		textView2.setText(values.get(position).getName());
		if (values.get(position).isPlay()) {
			textView3.setText("total");
		} else {
			textView3.setText("placed");
		}
		textView4.setText(Integer.toString(values.get(position).getGoals()));
		textView5.setText(Integer.toString(values.get(position).getPoints()));
		textView6.setText(Integer.toString(values.get(position).getWides()));
		textView7.setText(Integer.toString(values.get(position).getMisses()));
		return rowView;
	}
}
