package mhashim6.android.thetwoauthentics.app.results;

import android.content.Context;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import java.util.List;

import mhashim6.android.thetwoauthentics.R;
import mhashim6.android.thetwoauthentics.app.DatabasesLogic;
import mhashim6.android.thetwoauthentics.app.Utils;
import mhashim6.android.thetwoauthentics.model.Hadith;

import static mhashim6.android.thetwoauthentics.app.Utils.WORKERS;
import static mhashim6.android.thetwoauthentics.app.results.ResultsActivity.MAIN_THREAD;


/**
 * Created by mhashim6 on 02/07/2017.
 */

class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.HadithViewHolder> {

	private final DatabasesLogic DATABASES_MANAGER;

	private final List<Hadith> AHADITH;
	private final boolean IS_ARABIC;
	private final int TYPE;
//===================================================

	ResultsAdapter(final DatabasesLogic dbsManager,
				   final List<Hadith> ahadith, boolean isArabic, int type) {
		AHADITH = ahadith;
		DATABASES_MANAGER = dbsManager;
		IS_ARABIC = isArabic;
		TYPE = type;
	}
//===================================================

	@Override
	public HadithViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View hadithView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.hadith_view, parent, false);

		return new HadithViewHolder(hadithView);
	}
//===================================================

	@Override
	public void onBindViewHolder(final HadithViewHolder holder, int position) {
		final Hadith currentHadith = AHADITH.get(position);

		/*number*/
		holder.numTextView.setText(IS_ARABIC ?
				Utils.getArabicNumber(position + 1)
				: String.valueOf(position + 1));

		/*text*/
		final String text = currentHadith.text();
		holder.hadithTextView.setText(text);

		/*highlight*/
		WORKERS.execute(holder);

		/*menu*/
		holder.optionsBtn.setOnClickListener(holder);
	}
//===================================================

	@Override
	public int getItemCount() {
		return AHADITH.size();
	}
//===================================================

	final class HadithViewHolder extends RecyclerView.ViewHolder
			implements Runnable, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

		final AppCompatTextView hadithTextView;
		final AppCompatTextView numTextView;
		final AppCompatImageButton optionsBtn;
		private long snackLastClickTime = 0;

		HadithViewHolder(View hadithView) {
			super(hadithView);

			numTextView = hadithView.findViewById(R.id.numTextView);
			hadithTextView = hadithView.findViewById(R.id.hadithTextView);
			optionsBtn = hadithView.findViewById(R.id.more_btn);
		}
//===================================================

		@Override
		public void run() { //runnable to highlight text in a WORKERS' thread.
			final int position = getAdapterPosition();
			final Hadith currentHadith = AHADITH.get(position);
			final String text = currentHadith.text();

			Utils.HighlightedText highlightedText = Utils.getQuranHighlightedText(text);
			if (highlightedText.isHighlighted())
				MAIN_THREAD.post(() -> hadithTextView.setText(highlightedText.text()));
		}
//===================================================

		@Override
		public void onClick(View view) { //option button action.
			Context context = itemView.getContext();

			PopupMenu popup = new PopupMenu(context, view);
			Menu menu = popup.getMenu();
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.menu_hadith_options, menu);

			if (TYPE == ResultsActivity.RESULTS)
				menu.add(Menu.NONE, R.id.save_item, Menu.NONE, R.string.save);
			else
				menu.add(Menu.NONE, R.id.remove_item, Menu.NONE, R.string.remove);

			popup.setOnMenuItemClickListener(HadithViewHolder.this);
			popup.show();
		}
//===================================================

		@Override
		public boolean onMenuItemClick(MenuItem item) { //options menu.
			final int position = getAdapterPosition();
			final Hadith currentHadith = AHADITH.get(position);
			Context context = itemView.getContext();

			switch (item.getItemId()) {
				case R.id.copy_item:
					Utils.copyHadith(context, currentHadith);
					break;

				case R.id.share_item:
					Utils.shareHadith(context, currentHadith);
					break;

				case R.id.lookup_item:
					WORKERS.execute(() -> Utils.webSearchHadith(context, currentHadith));
					break;

				case R.id.save_item:
					saveHadith(context, currentHadith);
					break;

				case R.id.remove_item:
					removeHadith(context, currentHadith, position);
					break;
			}
			return true;
		}

		private void saveHadith(Context context, Hadith hadith) {
			WORKERS.execute(() -> DATABASES_MANAGER.saveHadith(hadith));
			makeSnackBar((ResultsActivity) context, R.string.saved).show();
		}

		private void removeHadith(Context context, Hadith hadith, int position) {
			makeSnackBar((ResultsActivity) context, R.string.confirm)
					.setAction(R.string.yes, v -> {

						//check for weird users
						if (SystemClock.elapsedRealtime() - snackLastClickTime < 1000)
							return;
						snackLastClickTime = SystemClock.elapsedRealtime();

						AHADITH.remove(position);
						notifyItemRemoved(position);
						notifyItemRangeChanged(position, getItemCount());
						WORKERS.execute(() -> DATABASES_MANAGER.removeSavedHadith(hadith));
					}).show();
		}
	}
//===================================================

	private static Snackbar makeSnackBar(AppCompatActivity activity, int textId) {
		return Snackbar.make(activity.findViewById(R.id.recycler_view),
				textId,
				Snackbar.LENGTH_LONG);
	}
}