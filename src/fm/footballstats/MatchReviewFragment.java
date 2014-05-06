/*
 *  MatchReviewFragment.java
 *
 *  Written by: Fintan Mahon 12101524
 *  
 *  Description: GUI to display match score and match statistics data summary. 
 *  Also can start activities to view detailed tables of match statistics
 *  
 * store data to database tables and pass relevant details into MatchRecordReview
 *  
 *  Written on: Jan 2013
 *  
 * 
 */
package fm.footballstats;

import fm.footballstats.R;
import fm.footballstats.db.FreeContentProvider;
import fm.footballstats.db.PuckOutContentProvider;
import fm.footballstats.db.ShotContentProvider;
import fm.footballstats.db.run.CustomListActivity;
import fm.footballstats.db.run.FreesListActivity;
import fm.footballstats.db.run.PuckOutsListActivity;
import fm.footballstats.db.run.ShotsListActivity;
import fm.footballstats.db.run.ShotsPlayerListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MatchReviewFragment extends Fragment {
	private int homeGoals, homePoints, homeTotal, oppGoals, oppPoints,
			oppTotal;
	private TextView tHomeGoals, tHomePoints, tOppGoals, tOppPoints;
	private TextView tShotsTotalHome;
	private TextView tShotGoalsHome, tShotGoalsHomePlay;
	private TextView tShotWidesHome, tShotWidesHomePlay;
	private TextView tShotPointsHome, tShotPointsHomePlay;
	private TextView tShot45sHome, tShot45sHomePlay;
	private TextView tShotSavedHome, tShotSavedHomePlay;
	private TextView tShotShortHome, tShotShortHomePlay;
	private TextView tShotPostsHome, tShotPostsHomePlay;

	private TextView tFreeWonHome, tFreeWonHomePerCent;
	private TextView tFreeWonHomeOwn20, tFreeWonHomeOwn20PerCent;
	private TextView tFreeWonHomeOpp, tFreeWonHomeOppPerCent;
	private TextView tFreeWonOpp, tFreeWonOppPerCent;
	private TextView tFreeWonOppOwn20, tFreeWonOppOwn20PerCent;
	private TextView tFreeWonOppOpp, tFreeWonOppOppPerCent;
	private TextView tPuckWonCleanHome, tPuckWonCleanHomePerCent;
	private TextView tPuckLostCleanHome, tPuckLostCleanHomePerCent;
	private TextView tPuckWonBreakHome, tPuckWonBreakHomePerCent;
	private TextView tPuckLostBreakHome, tPuckLostBreakHomePerCent;
	private TextView tPuckOtherHome, tPuckOtherHomePerCent;
	private TextView tPuckWonCleanOpp, tPuckWonCleanOppPerCent;
	private TextView tPuckLostCleanOpp, tPuckLostCleanOppPerCent;
	private TextView tPuckWonBreakOpp, tPuckWonBreakOppPerCent;
	private TextView tPuckLostBreakOpp, tPuckLostBreakOppPerCent;
	private TextView tPuckOtherOpp, tPuckOtherOppPerCent;
	private TextView tOwnTeam, tOppTeam;
	private TextView tShotsTotalHomePlay, tShotsTotalOppPlay;
	private TextView tShotsHomeTotal, tShotsOppTotal;
	private TextView tFreeWonHomeOwnMid, tFreeWonHomeOwnMidPerCent;
	private TextView tFreeWonOppOwnMid, tFreeWonOppOwnMidPerCent;
	private TextView tPuckTotalHome, tPuckTotalOpp;
	private TextView tPuckWonCatchHome, tPuckLostCatchHome;
	private TextView tPuckWonCatchOpp, tPuckLostCatchOpp;
	private TextView tPuckWonCatchHomePerCent, tPuckLostCatchHomePerCent;
	private TextView tPuckWonCatchOppPerCent, tPuckLostCatchOppPerCent;
	private TextView tHomeScore, tOppScore;
	private String sOwnTeam, sOppTeam;

	private int puckWonCatchHome = 0, puckLostCatchHome = 0;
	private int puckWonCatchOpp = 0, puckLostCatchOpp = 0;
	private int shotHomeTotal = 0, shotGoalsHome = 0, shotPointsHome = 0;
	private int shotGoalsHomePlay = 0, shotPointsHomePlay = 0;
	private int shotHomeTotalPlay = 0, shotShortHome = 0, shotPostsHome = 0;
	private int shotWidesHome = 0, shot45sHome = 0, shotSavedHome = 0;
	private int freeConcededHome = 0, freeConcededHomeOwn20 = 0,
			freeConcededHomeOpp = 0;
	private int freeConcededOpp = 0, freeConcededOppOwn20 = 0,
			freeConcededOppOpp = 0;
	private int freeTotal = 0;
	private int shotHomeTotalScored = 0, shotHomePlayScored = 0;
	private int shotSavedHomePlay = 0, shotShortHomePlay = 0,
			shotPostsHomePlay = 0;
	private int shotGoalsOppPlay = 0, shotWidesOppPlay = 0, shot45sOppPlay = 0,
			shotPointsOppPlay = 0;
	private int shotSavedOppPlay = 0, shotShortOppPlay = 0,
			shotPostsOppPlay = 0;
	private int shotWidesHomePlay = 0, shot45sHomePlay = 0;
	private int shotOppTotalPlay = 0, shotOppPlayScored = 0,
			shotOppTotalScored = 0;
	private int shotHomeTotalScoredP = 0, shotHomePlayScoredP = 0;
	private int shotOppTotalScoredP = 0, shotOppPlayScoredP = 0;
	private int freeConcededHomeOwnMid, freeConcededOppOwnMid;
	private int puckTotalHome, puckTotalOpp;
	int puckWonCleanHome = 0, puckWonCleanHomePerCent = 0;
	int puckLostCleanHome = 0, puckLostCleanHomePerCent = 0;
	int puckWonBreakHome = 0, puckWonBreakHomePerCent = 0;
	int puckLostBreakHome = 0, puckLostBreakHomePerCent = 0;
	int puckWonCleanOpp = 0, puckWonCleanOppPerCent = 0;
	int puckLostCleanOpp = 0, puckLostCleanOppPerCen = 0;
	int puckWonBreakOpp = 0, puckWonBreakOppPerCent = 0;
	int puckLostBreakOpp = 0, puckLostBreakOppPerCent = 0;
	int puckOutTotalHome = 0, puckOutTotalOpp = 0;
	int puckOtherHome = 0, puckOtherHomePerCent = 0;
	int puckOtherOpp = 00, puckOtherOppPerCent = 0;

	private TextView tHomeTotal, tHomeTotalPlay;
	private TextView tOppTotal, tOppTotalPlay;
	private TextView tShotsTotalOpp;
	private TextView tShotGoalsOpp, tShotGoalsOppPlay;
	private TextView tShotWidesOpp, tShotWidesOppPlay;
	private TextView tShotPointsOpp, tShotPointsOppPlay;
	private TextView tShot45sOpp, tShot45sOppPlay;
	private TextView tShotSavedOpp, tShotSavedOppPlay;
	private TextView tShotShortOpp, tShotShortOppPlay;
	private TextView tShotPostsOpp, tShotPostsOppPlay;
	private int shotOppTotal = 0, shotGoalsOpp = 0, shotPointsOpp = 0;
	private int shotWidesOpp = 0, shot45sOpp = 0, shotSavedOpp = 0,
			shotShortOpp = 0, shotPostsOpp = 0;

	@Override
	// start main method to display screen
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.review_frag_layout, container, false);

		// Open up shared preferences file to read in persisted data on startup
		SharedPreferences sharedPref = getActivity().getSharedPreferences(
				"team_stats_review_data", Context.MODE_PRIVATE);

		// get the tag name of this Fragment and pass it up to the parent
		// activity MatchApplication so that this Fragment may be accessed
		// by other fragments through using a reference created from tag name
		String myTag = getTag();
		((MatchApplication) getActivity()).setTagFragmentReview(myTag);

		// set up text buttons edittexts etc.
		tOwnTeam = (TextView) v.findViewById(R.id.textViewRevHome);
		tOppTeam = (TextView) v.findViewById(R.id.textViewRevOpp);

		tOwnTeam.setText(sharedPref.getString("OURTEAM", "OWN TEAM"));
		tOppTeam.setText(sharedPref.getString("OPPTEAM", "OPPOSITION"));

		tHomeScore = (TextView) v.findViewById(R.id.tVHomeScore);
		tOppScore = (TextView) v.findViewById(R.id.tVOppScore);

		tHomeTotalPlay = (TextView) v.findViewById(R.id.tVTotalHomePlay);
		tShotsHomeTotal = (TextView) v.findViewById(R.id.tVTotalHome);
		tShotsOppTotal = (TextView) v.findViewById(R.id.tVTotalOpp);
		tOppTotalPlay = (TextView) v.findViewById(R.id.tVTotalOppPlay);

		tShotsTotalHome = (TextView) v.findViewById(R.id.tVwShotsTotalNo);
		tShotsTotalHomePlay = (TextView) v.findViewById(R.id.tVwShotsTotalP);
		tShotGoalsHome = (TextView) v.findViewById(R.id.tVwShotsGoalsNo);
		tShotGoalsHomePlay = (TextView) v.findViewById(R.id.tVwShotsGoalsP);
		tShotPointsHome = (TextView) v.findViewById(R.id.tVwShotsPointsNo);
		tShotPointsHomePlay = (TextView) v.findViewById(R.id.tVwShotsPointsP);
		tShotWidesHome = (TextView) v.findViewById(R.id.tVwShotsWidesNo);
		tShotWidesHomePlay = (TextView) v.findViewById(R.id.tVwShotsWidesP);
		tShot45sHome = (TextView) v.findViewById(R.id.tVwShots45sNo);
		tShot45sHomePlay = (TextView) v.findViewById(R.id.tVwShots45sP);
		tShotSavedHome = (TextView) v.findViewById(R.id.tVwShotsSavedNo);
		tShotSavedHomePlay = (TextView) v.findViewById(R.id.tVwShotsSavedP);
		tShotShortHome = (TextView) v.findViewById(R.id.tVwShotsShortNo);
		tShotShortHomePlay = (TextView) v.findViewById(R.id.tVwShotsShortP);
		tShotPostsHome = (TextView) v.findViewById(R.id.tVwShotsPostsNo);
		tShotPostsHomePlay = (TextView) v.findViewById(R.id.tVwShotsPostsP);

		tShotsTotalOpp = (TextView) v.findViewById(R.id.tVwShotsTotalOppNo);
		tShotsTotalOppPlay = (TextView) v.findViewById(R.id.tVwShotsTotalOppP);
		tShotGoalsOpp = (TextView) v.findViewById(R.id.tVwShotsGoalsOppNo);
		tShotGoalsOppPlay = (TextView) v.findViewById(R.id.tVwShotsGoalsOppP);
		tShotPointsOpp = (TextView) v.findViewById(R.id.tVwShotsPointsOppNo);
		tShotPointsOppPlay = (TextView) v.findViewById(R.id.tVwShotsPointsOppP);
		tShotWidesOpp = (TextView) v.findViewById(R.id.tVwShotsWidesOppNo);
		tShotWidesOppPlay = (TextView) v.findViewById(R.id.tVwShotsWidesOppP);
		tShot45sOpp = (TextView) v.findViewById(R.id.tVwShots45sOppNo);
		tShot45sOppPlay = (TextView) v.findViewById(R.id.tVwShots45sOppP);
		tShotSavedOpp = (TextView) v.findViewById(R.id.tVwShotsSavedOppNo);
		tShotSavedOppPlay = (TextView) v.findViewById(R.id.tVwShotsSavedOppP);
		tShotShortOpp = (TextView) v.findViewById(R.id.tVwShotsShortOppNo);
		tShotShortOppPlay = (TextView) v.findViewById(R.id.tVwShotsShortOppP);
		tShotPostsOpp = (TextView) v.findViewById(R.id.tVwShotsPostsOppNo);
		tShotPostsOppPlay = (TextView) v.findViewById(R.id.tVwShotsPostsOppP);

		// Set up output for frees
		tFreeWonHome = (TextView) v.findViewById(R.id.tVwFreeWonHome);
		tFreeWonHomePerCent = (TextView) v.findViewById(R.id.tVwFreeWonHomeP);
		tFreeWonHomeOwn20 = (TextView) v.findViewById(R.id.tVwFreeWonHomeOwn20);
		tFreeWonHomeOwn20PerCent = (TextView) v
				.findViewById(R.id.tVwFreeWonHomeOwn20P);
		tFreeWonHomeOwnMid = (TextView) v
				.findViewById(R.id.tVwFreeWonHomeOwnMid);
		tFreeWonHomeOwnMidPerCent = (TextView) v
				.findViewById(R.id.tVwFreeWonHomeOwnMidP);
		tFreeWonHomeOpp = (TextView) v.findViewById(R.id.tVwFreeWonHomeOpp);
		tFreeWonHomeOppPerCent = (TextView) v
				.findViewById(R.id.tVwFreeWonHomeOppP);
		tFreeWonOpp = (TextView) v.findViewById(R.id.tVwFreeWonOpp);
		tFreeWonOppPerCent = (TextView) v.findViewById(R.id.tVwFreeWonOppP);

		tFreeWonOppOwn20 = (TextView) v.findViewById(R.id.tVwFreeWonOppOwn20);
		tFreeWonOppOwn20PerCent = (TextView) v
				.findViewById(R.id.tVwFreeWonOppOwn20P);
		tFreeWonOppOwnMid = (TextView) v.findViewById(R.id.tVwFreeWonOppOwnMid);
		tFreeWonOppOwnMidPerCent = (TextView) v
				.findViewById(R.id.tVwFreeWonOppOwnMidP);
		tFreeWonOppOpp = (TextView) v.findViewById(R.id.tVwFreeWonOppOpp);
		tFreeWonOppOppPerCent = (TextView) v
				.findViewById(R.id.tVwFreeWonOppOppP);

		// Set up output for puckouts
		tPuckWonCleanHome = (TextView) v.findViewById(R.id.tVwPuckWonCleanHome);
		tPuckWonCleanHomePerCent = (TextView) v
				.findViewById(R.id.tVwPuckWonCleanHomeP);
		tPuckLostCleanHome = (TextView) v
				.findViewById(R.id.tVPuckLostCleanHome);
		tPuckLostCleanHomePerCent = (TextView) v
				.findViewById(R.id.tVwPuckLostCleanHomeP);
		tPuckWonBreakHome = (TextView) v.findViewById(R.id.tVPuckWonBreakHome);
		tPuckWonBreakHomePerCent = (TextView) v
				.findViewById(R.id.tVwPuckWonBreakHomeP);
		tPuckLostBreakHome = (TextView) v
				.findViewById(R.id.tVPuckLostBreakHome);
		tPuckLostBreakHomePerCent = (TextView) v
				.findViewById(R.id.tVwPuckLostBreakHomeP);
		tPuckOtherHome = (TextView) v.findViewById(R.id.tVwPuckOtherHome);
		tPuckOtherHomePerCent = (TextView) v
				.findViewById(R.id.tVwPuckOtherHomeP);
		tPuckWonCleanOpp = (TextView) v.findViewById(R.id.tVwPuckWonCleanOpp);
		tPuckWonCleanOppPerCent = (TextView) v
				.findViewById(R.id.tVwPuckWonCleanOppP);
		tPuckLostCleanOpp = (TextView) v.findViewById(R.id.tVPuckLostCleanOpp);
		tPuckLostCleanOppPerCent = (TextView) v
				.findViewById(R.id.tVwPuckLostCleanOppP);
		tPuckWonBreakOpp = (TextView) v.findViewById(R.id.tVPuckWonBreakOpp);
		tPuckWonBreakOppPerCent = (TextView) v
				.findViewById(R.id.tVwPuckWonBreakOppP);
		tPuckLostBreakOpp = (TextView) v.findViewById(R.id.tVPuckLostBreakOpp);
		tPuckLostBreakOppPerCent = (TextView) v
				.findViewById(R.id.tVwPuckLostBreakOppP);
		tPuckOtherOpp = (TextView) v.findViewById(R.id.tVwPuckOtherOpp);
		tPuckOtherOppPerCent = (TextView) v.findViewById(R.id.tVwPuckOtherOppP);
		tPuckTotalHome = (TextView) v.findViewById(R.id.tVwPuckTotalHome);
		tPuckTotalOpp = (TextView) v.findViewById(R.id.tVwPuckTotalOpp);
		tPuckWonCatchHome = (TextView) v.findViewById(R.id.tVwPuckWonCatchHome);
		tPuckWonCatchHomePerCent = (TextView) v
				.findViewById(R.id.tVwPuckWonCatchHomeP);
		tPuckLostCatchHome = (TextView) v
				.findViewById(R.id.tVPuckLostCatchHome);
		tPuckLostCatchHomePerCent = (TextView) v
				.findViewById(R.id.tVwPuckLostCatchHomeP);
		tPuckWonCatchOpp = (TextView) v.findViewById(R.id.tVwPuckWonCatchOpp);
		tPuckLostCatchOpp = (TextView) v.findViewById(R.id.tVPuckLostCatchOpp);
		tPuckWonCatchOppPerCent = (TextView) v
				.findViewById(R.id.tVwPuckWonCatchOppP);
		tPuckLostCatchOppPerCent = (TextView) v
				.findViewById(R.id.tVwPuckLostCatchOppP);

		// Read in score from persisted data
		homeGoals = sharedPref.getInt("HOMEGOALS", 0);
		homePoints = sharedPref.getInt("HOMEPOINTS", 0);
		oppGoals = sharedPref.getInt("OPPGOALS", 0);
		oppPoints = sharedPref.getInt("OPPPOINTS", 0);

		// update screen if persisted data exists
		if (homeGoals + homePoints + oppGoals + oppPoints > 0) {
			settHomeGoals(homeGoals);
			settHomePoints(homePoints);
			settOppGoals(oppGoals);
			settOppPoints(oppPoints);
			updateScores();
		}

		// launch activity to look at shots table in database
		Button bShots = (Button) v.findViewById(R.id.revShots);
		bShots.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						ShotsListActivity.class);
				v.getContext().startActivity(intent);
			}
		});

		Button bShotsPlayer = (Button) v.findViewById(R.id.revShotsPlayer);
		bShotsPlayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						ShotsPlayerListActivity.class);
				v.getContext().startActivity(intent);
			}
		});
		
		Button bCustom = (Button) v.findViewById(R.id.revCustom);
		bCustom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						CustomListActivity.class);
				v.getContext().startActivity(intent);
			}
		});

		// launch activity to look at frees table in database
		Button bFrees = (Button) v.findViewById(R.id.revFrees);
		bFrees.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						FreesListActivity.class);
				v.getContext().startActivity(intent);
			}
		});

		// launch activity to look at puckouts table in database
		Button bPuckOuts = (Button) v.findViewById(R.id.revPuckouts);
		bPuckOuts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						PuckOutsListActivity.class);
				v.getContext().startActivity(intent);
			}
		});
		fillData();
		return v;

	}

	public void fillData() {

		// SHOTS BABY
		String outcome, type, freePosn, poOutcome;
		boolean fromPlay = false;
		shotHomeTotal = shotGoalsHome = shotPointsHome = 0;
		shotWidesHome = shot45sHome = shotSavedHome = shotShortHome = shotPostsHome = 0;
		shotGoalsHomePlay = shotWidesHomePlay = shot45sHomePlay = shotPointsHomePlay = 0;
		shotSavedHomePlay = shotShortHomePlay = shotPostsHomePlay = shotHomeTotalPlay=0;
		Uri allTitles = ShotContentProvider.CONTENT_URI_BASIC;

		// get home team first then opposition
		String[] args = { tOwnTeam.getText().toString() };
		Cursor c1 = getActivity().getContentResolver().query(allTitles, null,
				"team=?", args, null);

		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				fromPlay = false;
				outcome = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.OUTCOME));
				type = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.TYPE));
				if (type == null || type.equals("")
						|| type.substring(0, 2).equals("ki")
						|| type.substring(0, 2).equals("by")) {
					fromPlay = true;
				}
				if (outcome.equals("goal")) {
					shotGoalsHome++;
					if (fromPlay) {
						shotGoalsHomePlay++;
					}
				} else if (outcome.equals("point")) {
					shotPointsHome++;
					if (fromPlay) {
						shotPointsHomePlay++;
					}
				} else if (outcome.equals("wide")) {
					shotWidesHome++;
					if (fromPlay) {
						shotWidesHomePlay++;
					}
				} else if (outcome.equals("45/65")) {
					shot45sHome++;
					if (fromPlay) {
						shot45sHomePlay++;
					}
				} else if (outcome.equals("saved")) {
					shotSavedHome++;
					if (fromPlay) {
						shotSavedHomePlay++;
					}
				} else if (outcome.equals("short")) {
					shotShortHome++;
					if (fromPlay) {
						shotShortHomePlay++;
					}
				} else if (outcome.equals("off posts")) {
					shotPostsHome++;
					if (fromPlay) {
						shotPostsHomePlay++;
					}
				}
			} while (c1.moveToNext());
			c1.close();
			shotHomeTotal = shotGoalsHome + shotPointsHome + shotWidesHome
					+ shot45sHome + shotSavedHome + shotShortHome
					+ shotPostsHome;
			shotHomeTotalPlay = shotGoalsHomePlay + shotPointsHomePlay
					+ shotWidesHomePlay + shot45sHomePlay + shotSavedHomePlay
					+ shotShortHomePlay + shotPostsHomePlay;
			shotHomeTotalScored = shotGoalsHome + shotPointsHome;
			shotHomePlayScored = shotGoalsHomePlay + shotPointsHomePlay;
			if (shotHomeTotal > 0) {
				shotHomeTotalScoredP = shotHomeTotalScored * 100
						/ shotHomeTotal;
			}
			if (shotHomeTotalPlay > 0) {
				shotHomePlayScoredP = shotHomePlayScored * 100
						/ shotHomeTotalPlay;
			}

		}
		tShotGoalsHome.setText(String.valueOf(shotGoalsHome));
		tShotPointsHome.setText(String.valueOf(shotPointsHome));
		tShotWidesHome.setText(String.valueOf(shotWidesHome));
		tShot45sHome.setText(String.valueOf(shot45sHome));
		tShotSavedHome.setText(String.valueOf(shotSavedHome));
		tShotShortHome.setText(String.valueOf(shotShortHome));
		tShotPostsHome.setText(String.valueOf(shotPostsHome));
		tShotGoalsHomePlay.setText(String.valueOf(shotGoalsHomePlay));
		tShotPointsHomePlay.setText(String.valueOf(shotPointsHomePlay));
		tShotWidesHomePlay.setText(String.valueOf(shotWidesHomePlay));
		tShot45sHomePlay.setText(String.valueOf(shot45sHomePlay));
		tShotSavedHomePlay.setText(String.valueOf(shotSavedHomePlay));
		tShotShortHomePlay.setText(String.valueOf(shotShortHomePlay));
		tShotPostsHomePlay.setText(String.valueOf(shotPostsHomePlay));
		tShotsTotalHome.setText(String.valueOf(shotHomeTotal));
		tShotsTotalHomePlay.setText(String.valueOf(shotHomeTotalPlay));
		tShotsHomeTotal.setText("Total Shots: " + String.valueOf(shotHomeTotal)
				+ ", scored: " + String.valueOf(shotHomeTotalScored) + " ("
				+ String.valueOf(shotHomeTotalScoredP) + "%)");
		tHomeTotalPlay.setText("Shots from play: "
				+ String.valueOf(shotHomeTotalPlay) + ", scored: "
				+ String.valueOf(shotHomePlayScored) + " ("
				+ String.valueOf(shotHomePlayScoredP) + "%)");

		//
		// opposition shots
		shotOppTotal = shotGoalsOpp = shotPointsOpp = 0;
		shotWidesOpp = shot45sOpp = shotSavedOpp = shotShortOpp = shotPostsOpp = 0;
		shotGoalsOppPlay = shotWidesOppPlay = shot45sOppPlay = shotPointsOppPlay = 0;
		shotSavedOppPlay = shotShortOppPlay = shotPostsOppPlay = shotOppTotalPlay=0;
		// get home team first then opposition
		String[] args1 = { tOppTeam.getText().toString() };
		c1 = getActivity().getContentResolver().query(allTitles, null,
				"team=?", args1, null);

		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				fromPlay = false;
				outcome = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.OUTCOME));
				type = c1.getString(c1
						.getColumnIndexOrThrow(ShotContentProvider.TYPE));
				if (type == null || type.equals("")
						|| type.substring(0, 2).equals("ki")
						|| type.substring(0, 2).equals("by")) {
					fromPlay = true;
				}
				if (outcome.equals("goal")) {
					shotGoalsOpp++;
					if (fromPlay) {
						shotGoalsOppPlay++;
					}
				} else if (outcome.equals("point")) {
					shotPointsOpp++;
					if (fromPlay) {
						shotPointsOppPlay++;
					}
				} else if (outcome.equals("wide")) {
					shotWidesOpp++;
					if (fromPlay) {
						shotWidesOppPlay++;
					}
				} else if (outcome.equals("45/65")) {
					shot45sOpp++;
					if (fromPlay) {
						shot45sOppPlay++;
					}
				} else if (outcome.equals("saved")) {
					shotSavedOpp++;
					if (fromPlay) {
						shotSavedOppPlay++;
					}
				} else if (outcome.equals("short")) {
					shotShortOpp++;
					if (fromPlay) {
						shotShortOppPlay++;
					}
				} else if (outcome.equals("off posts")) {
					shotPostsOpp++;
					if (fromPlay) {
						shotPostsOppPlay++;
					}
				}
			} while (c1.moveToNext());
			c1.close();
			shotOppTotal = shotGoalsOpp + shotPointsOpp + shotWidesOpp
					+ shot45sOpp + shotSavedOpp + shotShortOpp + shotPostsOpp;
			shotOppTotalPlay = shotGoalsOppPlay + shotPointsOppPlay
					+ shotWidesOppPlay + shot45sOppPlay + shotSavedOppPlay
					+ shotShortOppPlay + shotPostsOppPlay;
			shotOppTotalScored = shotGoalsOpp + shotPointsOpp;
			shotOppPlayScored = shotGoalsOppPlay + shotPointsOppPlay;
			if (shotOppTotal > 0) {
				shotOppTotalScoredP = shotOppTotalScored * 100 / shotOppTotal;
			}
			if (shotOppTotalPlay > 0) {
				shotOppPlayScoredP = shotOppPlayScored * 100 / shotOppTotalPlay;
			}
		}
		tShotGoalsOpp.setText(String.valueOf(shotGoalsOpp));
		tShotPointsOpp.setText(String.valueOf(shotPointsOpp));
		tShotWidesOpp.setText(String.valueOf(shotWidesOpp));
		tShot45sOpp.setText(String.valueOf(shot45sOpp));
		tShotSavedOpp.setText(String.valueOf(shotSavedOpp));
		tShotShortOpp.setText(String.valueOf(shotShortOpp));
		tShotPostsOpp.setText(String.valueOf(shotPostsOpp));
		tShotGoalsOppPlay.setText(String.valueOf(shotGoalsOppPlay));
		tShotPointsOppPlay.setText(String.valueOf(shotPointsOppPlay));
		tShotWidesOppPlay.setText(String.valueOf(shotWidesOppPlay));
		tShot45sOppPlay.setText(String.valueOf(shot45sOppPlay));
		tShotSavedOppPlay.setText(String.valueOf(shotSavedOppPlay));
		tShotShortOppPlay.setText(String.valueOf(shotShortOppPlay));
		tShotPostsOppPlay.setText(String.valueOf(shotPostsOppPlay));
		tShotsTotalOpp.setText(String.valueOf(shotOppTotal));
		tShotsTotalOppPlay.setText(String.valueOf(shotOppTotalPlay));
		tShotsOppTotal.setText("Total Shots: " + String.valueOf(shotOppTotal)
				+ ", scored: " + String.valueOf(shotOppTotalScored) + " ("
				+ String.valueOf(shotOppTotalScoredP) + "%)");
		tOppTotalPlay.setText("Shots from play: "
				+ String.valueOf(shotOppTotalPlay) + ", scored: "
				+ String.valueOf(shotOppPlayScored) + " ("
				+ String.valueOf(shotOppPlayScoredP) + "%)");
		//
		// its FREE TIME
		// own team
		freeConcededHome = freeConcededHomeOwn20 = freeConcededHomeOwnMid = freeConcededHomeOpp = 0;
		allTitles = FreeContentProvider.CONTENT_URI;
		c1 = getActivity().getContentResolver().query(allTitles, null,
				"team=?", args, null);
		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				freePosn = c1.getString(c1
						.getColumnIndexOrThrow(FreeContentProvider.POSITION));
				freeConcededHome++;
				try {
					// work out which half of field free was in according to
					// position number recorded greater or less than 10
					if (((Integer.valueOf(freePosn)) > 0)
							&& ((Integer.valueOf(freePosn)) <= 4)) {
						freeConcededHomeOwn20++;
					}

					else if (((Integer.valueOf(freePosn)) > 4)
							&& ((Integer.valueOf(freePosn)) <= 10)) {
						freeConcededHomeOwnMid++;
					}

					else if (((Integer.valueOf(freePosn)) > 10)
							&& ((Integer.valueOf(freePosn)) <= 20)) {
						freeConcededHomeOpp++;
					}
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "pitch position error");
				}
			} while (c1.moveToNext());
			c1.close();
		}
		tFreeWonHome.setText(String.valueOf(freeConcededHome));
		tFreeWonHomeOwn20.setText(String.valueOf(freeConcededHomeOwn20));
		tFreeWonHomeOwnMid.setText(String.valueOf(freeConcededHomeOwnMid));
		tFreeWonHomeOpp.setText(String.valueOf(freeConcededHomeOpp));
		if (freeConcededHome > 0) {
			tFreeWonHomeOwn20PerCent.setText(String
					.valueOf(freeConcededHomeOwn20 * 100 / freeConcededHome));
			tFreeWonHomeOwnMidPerCent.setText(String
					.valueOf(freeConcededHomeOwnMid * 100 / freeConcededHome));
			tFreeWonHomeOppPerCent.setText(String.valueOf(freeConcededHomeOpp
					* 100 / freeConcededHome));
		}
		//
		// opposition
		freeConcededOpp = freeConcededOppOwn20 = freeConcededOppOwnMid = freeConcededOppOpp = 0;
		allTitles = FreeContentProvider.CONTENT_URI;
		c1 = getActivity().getContentResolver().query(allTitles, null,
				"team=?", args1, null);
		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				freePosn = c1.getString(c1
						.getColumnIndexOrThrow(FreeContentProvider.POSITION));
				freeConcededOpp++;
				try {
					// work out which half of field free was in according to
					// position number recorded greater or less than 10
					if (((Integer.valueOf(freePosn)) > 16)
							&& ((Integer.valueOf(freePosn)) <= 20)) {
						freeConcededOppOwn20++;
					}

					else if (((Integer.valueOf(freePosn)) > 10)
							&& ((Integer.valueOf(freePosn)) <= 16)) {
						freeConcededOppOwnMid++;
					}

					else if (((Integer.valueOf(freePosn)) > 0)
							&& ((Integer.valueOf(freePosn)) <= 10)) {
						freeConcededOppOpp++;
					}
				} catch (NumberFormatException nfe) {
					Log.v("matchrecord", "pitch position error");
				}
			} while (c1.moveToNext());
			c1.close();
		}
		tFreeWonOpp.setText(String.valueOf(freeConcededOpp));
		tFreeWonOppOwn20.setText(String.valueOf(freeConcededOppOwn20));
		tFreeWonOppOwnMid.setText(String.valueOf(freeConcededOppOwnMid));
		tFreeWonOppOpp.setText(String.valueOf(freeConcededOppOpp));
		if (freeConcededOpp > 0) {
			tFreeWonOppOwn20PerCent.setText(String.valueOf(freeConcededOppOwn20
					* 100 / freeConcededOpp));
			tFreeWonOppOwnMidPerCent.setText(String
					.valueOf(freeConcededOppOwnMid * 100 / freeConcededOpp));
			tFreeWonOppOppPerCent.setText(String.valueOf(freeConcededOppOpp
					* 100 / freeConcededOpp));
		}

		// PUCK OUTs baby
		// own team
		puckWonCleanHome = puckLostCleanHome = puckWonBreakHome = 0;
		puckLostBreakHome = puckOtherHome = puckTotalHome = 0;
		puckWonCatchHome = puckLostCatchHome = 0;
		allTitles = PuckOutContentProvider.CONTENT_URI;
		c1 = getActivity().getContentResolver().query(allTitles, null,
				"team=?", args, null);
		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				poOutcome = c1.getString(c1
						.getColumnIndexOrThrow(PuckOutContentProvider.OUTCOME));
				if (poOutcome.equals("won high catch")) {
					puckWonCatchHome++;
				} else if (poOutcome.equals("won clean")) {
					puckWonCleanHome++;
				} else if (poOutcome.equals("won on break")) {
					puckWonBreakHome++;
				} else if (poOutcome.equals("lost high catch")) {
					puckLostCatchHome++;
				} else if (poOutcome.equals("lost clean")) {
					puckLostCleanHome++;
				} else if (poOutcome.equals("lost on break")) {
					puckLostBreakHome++;
				} else if ((poOutcome.equals("over sideline"))
						|| (poOutcome.equals("other"))) {
					puckOtherHome++;
				}
			} while (c1.moveToNext());
			c1.close();
		}
		puckTotalHome = shotGoalsOpp + shotPointsOpp + shotWidesOpp;
		tPuckTotalHome.setText("Total Kickouts: "
				+ String.valueOf(puckTotalHome)
				+ "   Won: "
				+ String.valueOf(puckWonCatchHome + puckWonCleanHome
						+ puckWonBreakHome)
				+ "   Lost: "
				+ String.valueOf(puckLostCatchHome + puckLostCleanHome
						+ puckLostBreakHome));
		tPuckWonCatchHome.setText(String.valueOf(puckWonCatchHome));
		tPuckLostCatchHome.setText(String.valueOf(puckLostCatchHome));
		tPuckWonCleanHome.setText(String.valueOf(puckWonCleanHome));
		tPuckLostCleanHome.setText(String.valueOf(puckLostCleanHome));
		tPuckWonBreakHome.setText(String.valueOf(puckWonBreakHome));
		tPuckLostBreakHome.setText(String.valueOf(puckLostBreakHome));
		tPuckOtherHome.setText(String.valueOf(puckOtherHome));
		if (puckTotalHome > 0) {
			tPuckWonCatchHomePerCent.setText(String.valueOf(puckWonCatchHome
					* 100 / puckTotalHome));
			tPuckLostCatchHomePerCent.setText(String.valueOf(puckLostCatchHome
					* 100 / puckTotalHome));
			tPuckWonCleanHomePerCent.setText(String.valueOf(puckWonCleanHome
					* 100 / puckTotalHome));
			tPuckLostCleanHomePerCent.setText(String.valueOf(puckLostCleanHome
					* 100 / puckTotalHome));
			tPuckWonBreakHomePerCent.setText(String.valueOf(puckWonBreakHome
					* 100 / puckTotalHome));
			tPuckLostBreakHomePerCent.setText(String.valueOf(puckLostBreakHome
					* 100 / puckTotalHome));
			tPuckOtherHomePerCent.setText(String.valueOf(puckOtherHome * 100
					/ puckTotalHome));
		}
		//
		//
		// opp team
		puckWonCleanOpp = puckLostCleanOpp = puckWonBreakOpp = 0;
		puckLostBreakOpp = puckOtherOpp = puckTotalOpp = 0;
		puckWonCatchOpp = puckLostCatchOpp = 0;
		allTitles = PuckOutContentProvider.CONTENT_URI;
		c1 = getActivity().getContentResolver().query(allTitles, null,
				"team=?", args1, null);
		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				poOutcome = c1.getString(c1
						.getColumnIndexOrThrow(PuckOutContentProvider.OUTCOME));
				if (poOutcome.equals("won high catch")) {
					puckWonCatchOpp++;
				} else if (poOutcome.equals("won clean")) {
					puckWonCleanOpp++;
				} else if (poOutcome.equals("won on break")) {
					puckWonBreakOpp++;
				} else if (poOutcome.equals("lost high catch")) {
					puckLostCatchOpp++;
				} else if (poOutcome.equals("lost clean")) {
					puckLostCleanOpp++;
				} else if (poOutcome.equals("lost on break")) {
					puckLostBreakOpp++;
				} else if ((poOutcome.equals("over sideline"))
						|| (poOutcome.equals("other"))) {
					puckOtherOpp++;
				}
			} while (c1.moveToNext());
			c1.close();
		}
		puckTotalOpp = shotGoalsHome + shotPointsHome + shotWidesHome;
		tPuckTotalOpp.setText("Total Kickouts: "
				+ String.valueOf(puckTotalOpp)
				+ "   Won: "
				+ String.valueOf(puckWonCatchOpp + puckWonCleanOpp
						+ puckWonBreakOpp)
				+ "   Lost: "
				+ String.valueOf(puckLostCatchOpp + puckLostCleanOpp
						+ puckLostBreakOpp));

		tPuckWonCatchOpp.setText(String.valueOf(puckWonCatchOpp));
		tPuckLostCatchOpp.setText(String.valueOf(puckLostCatchOpp));
		tPuckWonCleanOpp.setText(String.valueOf(puckWonCleanOpp));
		tPuckLostCleanOpp.setText(String.valueOf(puckLostCleanOpp));
		tPuckWonBreakOpp.setText(String.valueOf(puckWonBreakOpp));
		tPuckLostBreakOpp.setText(String.valueOf(puckLostBreakOpp));
		tPuckOtherOpp.setText(String.valueOf(puckOtherOpp));
		if (puckTotalOpp > 0) {
			tPuckWonCatchOppPerCent.setText(String.valueOf(puckWonCatchOpp
					* 100 / puckTotalOpp));
			tPuckLostCatchOppPerCent.setText(String.valueOf(puckLostCatchOpp
					* 100 / puckTotalOpp));
			tPuckWonCleanOppPerCent.setText(String.valueOf(puckWonCleanOpp
					* 100 / puckTotalOpp));
			tPuckLostCleanOppPerCent.setText(String.valueOf(puckLostCleanOpp
					* 100 / puckTotalOpp));
			tPuckWonBreakOppPerCent.setText(String.valueOf(puckWonBreakOpp
					* 100 / puckTotalOpp));
			tPuckLostBreakOppPerCent.setText(String.valueOf(puckLostBreakOpp
					* 100 / puckTotalOpp));
			tPuckOtherOppPerCent.setText(String.valueOf(puckOtherOpp * 100
					/ puckTotalOpp));
		}
	}

	// ///////////UPDATE SCORES////////////////////////////
	// methods called from RECORD fragment to update score
	// and totals
	public void settHomeGoals(int i) {
		homeGoals = i;
		homeTotal = homeGoals * 3 + homePoints;
		updateScores();
	}

	public void settHomePoints(int i) {
		homePoints = i;
		homeTotal = homeGoals * 3 + homePoints;
		updateScores();
	}

	public void settOppGoals(int i) {
		oppGoals = i;
		oppTotal = oppGoals * 3 + oppPoints;
		updateScores();
	}

	public void settOppPoints(int i) {
		oppPoints = i;
		oppTotal = oppGoals * 3 + oppPoints;
		updateScores();
	}

	public void updateScores() {
		String strTemp = "";
		strTemp = String.valueOf(homeGoals) + " - "
				+ String.valueOf(homePoints) + "  ("
				+ String.valueOf(homeTotal) + ")";
		tHomeScore.setText(strTemp);
		strTemp = String.valueOf(oppGoals) + " - " + String.valueOf(oppPoints)
				+ "  (" + String.valueOf(oppTotal) + ")";
		tOppScore.setText(strTemp);
	}

	// ///////////////////////////END OF ONCREATE///////////////////////////
	@Override
	public void onPause() {
		// persist data out to shared preferences file to be available for start
		// up
		super.onPause(); // Always call the superclass method first
		SharedPreferences sharedPref = getActivity().getSharedPreferences(
				"team_stats_review_data", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		editor.putString("OURTEAM", tOwnTeam.getText().toString());
		editor.putString("OPPTEAM", tOppTeam.getText().toString());

		editor.putInt("HOMEGOALS", homeGoals);
		editor.putInt("HOMEPOINTS", homePoints);
		editor.putInt("OPPGOALS", oppGoals);
		editor.putInt("OPPPOINTS", oppPoints);

		editor.commit();
	}

	// this method is called from the SETUP fragment to update the names of the
	// home and away teams and to receive team line and teams from setup screen
	public void setTeamNames(String homeTeam, String oppTeam) {
		if (!homeTeam.equals(""))
			tOwnTeam.setText(homeTeam);
		if (!oppTeam.equals(""))
			tOppTeam.setText(oppTeam);
	}

	public void setOppName(String oppTeam) {
		if (!oppTeam.equals(""))
			tOppTeam.setText(oppTeam);
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		fillData();
	}
}
