/*
 *  MatchRecordFragment.java
 *
 *  Written by: Fintan Mahon 12101524
 *  
 *  Description: GUI to get input and display output for
 *  1. match timer
 *  2. match score
 *  3. match statistics
 *  
 * store data to database tables and pass relevant details into MatchReviewFragment
 *  
 *  Written on: Jan 2013
 *  
 * 
 */
package fm.footballstats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import fm.footballstats.R;
import fm.footballstats.db.CustomContentProvider;
import fm.footballstats.db.FreeContentProvider;
import fm.footballstats.db.MatchContentProvider;
import fm.footballstats.db.PanelContentProvider;
import fm.footballstats.db.PositionContentProvider;
import fm.footballstats.db.PuckOutContentProvider;
import fm.footballstats.db.ShotContentProvider;

public class MatchRecordFragment extends Fragment {

	// declare and initialise variables
	public int minsPerHalf = 30;
	private int homeGoals = 0, homePoints = 0, oppGoals = 0, oppPoints = 0;
	private Timer timer;
	private TextView tHalf, tStartTime, tTimeGone, tTimeToGo, tTimeLeft,
			cstat1, cstat2;
	private TextView tHomeTotal, tUpDownDrawText, tHomeDifference, tOppTotal,
			tStats;
	private String puckOutReason = "", puckOutPlayer = "", puckOutPosn = "";
	private TextView tOurTeam, tOppTeam;
	private Button bResetTime, bResetScore, bResetStats;
	private Button bStartStop, bDecreaseTime, bIncreaseTime;
	private Button bDecHomeGoals, bHomeGoals, bHomePoints, bDecHomePoints;
	private Button bDecOppGoals, bOppGoals, bOppPoints, bDecOppPoints;
	private Button bShotHome, bShotOpp, bMinsPerHalf;
	private Button bFreeHome, bFreeOpp;
	private Button bPuckOutHome, bPuckOutOpp;
	private Button bCustomHome, bCustomOpp;
	private Button bUndo, bTweet, bText;
	private int SHOT_FREE_PUCK_OUT, txtButton;
	private long matchID = -1; // flag to determine if match ID created in
								// database
	private String[] TEAMOPP = new String[16];
	private String[] CUSTOMAP = new String[9];
	private String[] CUSTOMBP = new String[9];
	private String[] TEAM = new String[16];// stores output strings for saved
	// preferences

	private Handler h = new Handler();
	private long starttime = 0;
	private Date currentDate;
	private SimpleDateFormat sdf, sdftime;
	private AlertDialog alertshot = null, alertpitch = null;
	private String[] teamLineUp = new String[16];
	private String[] teamLineUpOpp = new String[16];
	private String[] minsList;
	private String[] undoString = new String[6];
	private HashMap<String, Integer> playerIDLookUp = new HashMap<String, Integer>();
	// setup uri to read panel from database
	private Uri allTitles = PanelContentProvider.CONTENT_URI;
	private String[] projection = { PanelContentProvider.PANELID,
			PanelContentProvider.FIRSTNAME, PanelContentProvider.SURNAME,
			PanelContentProvider.NICKNAME };
	private ArrayList<String[]> undoList = new ArrayList<String[]>();
	private long rowId;
	private String comment = "";
	private SharedPreferences sharedPref, sharedPrefSetup;
	private String shotResult = "", shotPlayer = "", shotType = "",
			shotPosn = "", customResult = "", customType = "";
	private String freeReason = "", freePlayer = "", freePosn = "", cTitle1,
			cTitle2;
	private GRadioGroup grPuckouts, grFrees;
	private String[] customResultStr = new String[9];
	private String[] customTypeStr = new String[9];
	private String[] free = new String[10];

