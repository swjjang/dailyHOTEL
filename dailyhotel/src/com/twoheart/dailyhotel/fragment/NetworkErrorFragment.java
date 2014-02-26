package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NetworkErrorFragment extends Fragment {
	
	private static final String TAG = "NetworkErrorFragment";
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_error, null);
		
		return view;
	}
}
