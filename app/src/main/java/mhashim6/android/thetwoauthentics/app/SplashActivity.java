package mhashim6.android.thetwoauthentics.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	onFirstRun(); //TODO
		new OpenDbTask().execute(null, null, null);
	}

	/*private void onFirstRun() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("first_run", true)) {
			prefs.edit().putString("limit_key", "50").commit();
			prefs.edit().putBoolean("first_run", false).commit();
		}
	}*/

	private final class OpenDbTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			DatabasesLogic.getInstance(SplashActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

}