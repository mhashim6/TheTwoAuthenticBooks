package mhashim6.android.thetwoauthentics.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import mhashim6.android.thetwoauthentics.R;
import mhashim6.android.thetwoauthentics.app.results.ResultsWrapper;

import static mhashim6.android.thetwoauthentics.app.results.ResultsActivity.RESULTS;
import static mhashim6.android.thetwoauthentics.app.results.ResultsActivity.SAVED;

public class MainActivity extends BaseActivity {

	private SearchView searchView;
	private AppCompatImageButton searchBtn;

	private DatabasesLogic databasesLogic;

	private String lastQuery;
	private int limit = 25;
//===================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		databasesLogic = DatabasesLogic.getInstance(this);

		initAppUpdater();

		initToolBar(null, false);

		//	initSpinner();
		initSearchView();
	}
//===================================================

	@Override
	protected void onResume() {
		super.onResume();
		Utils.WORKERS.execute(() -> {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			String limitString = preferences.getString("limit_key", "50");
			limit = Integer.parseInt(limitString) / 2;
		});
	}
//===================================================

	private void initSearchView() {
		searchView = findViewById(R.id.search_view);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchInBackground(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});

		searchBtn = findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(v -> searchInBackground(searchView.getQuery().toString()));
	}
//===================================================

	private void initAppUpdater() {
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("update_key", true)) {
			AppUpdater appUpdater = new AppUpdater(this)
					.setUpdateFrom(UpdateFrom.XML)
					.setUpdateXML("https://raw.githubusercontent.com/mhashim6/Al-Sahihan/master/update.xml")
					.setDisplay(Display.DIALOG)
					.showEvery(1)
					.setTitleOnUpdateAvailable(R.string.update_available)
					.setContentOnUpdateAvailable(R.string.update_advice)
					.setButtonUpdate(R.string.update_now)
					.setButtonDismiss(R.string.update_later)
					.setButtonDoNotShowAgain(null);
			appUpdater.start();
		}
	}
//===================================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem settings = menu.findItem(R.id.settings_item);
		settings.setOnMenuItemClickListener(item -> {
			startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), Utils.LANGUAGE_CHANGED);
			return true;
		});

		MenuItem saved = menu.findItem(R.id.saved_item);
		saved.setOnMenuItemClickListener(item -> {
			new SavedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
			return true;
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == AppCompatActivity.RESULT_OK) {
			recreate();
		}
	}
//===================================================

	private void initSaved() {
		ResultsWrapper saved = databasesLogic.getSavedAhadith();
		if (!saved.isEmpty())
			Utils.startResultsActivity(MainActivity.this, SAVED);
		else
			makeSnackBar(R.string.empty_saved).show();
	}
//===================================================

	private void searchInBackground(String query) {
		if (!"".equals(query.trim())) {
			new SearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
		}
	}
//===================================================

	private Snackbar makeSnackBar(int textId) {
		return makeSnackBar(getResources().getString(textId));
	}

	private Snackbar makeSnackBar(String text) {
		hideKeyboard();
		return Snackbar.make(findViewById(R.id.main_layout),
				text,
				Snackbar.LENGTH_LONG);
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
	}
//===================================================

	private class SearchTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			hideKeyboard();
			searchBtn.setEnabled(false);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(String... params) {
			search(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			searchBtn.setEnabled(true);
			progressBar.setVisibility(View.INVISIBLE);
		}
//===================================================

		private void search(final String query) {
			lastQuery = query;

			ResultsWrapper results = databasesLogic.search(query, limit);
			if (!results.isEmpty())
				Utils.startResultsActivity(MainActivity.this, RESULTS);
			else
				yellAtUser();
		}

		private void yellAtUser() {
			Snackbar sb = makeSnackBar(getResources().getString(R.string.no_results));
			sb.setAction(R.string.sunnah_search, v -> Utils.webSearchQuery(MainActivity.this, lastQuery));
			sb.show();
		}
	}

	private class SavedTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			initSaved();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
}