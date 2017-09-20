package mhashim6.android.thetwoauthentics.app;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import mhashim6.android.thetwoauthentics.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
