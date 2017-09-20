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

import java.util.List;

/**
 * Created by mhashim6 on 26/06/2017.
 */

public final class Muslim extends AbstractMuhaddith {
	private static Muslim instance = null;

	private Muslim(Context context) {
		openHelper = new MuslimDbOpenHelper(context);
	}
//===================================================

	public static Muslim getInstance(Context context) {
		if (instance == null)
			instance = new Muslim(context);
		return instance;
	}
//===================================================

	@Override
	public List<Hadith> findAhadithByStringKeys(int limit, String... keys) {
		return super.findAhadithByStringKeys(Muhaddith.MUSLIM, limit, keys);
	}
//===================================================

/*	@Override
	public Hadith findHadithByIntKey(int key) {
		return super.findHadithByIntKey(Muhaddith.MUSLIM, key);
	}
*/
	@Override
	public List<Hadith> findAhadithByIntKeys(Integer... keys) {
		return super.findAhadithByIntKeys(Muhaddith.MUSLIM, keys);
	}
//===================================================
}
