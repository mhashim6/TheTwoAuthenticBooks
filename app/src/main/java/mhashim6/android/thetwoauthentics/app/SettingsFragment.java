package mhashim6.android.thetwoauthentics.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import mhashim6.android.thetwoauthentics.R;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		final CheckBoxPreference arabic = (CheckBoxPreference) findPreference("arabic_key");
		arabic.setOnPreferenceChangeListener((preference, newValue) -> {
			boolean currentValue = arabic.isChecked();
			SharedPreferences.Editor preferencesEditor = (PreferenceManager.getDefaultSharedPreferences(getActivity()).edit());
			preferencesEditor.putBoolean("arabic_key", !currentValue).apply();
			/*startActivity(new Intent(getActivity(), MainActivity.class));
			System.exit(0);*/
			((App) getActivity().getApplication()).initLocale(getActivity());
			getActivity().setResult(AppCompatActivity.RESULT_OK);
			getActivity().finish();
			return true;
		});
	}
}