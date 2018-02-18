package mhashim6.android.thetwoauthentics.app.results;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import mhashim6.android.thetwoauthentics.model.Hadith;

/**
 * Created by mhashim6 on 12/08/2017.
 */

public class ResultsWrapper {

	public static final int SEARCH = 7;
	public static final int SAVED = -27;
//===================================================

	private final List<Hadith> bukhari, muslim;
	private final String query;
	private final int resultsType;

	public ResultsWrapper(List<Hadith> bukhari, List<Hadith> muslim, @Nullable String query, int resultsType) {
		this.bukhari = bukhari;
		this.muslim = muslim;
		this.query = query;
		this.resultsType = resultsType;
	}

	List<Hadith> bukhariList() {
		return bukhari == null ? Collections.emptyList() : bukhari;
	}

	List<Hadith> muslimList() {
		return muslim == null ? Collections.emptyList() : muslim;
	}

	String query() {
		return query;
	}

	int resultsType() {
		return resultsType;
	}

	public int number() {
		return bukhariList().size() + muslimList().size();
	}

	public boolean isEmpty() {
		return bukhariList().isEmpty() && muslimList().isEmpty();
	}

}
