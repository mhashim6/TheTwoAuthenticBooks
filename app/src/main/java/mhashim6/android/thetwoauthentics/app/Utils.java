package mhashim6.android.thetwoauthentics.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mhashim6.android.thetwoauthentics.R;
import mhashim6.android.thetwoauthentics.app.results.ResultsActivity;
import mhashim6.android.thetwoauthentics.model.Hadith;
import mhashim6.android.thetwoauthentics.model.Muhaddith;

import static mhashim6.android.thetwoauthentics.app.results.ResultsActivity.RESULTS_TYPE_KEY;


/**
 * Created by mhashim6 on 07/07/2017.
 */

public final class Utils {

	public static final ExecutorService WORKERS = Executors.newFixedThreadPool(5);

	public static final int LANGUAGE_CHANGED = 7;
//===================================================

	private static final NumberFormat ARABIC_NUMBER_FORMAT = NumberFormat.getInstance(new Locale("ar", "EG"));
	private static final String HADITH_LABEL = "Hadith";
	public static final String ARABIC_CODE = "ar";
	private static final String HADITH_FORMAT = "%s\n\n%s.\n\n%s\n%s";
	private static final String TEXT_TYPE = "text/plain";
//===================================================

	//	private static final Pattern HIGHLIGHT_PATTERN = Pattern.compile("\\s{2}(\\p{InArabic}+)\\s{2}");
	private static final String TASHKEEL_REGEX = "[\u0650\u0651\u0652\u064F\u064B\u064C\u064D\u064E]";
	private static final String HAMAZAT_REGEX = "[\u0622\u0623\u0625]";
	private static final String YAA_REGEX = "[\u0649\uFEEF\uFEF0\u06CC]";
	private static final String QUOTES_REGEX = "([^\"]\\S*|\".+?\")\\s*";
	private static final Pattern QUOTES_PATTERN = Pattern.compile(QUOTES_REGEX);
	private static final String ARABIC_ALEF = String.valueOf((char) Integer.parseInt("0627", 16));
	private static final String ARABIC_YAA = String.valueOf((char) Integer.parseInt("064A", 16));
//	private static final String HAA = String.valueOf((char) Integer.parseInt("0647", 16));
//	private static final String TAA = String.valueOf((char) Integer.parseInt("0629", 16));
//===================================================

	private static final String GOOGLE = "https://www.google.com/search?q=";
	private static final String SUNNAH = "https://www.sunnah.com/search?q=";
//===================================================

	private static final String EMPTY_STRING = "";
	private static final String WHITE_SPACE = " ";
	private static final String MULTIPLE_SPACES = " +";
	private static final String PLUS_SIGN = "+";
	private static final String SLASH = " | ";
	private static final String CURLY_START = "{";
	private static final String CURLY_END = "}";
	private static final String QUOTE = "\"";
//===================================================

	//	private static final int HIGHLIGHT_COLOR = Color.parseColor("#a9473f");
	private static final int QURAN_HIGHLIGHT_COLOR = Color.parseColor("#616161");
	private static final int PRIMARY_COLOR = Color.parseColor("#8E2E30");
//===================================================

	static final String APP_LINK = "https://play.google.com/store/apps/details?id=mhashim6.android.thetwoauthentics";
	private static final String APP_DESCRIPTION = "الصحيحان: البخاري ومسلم";
//===================================================

	private static final CustomTabsIntent customTabsIntent;

	static {
		CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
		builder.setToolbarColor(PRIMARY_COLOR);
		customTabsIntent = builder.build();
	}
//===================================================

	public static void startResultsActivity(AppCompatActivity activity, int type) {
		Intent i = new Intent(activity, ResultsActivity.class);
		i.putExtra(RESULTS_TYPE_KEY, type);
		activity.startActivityForResult(i, Utils.LANGUAGE_CHANGED);
	}
//===================================================

	public static String[] splitQuery(String query) {
		List<String> tokens = new ArrayList<>();
		Matcher m = QUOTES_PATTERN.matcher(query);
		while (m.find())
			tokens.add(m.group(1).replace(QUOTE, EMPTY_STRING));

		return tokens.toArray(new String[tokens.size()]);
	}

	public static String cleanQuery(String query) {
		return query.trim().replaceAll(MULTIPLE_SPACES, WHITE_SPACE);
	}

	public static String formatQuery(String query) {
		return query.replaceAll(HAMAZAT_REGEX, ARABIC_ALEF).
				replaceAll(YAA_REGEX, ARABIC_YAA);
		//.replaceAll(TAA, HAA);
	}

	/*public static String replaceSpacesWithSlash(String query) {
		return query == null? null : query.trim().replaceAll(MULTIPLE_SPACES, SLASH);
	}*/

	public static String joinQuery(String[] query) {
		if (query == null)
			return null;

		StringBuilder sb = new StringBuilder();
		for (String token : query)
			sb.append(token).append(SLASH);

		int length = sb.length();
		return sb.delete(length - 2, length).toString();
	}

	/*public static String removeQuotes(String query) {
		return query.trim().replace(QUOTE, EMPTY_STRING);
	}*/
//===================================================

	public static String getArabicNumber(int number) {
		return ARABIC_NUMBER_FORMAT.format(number);
	}
//===================================================

	public static void copyHadith(Context context, Hadith hadith) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText(HADITH_LABEL, formatHadith(context, hadith));
		if (clipboard != null)
			clipboard.setPrimaryClip(clip);
	}
