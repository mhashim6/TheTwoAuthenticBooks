package mhashim6.android.thetwoauthentics.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by mhashim6 on 05/07/2017.
 */

public abstract class AbstractMuhaddith implements Muhaddith {

	protected SQLiteOpenHelper openHelper;
	protected SQLiteDatabase database;
//===================================================

	private static final String HADITH_NUM_COL = "num";
	private static final String HADITH_TEXT_COL = "hadith_mushakkal";
//===================================================

	private static final String HADITH_SQL_CLAUSE = "SELECT * FROM ahadith_mushakkala WHERE num IN ( SELECT num FROM ahadith WHERE %s) LIMIT %d";
	private static final String HADITH_BY_NUMS_SQL_CLAUSE = "SELECT * FROM ahadith_mushakkala WHERE num in (%s)";
	//private static final String HADITH_BY_SINGLE_NUM_SQL_CLAUSE = "SELECT * FROM ahadith_mushakkala WHERE num = %s";

	private static final String LIKE_CLAUSE = "hadith LIKE '%";
	private static final String PERCENTAGE_SIGN = "%'";
//===================================================

	private static final String AND = " AND ";
	private static final String COMMA = ",";
//===================================================

	public void open() {
		database = openHelper.getReadableDatabase();
	}
//===================================================

	public void close() {
		openHelper.close();
	}
//===================================================

	final List<Hadith> findAhadithByStringKeys(int muhaddith, int limit, String... keys) {
		if (keys.length == 0)
			return Collections.emptyList();

		Cursor cursor = database.rawQuery(
				String.format(Locale.ENGLISH, HADITH_SQL_CLAUSE, convertStringKeysToSQL(keys), limit),
				null);
		return loadDataFromCursor(cursor, muhaddith);

	}
//===================================================

	final List<Hadith> findAhadithByIntKeys(int muhaddith, Integer... keys) {
		if (keys.length == 0)
			return Collections.emptyList();

		Cursor cursor = database.rawQuery(
				String.format(HADITH_BY_NUMS_SQL_CLAUSE, convertIntKeysToSQL(keys)),
				null);
		return loadDataFromCursor(cursor, muhaddith);
	}
//===================================================

	private static List<Hadith> loadDataFromCursor(Cursor cursor, int muhaddith) {
		Hadith[] ahadith;
		cursor.moveToFirst();
		ahadith = new Hadith[cursor.getCount()];

		for (int i = 0; !cursor.isAfterLast(); i++) {
			String text = cursor.getString(cursor.getColumnIndex(HADITH_TEXT_COL));
			ahadith[i] = new Hadith(
					muhaddith,
					cursor.getInt(cursor.getColumnIndex(HADITH_NUM_COL)),
					text);
			cursor.moveToNext();
		}
		cursor.close();
		return Arrays.asList(ahadith);
	}
//===================================================

	private static String convertStringKeysToSQL(String[] keys) {
		StringBuilder sqlClause = new StringBuilder();

		for (int i = 0; i < keys.length; i++) {
			sqlClause.append(LIKE_CLAUSE).append(keys[i]).append(PERCENTAGE_SIGN);
			if (i < (keys.length - 1))
				sqlClause.append(AND);
		}

		sqlClause.trimToSize();
		return sqlClause.toString();
	}
//===================================================

	private static String convertIntKeysToSQL(Integer[] keys) {
		StringBuilder sqlClause = new StringBuilder();

		for (int i = 0; i < keys.length; i++) {
			sqlClause.append(keys[i]);
			if (i < (keys.length - 1))
				sqlClause.append(COMMA);
		}

		sqlClause.trimToSize();
		return sqlClause.toString();
	}
//===================================================
}
