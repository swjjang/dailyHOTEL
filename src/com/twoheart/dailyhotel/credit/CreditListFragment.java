package com.twoheart.dailyhotel.credit;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.hotel.HotelTabInfoFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CreditListFragment extends Fragment{
	
	private View view;
	private static ArrayList<CreditListElement> creditList;
	
	private ListView listView;
	private CreditListAdapter adapter;
	
	public static CreditListFragment newInstance(ArrayList<CreditListElement> list) {
		creditList = list; 
		CreditListFragment fragment = new CreditListFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_credit_list, null);
		setListView();
		return view;
	}
	
	public void setListView() {
		listView = (ListView) view.findViewById(R.id.listview_credit);
		adapter = new CreditListAdapter(view.getContext(), R.layout.list_row_credit, creditList);
		listView.setAdapter(adapter);
	}
}
