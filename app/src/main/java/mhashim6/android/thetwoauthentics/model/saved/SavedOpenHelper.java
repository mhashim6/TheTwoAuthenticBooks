package mhashim6.android.thetwoauthentics.model.saved;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mhashim6 on 07/07/2017.
 */

class SavedOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE_SAVED = "saved";

	public static final String NUMBER_COL = "num";

	private static final String DATABASE_NAME = "saved.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_SAVED + "( " + NUMBER_COL
			+ " int NOT NULL UNIQUE);";
//===================================================

	public SavedOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
//===================================================

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}
//===================================================

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
		onCreate(db);
	}
//===================================================
}
