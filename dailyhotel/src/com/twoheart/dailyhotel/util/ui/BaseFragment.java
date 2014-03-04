package com.twoheart.dailyhotel.util.ui;

import com.twoheart.dailyhotel.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_error, null);
		
		
		
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	

}
