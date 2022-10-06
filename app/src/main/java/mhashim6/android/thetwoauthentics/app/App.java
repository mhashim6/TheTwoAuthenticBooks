package mhashim6.android.thetwoauthentics.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import mhashim6.android.thetwoauthentics.R;

/**
 * Created by mhashim6 on 12/07/2017.
 */

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		ViewPump.init(ViewPump.builder()
				.addInterceptor(new CalligraphyInterceptor(
						new CalligraphyConfig.Builder()
								.setDefaultFontPath("fonts/calibri.ttf")
								.setFontAttrId(R.attr.fontPath)
								.build()))
				.build());

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
		initLocale(getBaseContext());
	}

	public static void initLocale(@NonNull Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean forceArabic = preferences.getBoolean("arabic_key", false);

		final Locale locale;
		if (forceArabic)
			locale = new Locale("ar", "EG");
		else
			locale = Resources.getSystem().getConfiguration().locale;

		setLocale(context, locale);
		/*final Context appContext = context.getApplicationContext();
		if (context != appContext) {
			setLocale(appContext, locale);
		}*/
	}

	private static void setLocale(@NonNull Context context, @NonNull Locale locale) {
		final Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		config.locale = locale;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			config.setLayoutDirection(config.locale);
		}
		resources.updateConfiguration(config, resources.getDisplayMetrics());
	}
}