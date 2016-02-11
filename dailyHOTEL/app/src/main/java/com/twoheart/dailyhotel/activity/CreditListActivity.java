/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * CreditListFragment (적립금 내역 화면)
 * <p>
 * 적립금 내역 리스트를 보여주는 화면이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.CreditListAdapter;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import java.util.List;

/**
 * 적립금 입출력 내역 확인.
 *
 * @author jangjunho
 */
public class CreditListActivity extends BaseActivity
{
    public static final String KEY_BUNDLE_ARGUMENTS_CREDITLIST = "credit_list";
    private List<Credit> mCreditList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null)
        {
            mCreditList = intent.getParcelableArrayListExtra(KEY_BUNDLE_ARGUMENTS_CREDITLIST);
        } else
        {
            Util.restartApp(this);
            return;
        }

        setContentView(R.layout.activity_credit_list);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.act_credit_history));
    }

    private void initLayout()
    {
        ListView listView = (ListView) findViewById(R.id.listview_credit);
        listView.setEmptyView((TextView) findViewById(R.id.empty_listview_credit));

        if (mCreditList != null && mCreditList.size() != 0)
        {
            CreditListAdapter adapter = new CreditListAdapter(CreditListActivity.this, R.layout.list_row_credit, mCreditList);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(CreditListActivity.this).recordScreen(Screen.CREDIT_LIST);
        super.onStart();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }
}
