/*
 *  ShotsListActivity.java
 *
 *  Written by: Fintan Mahon
 *  Description: Lists shots statistics data from database. 
 *  
 *  
 *  Written on: Jan 2013
 *  
 * 
 */
package fm.footballstats.db.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fm.footballstats.HelpActivity;
import fm.footballstats.R;
import fm.footballstats.db.ShotContentProvider;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;



public class ShotsPlayerListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shotplayer_list_layout);
		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Shot item = (Shot) getListAdapter().getItem(position);
		Toast.makeText(this, item.getName() + " selected", Toast.LENGTH_LONG)
				.show();
	}

	// method to read in panel list from database and list contents on screen
	private void fillData() {
		ArrayList<Shot> shotsArray = new ArrayList<Shot>();
		ArrayList<String[]> listCursor = new ArrayList<String[]>();
		// ArrayList<String> noNames = new ArrayList<String>();
		// ArrayList<String[]> list = new ArrayList<String[]>();
		String str, str1;
		int goals = 0, points = 0, wides = 0, misses = 0, total = 0;
		String[] data = new String[6];
		boolean totalExists = false, play = true;
		// retreive shots info from database
		Uri allTitles = ShotContentProvider.CONTENT_URI;

		// load database info from PanelContentProvider into a cursor
		Cursor c1 = getContentResolver().query(allTitles, null, null, null,
				null);
		// convert cursor to arraylist
		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				data = new String[6];
				data[0] = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.TEAM));
				data[1] = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.OUTCOME));
				data[2] = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.TYPE));
				data[5] = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.PLAYER_ID));
				// sort out player
				str = c1.getString(c1.getColumnIndexOrThrow("panel_nickname"));
				if ((str == null) || str.equals("")) {
					data[3] = "unknown";
					int playerNo = 0;
					try {
						playerNo = -Integer.parseInt(data[5]);
					} catch (NumberFormatException nfe) {
						Log.v("matchrecord", "player input error #01");
					}
					if (playerNo > 0) {
						data[3] = Integer.toString(playerNo);
					}
				} else {
					data[3] = c1.getString(c1
							.getColumnIndexOrThrow("panel_nickname"));
				}
				data[4] = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.POSITION));

				listCursor.add(data);
			} while (c1.moveToNext());
			c1.close();

			while (listCursor.size() > 0) {
				goals = points = wides = misses = total = 0;
				str1 = listCursor.get(0)[1];
				// handle outcome
				if (str1 != null) {
					if (str1.equals("goal")) {
						goals++;
						total = 3;
					} else if (str1.equals("point")) {
						points++;
						total = 1;
					} else if (str1.equals("wide")) {
						wides++;
					} else if ((str1.equals("45/65")) || (str1.equals("saved"))
							|| (str1.equals("short"))
							|| (str1.equals("off posts"))) {
						misses++;
					}
				}
				// handle play/placed
				str1 = listCursor.get(0)[2];
				play = true;
				if (str1 != null) {
					if ((str1.equals("free")) || (str1.equals("45/65"))
							|| (str1.equals("sideline"))
							|| (str1.equals("penalty"))) {
						play = false;
					}
				}

				// check if name is already in shot array
				// get name
				str = listCursor.get(0)[3];// name
				str1 = listCursor.get(0)[0];// name
				totalExists = false;
				if (shotsArray.size() > 0) {
					for (int i = 0; i < shotsArray.size(); i++) {
						if (shotsArray.get(i).isPlay()
								&& str.equals(shotsArray.get(i).getName())
								&& str1.equals(shotsArray.get(i).getTeam())) {
							totalExists = true;
							break;
						}
					}
				} // if shotsArray
				if (!totalExists) {
					// add to totals
					shotsArray.add(new Shot(listCursor.get(0)[0], listCursor
							.get(0)[3], true, total, goals, points, wides,
							misses));
					if (!play) {
						// placed ball add another entry
						shotsArray.add(new Shot(listCursor.get(0)[0],
								listCursor.get(0)[3], play, total, goals,
								points, wides, misses));
					}
				} else {
					// update existing total first
					for (int i = 0; i < shotsArray.size(); i++) {
						if (str.equals(shotsArray.get(i).getName())
								&& shotsArray.get(i).isPlay() == true
								&& str1.equals(shotsArray.get(i).getTeam())) {
							shotsArray.get(i).updateTotal(total);
							shotsArray.get(i).updateGoals(goals);
							shotsArray.get(i).updatePoints(points);
							shotsArray.get(i).updateWides(wides);
							shotsArray.get(i).updateMisses(misses);
							// get current total and use it for placed ball
							// entry
							// so that sorting will work
							total = shotsArray.get(i).getTotal();
							break;
						}
					}
					// if placed create new or update entry
					boolean gotcha = false;
					for (int i = 0; i < shotsArray.size(); i++) {
						if (str.equals(shotsArray.get(i).getName())
								&& shotsArray.get(i).isPlay() == false
								&& str1.equals(shotsArray.get(i).getTeam())) {
							shotsArray.get(i).setTotal(total);
							if (!play) {
								shotsArray.get(i).updateGoals(goals);
								shotsArray.get(i).updatePoints(points);
								shotsArray.get(i).updateWides(wides);
								shotsArray.get(i).updateMisses(misses);
							}
							gotcha = true;
							break;
						}
					}

					if (!gotcha && !play) {
						// create new entry
						shotsArray.add(new Shot(listCursor.get(0)[0],
								listCursor.get(0)[3], play, total, goals,
								points, wides, misses));
					}

				}
				listCursor.remove(0);

			}// while

		}// if cursur > 0

		Collections.sort(shotsArray, new Comparator<Shot>() {
			@Override
			public int compare(Shot p1, Shot p2) {
				String p1Team = p1.getTeam();
				String p2Team = p2.getTeam();
				String p1Name = p1.getName();
				String p2Name = p2.getName();
				Boolean p1play = p1.isPlay();
				Boolean p2play = p2.isPlay();
				Integer p1Total = p1.getTotal();
				Integer p2Total = p2.getTotal();
				int result = p1Team.compareTo(p2Team);
				if (result == 0) {
					result = p2Total.compareTo(p1Total);
				}
				if (result == 0) {
					result = p1Name.compareTo(p2Name);
				}
				if (result == 0) {
					result = p2play ? 1 : 0;
				}

				return result;
			}
		});

		ShotAdapter adapter = new ShotAdapter(this, shotsArray);
		setListAdapter(adapter);
	}

	// set up long press menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.list_menu_longpress, menu);
	}

	@Override
	// deal with selection from long press menu
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete1:
			// Delete a row / player
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Uri uri = Uri
					.parse(ShotContentProvider.CONTENT_URI + "/" + info.id);
			getContentResolver().delete(uri, null, null);
			Toast.makeText(getApplicationContext(), "Shot Deleted",
					Toast.LENGTH_LONG).show();
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	// Set up Help in ActionBar Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuAction = getMenuInflater();
		menuAction.inflate(R.menu.help_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// create intent with name of help screen to be loaded
		// R.string.startHelp is in the assets
		switch (item.getItemId()) {
		case R.id.helpList:
			Intent ihelp = new Intent(this, HelpActivity.class);
			ihelp.putExtra("HELP_ID", R.string.reviewListHelp);
			startActivity(ihelp);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
