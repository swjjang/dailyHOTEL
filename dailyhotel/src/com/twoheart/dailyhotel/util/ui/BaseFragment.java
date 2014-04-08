package com.twoheart.dailyhotel.util.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;

public abstract class BaseFragment extends Fragment implements Constants {
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
