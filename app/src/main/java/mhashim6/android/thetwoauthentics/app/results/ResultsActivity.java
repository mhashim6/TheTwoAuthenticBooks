package mhashim6.android.thetwoauthentics.app.results;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hotchemi.android.rate.AppRate;
import mhashim6.android.thetwoauthentics.R;
import mhashim6.android.thetwoauthentics.app.BaseActivity;
import mhashim6.android.thetwoauthentics.app.DatabasesLogic;
import mhashim6.android.thetwoauthentics.app.SettingsActivity;
import mhashim6.android.thetwoauthentics.app.Utils;

import static mhashim6.android.thetwoauthentics.model.Muhaddith.ALBUKHARI;
import static mhashim6.android.thetwoauthentics.model.Muhaddith.MUSLIM;


public class ResultsActivity extends BaseActivity {

	public final static String RESULTS_TYPE_KEY = "RESULTS_TYPE_KEY";
	public static final int RESULTS = 5;
	public static final int SAVED = 15;
//===================================================

	private int dataType;
	private ResultsWrapper resultsWrapper;

	public static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		initInfo();
		initTabs();
		showNumber();

		initRate();
	}
//===================================================

	private void initInfo() {
		ResultsWrapper probableResults = ResultsHolder.getInstance().getResults();
		ResultsWrapper probableSaved = ResultsHolder.getInstance().getSaved();

		dataType = getIntent().getIntExtra(RESULTS_TYPE_KEY, SAVED);
		resultsWrapper = (dataType == RESULTS) ? probableResults : probableSaved;

		checkForTraitors();

		String title = null;
		String query = resultsWrapper.query();

		switch (resultsWrapper.searchType()) {
			case ResultsWrapper.SEARCH:
				title = query;
				break;

			case ResultsWrapper.NO_SEARCH_TYPE:
				title = getResources().getString(R.string.saved_title);
				break;
		}
		initToolBar(title, true);
	}

	private void checkForTraitors() {
		if (resultsWrapper.isEmpty()) //when activity gets killed because of low memory.
			finish();
	}
//==================================================

	private void showNumber() {
		if (dataType == RESULTS)
			MAIN_THREAD.postDelayed(() -> {
				int number = resultsWrapper.number();
				showSnackBar(isArabic() ? Utils.getArabicNumber(number) : String.valueOf(number));
			}, 500);
	}
//==================================================

	private void initRate() {
		AppRate.with(this)
				.setInstallDays(0)
				.setLaunchTimes(1)
				.setRemindInterval(1)
				.setDebug(false)
				.monitor();
		AppRate.showRateDialogIfMeetsConditions(this);
	}
//===================================================

	private void initTabs() {
		TabLayout tabLayout;
		ViewPager viewPager = findViewById(R.id.viewpager);
		initViewPager(viewPager);
		tabLayout = findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
	}
//===================================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem settings;
		if (dataType == SAVED)
			settings = menu.add(R.string.preferences).setIcon(R.drawable.ic_settings_white_18dp)
					.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		else {
			getMenuInflater().inflate(R.menu.menu_main, menu);

			MenuItem saved = menu.findItem(R.id.saved_item);
			saved.setOnMenuItemClickListener(item -> {
				new SavedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
				return true;
			});
			settings = menu.findItem(R.id.settings_item);
		}

		settings.setOnMenuItemClickListener(item -> {
			startActivityForResult(new Intent(ResultsActivity.this, SettingsActivity.class), Utils.LANGUAGE_CHANGED);
			return true;
		});

		return super.onCreateOptionsMenu(menu);
	}
//===================================================

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
//===================================================

	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}
//===================================================

	private void initSaved() {
		ResultsWrapper saved = DatabasesLogic.getInstance(this).getSavedAhadith();
		if (!saved.isEmpty())
			Utils.startResultsActivity(ResultsActivity.this, SAVED);
		else
			showSnackBar(R.string.empty_saved);
	}
//===================================================

	private void showSnackBar(int textId) {
		showSnackBar(getResources().getString(textId));
	}

	private void showSnackBar(String text) {
		Snackbar.make(findViewById(R.id.results_layout),
				text,
				Snackbar.LENGTH_LONG).show();
	}
//===================================================

	private void initViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		boolean isArabic = isArabic();

		if (!resultsWrapper.bukhariData().isEmpty()) {
			ResultsFragment albukhariFragment = new ResultsFragment();
			Bundle albukhariArgs = new Bundle();
			albukhariArgs.putInt(ResultsFragment.MUHADDITH_KEY, ALBUKHARI);
			albukhariArgs.putInt(RESULTS_TYPE_KEY, dataType);
			albukhariArgs.putBoolean(ResultsFragment.ARABIC_KEY, isArabic);
			albukhariFragment.setArguments(albukhariArgs);
			adapter.addFragment(albukhariFragment, getResources().getString(R.string.sahih_albukhari));
		}

		if (!resultsWrapper.muslimData().isEmpty()) {
			ResultsFragment muslimFragment = new ResultsFragment();
			Bundle muslimArgs = new Bundle();
			muslimArgs.putInt(ResultsFragment.MUHADDITH_KEY, MUSLIM);
			muslimArgs.putInt(RESULTS_TYPE_KEY, dataType);
			muslimArgs.putBoolean(ResultsFragment.ARABIC_KEY, isArabic);
			muslimFragment.setArguments(muslimArgs);
			adapter.addFragment(muslimFragment, getResources().getString(R.string.sahih_muslim));
		}
		viewPager.setAdapter(adapter);
	}
//==================================================

	private boolean isArabic() {
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("arabic_key", false) ||
				Utils.ARABIC_CODE.equals(Locale.getDefault().getLanguage());
	}
//==================================================

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
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
