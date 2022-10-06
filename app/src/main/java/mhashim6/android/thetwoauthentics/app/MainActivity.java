package mhashim6.android.thetwoauthentics.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import mhashim6.android.thetwoauthentics.R;
import mhashim6.android.thetwoauthentics.app.results.ResultsWrapper;

import static mhashim6.android.thetwoauthentics.app.Utils.WORKERS;
import static mhashim6.android.thetwoauthentics.app.results.ResultsActivity.SEARCH;
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
			startSavedActivity();
			return true;
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == AppCompatActivity.RESULT_OK) {
			recreate();
		}
	}
//===================================================

	private void startSavedActivity() {
		progressBar.setVisibility(View.VISIBLE);
		WORKERS.submit(() -> {
			ResultsWrapper saved = databasesLogic.getSavedAhadith();

			runOnUiThread(() -> {
				progressBar.setVisibility(View.GONE);

				if (!saved.isEmpty())
					Utils.startResultsActivity(MainActivity.this, SAVED);
				else
					makeSnackBar(R.string.empty_saved).show();
			});
		});
	}
//===================================================

	private void searchInBackground(String query) {
		if (!"".equals(query.trim()))
			search(query);
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

	private void search(final String query) {

		hideKeyboard();
		searchBtn.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);
		WORKERS.submit(() -> {
			lastQuery = query;

			ResultsWrapper results = databasesLogic.search(query, limit);
			runOnUiThread(() -> {
				if (!results.isEmpty())
					Utils.startResultsActivity(MainActivity.this, SEARCH);
				else
					yellAtUser();

				searchBtn.setEnabled(true);
				progressBar.setVisibility(View.INVISIBLE);
			});
		});
	}

	private void yellAtUser() {
		Snackbar sb = makeSnackBar(getResources().getString(R.string.no_results));
		sb.setAction(R.string.sunnah_search, v -> Utils.webSearchQuery(MainActivity.this, lastQuery));
		sb.show();
	}
}
