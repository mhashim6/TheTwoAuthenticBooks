/*
* Android Hadith Project.
* This program is free software: you can redistribute it and/or modify it under
* the terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see <http://www.gnu.org/licenses/>.
*
* you can contact me -> abohashem.com@gmail.com
*
*/


package mhashim6.android.thetwoauthentics.model;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by mhashim6 on 26/06/2017.
 */

class MuslimDbOpenHelper extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "muslim.db";
	private static final int DATABASE_VERSION = 5;
//===================================================

	public MuslimDbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		setForcedUpgrade();
	}
//===================================================
}
