package mhashim6.android.thetwoauthentics.app.results;


import static mhashim6.android.thetwoauthentics.app.results.ResultsWrapper.NO_SEARCH_TYPE;


/**
 * Created by mhashim6 on 12/08/2017.
 */

public class ResultsHolder {

	private static ResultsHolder instance = null;

	private ResultsWrapper results, saved;
	public static final ResultsWrapper EMPTY_RESULTS =
			new ResultsWrapper(null, null, null, NO_SEARCH_TYPE);

	private ResultsHolder() {

	}

	public static ResultsHolder getInstance() {
		if (instance == null)
			instance = new ResultsHolder();
		return instance;
	}

	public ResultsWrapper getResults() {
		return results == null ? EMPTY_RESULTS : results;
	}

	public void setResults(ResultsWrapper results) {
		this.results = results;
	}

	public ResultsWrapper getSaved() {
		return saved == null ? EMPTY_RESULTS : saved;
	}

	public void setSaved(ResultsWrapper saved) {
		this.saved = saved;
	}

}
