/*
 *  CustomActivity.java
 *
 *  Written by: Fintan Mahon 12101524
 *  Description: Displays relevant help screen
 *  
 *  Written on: Jan 2013
 *  
 * 
 *  
 */
package fm.footballstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import fm.footballstats.R;

public class CustomActivity extends Activity {
	private EditText[] bb = new EditText[16];
	private EditText[] etCustomA = new EditText[9];
	private EditText[] etCustomB = new EditText[9];
	private EditText[] free = new EditText[11];
	private String[] team = new String[16];
	private String[] TEAM = new String[16];
	private String[] TEAMP = new String[16];
	private String[] CUSTOMA = new String[9];
	private String[] CUSTOMAP = new String[9];
	private String[] CUSTOMB = new String[9];
	private String[] CUSTOMBP = new String[9];
	private String[] FREE = new String[8];
	private String[] FREEP = new String[10];
	private EditText cTitle1, cTitle2;

	private SharedPreferences sharedPrefSetup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_layout);
		sharedPrefSetup = getSharedPreferences("team_stats_setup_data",
				Context.MODE_PRIVATE);

		// this class is called with an intent from the calling application
		// HELP_ID is used to pass in the calling screen so that
		// the relevant help screen is to be displayed
		Button bQuit = (Button) findViewById(R.id.bQuit);
		bQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View w) {
				quit();
			}
		});
		Button bSave = (Button) findViewById(R.id.bSaveAll);
		bSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View w) {
				save();
				quit();
			}
		});
		Button bReset = (Button) findViewById(R.id.bReset);
		bReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View w) {
				reset();
			}
		});

		for (int i = 1; i <= 15; i++) {
			TEAMP[i] = "ToppP" + String.format("%02d", i);
			TEAM[i] = "Topp" + String.format("%02d", i);
			bb[i] = (EditText) findViewById(getResources()
					.getIdentifier("ButtonP" + String.format("%02d", i), "id",
							"fm.footballstats"));
			bb[i].setText(sharedPrefSetup.getString(TEAM[i], String.valueOf(i)));
		}

		// get custom stuff
		cTitle1 = (EditText) findViewById(R.id.etTitle1);
		cTitle1.setText(sharedPrefSetup.getString("CTITLE1", "stats 1"));
		cTitle2 = (EditText) findViewById(R.id.etTitle2);
		cTitle2.setText(sharedPrefSetup.getString("CTITLE2", "stats 2"));

		for (int i = 0; i < 9; i++) {
			CUSTOMAP[i] = "CustomAP" + String.format("%02d", i);
			CUSTOMA[i] = "CustomA" + String.format("%02d", i);
			etCustomA[i] = (EditText) findViewById(getResources()
					.getIdentifier("etTA" + String.format("%02d", i + 1), "id",
							"fm.footballstats"));
		}
		etCustomA[0].setText(sharedPrefSetup.getString(CUSTOMA[0], "shot"));
		etCustomA[1].setText(sharedPrefSetup
				.getString(CUSTOMA[1], "block made"));
		etCustomA[2].setText(sharedPrefSetup.getString(CUSTOMA[2], "blocked"));
		etCustomA[3]
				.setText(sharedPrefSetup.getString(CUSTOMA[3], "hook made"));
		etCustomA[4].setText(sharedPrefSetup.getString(CUSTOMA[4], "hooked"));
		etCustomA[5].setText(sharedPrefSetup.getString(CUSTOMA[5],
				"lift success"));
		etCustomA[6]
				.setText(sharedPrefSetup.getString(CUSTOMA[6], "lift fail"));
		etCustomA[7]
				.setText(sharedPrefSetup.getString(CUSTOMA[7], "hand pass"));
		etCustomA[8].setText(sharedPrefSetup.getString(CUSTOMA[8],
				"struck pass"));

		for (int i = 0; i < 9; i++) {
			CUSTOMBP[i] = "CustomBP" + String.format("%02d", i);
			CUSTOMB[i] = "CustomB" + String.format("%02d", i);
			etCustomB[i] = (EditText) findViewById(getResources()
					.getIdentifier("etTB" + String.format("%02d", i + 1), "id",
							"fm.footballstats"));
		}
		etCustomB[0].setText(sharedPrefSetup.getString(CUSTOMB[0],
				"good option"));
		etCustomB[1].setText(sharedPrefSetup.getString(CUSTOMB[1],
				"poor option"));
		etCustomB[2].setText(sharedPrefSetup.getString(CUSTOMB[2],
				"ruck ball won"));
		etCustomB[3].setText(sharedPrefSetup.getString(CUSTOMB[3],
				"ruck ball lost"));
		etCustomB[4].setText(sharedPrefSetup.getString(CUSTOMB[4],
				"possession won"));
		etCustomB[5].setText(sharedPrefSetup.getString(CUSTOMB[5],
				"possession lost"));
		etCustomB[6].setText(sharedPrefSetup.getString(CUSTOMB[6], "catch"));

		etCustomB[7].setText(sharedPrefSetup.getString(CUSTOMB[7], "good"));
		etCustomB[8].setText(sharedPrefSetup.getString(CUSTOMB[8], "poor"));

		// frees
		for (int i = 0; i < 8; i++) {
			FREEP[i] = "FreeP" + String.format("%02d", i);
			FREE[i] = "Free" + String.format("%02d", i);
			free[i] = (EditText) findViewById(getResources().getIdentifier(
					"etFrees" + String.format("%02d", i + 1), "id",
					"fm.footballstats"));
		}
		free[0].setText(sharedPrefSetup.getString(FREE[0], "steps"));
		free[1].setText(sharedPrefSetup.getString(FREE[1], "2 hops"));
		free[2].setText(sharedPrefSetup.getString(FREE[2], "throw"));
		free[3].setText(sharedPrefSetup.getString(FREE[3], "square ball"));
		free[4].setText(sharedPrefSetup.getString(FREE[4], "pick off ground"));
		free[5].setText(sharedPrefSetup.getString(FREE[5], "push/pull/trip"));
		free[6].setText(sharedPrefSetup.getString(FREE[6], "holding"));
		free[7].setText(sharedPrefSetup.getString(FREE[7], "striking"));
		free[8].setText(sharedPrefSetup.getString(FREE[8], "charging"));
		free[9].setText(sharedPrefSetup.getString(FREE[9], "other"));

		// ///////////////////////////// The end
	}

	@Override
	public void onPause() {
		// Save out the details so that they are available on restart
		super.onPause(); // Always call the superclass method first
		SharedPreferences.Editor editor = sharedPrefSetup.edit();
		for (int i = 1; i <= 15; i++) {
			editor.putString(TEAM[i],
					!bb[i].getText().toString().equals("") ? bb[i].getText()
							.toString() : String.valueOf(i));
		}
		for (int i = 0; i < 9; i++) {
			editor.putString(CUSTOMA[i], !etCustomA[i].getText().toString()
					.equals("") ? etCustomA[i].getText().toString() : "-");
		}
		for (int i = 0; i < 9; i++) {
			editor.putString(CUSTOMB[i], !etCustomB[i].getText().toString()
					.equals("") ? etCustomB[i].getText().toString() : "-");
		}
		for (int i = 0; i < 10; i++) {
			editor.putString(FREE[i],
					!free[i].getText().toString().equals("") ? free[i]
							.getText().toString() : "-");
		}
		editor.putString("CTITLE1",
				!cTitle1.getText().toString().equals("") ? cTitle1.getText()
						.toString() : "stats 1");
		editor.putString("CTITLE2",
				!cTitle2.getText().toString().equals("") ? cTitle2.getText()
						.toString() : "stats 2");
		editor.commit();
	}

	public void save() {
		SharedPreferences.Editor editor = sharedPrefSetup.edit();
		for (int i = 1; i <= 15; i++) {
			editor.putString(TEAMP[i],
					!bb[i].getText().toString().equals("") ? bb[i].getText()
							.toString() : String.valueOf(i));
		}
		for (int i = 0; i < 9; i++) {
			editor.putString(CUSTOMAP[i], !etCustomA[i].getText().toString()
					.equals("") ? etCustomA[i].getText().toString() : "-");
		}
		for (int i = 0; i < 9; i++) {
			editor.putString(CUSTOMBP[i], !etCustomB[i].getText().toString()
					.equals("") ? etCustomB[i].getText().toString() : "-");
		}
		for (int i = 0; i < 10; i++) {
			editor.putString(FREEP[i],
					!free[i].getText().toString().equals("") ? free[i]
							.getText().toString() : "-");
		}
		editor.putString("CTITLE1P",
				!cTitle1.getText().toString().equals("") ? cTitle1.getText()
						.toString() : "stats 1");
		editor.putString("CTITLE2P",
				!cTitle2.getText().toString().equals("") ? cTitle2.getText()
						.toString() : "stats 2");
		editor.commit();
	}

	public void quit() {
		finish();
	}

	public void reset() {
		for (int i = 1; i <= 15; i++) {
			bb[i].setText(String.valueOf(i));
		}
		etCustomA[0].setText("shot");
		etCustomA[1].setText("block made");
		etCustomA[2].setText("blocked");
		etCustomA[3].setText("hook made");
		etCustomA[4].setText("hooked");
		etCustomA[5].setText("lift success");
		etCustomA[6].setText("lift fail");
		etCustomA[7].setText("hand pass");
		etCustomA[8].setText("struck pass");
		etCustomB[0].setText("good option");
		etCustomB[1].setText("poor option");
		etCustomB[2].setText("ruck ball won");
		etCustomB[3].setText("ruck ball lost");
		etCustomB[4].setText("possession won");
		etCustomB[5].setText("possession lost");
		etCustomB[6].setText("catch");
		etCustomB[7].setText("good");
		etCustomB[8].setText("poor");
		cTitle1.setText("stats 1");
		cTitle2.setText("stats 2");
		free[0].setText("steps");
		free[1].setText("2 hops");
		free[2].setText("throw");
		free[3].setText("square ball");
		free[4].setText("pick off ground");
		free[5].setText("push/pull/trip");
		free[6].setText("holding");
		free[7].setText("striking");
		free[8].setText("charging");
		free[9].setText("other");
		save();
	}

}