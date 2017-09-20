package mhashim6.android.thetwoauthentics.model;

import java.util.List;

/**
 * Created by mhashim6 on 05/07/2017.
 */

public interface Muhaddith {
	int ALBUKHARI = 7;
	int MUSLIM = -7;

	List<Hadith> findAhadithByStringKeys(int limit, String... keys);

	//Hadith findHadithByIntKey(int key);

	List<Hadith> findAhadithByIntKeys(Integer... keys);
}
