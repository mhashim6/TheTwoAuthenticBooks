package mhashim6.android.thetwoauthentics.app;

import android.content.Context;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ProgressBar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import mhashim6.android.thetwoauthentics.R;

/**
 * Created by mhashim6 on 19/09/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

	protected ProgressBar progressBar;

	protected void initToolBar(String title, boolean setDisplayHomeAsUpEnabled) {
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(title);
		toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.offwhite, null));
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(setDisplayHomeAsUpEnabled);
		progressBar = findViewById(R.id.progress_bar);
	}
//===================================================

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
		App.initLocale(this);
	}
}
