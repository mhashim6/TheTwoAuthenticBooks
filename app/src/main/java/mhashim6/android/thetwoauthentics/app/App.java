package mhashim6.android.thetwoauthentics.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.Locale;

import mhashim6.android.thetwoauthentics.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by mhashim6 on 12/07/2017.
 */

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/calibri.ttf")
				.setFontAttrId(R.attr.fontPath)
				.build()
		);

		initLocale(getApplicationContext());

	/*	Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			Log.d("hi", "lol");
			Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			Process.killProcess(Process.myPid());
		});*/
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initLocale(getApplicationContext());
	}

	public void initLocale(@NonNull Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean forceArabic = preferences.getBoolean("arabic_key", false);

		final Locale locale;
		if (forceArabic)
			locale = new Locale("ar");
		else
			locale = Resources.getSystem().getConfiguration().locale;

		setLocale(context, locale);
		/*final Context appContext = context.getApplicationContext();
		if (context != appContext) {
			setLocale(appContext, locale);
		}*/
	}

	private void setLocale(@NonNull Context context, @NonNull Locale locale) {
		final Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		config.locale = locale;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			config.setLayoutDirection(config.locale);
		}
		resources.updateConfiguration(config, resources.getDisplayMetrics());
	}
}