package mhashim6.android.thetwoauthentics.app.results;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mhashim6.android.thetwoauthentics.R;
import mhashim6.android.thetwoauthentics.app.DatabasesLogic;
import mhashim6.android.thetwoauthentics.model.Hadith;
import mhashim6.android.thetwoauthentics.model.Muhaddith;

public class ResultsFragment extends Fragment {
	public final static String MUHADDITH_KEY = "MUHADDITH_KEY";
	public final static String ARABIC_KEY = "ARABIC_KEY";
	private int muhaddith;
	private int type;
	private boolean isArabic;

	private List<Hadith> ahadith;
	private ResultsAdapter adapter;
	private RecyclerView recyclerView;

	public ResultsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_results, container, false);

		initInformation();

		initRecyclerView(rootView);
		showData();
		return rootView;
	}
//==================================================

	private void initRecyclerView(View rootView) {
		recyclerView = rootView.findViewById(R.id.recycler_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
	}
//==================================================

	private void initInformation() {
		Bundle args = getArguments();
		muhaddith = args.getInt(MUHADDITH_KEY);
		type = args.getInt(ResultsActivity.RESULTS_TYPE_KEY);
		isArabic = args.getBoolean(ARABIC_KEY);
	}
//==================================================

	private void showData() {
		ResultsWrapper resultsWrapper = type == ResultsActivity.RESULTS ? ResultsHolder.getInstance().getResults()
				: ResultsHolder.getInstance().getSaved();

		DatabasesLogic dbsManager = DatabasesLogic.getInstance(getActivity());

		ahadith = muhaddith == Muhaddith.ALBUKHARI ?
				resultsWrapper.bukhariData()
				: resultsWrapper.muslimData();

		/*populate with data*/
		adapter = new ResultsAdapter(dbsManager, ahadith, isArabic, type);
		recyclerView.setAdapter(adapter);
	}
//==================================================

}
