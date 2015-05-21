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
package com.twoheart.dailyhotel.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.CreditListAdapter;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

/**
 * 적립금 입출력 내역 확인.
 * 
 * @author jangjunho
 *
 */
public class CreditListActivity extends BaseActivity
{
	public static final String KEY_BUNDLE_ARGUMENTS_CREDITLIST = "credit_list";
	private List<Credit> mCreditList;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_credit_list);
		setActionBar(R.string.act_credit_history);

		Intent intent = getIntent();

		if (intent != null)
		{
			mCreditList = intent.getParcelableArrayListExtra(KEY_BUNDLE_ARGUMENTS_CREDITLIST);
		} else
		{
			finish();
			return;
		}

		initLayout();
	}

	private void initLayout()
	{
		ListView listView = (ListView) findViewById(R.id.listview_credit);
		listView.setEmptyView((TextView) findViewById(R.id.empty_listview_credit));

		CreditListAdapter adapter = new CreditListAdapter(CreditListActivity.this, R.layout.list_row_credit, mCreditList);
		listView.setAdapter(adapter);
	}

	@Override
	public void onResume()
	{
		RenewalGaManager.getInstance(CreditListActivity.this).recordScreen("creditHistory", "/credit-with-logon/history");
		super.onResume();
	}

	@Override
	public void finish()
	{
		super.finish();
		
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}
}