	@Override
	// start main method to display screen
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.record_frag_layout, container, false);
		String myTag = getTag();
		((MatchApplication) getActivity()).setTagFragmentRecord(myTag);

		// on very first start up after installation
		// if no player names from setup screen just use a number to indicate
		// position
		if (teamLineUp[1] == null) {
			for (int i = 1; i <= 15; i++) {
				teamLineUp[i] = String.valueOf(i);
				teamLineUpOpp[i] = String.valueOf(i);
			}
		}

		// open sharedpreferences file to read in saved persisted data on
		// startup
		sharedPref = getActivity().getSharedPreferences(
				"team_stats_record_data", Context.MODE_PRIVATE);

		sharedPrefSetup = getActivity().getSharedPreferences(
				"team_stats_setup_data", Context.MODE_PRIVATE);

		// get the tag name of this Fragment and pass it up to the parent
		// activity MatchApplication so that this Fragment may be accessed
		// by other fragments through using a reference created from tag name

		for (int i = 0; i < 9; i++) {
			CUSTOMAP[i] = "CustomAP" + String.format("%02d", i);
			CUSTOMBP[i] = "CustomBP" + String.format("%02d", i);
		}
		customResultStr[0] = (sharedPrefSetup.getString(CUSTOMAP[0], "hand pass complete"));
		customResultStr[1] = (sharedPrefSetup.getString(CUSTOMAP[1], "hand pass fail"));
		customResultStr[2] = (sharedPrefSetup.getString(CUSTOMAP[2],
				"kick pass complete"));
		customResultStr[3] = (sharedPrefSetup.getString(CUSTOMAP[3],
				"kick pass fail"));
		customResultStr[4] = (sharedPrefSetup.getString(CUSTOMAP[4],
				"possession won"));
		customResultStr[5] = (sharedPrefSetup.getString(CUSTOMAP[5],
				"possession lost"));
		customResultStr[6] = (sharedPrefSetup.getString(CUSTOMAP[6],
				"block"));
		customResultStr[7] = (sharedPrefSetup.getString(CUSTOMAP[7],
				"tackle made"));
		customResultStr[8] = (sharedPrefSetup.getString(CUSTOMAP[8],
				"custom option"));
		customTypeStr[0] = (sharedPrefSetup.getString(CUSTOMBP[0],
				"good option"));
		customTypeStr[1] = (sharedPrefSetup.getString(CUSTOMBP[1],
				"poor option"));
		customTypeStr[2] = (sharedPrefSetup.getString(CUSTOMBP[2], "black card"));
		customTypeStr[3] = (sharedPrefSetup.getString(CUSTOMBP[3], "yellow card"));
		customTypeStr[4] = (sharedPrefSetup.getString(CUSTOMBP[4],
				"red card"));
		customTypeStr[5] = (sharedPrefSetup.getString(CUSTOMBP[5],
				"custom option"));
		customTypeStr[6] = (sharedPrefSetup.getString(CUSTOMBP[6], "custom option"));
		customTypeStr[7] = (sharedPrefSetup.getString(CUSTOMBP[7], "custom option"));
		customTypeStr[8] = (sharedPrefSetup.getString(CUSTOMBP[8], "custom option"));
		cTitle1 = sharedPrefSetup.getString("CTITLE1P", "stats 1");
		cTitle2 = sharedPrefSetup.getString("CTITLE2P", "stats 2");
		// read in free from setup
		free[0] = sharedPrefSetup.getString("FreeP00", "steps");
		free[1] = sharedPrefSetup.getString("FreeP01", "2 hops");
		free[2] = sharedPrefSetup.getString("FreeP02", "throw");
		free[3] = sharedPrefSetup.getString("FreeP03", "square ball");
		free[4] = sharedPrefSetup.getString("FreeP04", "pick off ground");
		free[5] = sharedPrefSetup.getString("FreeP05", "push/pull/trip");
		free[6] = sharedPrefSetup.getString("FreeP06", "holding");
		free[7] = sharedPrefSetup.getString("FreeP07", "striking");
		free[8] = sharedPrefSetup.getString("FreeP08", "charging");
		free[9] = sharedPrefSetup.getString("FreeP09", "other");

		// set up text buttons edittexts etc.
		tHalf = (TextView) v.findViewById(R.id.which_half);
		tStartTime = (TextView) v.findViewById(R.id.start_time);
		tTimeGone = (TextView) v.findViewById(R.id.time_gone);
		tTimeToGo = (TextView) v.findViewById(R.id.time_to_go);
		tTimeLeft = (TextView) v.findViewById(R.id.time_left);
		tHomeTotal = (TextView) v.findViewById(R.id.home_total);
		tUpDownDrawText = (TextView) v.findViewById(R.id.up_down_draw_text);
		tHomeDifference = (TextView) v.findViewById(R.id.home_difference);
		tOppTotal = (TextView) v.findViewById(R.id.opp_total);
		tOurTeam = (TextView) v.findViewById(R.id.ourTeam);
		tOppTeam = (TextView) v.findViewById(R.id.oppTeam);
		

		tStats = (TextView) v.findViewById(R.id.textViewStats);

		bStartStop = (Button) v.findViewById(R.id.start_stop_timer);
		bDecreaseTime = (Button) v.findViewById(R.id.decrease_timer);
		bIncreaseTime = (Button) v.findViewById(R.id.increase_timer);
		bResetTime = (Button) v.findViewById(R.id.reset_timer);

		bDecHomeGoals = (Button) v.findViewById(R.id.dec_home_goals);
		bHomeGoals = (Button) v.findViewById(R.id.home_goals);
		bHomePoints = (Button) v.findViewById(R.id.home_points);
		bDecHomePoints = (Button) v.findViewById(R.id.dec_home_points);
		bDecOppGoals = (Button) v.findViewById(R.id.dec_opp_goals);
		bOppGoals = (Button) v.findViewById(R.id.opp_goals);
		bOppPoints = (Button) v.findViewById(R.id.opp_points);
		bDecOppPoints = (Button) v.findViewById(R.id.dec_opp_points);
		bResetScore = (Button) v.findViewById(R.id.reset_score);
		bResetStats = (Button) v.findViewById(R.id.reset_stats);
		bUndo = (Button) v.findViewById(R.id.buttonUndo);
		// bUndo.setOnClickListener(undoOnClickListener);

		// read in undolist
		retrieveUndoList();
		if (undoList.size() > 0) {
			updateStatsList();
			bUndo.setTextColor(Color.parseColor("#000000"));
			bUndo.setOnClickListener(undoOnClickListener);
		}
		bTweet = (Button) v.findViewById(R.id.bTweet);
		bTweet.setOnClickListener(tweetScoreListener);
		bText = (Button) v.findViewById(R.id.bText);
		bText.setOnClickListener(tweetScoreListener);

		// //////////////////////set Team Names//////////////////////////
		// use persisted data if it exists else use default data
		tOurTeam.setText(sharedPref.getString("OURTEAM", "OWN TEAM"));
		tOppTeam.setText(sharedPref.getString("OPPTEAM", "OPPOSITION"));
		tStats.setText(sharedPref.getString("STATSLIST", ""));
		// //////////set up array of names to persist team lineup////////
		// and read in saved lineup if it exists. Default to position number
		// if there are no saved names
		TEAM[0] = "T00";
		for (int i = 1; i <= 15; i++) {
			TEAM[i] = "T" + String.format("%02d", i);
			teamLineUp[i] = sharedPref.getString(TEAM[i], String.valueOf(i));
			TEAMOPP[i] = "ToppP" + String.format("%02d", i);
			teamLineUpOpp[i] = sharedPrefSetup.getString(TEAMOPP[i],
					String.valueOf(i));
		}

		// /////////////////////load MATCHID flag//////////////////////////
		// load matchID to use as foreign key in database saves. Defaults
		// to -1 if an actual value does not exist
		matchID = sharedPref.getLong("MATCHID", -1);

		// ///////////////////MINUTES PER HALF SECTION////////////////////////
		bMinsPerHalf = (Button) v.findViewById(R.id.mins_per_half);
		// set mins per half from saved value if it exists, else default to 30
		bMinsPerHalf.setText(String.valueOf(sharedPref
				.getInt("MINSPERHALF", 30)));
		minsPerHalf = sharedPref.getInt("MINSPERHALF", 30);
		// set click listener for mins per half button
		bMinsPerHalf.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View w) {
				Button b = (Button) w;
				// read list of allowable times from array in assets and put in
				// adapter to display in alertdialog for selection
				minsList = getResources().getStringArray(R.array.minsPerHalf);
				ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
						getActivity(), R.layout.single_row_layout, minsList);
				new AlertDialog.Builder(getActivity())
						.setTitle("set minutes per half")
						.setAdapter(adapter1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// convert string input to integer
										minsPerHalf = Integer
												.valueOf(minsList[which]);
										// put new value on button
										bMinsPerHalf.setText(minsList[which]);
										dialog.dismiss();
									}
								}).create().show();
			}
		});

		// ///////////////////////////TIMER SETUP///////////////////////////
		// retrieve saved value if its there
		starttime = sharedPref.getLong("STARTTIME", 0);
		String[] str = new String[2];// stores display text for 1st/2nd half
		// set text on screen according to whether in first half or 2nd half
		// and whether timer is running or not
		if ((sharedPref.getString("TIMERBUTTON", "start") == "stop")
				&& (sharedPref.getString("HALFTEXT", "START FIRST HALF") == "IN FIRST HALF")) {
			str = settTimer("start", "START FIRST HALF");
			bStartStop.setText(str[0]);
			tHalf.setText(str[1]);
		} else if ((sharedPref.getString("TIMERBUTTON", "start") == "stop")
				&& (sharedPref.getString("HALFTEXT", "START FIRST HALF") == "IN SECOND HALF")) {
			str = settTimer("start", "START SECOND HALF");
			bStartStop.setText(str[0]);
			tHalf.setText(str[1]);
		} else if ((sharedPref.getString("TIMERBUTTON", "start") == "start")
				&& (sharedPref.getString("HALFTEXT", "START FIRST HALF") == "START SECOND HALF")) {
			str = settTimer("stop", "IN FIRST HALF");
			bStartStop.setText(str[0]);
			tHalf.setText(str[1]);
		}

		// clicklistener for start/stop button toggle
		bStartStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				String[] str = new String[2];
				str = settTimer( b.getText().toString(), tHalf.getText().toString());
				b.setText(str[0]);
				tHalf.setText(str[1]);
			}
		});

		// clicklistener for increment time button
		// if clicked add a minute to the timer be subtracting a minute from the
		// timer starttime. Update starttime text too
		bIncreaseTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (starttime != 0) {
					starttime = starttime - 30000;
					currentDate = new Date(starttime);
					sdf = new SimpleDateFormat("HH:mm:ss   dd-MM-yyyy");
					tStartTime.setText("Start Time: " + sdf.format(currentDate));
				}
			}
		});

		// clicklistener for decrement time button
		// if clicked take a minute to the timer be subtracting a minute from
		// the
		// timer starttime. Update starttime text too
		bDecreaseTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((starttime != 0)
						&& (System.currentTimeMillis() - starttime > 30000)) {
					starttime = starttime + 30000;
					currentDate = new Date(starttime);
					sdf = new SimpleDateFormat("HH:mm:ss   dd-MM-yyyy");
					tStartTime.setText("Start Time: " + sdf.format(currentDate));
				}
			}
		});

		// reset timer button click listener
		bResetTime.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (timer != null) {
					timer.cancel();
					timer.purge();
					h.removeCallbacks(run);
				}
				bStartStop.setText("start");
				tHalf.setText("START FIRST HALF");
				tTimeGone.setText("00:00");
				tTimeToGo.setText("00:00");
				tStartTime.setText("Start Time: 00:00");
				starttime = 0;
				v.playSoundEffect(SoundEffectConstants.CLICK);
				return true;
			}
		});

		// /////////////////////////////SCORE///////////////////////////////////////
		// one clickListener handles all input from score buttons
		bDecHomeGoals.setOnClickListener(scoreAddClickListener);
		bHomeGoals.setOnClickListener(scoreAddClickListener);
		bHomePoints.setOnClickListener(scoreAddClickListener);
		bDecHomePoints.setOnClickListener(scoreAddClickListener);
		bDecOppGoals.setOnClickListener(scoreAddClickListener);
		bOppGoals.setOnClickListener(scoreAddClickListener);
		bOppPoints.setOnClickListener(scoreAddClickListener);
		bDecOppPoints.setOnClickListener(scoreAddClickListener);

		// ///////HANDLE SCORES FROM PERSISTED SHARED PREFERENCES////
		homeGoals = sharedPref.getInt("HOMEGOALS", 0);
		homePoints = sharedPref.getInt("HOMEPOINTS", 0);
		oppGoals = sharedPref.getInt("OPPGOALS", 0);
		oppPoints = sharedPref.getInt("OPPPOINTS", 0);

		if (homeGoals + homePoints + oppGoals + oppPoints > 0) {
			bHomeGoals.setText(String.valueOf(homeGoals));
			bHomePoints.setText(String.valueOf(homePoints));
			bOppGoals.setText(String.valueOf(oppGoals));
			bOppPoints.setText(String.valueOf(oppPoints));
			setTotals();
		}

		// reset score button click listener
		// set scores back to 0
		bResetScore.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// reset score in this fragment and also on REVIEW fragment
				bHomeGoals.setText("+");
				homeGoals = homePoints = oppGoals = oppPoints = 0;
				// reset score in REVIEW fragment
				// get reference to REVIEW fragment from parent activity
				// MatchApplication and use reference to execute setHomeGoals
				// method in REVIEW fragment which will reset score there to 0
				((MatchApplication) getActivity()).getFragmentReview()
						.settHomeGoals(0);
				bHomePoints.setText("+");
				((MatchApplication) getActivity()).getFragmentReview()
						.settHomePoints(0);
				bOppGoals.setText("+");
				((MatchApplication) getActivity()).getFragmentReview()
						.settOppGoals(0);
				bOppPoints.setText("+");
				((MatchApplication) getActivity()).getFragmentReview()
						.settOppPoints(0);
				tHomeTotal.setText("(0)");
				tOppTotal.setText("(0)");
				tUpDownDrawText.setText("drawn game. ");
				tHomeDifference.setText(" ");
				v.playSoundEffect(SoundEffectConstants.CLICK);
				return true;
			}
		});

		// stats button click listener just diplays message to longpress
		bResetStats.setOnClickListener(resetClickListener);
		bResetScore.setOnClickListener(resetClickListener);
		bResetTime.setOnClickListener(resetClickListener);

		// reset stats button click listener
		// set all stats back to zero on REVIEW fragment screen
		bResetStats.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// get reference to REVIEW fragment from parent activity
				// MatchApplication and use reference to execute resetStats
				// method in REVIEW fragment which will reset stats there to 0
				// ((MatchApplication) getActivity()).getFragmentReview()
				// .resetStats();
				// clear undo list
				undoList.clear();
				updateStatsList();
				// empty database tabless
				getActivity().getContentResolver().delete(
						Uri.parse(FreeContentProvider.CONTENT_URI + "/"), null,
						null);
				getActivity().getContentResolver().delete(
						Uri.parse(ShotContentProvider.CONTENT_URI + "/"), null,
						null);
				getActivity().getContentResolver().delete(
						Uri.parse(PuckOutContentProvider.CONTENT_URI + "/"),
						null, null);
				v.playSoundEffect(SoundEffectConstants.CLICK);
				Toast.makeText(getActivity(), "Stats Reset", Toast.LENGTH_SHORT)
						.show();
				puckOutReason = "";
				puckOutPlayer = "";
				puckOutPosn = "";
				freeReason = "";
				freePlayer = "";
				freePosn = "";
				shotResult = "";
				shotType = "";
				shotPlayer = "";
				shotPosn = "";
				return true;
			}
		});

		// ////////////////////SHOT STATS SETUP///////////////////

		bShotHome = (Button) v.findViewById(R.id.buttonShotHome);
		bShotHome.setOnClickListener(statsClickListener);
		bShotOpp = (Button) v.findViewById(R.id.buttonShotOpp);
		bShotOpp.setOnClickListener(statsClickListener);

		// //////////////////////////FREE STATS
		// SETUP////////////////////////////

		bFreeHome = (Button) v.findViewById(R.id.buttonFreeHome);
		bFreeHome.setOnClickListener(statsClickListener);
		bFreeOpp = (Button) v.findViewById(R.id.buttonFreeOpp);
		bFreeOpp.setOnClickListener(statsClickListener);

		// //////////////////////PUCKOUT STATS SETUP////////////////////////////

		bPuckOutHome = (Button) v.findViewById(R.id.buttonPuckHome);
		bPuckOutHome.setOnClickListener(statsClickListener);
		bPuckOutOpp = (Button) v.findViewById(R.id.buttonPuckOpp);
		bPuckOutOpp.setOnClickListener(statsClickListener);

		bCustomHome = (Button) v.findViewById(R.id.buttonCustomHome);
		bCustomHome.setOnClickListener(statsClickListener);
		bCustomOpp = (Button) v.findViewById(R.id.buttonCustomOpp);
		bCustomOpp.setOnClickListener(statsClickListener);

		// load panel from database and assign to arraylist ready to be used
		// in stats recording dialogs
		CursorLoader cL = new CursorLoader(getActivity(), allTitles,
				projection, null, null, PanelContentProvider.NICKNAME);
		Cursor c1 = cL.loadInBackground();
		c1.moveToFirst();
		// read player nicknames and players IDs into a hash map so you can
		// get ID associated with player nickname
		playerIDLookUp.clear();
		if (c1.getCount() > 0) {
			do {
				// read player / playerID pairs into hashmap
				playerIDLookUp
						.put(c1.getString(c1
								.getColumnIndexOrThrow(PanelContentProvider.NICKNAME)),
								c1.getInt(c1
										.getColumnIndexOrThrow(PanelContentProvider.PANELID)));
			} while (c1.moveToNext());
		}

		return v;
	}

	// ********************************************************************//
	// ///////////////////////////END OF ONCREATE SECTION //////////////////
	// ********************************************************************//

	// for reset buttons diplay message to long click, won't work with ordinary
	// click
	OnClickListener resetClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// get reference to REVIEW fragment from parent activity
			// MatchApplication and use reference to execute resetStats
			// method in REVIEW fragment which will reset stats there to 0
			Toast.makeText(getActivity(), "Long Press to Reset",
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onPause() {
		// Save/persist data to be used on reopen
		super.onPause(); // Always call the superclass method first
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("MINSPERHALF", minsPerHalf);
		editor.putLong("STARTTIME", starttime);
		editor.putInt("HOMEGOALS", homeGoals);
		editor.putInt("HOMEPOINTS", homePoints);
		editor.putInt("OPPGOALS", oppGoals);
		editor.putInt("OPPPOINTS", oppPoints);
		editor.putString("TIMERBUTTON", bStartStop.getText().toString());
		editor.putString("HALFTEXT", tHalf.getText().toString());
		editor.putString("OURTEAM", tOurTeam.getText().toString());
		editor.putString("OPPTEAM", tOppTeam.getText().toString());
		editor.putString("STATSLIST", tStats.getText().toString());
		editor.putLong("MATCHID", matchID);
		// save team lineup
		// for (int i = 0; i <= 15; i++) {
		// editor.putString(TEAM[i], teamLineUp[i]);
		// }
		editor.commit();
		storeUndoList();
	}

	OnClickListener tweetScoreListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			txtButton = ((Button) v).getId();

			switch (txtButton) {
			case R.id.bTweet:
				try {
					Intent shareIntent = findTwitterClient();
					shareIntent.putExtra(Intent.EXTRA_TEXT, getScore());
					startActivity(Intent.createChooser(shareIntent, "Share"));
				} catch (Exception ex) {
					Log.e("Error in Tweet", ex.toString());
					Toast.makeText(
							getActivity(),
							"Can't find twitter client\n"
									+ "Please install Twitter App\nand login to Twitter",
							Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.bText:
				try {
					Intent intentText = new Intent(Intent.ACTION_VIEW);
					intentText.setType("vnd.android-dir/mms-sms");
					intentText.putExtra("sms_body", getScore());
					// intentText.setData(Uri.parse("sms: " + phone));
					startActivity(intentText);
				} catch (Exception ex) {
					Log.e("Error in Text", ex.toString());
					Toast.makeText(getActivity(),
							"Unable to send text message", Toast.LENGTH_LONG)
							.show();
				}
				break;
			}
		}
	};

	private String getScore() {
		// String str = getTime().equals("") ? "" : getTime() + "mins ";
		String str = "";
		String str1 = tHalf.getText().toString();
		if ((str1.contains("START F") && (getTime().equals("")))) {
			str = "";
		} else if (str1.contains("IN F")) {
			str = getTime() + "mins 1st half. ";
		} else if (str1.contains("START S")) {
			str = "Half Time. ";
		} else if (str1.contains("IN S")) {
			str = getTime() + "mins 2nd half. ";
		}
		if ((str1.contains("START F") && (!tTimeGone.getText().toString()
				.equals("00:00")))) {
			str = "Full Time. ";
		}
		comment = sharedPrefSetup.getString("LOCATION", "");
		String str2 = (comment.equals("")) ? "" : comment + ". ";
		return str2
				+ str
				+ tOurTeam.getText()
				+ ":"
				+ (bHomeGoals.getText().equals("+") ? "0" : bHomeGoals
						.getText())
				+ "-"
				+ (bHomePoints.getText().equals("+") ? "0" : bHomePoints
						.getText())
				+ tHomeTotal.getText()
				+ "  "
				+ tOppTeam.getText()
				+ ":"
				+ (bOppGoals.getText().equals("+") ? "0" : bOppGoals.getText())
				+ "-"
				+ (bOppPoints.getText().equals("+") ? "0" : bOppPoints
						.getText()) + tOppTotal.getText() + ". ";
	};

	public String getTime() {
		if (starttime > 0) {
			long millis = System.currentTimeMillis() - starttime;
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			return String.format("%02d", minutes);
		} else {
			return "";
		}
	};

	public Intent findTwitterClient() {
		final String[] twitterApps = {
				// package // name - nb installs (thousands)
				"com.twitter.android", // official - 10 000
				"com.twidroid", // twidroid - 5 000
				"com.handmark.tweetcaster", // Tweecaster - 5 000
				"com.thedeck.android" }; // TweetDeck - 5 000 };
		Intent tweetIntent = new Intent();
		tweetIntent.setType("text/plain");
		final PackageManager packageManager = getActivity().getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

		for (int i = 0; i < twitterApps.length; i++) {
			for (ResolveInfo resolveInfo : list) {
				String p = resolveInfo.activityInfo.packageName;
				if (p != null && p.startsWith(twitterApps[i])) {
					tweetIntent.setPackage(p);
					return tweetIntent;
				}
			}
		}
		return null;
	};

	// Run Match timer section. Set text strings and timer based on 4
	// possibilities:
	// 1. ready to start first half
	// 2. first half running
	// 3. first half ended ready to start second half
	// 4. second half running
	private String[] settTimer(String bStr, String bHalf) {
		String[] str = new String[2];
		sdf = new SimpleDateFormat("HH:mm:ss   dd-MM-yyyy");
		sdftime = new SimpleDateFormat("HH:mm:ss");

		if (bStr.equals("start") && bHalf.equals("START SECOND HALF")) {
			// 3. first half ended ready to start second half
			if (starttime == 0)
				starttime = System.currentTimeMillis();
			currentDate = new Date(starttime);
			tStartTime.setText("Second Half Start Time: "
					+ sdf.format(currentDate));
			timer = new Timer();
			h.postDelayed(run, 0);
			str[0] = "stop";
			str[1] = "IN SECOND HALF";
			return str;
		} else if (bStr.equals("stop") && bHalf.equals("IN SECOND HALF")) {
			// 4. second half running
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			h.removeCallbacks(run);
			// when second half stops write mins per half, secon half start time
			// and final score to database
			if (matchID >= 0) {
				ContentValues values = new ContentValues();
				values.put("time2", sdftime.format(currentDate));
				values.put("owngoals2", homeGoals);
				values.put("ownpoints2", homePoints);
				values.put("oppgoals2", oppGoals);
				values.put("opppoints2", oppPoints);
				Uri uri = Uri.parse(MatchContentProvider.CONTENT_URI + "/"
						+ matchID);
				getActivity().getContentResolver().update(uri, values, null,
						null);
			}

			starttime = 0;
			str[0] = "start";
			str[1] = "START FIRST HALF";
			return str;
		} else if (bStr.equals("stop") && bHalf.equals("IN FIRST HALF")) {
			// 2. first half running
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			h.removeCallbacks(run);
			// when first half stops write mins per half, start time and score
			// to
			// database
			if ((matchID >= 0) && (currentDate != null)) {
				ContentValues values = new ContentValues();
				values.put("minshalf", minsPerHalf);
				values.put("time1", sdftime.format(currentDate));
				values.put("owngoals1", homeGoals);
				values.put("ownpoints1", homePoints);
				values.put("oppgoals1", oppGoals);
				values.put("opppoints1", oppPoints);
				Uri uri = Uri.parse(MatchContentProvider.CONTENT_URI + "/"
						+ matchID);
				getActivity().getContentResolver().update(uri, values, null,
						null);
			}

			starttime = 0;
			str[0] = "start";
			str[1] = "START SECOND HALF";

			return str;
		} else {
			// 1. ready to start first half
			// save player line up if not saved already
			Uri allTitles = PositionContentProvider.CONTENT_URI;
			CursorLoader cL = new CursorLoader(getActivity(), allTitles, null,
					null, null, null);
			Cursor c1 = cL.loadInBackground();
			Log.e("posiiotn", " " + c1.getCount());
			if (c1.getCount() <= 0) {
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
					if (playerIDLookUp.get(teamLineUp[i]) != null) {
						players.put("playerID",
								playerIDLookUp.get(teamLineUp[i]));
					} else {
						players.put("playerID", -1);
					}
					// these players are in the starting lineup so save
					// using code "start"
					players.put("code", "start");
					// insert into database
					getActivity().getContentResolver().insert(
							PositionContentProvider.CONTENT_URI, players);
				}
			}
			if (starttime == 0)
				starttime = System.currentTimeMillis();
			currentDate = new Date(starttime);
			sdf = new SimpleDateFormat("HH:mm:ss   dd-MM-yyyy");
			tStartTime.setText("First Half Start Time: "
					+ sdf.format(currentDate));
			timer = new Timer();
			h.postDelayed(run, 0);
			str[0] = "stop";
			str[1] = "IN FIRST HALF";
			return str;
		}
	}

	// *****************************************************************//
	// *****************************************************************//
	// *****************************************************************//
	// clickListener to Deal With SHOTS input when user touches commit button
	// OnClickListener shotCommitClickListener = new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// int button = ((Button) v).getId();
	// // ca.. method to update shots and scores

	private void commitShots(int button) {
		updateShots(button, 1);
		// write shots stats data to database
		// validation - must have saved starting setup first to
		// get matchID foreign key
		if (matchID < 0) {
			Toast.makeText(
					getActivity(),
					"Shot Not Saved to Database\n"
							+ "Save Starting Team on Setup Page First",
					Toast.LENGTH_LONG).show();
		} else {
			ContentValues values = new ContentValues();
			values.put("matchID", matchID);
			Date currentDate = new Date(System.currentTimeMillis());
			sdf = new SimpleDateFormat("HH:mm:ss");
			values.put("time", sdf.format(currentDate));
			// slight difference in handling home team and opposition team
			// if (button == R.id.buttonShotHome)
			// values.put("team", tOurTeam.getText().toString());
			// else
			// values.put("team", tOppTeam.getText().toString());

			if (button == R.id.buttonShotHome) {
				values.put("team", tOurTeam.getText().toString());
				// for home team look up hashMap to get PlayerID as foreign
				// key
				if (playerIDLookUp.get(shotPlayer) != null) {
					values.put("playerID", playerIDLookUp.get(shotPlayer));
				} else {
					// enter blank if name/id not found
					int playerNo = 0;
					try {
						playerNo = Integer.parseInt(shotPlayer);
					} catch (NumberFormatException nfe) {
						Log.v("matchrecord", "player input error #01");
					}

					if (playerNo > 0)
						// write it as negative number to distinguish it from
						// home team playerIDs which are always positive
						values.put("playerID", -Integer.parseInt(shotPlayer));
					else
						values.put("playerID", "");
				}
			} else {
				values.put("team", tOppTeam.getText().toString());
				// save player position number for opposition team, no name
				int playerNo = 0;
				try {
					playerNo = Integer.parseInt(shotPlayer);
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "player input error #01");
				}

				if (playerNo > 0)
					// write it as negative number to distinguish it from
					// home team playerIDs which are always positive
					values.put("playerID", -Integer.parseInt(shotPlayer));
				else
					values.put("playerID", "");
			}
			values.put("outcome", shotResult);
			values.put("type", shotType);
			values.put("posn", shotPosn);
			// use ShotContentProvider to ass record to database
			Uri uri = getActivity().getContentResolver().insert(
					ShotContentProvider.CONTENT_URI, values);
			long id = Long.parseLong(uri.getLastPathSegment());
			if (id > 0) {
				rowId = id;
			}
			Toast.makeText(getActivity(), "Shot Saved to database",
					Toast.LENGTH_SHORT).show();
		}
		// store details in undoList
		String[] undoString = { String.valueOf(rowId), String.valueOf(button),
				"shot", shotResult, shotType, shotPosn, shotPlayer };
		// push onto undoList Stack
		undoList.add(0, undoString);
		// keep stack size at 5 maximum
		if (undoList.size() > 5)
			undoList.remove(5);
		updateStatsList();
		// light up undo button
		bUndo.setTextColor(Color.parseColor("#000000"));
		bUndo.setOnClickListener(undoOnClickListener);

		// reset text and buttons to null and normal colour
		shotResult = "";
		shotType = "";
		shotPlayer = "";
		shotPosn = "";
		((MatchApplication) getActivity()).getFragmentReview().fillData();
	};

	private void storeUndoList() {
		// store undo list in shared preferences
		SharedPreferences.Editor editor = sharedPref.edit();
		StringBuilder sb = new StringBuilder();
		String str;
		for (int i = 0; i < undoList.size(); i++) {
			sb.setLength(0);
			for (int j = 0; j < undoList.get(i).length; j++) {
				str = undoList.get(i)[j];
				if (str == null || str.equals("")) {
					sb.append(" ");
				} else {
					sb.append(undoList.get(i)[j]);
				}
				sb.append(",Z,"); // seperator
			}
			str = "UNDOLIST" + i;
			editor.putString(str, sb.toString());
		}
		editor.putInt("UNDOLISTSIZE", undoList.size());
		editor.commit();
	};

	private void retrieveUndoList() {
		int num = sharedPref.getInt("UNDOLISTSIZE", 0);
		undoList.clear();
		String str;
		for (int i = 0; i < num; i++) {
			str = "UNDOLIST" + i;
			String[] undoString = (sharedPref.getString(str, "")).split(",Z,");
			undoList.add(0, undoString);
		}
	};

	public void updateStatsList() {
		StringBuffer sb = new StringBuffer("");
		String str, str2, str1;
		int num;
		String[] undoString;
		for (int i = 0; i < undoList.size(); i++) {
			undoString = undoList.get(i);
			num = Integer.parseInt(undoString[1]);
			str1 = (undoString[4] == null || undoString[4].equals("")) ? ""
					: "-" + undoString[4];
			str2 = ((num == R.id.buttonShotHome)
					|| (num == R.id.buttonCustomHome)
					|| (num == R.id.buttonFreeHome) || (num == R.id.buttonPuckHome)) ? tOurTeam
					.getText().toString() + "-"
					: tOppTeam.getText().toString() + "-";
			str = !undoString[5].equals("") ? (str2 + " " + undoString[2] + "-"
					+ undoString[3] + str1 + "-" + undoString[6] + "-"
					+ "pitch posn: " + undoString[5] + "\n") : (str2 + "-"
					+ undoString[2] + "-" + undoString[3] + str1 + "-"
					+ undoString[6] + "\n");
			sb.append(str);
		}
		if (sb.length() >= 1) {
			tStats.setText(sb.toString());
		} else {
			tStats.setText("");
		}
	}

	// //////////////////////////////////////////////////////////////////////
	// method to update score and update shots data in review scrreen
	public void updateShots(int button, int count) {
		switch (button) {
		case R.id.buttonShotHome:
			// for home team commit
			// WRITE TO REVIEW PAGE///////////////////////////////////
			if (shotResult.equals("goal")) {
				// increment goal counter
				if (homeGoals + count >= 0) {
					homeGoals = homeGoals + count;
					bHomeGoals.setText(String.valueOf(homeGoals));
					// update totals
					setTotals();
					// increment score in REVIEW fragment
					((MatchApplication) getActivity()).getFragmentReview()
							.settHomeGoals(homeGoals);
					// change display from + to 0 if first score
					if (bHomePoints.getText().equals("+"))
						bHomePoints.setText("0");
					// remind user score is updated in case they try and do it
					// manually
					Toast.makeText(getActivity(), "Score Updated",
							Toast.LENGTH_SHORT).show();
				}
			} else if (shotResult.equals("point")) {
				// increment points counter
				if (homePoints + count >= 0) {
					homePoints = homePoints + count;
					bHomePoints.setText(String.valueOf(homePoints));
					// update totals
					setTotals();
					// increment score in REVIEW fragment
					((MatchApplication) getActivity()).getFragmentReview()
							.settHomePoints(homePoints);
					// change display from + to 0 if first score
					if (bHomeGoals.getText().equals("+"))
						bHomeGoals.setText("0");
					// remind user score is updated in case they try and do it
					// manually
					Toast.makeText(getActivity(), "Score Updated",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.buttonShotOpp:
			// for opposition team
			// WRITE TO REVIEW PAGE///////////////////////////////////
			if (shotResult.equals("goal")) {
				// increment goal counter
				if (oppGoals + count >= 0) {
					oppGoals = oppGoals + count;
					bOppGoals.setText(String.valueOf(oppGoals));
					// update totals
					setTotals();
					// increment score in REVIEW fragment
					((MatchApplication) getActivity()).getFragmentReview()
							.settOppGoals(oppGoals);
					if (bOppPoints.getText().equals("+"))
						bOppPoints.setText("0");
					Toast.makeText(getActivity(), "Score Updated",
							Toast.LENGTH_SHORT).show();
				}
			} else if (shotResult.equals("point")) {
				// increment points counter
				if (oppPoints + count >= 0) {
					oppPoints = oppPoints + count;
					bOppPoints.setText(String.valueOf(oppPoints));
					// update totals
					setTotals();
					// increment score in REVIEW fragment
					((MatchApplication) getActivity()).getFragmentReview()
							.settOppPoints(oppPoints);
					if (bOppGoals.getText().equals("+"))
						bOppGoals.setText("0");
					Toast.makeText(getActivity(), "Score Updated",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}

	// kk
	private void commitCustom(int button) {
		// write shots stats data to database
		// validation - must have saved starting setup first to
		// get matchID foreign key

		if (shotResult.equals("") && shotType.equals("")
				&& shotPlayer.equals("")) {
			return;
		}

		if (matchID < 0) {
			Toast.makeText(
					getActivity(),
					"Event Not Saved to Database\n"
							+ "Save Starting Team on Setup Page First",
					Toast.LENGTH_LONG).show();
		} else {
			ContentValues values = new ContentValues();
			values.put("matchID", matchID);
			Date currentDate = new Date(System.currentTimeMillis());
			sdf = new SimpleDateFormat("HH:mm:ss");
			values.put("time", sdf.format(currentDate));
			if (button == R.id.buttonCustomHome) {
				values.put("team", tOurTeam.getText().toString());
				// for home team look up hashMap to get PlayerID as foreign
				// key
				if (playerIDLookUp.get(shotPlayer) != null) {
					values.put("playerID", playerIDLookUp.get(shotPlayer));
				} else {
					// enter blank if name/id not found
					int playerNo = 0;
					try {
						playerNo = Integer.parseInt(shotPlayer);
					} catch (NumberFormatException nfe) {
						Log.v("matchrecord", "player input error #01");
					}

					if (playerNo > 0)
						// write it as negative number to distinguish it from
						// home team playerIDs which are always positive
						values.put("playerID", -Integer.parseInt(shotPlayer));
					else
						values.put("playerID", "");
				}
			} else {
				values.put("team", tOppTeam.getText().toString());
				// save player position number for opposition team, no name
				int playerNo = 0;
				try {
					playerNo = Integer.parseInt(shotPlayer);
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "player input error #01");
				}

				if (playerNo > 0)
					// write it as negative number to distinguish it from
					// home team playerIDs which are always positive
					values.put("playerID", -Integer.parseInt(shotPlayer));
				else
					values.put("playerID", "");
			}
			values.put("outcome", shotResult);
			values.put("type", shotType);
			values.put("posn", shotPosn);
			// use ShotContentProvider to ass record to database
			Uri uri = getActivity().getContentResolver().insert(
					CustomContentProvider.CONTENT_URI, values);
			long id = Long.parseLong(uri.getLastPathSegment());
			if (id > 0) {
				rowId = id;
			}
			Toast.makeText(getActivity(), "Shot Saved to database",
					Toast.LENGTH_SHORT).show();
		}

		// store details in undoList
		String[] undoString = { String.valueOf(rowId), String.valueOf(button),
				"custom", shotResult, shotType, shotPosn, shotPlayer };
		// push onto undoList Stack
		undoList.add(0, undoString);
		// keep stack size at 5 maximum
		if (undoList.size() > 5)
			undoList.remove(5);
		updateStatsList();
		// light up undo button
		bUndo.setTextColor(Color.parseColor("#000000"));
		bUndo.setOnClickListener(undoOnClickListener);

		// reset text and buttons to null and normal colour
		shotResult = "";
		shotType = "";
		shotPlayer = "";
		shotPosn = "";
		((MatchApplication) getActivity()).getFragmentReview().fillData();
	};

	// *****************************************************************//
	// *****************************************************************//
	// *****************************************************************//
	// clickListener to Deal With FREES input when user touches commit button
	// OnClickListener freeCommitClickListener = new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// int button = ((Button) v).getId();
	// // call method to update frees display on review page

	private void commitFrees(int button) {
		// updateFrees(button, 1);
		// write to database
		// validation - must have saved starting setup in setup screen first
		// to
		// get matchID foreign key
		if (matchID < 0) {
			Toast.makeText(
					getActivity(),
					"Free Not Saved to Database\n"
							+ "Save Starting Team on Setup Page First",
					Toast.LENGTH_LONG).show();
		} else {
			ContentValues values = new ContentValues();
			values.put("matchID", matchID);
			Date currentDate = new Date(System.currentTimeMillis());
			sdf = new SimpleDateFormat("HH:mm:ss");
			values.put("time", sdf.format(currentDate));
			if (button == R.id.buttonFreeHome)
				values.put("team", tOurTeam.getText().toString());
			else
				values.put("team", tOppTeam.getText().toString());
			getReason(button);
			values.put("reason", freeReason);
			// look up hash Map to get PlayerID as foreign key
			if (playerIDLookUp.get(freePlayer) != null) {
				values.put("playerID", playerIDLookUp.get(freePlayer));
			} else {
				int playerNo = 0;
				try {
					playerNo = Integer.parseInt(freePlayer);
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "player input error #01");
				}

				if (playerNo > 0)
					// write it as negative number to distinguish it from
					// home team playerIDs which are always positive
					values.put("playerID", -Integer.parseInt(freePlayer));
				else
					values.put("playerID", "");
			}
			values.put("posn", freePosn);
			// use free content provider to insert into database table
			Uri uri = getActivity().getContentResolver().insert(
					FreeContentProvider.CONTENT_URI, values);
			long id = Long.parseLong(uri.getLastPathSegment());
			if (id > 0) {
				rowId = id;
			}
			Toast.makeText(getActivity(), "Free Saved to database",
					Toast.LENGTH_SHORT).show();
		}

		// store details in undoList
		String[] undoString = { String.valueOf(rowId), String.valueOf(button),
				"frees", freeReason, null, freePosn, freePlayer };
		// push onto undoList Stack
		undoList.add(0, undoString);
		// keep stack size at 5 maximum
		if (undoList.size() > 5) {
			undoList.remove(5);
		}
		updateStatsList();
		bUndo.setTextColor(Color.parseColor("#000000"));
		bUndo.setOnClickListener(undoOnClickListener);

		// reset text and buttons to null and normal colour
		freeReason = "";
		freePlayer = "";
		freePosn = "";

		// update review page
		((MatchApplication) getActivity()).getFragmentReview().fillData();
	};

	private void commitPuckOuts(int button) {

		// write to database
		// validation - must have saved starting setup first to
		// get matchID foreign key
		if (matchID < 0) {
			Toast.makeText(
					getActivity(),
					"Puckout Not Saved to Database\n"
							+ "Save Starting Team on Setup Page First",
					Toast.LENGTH_LONG).show();
		} else {
			ContentValues values = new ContentValues();
			values.put("matchID", matchID);
			Date currentDate = new Date(System.currentTimeMillis());
			sdf = new SimpleDateFormat("HH:mm:ss");
			values.put("time", sdf.format(currentDate));
			if (button == R.id.buttonPuckHome)
				values.put("team", tOurTeam.getText().toString());
			else
				values.put("team", tOppTeam.getText().toString());
			getReason(button);
			values.put("outcome", puckOutReason);
			// look up hash Map to get PlayerID as foreign key
			if (playerIDLookUp.get(puckOutPlayer) != null) {
				values.put("playerID", playerIDLookUp.get(puckOutPlayer));
			} else {
				int playerNo = 0;
				try {
					playerNo = Integer.parseInt(puckOutPlayer);
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "player input error #01");
				}

				if (playerNo > 0)
					// write it as negative number to distinguish it from
					// home team playerIDs which are always positive
					values.put("playerID", -Integer.parseInt(puckOutPlayer));
				else
					values.put("playerID", "");
			}
			values.put("posn", puckOutPosn);
			// use puckout content provider to insert into database table
			Uri uri = getActivity().getContentResolver().insert(
					PuckOutContentProvider.CONTENT_URI, values);
			long id = Long.parseLong(uri.getLastPathSegment());
			if (id > 0) {
				rowId = id;
			}

			Toast.makeText(getActivity(), "Puckout Saved to database",
					Toast.LENGTH_SHORT).show();
		}

		// store details in undoList
		String[] undoString = { String.valueOf(rowId), String.valueOf(button),
				"puckouts", puckOutReason, null, puckOutPosn, puckOutPlayer };
		// push onto undoList Stack
		undoList.add(0, undoString);
		// keep stack size at 5 maximum
		if (undoList.size() > 5) {
			undoList.remove(5);
		}
		updateStatsList();
		bUndo.setTextColor(Color.parseColor("#000000"));
		bUndo.setOnClickListener(undoOnClickListener);

		// reset text and buttons to null and normal colour
		// reset clickListener to null
		puckOutReason = "";
		puckOutPlayer = "";
		puckOutPosn = "";
		// update review page
		((MatchApplication) getActivity()).getFragmentReview().fillData();
	};

	public void updateStats(int button) {
		switch (button) {
		case R.id.buttonShotHome:
		case R.id.buttonShotOpp:
			commitShots(button);
			break;
		case R.id.buttonCustomHome:
		case R.id.buttonCustomOpp:
			commitCustom(button);
			break;
		case R.id.buttonFreeHome:
		case R.id.buttonFreeOpp:
			commitFrees(button);
			break;
		case R.id.buttonPuckHome:
		case R.id.buttonPuckOpp:
			commitPuckOuts(button);
			break;
		}

	}

	DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			updateStats(SHOT_FREE_PUCK_OUT);
		}

	};

	// **********************************************************************//
	/*-------------------DIALOG FOR STATS INPUT*---------------------------*///
	// handles button clicks for shot / free / puckout ///
	// **********************************************************************//
	OnClickListener statsClickListener = new OnClickListener() {
		String dialogNeutral; // For Shots input
		String dialogTitle; // For Shots input

		@Override
		public void onClick(View w) {
			// clear default values from input textfields
			// use SHOT_FREE_PUCK_OUT to store which button was pressed
			SHOT_FREE_PUCK_OUT = ((Button) w).getId();

			// throw up stats input screen layout
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View vv = inflater.inflate(R.layout.shot_layout, null);

			// light up the relative COMMIT button and set clickListener ready
			// for when input is finished
			switch (SHOT_FREE_PUCK_OUT) {
			case R.id.buttonShotHome:
				shotResult = "";
				shotType = "";
				shotPlayer = "";
				shotPosn = "";
				dialogNeutral = "Enter Location";
				dialogTitle = "select pitch position";
				break;
			case R.id.buttonFreeHome:
				freeReason = "";
				freePlayer = "";
				freePosn = "";
				dialogNeutral = "Select Player";
				dialogTitle = "if applicable, select own player who gave away free";
				vv = inflater.inflate(R.layout.free_layout, null);
				break;
			case R.id.buttonPuckHome:
				puckOutReason = "";
				puckOutPlayer = "";
				puckOutPosn = "";
				dialogNeutral = "Select Player";
				dialogTitle = "select own player who won/lost your own puckout";
				vv = inflater.inflate(R.layout.puckout_layout, null);
				break;
			case R.id.buttonCustomHome:
				customResult = "";
				customType = "";
				shotPlayer = "";
				shotPosn = "";
				dialogNeutral = "Enter Location";
				dialogTitle = "select pitch position";
				vv = inflater.inflate(R.layout.customstats_layout, null);
				break;
			case R.id.buttonShotOpp:
				shotResult = "";
				shotType = "";
				shotPlayer = "";
				shotPosn = "";
				dialogNeutral = "Enter Location";
				dialogTitle = "select pitch position";
				break;
			case R.id.buttonFreeOpp:
				freeReason = "";
				freePlayer = "";
				freePosn = "";
				dialogNeutral = "Select Player";
				dialogTitle = "if applicable, select own player who was fouled";
				vv = inflater.inflate(R.layout.free_layout, null);
				break;
			case R.id.buttonPuckOpp:
				puckOutReason = "";
				puckOutPlayer = "";
				puckOutPosn = "";
				dialogNeutral = "Select Player";
				dialogTitle = "select own player who won/lost opposition puckout";
				vv = inflater.inflate(R.layout.puckout_layout, null);
				break;
			case R.id.buttonCustomOpp:
				customResult = "";
				customType = "";
				shotPlayer = "";
				shotPosn = "";
				dialogNeutral = "Enter Location";
				dialogTitle = "select pitch position";
				vv = inflater.inflate(R.layout.customstats_layout, null);
				break;
			}

			// set up entry dialog for selecting ptich position
			AlertDialog.Builder builder;
			switch (SHOT_FREE_PUCK_OUT) {
			case R.id.buttonShotHome:
			case R.id.buttonShotOpp:
			case R.id.buttonCustomHome:
			case R.id.buttonCustomOpp:

				builder = new AlertDialog.Builder(getActivity())
						.setView(vv)
						// ok button just closes the dialog
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// light up the save button
										updateStats(SHOT_FREE_PUCK_OUT);
										dialog.dismiss();
									}
								})

						// set up pitch position sub dialog/layout on the
						// neutral button
						.setNeutralButton(dialogNeutral,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// show Pitch Dialog
										LayoutInflater inflater = getActivity()
												.getLayoutInflater();
										// throw up pitch layout screenun
										View vv = inflater.inflate(
												R.layout.pitch_layout, null);
										AlertDialog.Builder builder = new AlertDialog.Builder(
												getActivity()).setTitle(
												dialogTitle).setView(vv);
										// Set up button click listener for each
										// pitch position
										Button[] bb = new Button[21];
										for (int i = 1; i <= 20; i++) {
											bb[i] = (Button) vv
													.findViewById(getResources()
															.getIdentifier(
																	"ButtonPitch"
																			+ String.format(
																					"%02d",
																					i),
																	"id",
																	"fm.footballstats"));
											bb[i].setOnClickListener(getPitchClickListener);
											bb[i].setText(String.valueOf(i));
										}
										MatchRecordFragment.this.alertpitch = builder
												.create();
										MatchRecordFragment.this.alertpitch
												.show();
										MatchRecordFragment.this.alertpitch
												.setOnDismissListener(onDismissListener);
										// dialog.dismiss();
									}
								});
				break;

			default:
				// for frees and puckouts
				builder = new AlertDialog.Builder(getActivity())
						.setView(vv)
						// ok button just closes the dialog
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// light up the save button
										updateStats(SHOT_FREE_PUCK_OUT);
										dialog.dismiss();
									}
								})

						// set up pitch position sub dialog/layout on the
						// neutral
						// button
						.setNeutralButton(dialogNeutral,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// show Pitch Dialog
										LayoutInflater inflater = getActivity()
												.getLayoutInflater();
										// throw up pitch layout screen
										View vv = inflater.inflate(
												R.layout.team_layout, null);
										AlertDialog.Builder builder = new AlertDialog.Builder(
												getActivity()).setTitle(
												dialogTitle).setView(vv);
										// Set up button click listener for each
										// pitch position

										Button[] bb = new Button[16];
										for (int i = 1; i <= 15; i++) {
											bb[i] = (Button) vv
													.findViewById(getResources()
															.getIdentifier(
																	"ButtonP"
																			+ String.format(
																					"%02d",
																					i),
																	"id",
																	"fm.footballstats"));
											// For Home team assign player
											// name to team lineup
											// For Opposition just use
											// position numbers

											bb[i].setText(teamLineUp[i]);
											bb[i].setOnClickListener(getPlayerClickListener);
										}

										MatchRecordFragment.this.alertpitch = builder
												.create();
										MatchRecordFragment.this.alertpitch
												.show();
										MatchRecordFragment.this.alertpitch
												.setOnDismissListener(onDismissListener);
										// dialog.dismiss();
									}
								});
				break;
			}

			// Set up the Radio button clickListeners for Shot Outcome / Free
			// Type / Puckout result input
			ScrollView sV = (ScrollView) vv.findViewById(R.id.scrollView22);
			sV.fullScroll(View.FOCUS_UP);
			sV.smoothScrollTo(0, 0);
			int k = 9;
			switch (SHOT_FREE_PUCK_OUT) {
			case R.id.buttonPuckHome:
			case R.id.buttonPuckOpp:
				// just 3 choices for puck outs
				RadioButton[] rbrpo = new RadioButton[8];
				for (int i = 0; i < 8; i++) {
					rbrpo[i] = (RadioButton) vv.findViewById(getResources()
							.getIdentifier(
									"radio_shot_r" + String.format("%02d", i),
									"id", "fm.footballstats"));
					rbrpo[i].setOnClickListener(getOutcomeClickListener);
				}
				grPuckouts = new GRadioGroup(rbrpo[0], rbrpo[1], rbrpo[2],
						rbrpo[3], rbrpo[4], rbrpo[5], rbrpo[6], rbrpo[7]);
				break;
			case R.id.buttonShotHome:
			case R.id.buttonShotOpp:
				k = 7;
			case R.id.buttonCustomHome:
			case R.id.buttonCustomOpp:
				// 7 choices for shots
				RadioButton[] rbrshot = new RadioButton[k];
				for (int i = 0; i < k; i++) {
					rbrshot[i] = (RadioButton) vv.findViewById(getResources()
							.getIdentifier(
									"radio_shot_r" + String.format("%02d", i),
									"id", "fm.footballstats"));
					rbrshot[i].setOnClickListener(getOutcomeClickListener);
				}
				if ((SHOT_FREE_PUCK_OUT == R.id.buttonCustomHome)
						|| (SHOT_FREE_PUCK_OUT == R.id.buttonCustomOpp)) {
					cstat1 = (TextView) vv.findViewById(R.id.TextStats1);
					cstat1.setText(cTitle1);
					cstat2 = (TextView) vv.findViewById(R.id.TextStats2);
					cstat2.setText(cTitle2);
					for (int i = 0; i < 9; i++) {
						rbrshot[i].setText(customResultStr[i]);
					}
				}
				break;

			default:
				// 6 choices frees
				RadioButton[] rbr = new RadioButton[10];
				for (int i = 0; i < 10; i++) {
					rbr[i] = (RadioButton) vv.findViewById(getResources()
							.getIdentifier(
									"radio_shot_r" + String.format("%02d", i),
									"id", "fm.footballstats"));
					rbr[i].setOnClickListener(getOutcomeClickListener);
					rbr[i].setText(free[i]);
				}
				grFrees = new GRadioGroup(rbr[0], rbr[1], rbr[2], rbr[3],
						rbr[4], rbr[5], rbr[6], rbr[7], rbr[8], rbr[9]);
				break;
			}

			// Set up the Radio button clickListeners for Shot Type / Free
			// Type / Puckout result input
			k = 9;
			switch (SHOT_FREE_PUCK_OUT) {
			case R.id.buttonShotHome:
			case R.id.buttonShotOpp:
				k = 6;
			case R.id.buttonCustomHome:
			case R.id.buttonCustomOpp:
				// 4 options for shot type
				RadioButton[] rbtShot = new RadioButton[k];
				for (int i = 0; i < k; i++) {
					rbtShot[i] = (RadioButton) vv.findViewById(getResources()
							.getIdentifier(
									"radio_shot_t" + String.format("%02d", i),
									"id", "fm.footballstats"));
					rbtShot[i].setOnClickListener(getTypeClickListener);
				}
				if ((SHOT_FREE_PUCK_OUT == R.id.buttonCustomHome)
						|| (SHOT_FREE_PUCK_OUT == R.id.buttonCustomOpp)) {
					for (int i = 0; i < 9; i++) {
						rbtShot[i].setText(customTypeStr[i]);
					}
				}
				break;
			default:
				break;
			}

			// for shots assign clickListener and names to team layout from
			// teamLineUp
			Button[] bb;
			switch (SHOT_FREE_PUCK_OUT) {
			case R.id.buttonShotHome:
			case R.id.buttonShotOpp:
			case R.id.buttonCustomHome:
			case R.id.buttonCustomOpp:
				bb = new Button[16];
				for (int i = 1; i <= 15; i++) {
					bb[i] = (Button) vv.findViewById(getResources()
							.getIdentifier(
									"ButtonP" + String.format("%02d", i), "id",
									"fm.footballstats"));
					// For Home team assign player name to team lineup
					// For Opposition just use position numbers
					if (SHOT_FREE_PUCK_OUT == bShotHome.getId()
							|| SHOT_FREE_PUCK_OUT == bCustomHome.getId()
							|| SHOT_FREE_PUCK_OUT == bFreeHome.getId()
							|| SHOT_FREE_PUCK_OUT == bFreeOpp.getId()
							|| SHOT_FREE_PUCK_OUT == bPuckOutOpp.getId()
							|| SHOT_FREE_PUCK_OUT == bPuckOutHome.getId()) {
						bb[i].setText(teamLineUp[i]);
					} else {
						bb[i].setText(teamLineUpOpp[i]);
					}
					bb[i].setOnClickListener(getPlayerClickListener);
				}
				break;
			// for frees and puckouts assign clicklistener to pitch positions
			default:
				bb = new Button[21];
				for (int i = 1; i <= 20; i++) {
					bb[i] = (Button) vv.findViewById(getResources()
							.getIdentifier(
									"ButtonPitch" + String.format("%02d", i),
									"id", "fm.footballstats"));
					bb[i].setOnClickListener(getPitchClickListener);
					bb[i].setText(String.valueOf(i));
				}
				break;
			}

			MatchRecordFragment.this.alertshot = builder.create();
			MatchRecordFragment.this.alertshot.show();
		}

	};

	// Listener to get player name
	OnClickListener getPlayerClickListener = new OnClickListener() {
		@Override
		public void onClick(View vvv) {
			Button b = (Button) vvv;
			switch (SHOT_FREE_PUCK_OUT) {
			case R.id.buttonShotHome:
			case R.id.buttonShotOpp:
			case R.id.buttonCustomHome:
			case R.id.buttonCustomOpp:
				shotPlayer = b.getText().toString();
				break;
			case R.id.buttonFreeHome:
			case R.id.buttonFreeOpp:
				freePlayer = b.getText().toString();
				break;
			case R.id.buttonPuckHome:
			case R.id.buttonPuckOpp:
				puckOutPlayer = b.getText().toString();
				break;
			}
			// close off dialog if necessary
			if (MatchRecordFragment.this.alertpitch != null)
				MatchRecordFragment.this.alertpitch.dismiss();
		}
	};

	// Listener to get shot outcome
	OnClickListener getOutcomeClickListener = new OnClickListener() {
		@Override
		public void onClick(View vvv) {
			if ((SHOT_FREE_PUCK_OUT == R.id.buttonShotHome)
					|| (SHOT_FREE_PUCK_OUT == R.id.buttonShotOpp)) {
				switch (vvv.getId()) {
				case R.id.radio_shot_r00:
					shotResult = "goal";
					break;
				case R.id.radio_shot_r01:
					shotResult = "point";
					break;
				case R.id.radio_shot_r02:
					shotResult = "wide";
					break;
				case R.id.radio_shot_r03:
					shotResult = "45/65";
					break;
				case R.id.radio_shot_r04:
					shotResult = "saved";
					break;
				case R.id.radio_shot_r05:
					shotResult = "short";
					break;
				case R.id.radio_shot_r06:
					shotResult = "off posts";
					break;
				}
			} else if ((SHOT_FREE_PUCK_OUT == R.id.buttonCustomHome)
					|| (SHOT_FREE_PUCK_OUT == R.id.buttonCustomOpp)) {
				switch (vvv.getId()) {
				case R.id.radio_shot_r00:
					shotResult = customResultStr[0];
					break;
				case R.id.radio_shot_r01:
					shotResult = customResultStr[1];
					break;
				case R.id.radio_shot_r02:
					shotResult = customResultStr[2];
					break;
				case R.id.radio_shot_r03:
					shotResult = customResultStr[3];
					break;
				case R.id.radio_shot_r04:
					shotResult = customResultStr[4];
					break;
				case R.id.radio_shot_r05:
					shotResult = customResultStr[5];
					break;
				case R.id.radio_shot_r06:
					shotResult = customResultStr[6];
					break;
				case R.id.radio_shot_r07:
					shotResult = customResultStr[7];
					break;
				case R.id.radio_shot_r08:
					shotResult = customResultStr[8];
					break;
				}
			}
		}
	};

	private void getReason(int SHOT_FREE_PUCK_OUT) {
		if ((SHOT_FREE_PUCK_OUT == R.id.buttonFreeHome)
				|| (SHOT_FREE_PUCK_OUT == R.id.buttonFreeOpp)) {
			// Log.e("go frees"," "+grFrees.getID());
			switch (grFrees.getID()) {
			case R.id.radio_shot_r00:
				freeReason = free[0];
				break;
			case R.id.radio_shot_r01:
				freeReason = free[1];
				break;
			case R.id.radio_shot_r02:
				freeReason = free[2];
				break;
			case R.id.radio_shot_r03:
				freeReason = free[3];
				break;
			case R.id.radio_shot_r04:
				freeReason = free[4];
				break;
			case R.id.radio_shot_r05:
				freeReason = free[5];
				break;
			case R.id.radio_shot_r06:
				freeReason = free[6];
				break;
			case R.id.radio_shot_r07:
				freeReason = free[7];
				break;
			case R.id.radio_shot_r08:
				freeReason = free[8];
				break;
			case R.id.radio_shot_r09:
				freeReason = free[9];
				break;
			}
		} else if ((SHOT_FREE_PUCK_OUT == R.id.buttonPuckHome)
				|| (SHOT_FREE_PUCK_OUT == R.id.buttonPuckOpp)) {
			switch (grPuckouts.getID()) {
			case R.id.radio_shot_r00:
				puckOutReason = "won high catch";
				break;
			case R.id.radio_shot_r01:
				puckOutReason = "won clean";
				break;
			case R.id.radio_shot_r02:
				puckOutReason = "won on break";
				break;
			case R.id.radio_shot_r03:
				puckOutReason = "over sideline";
				break;
			case R.id.radio_shot_r04:
				puckOutReason = "lost high catch";
				break;
			case R.id.radio_shot_r05:
				puckOutReason = "lost clean";
				break;
			case R.id.radio_shot_r06:
				puckOutReason = "lost on break";
				break;
			case R.id.radio_shot_r07:
				puckOutReason = "other";
			}
		}
	}

	// listener to get shot type
	OnClickListener getTypeClickListener = new OnClickListener() {
		@Override
		public void onClick(View vvv) {

			if ((SHOT_FREE_PUCK_OUT == R.id.buttonShotHome)
					|| (SHOT_FREE_PUCK_OUT == R.id.buttonShotOpp)) {
				switch (vvv.getId()) {
				case R.id.radio_shot_t00:
					shotType = "kick";
					break;
				case R.id.radio_shot_t01:
					shotType = "by hand";
					break;
				case R.id.radio_shot_t02:
					shotType = "free";
					break;
				case R.id.radio_shot_t03:
					shotType = ("45/65");
					break;
				case R.id.radio_shot_t04:
					shotType = "sideline";
					break;
				case R.id.radio_shot_t05:
					shotType = "penalty";
					break;
				}
			} else if ((SHOT_FREE_PUCK_OUT == R.id.buttonCustomHome)
					|| (SHOT_FREE_PUCK_OUT == R.id.buttonCustomOpp)) {
				switch (vvv.getId()) {
				case R.id.radio_shot_t00:
					shotType = customTypeStr[0];
					break;
				case R.id.radio_shot_t01:
					shotType = customTypeStr[1];
					break;
				case R.id.radio_shot_t02:
					shotType = customTypeStr[2];
					break;
				case R.id.radio_shot_t03:
					shotType = customTypeStr[3];
					break;
				case R.id.radio_shot_t04:
					shotType = customTypeStr[4];
					break;
				case R.id.radio_shot_t05:
					shotType = customTypeStr[5];
					break;
				case R.id.radio_shot_t06:
					shotType = customTypeStr[6];
					break;
				case R.id.radio_shot_t07:
					shotType = customTypeStr[7];
					break;
				case R.id.radio_shot_t08:
					shotType = customTypeStr[8];
					break;
				}
			}
		}
	};

	// listener to get pitch position
	OnClickListener getPitchClickListener = new OnClickListener() {
		@Override
		public void onClick(View vvv) {
			Button b = (Button) vvv;
			// determine if we're dealing with shot or free or puckout
			switch (SHOT_FREE_PUCK_OUT) {
			case R.id.buttonShotHome:
			case R.id.buttonShotOpp:
			case R.id.buttonCustomHome:
			case R.id.buttonCustomOpp:
				shotPosn = b.getText().toString();
				break;
			case R.id.buttonFreeHome:
			case R.id.buttonFreeOpp:
				freePosn = b.getText().toString();
				break;
			case R.id.buttonPuckHome:
			case R.id.buttonPuckOpp:
				puckOutPosn = b.getText().toString();
				break;
			}
			if (MatchRecordFragment.this.alertpitch != null)
				MatchRecordFragment.this.alertpitch.dismiss();
		}
	};

	// *******************************************************************//
	// *******************************************************************//
	// *******************************************************************//
	// *******************************************************************//
	// *******************************************************************//

	// ///////////////////SCORE CLICK LISTENER//////////////////////////////////
	OnClickListener scoreAddClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			// for each case update score display in this fragment
			// and update score display in REVIEW fragment
			switch (v.getId()) {
			case R.id.home_goals:
				homeGoals++;
				bHomeGoals.setText(String.valueOf(homeGoals));
				((MatchApplication) getActivity()).getFragmentReview()
						.settHomeGoals(homeGoals);
				// if first score change + on buttons to 0
				if (bHomePoints.getText().equals("+"))
					bHomePoints.setText("0");
				break;
			case R.id.home_points:
				homePoints++;
				bHomePoints.setText(String.valueOf(homePoints));
				((MatchApplication) getActivity()).getFragmentReview()
						.settHomePoints(homePoints);
				// if first score change + on buttons to 0
				if (bHomeGoals.getText().equals("+"))
					bHomeGoals.setText("0");
				break;
			case R.id.opp_goals:
				oppGoals++;
				bOppGoals.setText(String.valueOf(oppGoals));
				((MatchApplication) getActivity()).getFragmentReview()
						.settOppGoals(oppGoals);
				// if first score change + on buttons to 0
				if (bOppPoints.getText().equals("+"))
					bOppPoints.setText("0");
				break;
			case R.id.opp_points:
				oppPoints++;
				bOppPoints.setText(String.valueOf(oppPoints));
				((MatchApplication) getActivity()).getFragmentReview()
						.settOppPoints(oppPoints);
				// if first score change + on buttons to 0
				if (bOppGoals.getText().equals("+"))
					bOppGoals.setText("0");
				break;
			case R.id.dec_home_goals:
				if (homeGoals > 0) {
					homeGoals--;
					bHomeGoals.setText(String.valueOf(homeGoals));
					((MatchApplication) getActivity()).getFragmentReview()
							.settHomeGoals(homeGoals);
					break;
				} else
					return;
			case R.id.dec_home_points:
				if (homePoints > 0) {
					homePoints--;
					bHomePoints.setText(String.valueOf(homePoints));
					((MatchApplication) getActivity()).getFragmentReview()
							.settHomePoints(homePoints);
					break;
				} else
					return;
			case R.id.dec_opp_goals:
				if (oppGoals > 0) {
					oppGoals--;
					bOppGoals.setText(String.valueOf(oppGoals));
					((MatchApplication) getActivity()).getFragmentReview()
							.settOppGoals(oppGoals);
					break;
				} else
					return;
			case R.id.dec_opp_points:
				if (oppPoints > 0) {
					oppPoints--;
					bOppPoints.setText(String.valueOf(oppPoints));
					((MatchApplication) getActivity()).getFragmentReview()
							.settOppPoints(oppPoints);
					break;
				} else
					return;
			}
			// update totals values and text
			setTotals();
		}
	};

	// method to calculate total score from goals and points
	// and update if home team is ahead or behind or if game is a draw
	private void setTotals() {
		int homeTotal = (homeGoals * 3) + homePoints;
		tHomeTotal.setText("(" + String.valueOf(homeTotal) + ")");
		int oppTotal = (oppGoals * 3) + oppPoints;
		tOppTotal.setText("(" + String.valueOf(oppTotal) + ")");

		if (homeTotal > oppTotal) {
			tUpDownDrawText.setText("up by: ");
			tHomeDifference.setText("(" + String.valueOf(homeTotal - oppTotal)
					+ ")");
		} else if (homeTotal < oppTotal) {
			tUpDownDrawText.setText("down by: ");
			tHomeDifference.setText("(" + String.valueOf(-homeTotal + oppTotal)
					+ ")");
		} else {
			tUpDownDrawText.setText("drawn game. ");
			tHomeDifference.setText(" ");
		}
	}

	// //////////////////////////TIMER///////////////////////////////
	// set up thread to run match timer
	Runnable run = new Runnable() {
		@Override
		public void run() {
			long millis = System.currentTimeMillis() - starttime;
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			tTimeGone.setText(String.format("%02d:%02d", minutes, seconds));
			if (minsPerHalf - minutes > 0) {
				tTimeToGo.setText(String.format("%02d:%02d", minsPerHalf - 1
						- minutes, 60 - seconds));
			} else {
				tTimeToGo.setText(String.format("%02d:%02d", minutes
						- minsPerHalf, seconds));
				tTimeLeft.setText("extra time:");
			}
			h.postDelayed(this, 1000);
		}
	};

	// this method is called from the SETUP fragment to update the names of the
	// home and away teams and to receive team line and teams from setup screen
	public void setTeamLineUp(String[] teamLineUp, String homeTeam,
			String oppTeam) {
		this.teamLineUp = teamLineUp;
		if (!homeTeam.equals(""))
			tOurTeam.setText(homeTeam);
		if (!oppTeam.equals(""))
			tOppTeam.setText(oppTeam);
	}

	public void setOppName(String oppTeam) {
		if (!oppTeam.equals(""))
			tOppTeam.setText(oppTeam);
	}

	// this method is called from the SETUP fragment to update provide the
	// matchID
	// value from the database. This is used as a foreign key when saving
	// records
	// to database
	public void setMatchID(long matchID) {
		this.matchID = matchID;
	}

	// Undo last 5 stats entries
	OnClickListener undoOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (undoList.size() > 0) {
				// pop next undo from stack
				String[] undoStr = new String[7];
				undoStr = undoList.remove(0);
				if (undoStr[2].equals("shot")) {
					shotResult = undoStr[3];
					shotType = undoStr[4];
					shotPlayer = undoStr[6];
					shotPosn = undoStr[5];
					updateShots(Integer.valueOf(undoStr[1]), -1);
					try {
						Uri uri = Uri.parse(ShotContentProvider.CONTENT_URI
								+ "/" + Long.valueOf(undoStr[0]));
						getActivity().getContentResolver().delete(uri, null,
								null);
					} catch (IllegalArgumentException e) {
						Log.e("shot undo", "- " + e);
					}
					shotResult = "";
					shotType = "";
					shotPlayer = "";
					shotPosn = "";
				} else if (undoStr[2].equals("custom")) {
					try {
						Uri uri = Uri.parse(CustomContentProvider.CONTENT_URI
								+ "/" + Long.valueOf(undoStr[0]));
						getActivity().getContentResolver().delete(uri, null,
								null);
					} catch (IllegalArgumentException e) {
						Log.e("custom undo", "- " + e);
					}
					shotResult = "";
					shotType = "";
					shotPlayer = "";
					shotPosn = "";
				} else if (undoStr[2].equals("frees")) {
					freeReason = undoStr[3];
					freePlayer = undoStr[6];
					freePosn = undoStr[5];
					// updateFrees(Integer.valueOf(undoStr[1]), -1);
					try {
						Uri uri = Uri.parse(FreeContentProvider.CONTENT_URI
								+ "/" + Long.valueOf(undoStr[0]));
						getActivity().getContentResolver().delete(uri, null,
								null);
					} catch (IllegalArgumentException e) {
						Log.e("free undo", "- " + e);
					}
					freeReason = "";
					freePlayer = "";
					freePosn = "";
				} else if (undoStr[2].equals("puckouts")) {
					puckOutReason = undoStr[3];
					puckOutPlayer = undoStr[6];
					puckOutPosn = undoStr[5];
					// updatePuckOuts(Integer.valueOf(undoStr[1]), -1);
					try {
						Uri uri = Uri.parse(PuckOutContentProvider.CONTENT_URI
								+ "/" + Long.valueOf(undoStr[0]));
						getActivity().getContentResolver().delete(uri, null,
								null);
					} catch (IllegalArgumentException e) {
						Log.e("puckouts undo", "- " + e);
					}
					puckOutReason = "";
					puckOutPlayer = "";
					puckOutPosn = "";
				}
				// if undo stack empty, turn off button
				if (undoList.size() <= 0) {
					bUndo.setTextColor(Color.parseColor("#bbbbbb"));
					bUndo.setOnClickListener(null);
				}
				updateStatsList();
				((MatchApplication) getActivity()).getFragmentReview()
						.fillData();
			} else
				Toast.makeText(getActivity(), "Error, nothing to Undo",
						Toast.LENGTH_SHORT).show();
		}
	};

}
