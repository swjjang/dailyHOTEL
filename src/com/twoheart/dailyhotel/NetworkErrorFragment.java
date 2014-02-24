package com.twoheart.dailyhotel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class NetworkErrorFragment extends SherlockFragment{
	
	private static final String TAG = "NetworkErrorFragment";
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_error, null);
		
		return view;
	}
}
