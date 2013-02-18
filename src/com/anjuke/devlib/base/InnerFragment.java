package com.anjuke.devlib.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anjuke.devlib.R;
import com.anjuke.devlib.common.GlobalInstance;

public abstract class InnerFragment extends Fragment {

	protected View innerView = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (GlobalInstance.dualPane) {
			getActivity().getActionBar().setTitle(getBarTitleWithPath());
		} else {
			getActivity().getActionBar().setTitle(getBarTitle());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		innerView = inflater
				.inflate(getFragmentLayoutResId(), container, false);
		initComponents();
		initEvents();
		return innerView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initLogic();
	}

	protected abstract int getBarTitle();

	protected abstract int getBarTitleWithPath();

	protected abstract void initComponents();
	
	protected abstract void initEvents();

	protected abstract void initLogic();

	protected abstract int getFragmentLayoutResId();
	
	protected abstract String getMainActivityName();

	protected abstract void initMenu(Menu menu);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onPause() {
		getActivity().setTitle(R.string.app_name);
		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (getActivity() == null) {
			return;
		}
		if (getActivity().getClass().getName()
				.equals(getMainActivityName())
				&& !GlobalInstance.dualPane) {
			return;
		}

		initMenu(menu);
	}

}
