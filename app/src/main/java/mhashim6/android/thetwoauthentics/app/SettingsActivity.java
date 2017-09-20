package mhashim6.android.thetwoauthentics.app;

import android.os.Bundle;
import android.view.MenuItem;

import mhashim6.android.thetwoauthentics.R;

public class SettingsActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initToolBar(null,true);
		getFragmentManager().beginTransaction().replace(R.id.settingsActivity, new SettingsFragment()).commit();
	}
//===================================================

	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}
//===================================================

}