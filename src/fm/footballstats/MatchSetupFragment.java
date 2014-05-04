/*
 *  MatchSetupFragment.java
 *
 *  Written by: Fintan Mahon 12101524
 *  
 *  Description: GUI to get input re match details and team lineup
 *  store details to database
 *  pass relevant details into MatchRecordFragment
 *  
 *  Written on: Jan 2013
 *  
 * 
 */
package fm.footballstats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import fm.footballstats.R;
import fm.footballstats.db.MatchContentProvider;
import fm.footballstats.db.PanelContentProvider;
import fm.footballstats.db.PositionContentProvider;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class MatchSetupFragment extends Fragment {
	// ArrayList to store panel from database
	private ArrayList<String> panelList = new ArrayList<String>();

	// HashMap to Store Player Name and ID for lookup on saving.
	private HashMap<String, Integer> playerIDLookUp = new HashMap<String, Integer>();
	private String[] minsList;// stores list of possible match times
	private String[] teamLineUpCurrent = new String[26];// stores selected team
	private String[] teamLineUpOriginal = new String[26];// stores selected team
	private Button[] bTeam = new Button[26];// array of buttons for team
											// selection
	private String[] TEAM = new String[26];// stores output strings for saved
	// preferences
	private SimpleDateFormat sdf;
	private Button b;
	// private MatchRecordFragment fragmentRecord;//referenence
	private EditText tTeamOpp, tLoc, tDate;
	private TextView tTeamHome;
	long matchID;
	int matchSaved = 0; // flag to tell if match details saved
	private String panelName, oppName;
	private Date currentDate;
	private SimpleDateFormat sdfdate;
	private boolean loaded = false;
	private Handler handler;
	private Runnable checkLoaded;
	SharedPreferences sharedPref;

	// setup uri to read panel from database using content provider
	Uri allTitles = PanelContentProvider.CONTENT_URI;
	String[] projection = { PanelContentProvider.PANELID,
			PanelContentProvider.FIRSTNAME, PanelContentProvider.SURNAME,
			PanelContentProvider.NICKNAME };

	@Override
	// start main method to display screen
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.setup_frag_layout, container, false);
		// set up text view and buttons
		tDate = (EditText) v.findViewById(R.id.t_setup_date);
		tTeamHome = (TextView) v.findViewById(R.id.t_setup_team1);
		tTeamOpp = (EditText) v.findViewById(R.id.t_setup_team2);
		tLoc = (EditText) v.findViewById(R.id.t_setup_loc);
		Button bButtonReset = (Button) v.findViewById(R.id.button_setup_reset);
		Button bButtonChange = (Button) v.findViewById(R.id.button_setup_pos);

		oppName = tTeamOpp.getText().toString();

		// Set buttonlisteners and use position numbers as default team lineup
		TEAM[0] = "T00";
		for (int i = 1; i <= 15; i++) {
			// set listener on team buttons
			bTeam[i] = (Button) v.findViewById(getResources()
					.getIdentifier("ButtonP" + String.format("%02d", i), "id",
							"fm.footballstats"));
			bTeam[i].setOnClickListener(teamSetupClickListener);
			// setup teamlineup if null then add number, if number or name
			// nothing required
			if (teamLineUpCurrent[i] == null) {
				teamLineUpCurrent[i] = String.valueOf(i);
				teamLineUpOriginal[i] = teamLineUpCurrent[i];
			}
			// set up strings for storing preferences to persist team data
			TEAM[i] = "T" + String.format("%02d", i);
		}

		sharedPref = getActivity().getSharedPreferences(
				"team_stats_setup_data", Context.MODE_PRIVATE);

		// read persisted stored data to set up screen on restart

		for (int i = 0; i <= 15; i++) {
			teamLineUpCurrent[i] = sharedPref.getString(TEAM[i],
					String.valueOf(i));
			teamLineUpOriginal[i] = teamLineUpCurrent[i];
			// player nicknames constrained on input to be >= 3 characters in
			// length so replace the player number with player nickname if
			// length greater than 3
			if ((teamLineUpCurrent[i].length() >= 3))
				bTeam[i].setText(teamLineUpCurrent[i]);
		}
		// restore match saved status
		// =1 means match setup saved already, can't be saved again
		matchSaved = sharedPref.getInt("MATCHSAVED", 0);
		// load matchID to use as foreign key in database saves. Defaults
		// to -1 if an actual value does not exist
		matchID = sharedPref.getLong("MATCHID", -1);

		// get panel name from storage too
		SharedPreferences sharedPrefPanel = getActivity().getSharedPreferences(
				"panellist", Context.MODE_PRIVATE);
		panelName = sharedPrefPanel.getString("PANELNAME", null);

		// setup input edittext boxes
		// put todays date in the Date box as default
		currentDate = new Date(System.currentTimeMillis());
		sdfdate = new SimpleDateFormat("dd/MM/yyyy");
		tDate.setText(sdfdate.format(currentDate));

		// check if data already entered. If data exists already update
		// the edittext fields
		// if (!sharedPref.getString("MATCHDATE", "").equals(""))
		// tDate.setText(sharedPref.getString("MATCHDATE", ""));
		// else
		// tDate.setText(sdfdate.format(currentDate));

		if (panelName != null)
			tTeamHome.setText(panelName);
		if (!sharedPref.getString("OPPTEAM", "").equals(""))
			tTeamOpp.setText(sharedPref.getString("OPPTEAM", ""));
		if (!sharedPref.getString("LOCATION", tLoc.toString()).equals(""))
			tLoc.setText(sharedPref.getString("LOCATION", ""));

		// load panel from database and assign to arraylist
		CursorLoader cL;
		if (panelName == null)
			cL = new CursorLoader(getActivity(), allTitles, projection,
					PanelContentProvider.PANELNAME + " is null ", null,
					PanelContentProvider.NICKNAME);
		else
			cL = new CursorLoader(getActivity(), allTitles, projection,
					PanelContentProvider.PANELNAME + " = '" + panelName + "'",
					null, PanelContentProvider.NICKNAME);
		Cursor c1 = cL.loadInBackground();
		if (c1.getCount() > 0) {
			c1.moveToFirst();
			playerIDLookUp.clear();
			panelList.clear();
			do {
				// read in player nicknames
				panelList.add(c1.getString(c1
						.getColumnIndexOrThrow(PanelContentProvider.NICKNAME)));
				// read player / playerID pairs into hashmap so that you can
				// look up and find player ID from player name
				playerIDLookUp
						.put(c1.getString(c1
								.getColumnIndexOrThrow(PanelContentProvider.NICKNAME)),
								c1.getInt(c1
										.getColumnIndexOrThrow(PanelContentProvider.PANELID)));
			} while (c1.moveToNext());
			// insert SWAP into panelist in 1st position to facilitate position
			// changes and substitutions
			panelList.add(0, "-RESET POSITION TO NUMBER-");
			// remove from list names of players that are already selected and
			// assigned to a button onscreen
			for (int j = 0; j <= 15; j++) {
				if (panelList.indexOf(teamLineUpCurrent[j]) != -1)
					panelList.remove(teamLineUpCurrent[j]);
			}

		}
		c1.close();
		// use panelNameTemp to avoid crash from passing null argument
		if (panelName == null)
			panelName = "";
		// save data to database
		// validate not saved already and no null value in date field
		// must have entered date before you can save
		if (matchSaved == 0) {
			// save match details to db
			ContentValues values = new ContentValues();
			values.put("date", tDate.getText().toString());
			values.put("location", tLoc.getText().toString());
			values.put("homeTeam", panelName);
			values.put("oppTeam", tTeamOpp.getText().toString());
			Uri uri = getActivity().getContentResolver().insert(
					MatchContentProvider.CONTENT_URI, values);
			matchID = Long.parseLong(uri.getLastPathSegment());
			// set flag to say data saved
			matchSaved = 1;
		}
		// set up connections to other fragments
		// do it timer to wait for them to be loaded
		loaded = false;
		handler = new Handler();
		checkLoaded = new Runnable() {
			@Override
			public void run() {
				updateFragments();
				if (loaded) {
					handler.removeCallbacks(checkLoaded);
				} else {
					handler.postDelayed(checkLoaded, 100);
				}
			}
		};
		checkLoaded.run();

		// //////////////////////////////////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////

		bButtonReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// get reference to REVIEW fragment from parent activity
				// MatchApplication and use reference to execute resetStats
				// method in REVIEW fragment which will reset stats there to 0
				Toast.makeText(getActivity(), "Long Press to Reset",
						Toast.LENGTH_SHORT).show();
			}
		});

		// Listener for reset team button
		// resets team lineup and edittext fields
		bButtonReset.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// Reset team lineup to default position numbers
				// and assign numbers ot buttons on screen
				for (int i = 1; i <= 15; i++) {
					teamLineUpCurrent[i] = String.valueOf(i);
					teamLineUpOriginal[i] = teamLineUpCurrent[i];
					bTeam[i].setText(String.valueOf(i));
				}
				// Reload panel from database
				CursorLoader cL;
				if (panelName == null)
					cL = new CursorLoader(getActivity(), allTitles, projection,
							PanelContentProvider.PANELNAME + " is null ", null,
							PanelContentProvider.NICKNAME);
				else
					cL = new CursorLoader(getActivity(), allTitles, projection,
							PanelContentProvider.PANELNAME + " = '" + panelName
									+ "'", null, PanelContentProvider.NICKNAME);
				Cursor c1 = cL.loadInBackground();
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					panelList.clear();
					playerIDLookUp.clear();
					do {
						// read in player nicknames
						panelList.add(c1.getString(c1
								.getColumnIndexOrThrow(PanelContentProvider.NICKNAME)));
						// read player / playerID pairs into hashmap so that you
						// can
						// look up and find player ID from player name
						playerIDLookUp.put(
								c1.getString(c1
										.getColumnIndexOrThrow(PanelContentProvider.NICKNAME)),
								c1.getInt(c1
										.getColumnIndexOrThrow(PanelContentProvider.PANELID)));
					} while (c1.moveToNext());
				}
				c1.close();
				// insert reset into panelist in 1st position to facilitate
				// substitutions and positon changes
				panelList.add(0, "-RESET POSITION TO NUMBER-");
				v.playSoundEffect(SoundEffectConstants.CLICK);
				return true;
			}
		});



		// Listener for make change button
		// This records postion changes or substitutions and updates team linup
		// in MatchReviewFragemnt and writes change to databse
		bButtonChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// compare current line up to saved version. It there is a
				// change,
				// write to database
				for (int i = 1; i <= 15; i++) {
					if (!teamLineUpCurrent[i].equals(teamLineUpOriginal[i]))
					// there's a change
					{
						// copy change to original line up
						teamLineUpOriginal[i] = teamLineUpCurrent[i];

						if (matchSaved == 1) {
							// write change to database

							Date currentTime = new Date(System
									.currentTimeMillis());
							sdf = new SimpleDateFormat("HH:mm:ss");
							ContentValues players = new ContentValues();
							players.clear();
							players.put("matchID", matchID);
							players.put("time", sdf.format(currentTime));
							players.put("posn", i);
							// look up hashmap playerIDLookUp to get the
							// playerID number from the player name. If player
							// name not found store -1
							if (playerIDLookUp.get(teamLineUpCurrent[i]) != null) {
								players.put("playerID", playerIDLookUp
										.get(teamLineUpCurrent[i]));
							} else {
								players.put("playerID", -1);
							}
							// these players are in the starting lineup so save
							// using code "start"
							players.put("code", "change");
							// insert into database
							getActivity().getContentResolver().insert(
									PositionContentProvider.CONTENT_URI,
									players);
						}
					}
				}
				savePlayers("changed");
			}
		});

		return v;
	}

	@Override
	public void onPause() {
		// Save out the details so that they are available on restart
		super.onPause(); // Always call the superclass method first
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("MATCHDATE", tDate.getText().toString());
		editor.putString("HOMETEAM", panelName);
		editor.putString("OPPTEAM", tTeamOpp.getText().toString());
		editor.putString("LOCATION", tLoc.getText().toString());
		editor.putInt("MATCHSAVED", matchSaved);
		editor.putLong("MATCHID", matchID);
		for (int i = 0; i <= 15; i++) {
			editor.putString(TEAM[i], teamLineUpCurrent[i]);
		}
		editor.commit();
	}

	public void updateFragments() {
		// pass match ID returned from database to RECORD
		// Fragment to act as foreign key in other database
		// tables
		// get reference to RECORD fragment from parent activity
		// MatchApplication and use reference to execute
		// setMatchID method in RECORD which will set the
		// matchID
		// pass team line up to Record Page
		// get reference to RECORD fragment from parent activity
		// MatchApplication
		// and use reference to execute setTeamLineUp method in RECORD
		// fragment which will set team names and team lineup

		try {
			((MatchApplication) getActivity()).getFragmentReview()
					.setTeamNames(panelName, tTeamOpp.getText().toString());
			((MatchApplication) getActivity()).getFragmentRecord()
					.setTeamLineUp(teamLineUpCurrent, panelName, oppName);
			((MatchApplication) getActivity()).getFragmentRecord().setMatchID(
					matchID);
			tTeamOpp.addTextChangedListener(watcher);
			loaded = true;
		} catch (Exception e) {
			Log.e("error fragments loading", "" + e.toString());
		}
	}

	public void savePlayers(String code) {
		// save starting team values to database player
		ContentValues players = new ContentValues();
		Date currentTime = new Date(System.currentTimeMillis());
		sdf = new SimpleDateFormat("HH:mm:ss");
		for (int i = 1; i <= 15; i++) {
			players.clear();
			players.put("matchID", matchID);

			players.put("time", sdf.format(currentTime));
			players.put("posn", i);
			// look up hashmap playerIDLookUp to get the
			// playerID number from the player name. If player
			// name not found store -1
			if (playerIDLookUp.get(teamLineUpCurrent[i]) != null) {
				players.put("playerID",
						playerIDLookUp.get(teamLineUpCurrent[i]));
			} else {
				players.put("playerID", -1);
			}
			// these players are in the starting lineup so save
			// using code "start"
			players.put("code", code);
			// insert into database
			getActivity().getContentResolver().insert(
					PositionContentProvider.CONTENT_URI, players);
		}
	}

	// Listener to select team lineup
	OnClickListener teamSetupClickListener = new OnClickListener() {

		@Override
		public void onClick(View w) {
			b = (Button) w;
			ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
					getActivity(), R.layout.single_row_layout, panelList);
			new AlertDialog.Builder(getActivity())
					.setTitle("select player")
					.setAdapter(adapter1,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// find which position number button has
									// been clicked
									String posnNo = getResources()
											.getResourceName(b.getId());
									int index = Integer.parseInt(posnNo
											.substring(posnNo.length() - 2,
													posnNo.length()));
									if (which == 0) {
										if (b.getText().length() > 2) {
											String s =  b.getText().toString();
											b.setText(String.valueOf(index));
											teamLineUpCurrent[index] = String
													.valueOf(index);
											panelList.add(s);
											panelList
													.remove("-RESET POSITION TO NUMBER-");
											Collections.sort(panelList);
											panelList
													.add(0,
															"-RESET POSITION TO NUMBER-");
										}

									} else {
										// Where a Player is not already
										// selected
										// for team the button text will be just
										// the
										// position number and so length < 3.
										// assign player to button/teamlineup
										// and
										// remove from panelList
										if (b.getText().length() < 3) {
											b.setText(panelList.get(which));
											teamLineUpCurrent[index] = panelList
													.get(which);
											panelList.remove(which);
										}
										// where Player already selected in
										// position
										// need to swap. Assign new player to
										// button/teamlineup. Add swapped out
										// player
										// back into panelList and Sort
										//
										else {
											String s =  b.getText().toString();
											b.setText(panelList.get(which));
											teamLineUpCurrent[index] = panelList
													.get(which);
											panelList.remove(which);
											if (panelList.indexOf(s) == -1)
												panelList.add(s);
											Collections.sort(panelList);
											// if SWAP is in sorted panelist,
											// remove
											// it and add it back in 1st
											// position
											if (panelList
													.indexOf("-RESET POSITION TO NUMBER-") != -1) {
												panelList
														.remove("-RESET POSITION TO NUMBER-");
												panelList
														.add(0,
																"-RESET POSITION TO NUMBER-");
											}
										}
									}
									dialog.dismiss();
								}
							}).create().show();
		}
	};
	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			oppName = s.toString();
			((MatchApplication) getActivity()).getFragmentRecord()
			.setOppName(oppName);
			((MatchApplication) getActivity()).getFragmentReview()
			.setOppName(oppName);
		}
	};
}
