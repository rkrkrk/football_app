/*
 *  StartupActivity.java
 *
 *  Written by: Fintan Mahon 12101524
 *  Description: Start up screen Activity
 *  Written on: Jan 2013
 *  
 *  Uses modified code from
 *  http://stackoverflow.com/questions/11043175/trying-to-copy-sqlite-db-from-data-to-sd-card
 */

package fm.footballstats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import fm.footballstats.R;
import fm.footballstats.db.FreeContentProvider;
import fm.footballstats.db.MatchContentProvider;
import fm.footballstats.db.PositionContentProvider;
import fm.footballstats.db.PuckOutContentProvider;
import fm.footballstats.db.ShotContentProvider;
import fm.footballstats.db.run.PanelListActivity;
import fm.footballstats.db.run.TrainingListActivity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartupActivity extends FragmentActivity {
	private static final String DATABASE_NAME = "team";
	private Context context;
	private String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Football Manager");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup_layout);
		context = this;

		// set up Buttons
		// One Listener deals with all input
		Button bManagePanel = (Button) findViewById(R.id.managepanel);
		bManagePanel.setOnClickListener(setupClickListener);

		Button bTraining = (Button) findViewById(R.id.training);
		bTraining.setOnClickListener(setupClickListener);

		Button bReset = (Button) findViewById(R.id.resetdata);
		bReset.setOnClickListener(setupClickListener);
		bReset.setText(Html
				.fromHtml("<strong>start new match</strong><br/><font color='red'><small>(long press)</small></font>"));

		Button bMatch = (Button) findViewById(R.id.match);
		bMatch.setOnClickListener(setupClickListener);

		Button bEmail = (Button) findViewById(R.id.email);
		bEmail.setOnClickListener(setupClickListener);

		Button bCustom = (Button) findViewById(R.id.customStats);
		bCustom.setOnClickListener(setupClickListener);

		Button bHelp = (Button) findViewById(R.id.sHelp);
		bHelp.setOnClickListener(setupClickListener);

		Button bQuit = (Button) findViewById(R.id.quit);
		bQuit.setOnClickListener(setupClickListener);
		
		// stats button click listener just diplays message to longpress

		// reset stats button click listener
		// set all stats back to zero on REVIEW fragment screen
		bReset.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// start up new match for recording
				// reset persisted data in SharedPreferences files for each of
				// the three Activities: Setup, Record, Review
				SharedPreferences sharedPrefRecord = getSharedPreferences(
						"team_stats_record_data", Context.MODE_PRIVATE);
				SharedPreferences.Editor editorRecord = sharedPrefRecord.edit();
				editorRecord.putLong("STARTTIME", 0);
				editorRecord.putString("TIMERBUTTON", "start");
				editorRecord.putString("HALFTEXT", "START FIRST TIME");
				editorRecord.putInt("HOMEGOALS", 0);
				editorRecord.putInt("HOMEPOINTS", 0);
				editorRecord.putInt("OPPGOALS", 0);
				editorRecord.putInt("OPPPOINTS", 0);
				editorRecord.putLong("MATCHID", -1);
				editorRecord.putString("OURTEAM", "OWN TEAM");
				editorRecord.putString("OPPTEAM", "OPPOSITION TEAM");
				editorRecord.putString("STATSLIST", "");
				editorRecord.putInt("MINSPERHALF", 30);
				editorRecord.putInt("UNDOLISTSIZE", 0);
				editorRecord.commit();

				SharedPreferences sharedPrefReview = getSharedPreferences(
						"team_stats_review_data", Context.MODE_PRIVATE);
				SharedPreferences.Editor editorReview = sharedPrefReview.edit();
				editorReview.putString("OURTEAM", "OWN TEAM");
				editorReview.putString("OPPTEAM", "OPPOSITION TEAM");

				editorReview.putInt("HOMEGOALS", 0);
				editorReview.putInt("HOMEPOINTS", 0);
				editorReview.putInt("OPPGOALS", 0);
				editorReview.putInt("OPPPOINTS", 0);
				editorReview.commit();

				SharedPreferences sharedPrefSetup = getSharedPreferences(
						"team_stats_setup_data", Context.MODE_PRIVATE);
				SharedPreferences.Editor editorSetup = sharedPrefSetup.edit();
				editorSetup.putString("MATCHDATE", "");
				editorSetup.putString("HOMETEAM", "");
				editorSetup.putString("OPPTEAM", "");
				editorSetup.putString("LOCATION", "");
				editorSetup.putInt("MATCHSAVED", 0);
				editorSetup.commit();
				// empty databases
				getContentResolver().delete(
						Uri.parse(FreeContentProvider.CONTENT_URI + "/"), null,
						null);
				getContentResolver().delete(
						Uri.parse(ShotContentProvider.CONTENT_URI + "/"), null,
						null);
				getContentResolver().delete(
						Uri.parse(PuckOutContentProvider.CONTENT_URI + "/"),
						null, null);
				getContentResolver().delete(
						Uri.parse(MatchContentProvider.CONTENT_URI + "/"),
						null, null);
				getContentResolver().delete(
						Uri.parse(PositionContentProvider.CONTENT_URI + "/"),
						null, null);

				Intent intent6 = new Intent(v.getContext(),
						MatchApplication.class);
				v.getContext().startActivity(intent6);

				return true;
			}
		});
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
		Intent ihelp = new Intent(this, HelpActivity.class);
		ihelp.putExtra("HELP_ID", R.string.startHelp);
		startActivity(ihelp);
		return super.onMenuItemSelected(featureId, item);
	}

	// Listener do deal with Button Clicks
	OnClickListener setupClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.managepanel:
				// start up activity to manage Panel of players
				intent = new Intent(v.getContext(), PanelListActivity.class);
				v.getContext().startActivity(intent);
				break;
			case R.id.training:
				// start up activity to record training attendance
				intent = new Intent(v.getContext(), TrainingListActivity.class);
				v.getContext().startActivity(intent);
				break;
			case R.id.match:
				// start up activity to continue an existing match
				intent = new Intent(v.getContext(), MatchApplication.class);
				v.getContext().startActivity(intent);
				break;
			case R.id.email:
				// call CVSExport class to save database tables to CSV files
				try {
					new CSVExport(context).execute("match");
				} catch (Exception ex) {
					Log.e("Error in StartupActivity", ex.toString());
				}

				// save and email data
				try {
					// get device storage directory
					File sd = Environment.getExternalStorageDirectory();
					// File data = Environment.getDataDirectory();
					// Log.v("sd", "- " + sd);
					// Log.v("data", "- " + data);

					// copy database file from App assets to external storage
					// from where it can be accessed or emailed.
					// If statement to check if its possible to write to storage
					if (sd.canWrite()) {
						// get current database
						File currentDB = new File("/data/data/"
								+ getPackageName() + "/databases/",
								DATABASE_NAME);
						// create new sub directory to store app data
						File dir = new File(sd, "match_BU");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						// get todays date to use as a timestamp when saving
						// files
						// Date currentDate = new
						// Date(System.currentTimeMillis());
						// SimpleDateFormat sdf = new
						// SimpleDateFormat("ddMMyyyy_");
						// date = sdf.format(currentDate);
						// add date timestamp to output database file name
						File backupDB = new File(dir, DATABASE_NAME + ".db");

						// copy database to storage
						// N.B. this code based on
						// http://stackoverflow.com/questions/11043175/trying-to-copy-sqlite-db-from-data-to-sd-card
						FileInputStream in = new FileInputStream(currentDB);
						FileOutputStream out = new FileOutputStream(backupDB);
						FileChannel src = in.getChannel();
						FileChannel dst = out.getChannel();
						dst.transferFrom(src, 0, src.size());
						in.close();
						out.close();
					}
				} catch (IOException e) {
					Log.v("error in save DB", "- " + e.toString());
				}
				// set up for emailing database and CSV files from storage
				Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "match database");
				emailIntent.putExtra(Intent.EXTRA_TEXT, "match data attched");
				emailIntent.setType("text/plain");
				String[] emailAttachments = new String[] {
						// Environment.getExternalStorageDirectory()
						// + "/match_BU/" + date + DATABASE_NAME + ".db",
						Environment.getExternalStorageDirectory()
								+ "/match_BU/" + "shots.csv",
						Environment.getExternalStorageDirectory()
								+ "/match_BU/" + "frees.csv",
						Environment.getExternalStorageDirectory()
								+ "/match_BU/" + "kickouts.csv",
						Environment.getExternalStorageDirectory()
								+ "/match_BU/" + "custom.csv",
						Environment.getExternalStorageDirectory()
								+ "/match_BU/" + "positions.csv" };
				// put email attachments into an ArrayList
				ArrayList<Uri> uris = new ArrayList<Uri>();
				for (String file : emailAttachments) {
					File uriFiles = new File(file);
					Uri u = Uri.fromFile(uriFiles);
					uris.add(u);
				}
				emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
						uris);
				startActivity(Intent.createChooser(emailIntent, "Email:"));
				break;
			case R.id.resetdata:
				Toast.makeText(StartupActivity.this,
						"Long Press to Start New Match", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.customStats:
				// start up activity to continue an existing match
				intent = new Intent(v.getContext(), CustomActivity.class);
				v.getContext().startActivity(intent);
				break;
			case R.id.sHelp:
				intent = new Intent(v.getContext(), HelpActivity.class);
				intent.putExtra("HELP_ID", R.string.startHelp);
				v.getContext().startActivity(intent);
				break;
			case R.id.quit:
				// close activity and quit
				finish();
				break;
			}

		}
	};
}
