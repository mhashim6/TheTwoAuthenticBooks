package mhashim6.android.thetwoauthentics.model.saved;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import mhashim6.android.thetwoauthentics.model.Hadith;
import mhashim6.android.thetwoauthentics.model.Muhaddith;

/**
 * Created by mhashim6 on 07/07/2017.
 */

public final class SavedAccess {

	private SQLiteDatabase database;
	private final SavedOpenHelper openHelper;

	private static SavedAccess instance = null;
	private static final String TAG = "SAVE";

	private static final String SELECT_FROM = "SELECT %s FROM %s";
//===================================================

	private SavedAccess(Context context) {
		openHelper = new SavedOpenHelper(context);
	}
//===================================================

	public static SavedAccess getInstance(Context context) {
		if (instance == null)
			instance = new SavedAccess(context);
		return instance;
	}
//===================================================

	public void open() throws SQLException {
		database = openHelper.getWritableDatabase();
		Log.i(TAG, "Successfully opened saved.db");
	}
//===================================================

	public void close() {
		openHelper.close();
	}

	public int[] getAllSavedAhadithIndexes() {
		int[] ahadith;

		Cursor cursor = database.rawQuery(String.format(SELECT_FROM, SavedOpenHelper.NUMBER_COL, SavedOpenHelper.TABLE_SAVED), null);
		cursor.moveToFirst();

		ahadith = new int[cursor.getCount()];
		for (int i = 0; !cursor.isAfterLast(); i++) {
			ahadith[i] = cursor.getInt(cursor.getColumnIndex(SavedOpenHelper.NUMBER_COL));
			cursor.moveToNext();
		}
		cursor.close();

		return ahadith;
	}
//===================================================

	public void saveHadith(Hadith hadith) {
		saveHadith(getProperNumber(hadith));
	}
//===================================================

	private void saveHadith(int hadithNumber) {
		ContentValues values = new ContentValues();
		values.put(SavedOpenHelper.NUMBER_COL, hadithNumber);
		database.insert(SavedOpenHelper.TABLE_SAVED, null, values);
		Log.i(TAG, "added hadith: " + hadithNumber);
	}
//===================================================

	public void removeHadith(Hadith hadith) {
		removeHadith(getProperNumber(hadith));
	}
//===================================================

	private void removeHadith(int hadithNumber) {
		database.delete(SavedOpenHelper.TABLE_SAVED, SavedOpenHelper.NUMBER_COL + "=" + hadithNumber, null);
		Log.i(TAG, "removed hadith: " + hadithNumber);
	}
//===================================================

	private int getProperNumber(Hadith hadith) {
		return hadith.muhaddith() == Muhaddith.ALBUKHARI ?
				hadith.number() : -hadith.number();
	}
//===================================================

}
