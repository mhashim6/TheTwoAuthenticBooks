package mhashim6.android.thetwoauthentics.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import static mhashim6.android.thetwoauthentics.app.Utils.WORKERS;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//	onFirstRun(); //TODO
		openDB();
	}

	/*private void onFirstRun() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("first_run", true)) {
			prefs.edit().putString("limit_key", "50").commit();
			prefs.edit().putBoolean("first_run", false).commit();
		}
	}*/

	private void openDB() {
		WORKERS.submit(() -> {
			DatabasesLogic.getInstance(SplashActivity.this);
			runOnUiThread(() -> {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
			});
		});
	}
}