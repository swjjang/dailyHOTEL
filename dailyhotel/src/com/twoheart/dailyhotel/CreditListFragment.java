/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * CreditListFragment (적립금 내역 화면)
 * 
 * 적립금 내역 리스트를 보여주는 화면이다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.adapter.CreditListAdapter;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

/**
 * 적립금 입출력 내역 확인.
 * @author jangjunho
 *
 */
public class CreditListFragment extends BaseFragment {
	
	private static final String KEY_BUNDLE_ARGUMENTS_CREDITLIST = "credit_list";
	
	private ListView mListView;
	private CreditListAdapter mAdapter;
	private List<Credit> mCreditList;
	
	public static CreditListFragment newInstance(List<Credit> creditList) {
		
		CreditListFragment newFragment = new CreditListFragment();
		
		Bundle arguments = new Bundle();
		arguments.putParcelableArrayList(KEY_BUNDLE_ARGUMENTS_CREDITLIST, (ArrayList<Credit>) creditList);
		
		newFragment.setArguments(arguments);
		
		return newFragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCreditList = getArguments().getParcelableArrayList(KEY_BUNDLE_ARGUMENTS_CREDITLIST);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_credit_list, container, false);

		mListView = (ListView) view.findViewById(R.id.listview_credit);
		mListView.setEmptyView((TextView) view.findViewById(R.id.empty_listview_credit));
		mAdapter = new CreditListAdapter(view.getContext(), R.layout.list_row_credit, mCreditList);
		mListView.setAdapter(mAdapter);
		
		return view;
	}

}
