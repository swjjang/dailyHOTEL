package com.twoheart.dailyhotel;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.CreditListAdapter;
import com.twoheart.dailyhotel.obj.Credit;

public class CreditListFragment extends Fragment{
	
	private ListView mListView;
	private CreditListAdapter mAdapter;
	private List<Credit> mCreditList;
	
	public CreditListFragment(List<Credit> creditList) {
		mCreditList = creditList;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_credit_list, null);

		mListView = (ListView) view.findViewById(R.id.listview_credit);
		mListView.setEmptyView((TextView) view.findViewById(R.id.empty_listview_credit));
		mAdapter = new CreditListAdapter(view.getContext(), R.layout.list_row_credit, mCreditList);
		mListView.setAdapter(mAdapter);
		
		return view;
	}
	
}
