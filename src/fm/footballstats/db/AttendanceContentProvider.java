/*
 *  AttendanceContentProvider.java
 *
 *  Reference: BooksProvider class from Beginning Android 4 Application Development by Wei-Meng Lee
 *  http://www.wrox.com/WileyCDA/WroxTitle/Beginning-Android-4-Application-Development.productCd-1118199545,descCd-DOWNLOAD.html
 *  modified to suit
 *  
 *  Description: This class is the Content Provider for the attendance table in the App database
 *  it facilitates CRUD operations on the database table and notifies ContentResolver whenever
 *  the database is changed
 *  
 *  All ContentProvider classes are based on PanelContentProvider.java.  Check there for best annotation
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

public class AttendanceContentProvider extends BaseProvider {
	public static final String PROVIDER_NAME = "fm.footballstats.provider.attendance";
	public static final String BASE_PATH = "attendees";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/" + BASE_PATH);

	public static final String _ID = "_id";
	private static final int ATTENDANCE = 1;
	private static final int ATTENDANCE_ID = 2;
	private static final int ATTENDANCE_T = 3;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, BASE_PATH, ATTENDANCE);
		uriMatcher.addURI(PROVIDER_NAME, BASE_PATH + "/#", ATTENDANCE_ID);
		uriMatcher.addURI(PROVIDER_NAME, BASE_PATH + "_T", ATTENDANCE_T);
	}

	// ---for database use---
	private SQLiteDatabase matchAppDB;
	public static final String DATABASE_TABLE = "attendance";
	public static final String ATTENDANCEID = "_id";
	public static final String TRAINING_ID = "trainingID";
	public static final String PLAYER_ID = "playerID";

	@Override
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		matchAppDB = dbHelper.getWritableDatabase();
		return (matchAppDB == null) ? false : true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case ATTENDANCE:
			count = matchAppDB.delete(DATABASE_TABLE, selection, selectionArgs);
			break;
		case ATTENDANCE_ID:
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
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		// ---get all players---
		case ATTENDANCE:
			return "fm.footballstats.cursor.dir/fm.footballstats.players ";
			// ---get a particular player---
		case ATTENDANCE_ID:
			return "fm.footballstats.cursor.item/fm.footballstats.players ";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	// ---Original returns URI - changed to return long
	@Override
	public Uri insert(Uri uri, ContentValues values) {

		long rowID = matchAppDB.insert(DATABASE_TABLE, "", values);

		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c;
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(DATABASE_TABLE);

		switch (uriMatcher.match(uri)) {
		// ---get all players---
		case ATTENDANCE_ID:
			sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
			if (sortOrder == null || sortOrder == "")
				sortOrder = ATTENDANCEID;
			c = sqlBuilder.query(matchAppDB, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case ATTENDANCE:
			if (sortOrder == null || sortOrder == "")
				sortOrder = ATTENDANCEID;
			c = sqlBuilder.query(matchAppDB, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case ATTENDANCE_T:
			c = matchAppDB
			.rawQuery(
					"SELECT a.trainingID, p.nickname, p.firstname, p.surname "
							+ "FROM attendance a " 
							+ "left join panel p on a.playerID=p._id "
							+ "where a.trainingID='" + selection + "' "
							+ "order by p.nickname", null);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		// ---register to watch a content URI for changes---
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case ATTENDANCE:
			count = matchAppDB.update(DATABASE_TABLE, values, selection,
					selectionArgs);
			break;
		case ATTENDANCE_ID:
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
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
