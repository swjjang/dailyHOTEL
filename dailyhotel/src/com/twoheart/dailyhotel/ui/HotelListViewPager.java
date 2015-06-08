/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * CreditFragment (적립금 화면)
 * 
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어 
 * 해당 화면을 띄워주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class HotelListViewPager extends ViewPager
{
	private Context mContext;

	public HotelListViewPager(Context context)
	{
		super(context);

		mContext = context;

		initLayout(context);
	}

	public HotelListViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initLayout(context);
	}

	private void initLayout(Context context)
	{

	}
}