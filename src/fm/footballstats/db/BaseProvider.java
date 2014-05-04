/*
 *  BaseProvider.java
 *
 *  Written by: Fintan Mahon 12101524
 *  
 *  Description: Create all tables in SQLite databasen team
 *  
 *  Written on: Jan 2013
 *  
 * 
 */
package fm.footballstats.db;

import android.content.ContentProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class BaseProvider extends ContentProvider {

	DatabaseHelper dbHelper;

	public static final String DATABASE_NAME = "team";
	public static final int DATABASE_VERSION = 3;

	// setup table to store panel player detailscreate table if not exists TableName (col1 typ1, ..., colN typN)
	private static final String CREATE_TABLE_PANEL = "create table if not exists "
			+ PanelContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ PanelContentProvider.FIRSTNAME
			+ " text not null collate nocase, " + PanelContentProvider.SURNAME
			+ " text not null, " + PanelContentProvider.NICKNAME
			+ " text not null, " + PanelContentProvider.PHONE
			+ " text not null, " + PanelContentProvider.ADDRESS
			+ " text not null," + PanelContentProvider.PANELNAME + " text);";

	// setup table to store match details
	private static final String CREATE_TABLE_MATCH = "create table if not exists "
			+ MatchContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ MatchContentProvider.DATE + " text not null, "
			+ MatchContentProvider.LOCATION + " text, "
			+ MatchContentProvider.HOMETEAM + " text, "
			+ MatchContentProvider.OPPOSITION + " text, "
			+ MatchContentProvider.MINSPERHALF + " integer, "
			+ MatchContentProvider.FIRSTHALFTIME + " text, "
			+ MatchContentProvider.SECONDHALFTIME + " text, "
			+ MatchContentProvider.FIRSTHALFGOALSOWN + " integer, "
			+ MatchContentProvider.FIRSTHALFPOINTSOWN + " integer, "
			+ MatchContentProvider.SECONDHALFGOALSOWN + " integer, "
			+ MatchContentProvider.SECONDHALFPOINTSOWN + " integer, "
			+ MatchContentProvider.FIRSTHALFGOALSOPP + " integer, "
			+ MatchContentProvider.FIRSTHALFPOINTSOPP + " integer, "
			+ MatchContentProvider.SECONDHALFGOALSOPP + " integer, "
			+ MatchContentProvider.SECONDHALFPOINTSOPP + " integer);";

	// setup table to store shots details
	private static final String CREATE_TABLE_SHOTS = "create table if not exists "
			+ ShotContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ ShotContentProvider.MATCH_ID + " integer, "
			+ ShotContentProvider.TIME + " text not null, "
			+ ShotContentProvider.TEAM + " text not null, "
			+ ShotContentProvider.OUTCOME + " text not null, "
			+ ShotContentProvider.PLAYER_ID + " integer not null, "
			+ ShotContentProvider.TYPE + " text, "
			+ ShotContentProvider.POSITION + " integer);";

	// setup table to store shots details
	public static final String CREATE_TABLE_CUSTOM = "create table if not exists "
			+ CustomContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ CustomContentProvider.MATCH_ID + " integer, "
			+ CustomContentProvider.TIME + " text not null, "
			+ CustomContentProvider.TEAM + " text not null, "
			+ CustomContentProvider.OUTCOME + " text not null, "
			+ CustomContentProvider.PLAYER_ID + " integer not null, "
			+ CustomContentProvider.TYPE + " text, "
			+ CustomContentProvider.POSITION + " integer);";

	// setup table to store puck outs details
	private static final String CREATE_TABLE_PUCKS = "create table if not exists "
			+ PuckOutContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ PuckOutContentProvider.MATCH_ID + " integer, "
			+ PuckOutContentProvider.TIME + " text not null, "
			+ PuckOutContentProvider.TEAM + " text not null, "
			+ PuckOutContentProvider.OUTCOME + " text not null, "
			+ PuckOutContentProvider.PLAYER_ID + " integer not null, "
			+ PuckOutContentProvider.POSITION + " integer);";

	// setup table to store frees details
	private static final String CREATE_TABLE_FREES = "create table if not exists "
			+ FreeContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ FreeContentProvider.MATCH_ID + " integer, "
			+ FreeContentProvider.TIME + " text not null, "
			+ FreeContentProvider.TEAM + " text not null, "
			+ FreeContentProvider.REASON + " text not null, "
			+ FreeContentProvider.PLAYER_ID + " integer not null, "
			+ FreeContentProvider.POSITION + " integer);";

	// setup table to store team line/player positions details
	private static final String CREATE_TABLE_POSITIONS = "create table if not exists "
			+ PositionContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ PositionContentProvider.MATCH_ID + " integer, "
			+ PositionContentProvider.TIME + " text not null, "
			+ PositionContentProvider.TEAMPOSITION + " integer, "
			+ PositionContentProvider.PLAYER_ID + " integer not null, "
			+ PositionContentProvider.CODE + " text);";

	// setup table to store training session details
	private static final String CREATE_TABLE_TRAINING = "create table if not exists "
			+ TrainingContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ TrainingContentProvider.DATE + " text, "
			+ TrainingContentProvider.LOCATION + " text, "
			+ TrainingContentProvider.COMMENTS + " text, "
			+ TrainingContentProvider.FILTER + " text, "
			+ TrainingContentProvider.PANELNAME + " text);";

	// setup table to store player attendance at training details
	private static final String CREATE_TABLE_ATTENDANCE = "create table if not exists "
			+ AttendanceContentProvider.DATABASE_TABLE
			+ " (_id integer primary key autoincrement, "
			+ AttendanceContentProvider.TRAINING_ID + " integer not null, "
			+ AttendanceContentProvider.PLAYER_ID + " integer not null);";

	// inner class to create database
	static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		// method to create database tables defined above
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_PANEL);
			db.execSQL(CREATE_TABLE_MATCH);
			db.execSQL(CREATE_TABLE_SHOTS);
			db.execSQL(CREATE_TABLE_CUSTOM);
			db.execSQL(CREATE_TABLE_PUCKS);
			db.execSQL(CREATE_TABLE_FREES);
			db.execSQL(CREATE_TABLE_POSITIONS);
			db.execSQL(CREATE_TABLE_TRAINING);
			db.execSQL(CREATE_TABLE_ATTENDANCE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// code to upgrade your db here (all of your tables, indexes,
			// triggers...)
			// upgrade to add panelname to DB
			
		}
	}

}
