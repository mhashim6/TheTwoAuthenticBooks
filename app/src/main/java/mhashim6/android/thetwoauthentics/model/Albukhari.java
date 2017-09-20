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
 * Created by mhashim6 on 23/06/2017.
 */

public final class Albukhari extends AbstractMuhaddith {
	private static Albukhari instance = null;

	private Albukhari(Context context) {
		openHelper = new AlBukhariDbOpenHelper(context);
	}
//===================================================

	public static Albukhari getInstance(Context context) {
		if (instance == null)
			instance = new Albukhari(context);
		return instance;
	}
//===================================================

	@Override
	public List<Hadith> findAhadithByStringKeys(int limit, String... keys) {
		return super.findAhadithByStringKeys(AbstractMuhaddith.ALBUKHARI, limit, keys);

	}
//===================================================

	/*@Override
	public Hadith findHadithByIntKey(int key) {
		return super.findHadithByIntKey(AbstractMuhaddith.ALBUKHARI, key);
	}
*/
	@Override
	public List<Hadith> findAhadithByIntKeys(Integer... keys) {
		return super.findAhadithByIntKeys(AbstractMuhaddith.ALBUKHARI, keys);
	}
//===================================================
}