//===================================================

	public static void shareHadith(Context context, Hadith hadith) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, formatHadith(context, hadith));
		sendIntent.setType(TEXT_TYPE);
		context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_with)));
	}

	/*public static final void shareImage(Context context, View hadithView) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/png");
		String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(),
				loadBitmapFromView(hadithView), "hadithTitle", null); //TODO
		Uri bitmapUri = Uri.parse(bitmapPath);
		intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);

		context.startActivity(Intent.createChooser(intent, "Share"));
	}

	private static Bitmap loadBitmapFromView(View v) {
		Bitmap bitmap;
		v.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v.getDrawingCache());
		v.setDrawingCacheEnabled(false);
		return bitmap;
	}*/
//===================================================

	public static void webSearchQuery(Context context, String query) {
		customTabsIntent.launchUrl(context, Uri.parse(SUNNAH + query));
	}

	public static void webSearchHadith(Context context, Hadith hadith) {
		customTabsIntent.launchUrl(context, WebSearch.getSearchUri(hadith, GOOGLE));
	}

	/*public final void webTranslateHadith(Context context, Hadith hadith) {
		customTabsIntent.launchUrl(context, WebSearch.getSearchUri(hadith, SUNNAH));
	}*/
//===================================================

	private static String formatHadith(Context context, Hadith hadith) {
		String text = hadith.text();

		return String.format(HADITH_FORMAT, text,
				(hadith.muhaddith() == Muhaddith.ALBUKHARI ?
						context.getString(R.string.sahih_albukhari) : context.getString(R.string.sahih_muslim)), APP_DESCRIPTION, APP_LINK);
	}
//===================================================
	/*public static SpannableStringBuilder getHighlightedText(String origString){
	List<ExtractedResult> results = FuzzySearch.extractAll("الاناه", Arrays.asList(origString.split(WHITE_SPACE)),90);

		int index, length;
		for (ExtractedResult r : results){
			index = origString.indexOf(r.getString());
			length = r.getString().length();
			spBuilder.setSpan(QURAN_COLOR_SPAN,
					index,
					index + length,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}*/

	public static HighlightedText getQuranHighlightedText(String origString) {
		SpannableStringBuilder spBuilder = null;
		boolean isHighlighted = false;

		int index = origString.indexOf(CURLY_START);
		if (index > -1) {
			spBuilder = new SpannableStringBuilder(origString);
			int end = origString.indexOf(CURLY_END);
			while (index >= 0) { //if the word is repeated

				spBuilder.setSpan(new ForegroundColorSpan(QURAN_HIGHLIGHT_COLOR),
						index,
						end + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				index = origString.indexOf(CURLY_START, index + 1);
				end = origString.indexOf(CURLY_END, end + 1);
			}
			isHighlighted = true;
		}
		boolean finalIsHighlighted = isHighlighted;
		SpannableStringBuilder finalSpBuilder = spBuilder;

		return new HighlightedText() {
			@Override
			public SpannableStringBuilder text() {
				return finalSpBuilder;
			}

			@Override
			public boolean isHighlighted() {
				return finalIsHighlighted;
			}
		};
	}

/*	public static SpannableStringBuilder getHighlightedText(String origString) {
		List<String> stringsToHighlight = getStringsToHighlight(origString, HIGHLIGHT_PATTERN);

		SpannableStringBuilder spBuilder = getQuranHighlightedText(origString); //Highlight quran first.

		for (String text : stringsToHighlight) {
			int index = origString.indexOf(text);

			while (index >= 0) { //if the word is repeated
				spBuilder.setSpan(new ForegroundColorSpan(HIGHLIGHT_COLOR),
						index,
						index + text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				index = origString.indexOf(text, index + 1);
			}
		}
		return spBuilder;
	}*/
//===================================================

/*	private static List<String> getStringsToHighlight(String origString, Pattern pattern) {
		ArrayList<String> strings = new ArrayList<>();

		Matcher m = pattern.matcher(origString);
		while (m.find()) {
			String found = m.group(1);
			if (found.length() > 3)
				strings.add(found);
		}
		return strings;
	}*/
//===================================================

	private static String removeTashkeel(String text) {
		return text.replaceAll(TASHKEEL_REGEX, EMPTY_STRING);
	}
//===================================================

	public interface HighlightedText {
		SpannableStringBuilder text();

		boolean isHighlighted();
	}

	private static class WebSearch {

		static Uri getSearchUri(Hadith hadith, String engine) {
			return Uri.parse(engine + generateGoogleQuery(hadith.text()));
		}

	/*	private static String generateSunnahQuery(String query) {

			if (query.contains(CURLY_START)) { //check for Ayat.
				query = Utils.removeTashkeel(query);
				int start = query.indexOf(CURLY_START) + 1;
				int end = query.indexOf(CURLY_END) - 1;
				query = query.substring(start, end);
				return query.replaceAll(MULTIPLE_SPACES, PLUS_SIGN);
			} else
				return generateGoogleQuery(query);
		}*/

		private static String generateGoogleQuery(String query) {
			query = Utils.removeTashkeel(query);
			String[] words = query.split(MULTIPLE_SPACES);

			if (words.length > 32) {
				Set<Integer> rnds = getUniqueRandoms(words.length - 1);
				StringBuilder builder = new StringBuilder();
				for (int rnd : rnds)
					builder.append(words[rnd]).append(PLUS_SIGN);
				return builder.toString();
			}
			return query.replaceAll(MULTIPLE_SPACES, PLUS_SIGN);
		}

	}

	private static Set<Integer> getUniqueRandoms(int k) {
		final Random RND = new Random();
		final Set<Integer> picked = new HashSet<>();
		while (picked.size() < 32) {
			picked.add(RND.nextInt(k + 1));
		}
		return picked;
	}

}
