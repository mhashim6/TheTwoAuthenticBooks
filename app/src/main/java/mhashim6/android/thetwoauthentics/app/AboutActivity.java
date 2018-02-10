package mhashim6.android.thetwoauthentics.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import mhashim6.android.thetwoauthentics.R;

import static mhashim6.android.thetwoauthentics.app.Utils.APP_LINK;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		initToolBar(null, true);

		initVersion();

		initButtons();
	}
//===================================================

	private void initVersion() {
		try {
			AppCompatTextView version = findViewById(R.id.version_textView);

			version.setText(String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
		} catch (PackageManager.NameNotFoundException e) {
			//nothing.
		}
	}
//===================================================

	private void initButtons() {
		AppCompatButton xda, credits, developer, donate;
		xda = findViewById(R.id.xda_btn);
		xda.setOnClickListener(v -> openWebPage("https://forum.xda-developers.com/android/apps-games/app-al-sahihan-albukhari-muslim-t3640212"));

		credits = findViewById(R.id.credits_btn);
		credits.setOnClickListener(v -> openWebPage("https://github.com/mhashim6/Al-Sahihan/blob/master/CREDITS.md"));

		donate = findViewById(R.id.donate_btn);
		donate.setOnClickListener(v -> {
			Toast.makeText(AboutActivity.this, R.string.thanks, Toast.LENGTH_LONG).show();
			openWebPage("https://www.paypal.me/mhashim6");
		});

		developer = findViewById(R.id.developer_btn);
		developer.setOnClickListener(v -> openWebPage("https://github.com/mhashim6"));

	}
//===================================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem storeItem = menu.add(R.string.rate);
		storeItem.setIcon(R.drawable.ic_rate_review_white_18dp);
		storeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		storeItem.setOnMenuItemClickListener(item -> {
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=mhashim6.android.thetwoauthentics")));
			} catch (android.content.ActivityNotFoundException e) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_LINK)));
			}
			return true;
		});
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}
//===================================================

	public final void openWebPage(String url) {
		Utils.openWebPage(this, Uri.parse(url));
	}
}
