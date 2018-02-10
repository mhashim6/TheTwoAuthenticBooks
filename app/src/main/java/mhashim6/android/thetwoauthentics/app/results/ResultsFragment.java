package mhashim6.android.thetwoauthentics.app.results;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mhashim6.android.thetwoauthentics.R;

public class ResultsFragment extends Fragment {
	private ResultsAdapter adapter;

	public ResultsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		setRetainInstance(true);
		View rootView = inflater.inflate(R.layout.fragment_results, container, false);
		initRecyclerView(rootView);
		return rootView;
	}
//==================================================

	public void setAdapter(ResultsAdapter adapter) {
		this.adapter = adapter;
	}
//==================================================

	private void initRecyclerView(View rootView) {
		RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(adapter);
	}
//==================================================

}
