package mhashim6.android.thetwoauthentics.app;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import mhashim6.android.thetwoauthentics.app.results.ResultsHolder;
import mhashim6.android.thetwoauthentics.app.results.ResultsWrapper;
import mhashim6.android.thetwoauthentics.model.Albukhari;
import mhashim6.android.thetwoauthentics.model.Hadith;
import mhashim6.android.thetwoauthentics.model.Muhaddith;
import mhashim6.android.thetwoauthentics.model.Muslim;
import mhashim6.android.thetwoauthentics.model.saved.SavedAccess;

import static mhashim6.android.thetwoauthentics.app.Utils.WORKERS;
import static mhashim6.android.thetwoauthentics.app.results.ResultsWrapper.NO_SEARCH_TYPE;

/**
 * Created by mhashim6 on 12/07/2017.
 */

public class DatabasesLogic {

	private static DatabasesLogic instance;

	private Albukhari ALBUKHARI;
	private Muslim MUSLIM;

	private SavedAccess savedAccess;
//===================================================

	private DatabasesLogic(Context context) {
		init(context);
	}
//===================================================

	public static DatabasesLogic getInstance(Context context) {
		if (instance == null)
			instance = new DatabasesLogic(context);
		return instance;
	}
//===================================================

	private void init(Context context) {
		savedAccess = SavedAccess.getInstance(context);
		savedAccess.open();

		ALBUKHARI = Albukhari.getInstance(context);
		ALBUKHARI.open();

		MUSLIM = Muslim.getInstance(context);
		MUSLIM.open();
	}
//===================================================

	public final ResultsWrapper getSavedAhadith() {
		int[] savedNumbers = savedAccess.getAllSavedAhadith();
		if (savedNumbers.length == 0) return ResultsHolder.EMPTY_RESULTS;

		ArrayList<Integer> bukhariSaved = new ArrayList<>();
		ArrayList<Integer> muslimSaved = new ArrayList<>();

		for (int number : savedNumbers)
			if (number > 0)
				bukhariSaved.add(number);
			else
				muslimSaved.add(-number);

		return loadSavedAhadithFromNums(bukhariSaved, muslimSaved);
	}

	private ResultsWrapper loadSavedAhadithFromNums(List<Integer> bukhariSaved, List<Integer> muslimSaved) {
		final List<Hadith> bukhari = new ArrayList<>();
		final List<Hadith> muslim = new ArrayList<>();

		try {
			Future<List<Hadith>> bukhariFuture = WORKERS.submit(
					new SavedCallable(ALBUKHARI, bukhariSaved));

			Future<List<Hadith>> muslimFuture = WORKERS.submit(
					new SavedCallable(MUSLIM, muslimSaved));

			bukhari.addAll(bukhariFuture.get());
			muslim.addAll(muslimFuture.get());

		} catch (InterruptedException e) {
			//unlikely
		} catch (ExecutionException e) {
			//unlikely
		}

		ResultsWrapper saved = new ResultsWrapper(bukhari, muslim, null, NO_SEARCH_TYPE);
		ResultsHolder.getInstance().setSaved(saved);
		return saved;
	}

	public final void saveHadith(Hadith hadith) {
		savedAccess.saveHadith(hadith);
	}

	public final void removeSavedHadith(Hadith hadith) {
		savedAccess.removeHadith(hadith);
	}
//===================================================

	public final ResultsWrapper search(String query, final int[] muhaddithin, int limit) {

		query = Utils.cleanQuery(query);
		String queryToSearch = Utils.formatQuery(query);
		String[] queryArray = Utils.splitQuery(queryToSearch);

		final List<Hadith> bukhari = new ArrayList<>();
		final List<Hadith> muslim = new ArrayList<>();

		try {
			if (muhaddithin.length > 1) {
				Future<List<Hadith>> bukhariFuture = WORKERS.submit(
						new SearchingCallable(ALBUKHARI, queryArray, limit));

				Future<List<Hadith>> muslimFuture = WORKERS.submit(
						new SearchingCallable(MUSLIM, queryArray, limit));

				bukhari.addAll(bukhariFuture.get());
				muslim.addAll(muslimFuture.get());

			} else {
				switch (muhaddithin[0]) {
					case Muhaddith.ALBUKHARI:
						bukhari.addAll(ALBUKHARI.findAhadithByStringKeys(limit, queryArray));
						break;

					case Muhaddith.MUSLIM:
						muslim.addAll(MUSLIM.findAhadithByStringKeys(limit, queryArray));
						break;
				}
			}
		} catch (InterruptedException e) {
			//unlikely
		} catch (ExecutionException e) {
			//unlikely
		}

		final String finalQuery = query;
		ResultsWrapper results = new ResultsWrapper(bukhari, muslim, finalQuery, ResultsWrapper.SEARCH);
		ResultsHolder.getInstance().setResults(results);
		return results;
	}
//===================================================

	private static final class SearchingCallable implements Callable<List<Hadith>> {

		private final Muhaddith muhaddith;
		private final String[] query;
		private final int limit;

		SearchingCallable(Muhaddith muhaddith, String[] query, int limit) {
			this.muhaddith = muhaddith;
			this.query = query;
			this.limit = limit;
		}

		@Override
		public List<Hadith> call() throws Exception {
			return muhaddith.findAhadithByStringKeys(limit, query);
		}
	}

	private static final class SavedCallable implements Callable<List<Hadith>> {

		private final Muhaddith muhaddith;
		private final List<Integer> savedNums;

		SavedCallable(Muhaddith muhaddith, List<Integer> savedNums) {
			this.muhaddith = muhaddith;
			this.savedNums = savedNums;
		}

		@Override
		public List<Hadith> call() throws Exception {
			final List<Hadith> results = new ArrayList<>();
			results.addAll(muhaddith.findAhadithByIntKeys(savedNums.toArray(new Integer[savedNums.size()])));
			Collections.sort(results, (h1, h2) ->
					savedNums.indexOf(h2.number()) - savedNums.indexOf(h1.number()));
			return results;
		}
	}
}
