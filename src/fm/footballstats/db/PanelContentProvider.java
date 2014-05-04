/*
 *  PanelContentProvider.java
 *
 *  Reference: BooksProvider class from Beginning Android 4 Application Development by Wei-Meng Lee
 *  http://www.wrox.com/WileyCDA/WroxTitle/Beginning-Android-4-Application-Development.productCd-1118199545,descCd-DOWNLOAD.html
 *  modified to suit
 *  
 *  Description: This class is the Content Provider for the panel table in the App database
 *  it facilitates CRUD operations on the database table and notifies ContentResolver whenever
 *  the database is changed
 *  

 *  Written on: Jan 2013
 *  
 * 
 */
package fm.footballstats.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class PanelContentProvider extends BaseProvider {
	// set up uri for content provider
	public static final String PROVIDER_NAME = "fm.footballstats.provider.panel";
	public static final String BASE_PATH = "players";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/" + BASE_PATH);

	public static final String _ID = "_id";
	private static final int PLAYERS = 1;
	private static final int PLAYERS_ID = 2;

	// use urimatcher to parse input uri from contentresolver
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, BASE_PATH, PLAYERS);
		uriMatcher.addURI(PROVIDER_NAME, BASE_PATH + "/#", PLAYERS_ID);
	}

	// ---for database use---
	private SQLiteDatabase matchAppDB;
	public static final String DATABASE_TABLE = "panel";
	public static final String PANELID = "_id";
	public static final String FIRSTNAME = "firstname";
	public static final String SURNAME = "surname";
	public static final String NICKNAME = "nickname";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String PANELNAME = "panelname";

	@Override
	// open connection to team defined in BaseProvider Class
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		matchAppDB = dbHelper.getWritableDatabase();
		return (matchAppDB == null) ? false : true;
	}

	@Override
	// delete players from database
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		// delete all players
		case PLAYERS:
			count = matchAppDB.delete(DATABASE_TABLE, selection, selectionArgs);
			break;
		// delete single player
		case PLAYERS_ID:
			String id = uri.getPathSegments().get(1);
			count = matchAppDB.delete(DATABASE_TABLE, _ID
					+ " = "
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		// notify contentresolver of change
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		// ---get all players---
		case PLAYERS:
			return "fm.footballstats.cursor.dir/fm.footballstats.players ";
			// ---get a particular player---
		case PLAYERS_ID:
			return "fm.footballstats.cursor.item/fm.footballstats.players ";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	//
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = matchAppDB.insert(DATABASE_TABLE, "", values);
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			// notify contentresolver of change
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	//
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(DATABASE_TABLE);

		if (uriMatcher.match(uri) == PLAYERS_ID)
			// ---if getting a particular player---
			sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));

		if (sortOrder == null || sortOrder == "")
			sortOrder = PANELID;

		Cursor c = sqlBuilder.query(matchAppDB, projection, selection,
				selectionArgs, null, null, sortOrder);

		// notify contentresolver of change
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		// update single player
		case PLAYERS:
			count = matchAppDB.update(DATABASE_TABLE, values, selection,
					selectionArgs);
			break;
		// update all players
		case PLAYERS_ID:
			count = matchAppDB.update(
					DATABASE_TABLE,
					values,
					_ID
							+ " = "
							+ uri.getPathSegments().get(1)
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		// notify contentresolver of change
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
