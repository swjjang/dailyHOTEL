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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.widget.DailySignatureView;

/**
 * 신용카드 Final Check
 * 
 * @author sheldon
 *
 */
public class FinalCheckLayout extends FrameLayout
{
	private DailySignatureView mDailySignatureView;

	public FinalCheckLayout(Context context)
	{
		super(context);

		initLayout(context);
	}

	public FinalCheckLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initLayout(context);
	}

	public FinalCheckLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);

		initLayout(context);
	}

	public FinalCheckLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		initLayout(context);
	}

	private void initLayout(Context context)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_finalcheck, this, true);

		mDailySignatureView = (DailySignatureView) view.findViewById(R.id.signatureView);
	}

	public void setOnUserActionListener(DailySignatureView.OnUserActionListener listener)
	{
		if (mDailySignatureView != null)
		{
			mDailySignatureView.setOnUserActionListener(listener);
		}
	}

	//	public boolean isSignatureChecked()
	//	{
	//		if (mDailySignatureView == null)
	//		{
	//			return false;
	//		}
	//
	//		return mDailySignatureView.isSignatureChecked();
	//	}
	//
	//	public void clearSignature()
	//	{
	//		if (mDailySignatureView != null)
	//		{
	//			mDailySignatureView.clearSignature();
	//		}
	//	}
}