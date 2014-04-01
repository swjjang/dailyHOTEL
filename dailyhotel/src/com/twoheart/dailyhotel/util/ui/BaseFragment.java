package com.twoheart.dailyhotel.util.ui;

import com.twoheart.dailyhotel.util.GlobalFont;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	protected OnLoadCompleteListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (OnLoadCompleteListener) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		GlobalFont.apply((ViewGroup) getView().getRootView());
	}
	
	
	
	
}
