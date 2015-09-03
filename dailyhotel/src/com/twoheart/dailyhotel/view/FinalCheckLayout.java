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
package com.twoheart.dailyhotel.view;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailySignatureView;
import com.twoheart.dailyhotel.view.widget.FontManager;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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

		TextView messageTextView1 = (TextView) view.findViewById(R.id.messageTextView1);
		TextView messageTextView2 = (TextView) view.findViewById(R.id.messageTextView2);
		TextView messageTextView3 = (TextView) view.findViewById(R.id.messageTextView3);

		messageTextView1.setTypeface(FontManager.getInstance(context).getMediumTypeface());
		String message1 = context.getString(R.string.dialog_msg_payment_message01);

		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message1);

		int startIndex = message1.indexOf("취소");
		int length = "취소, 변경, 환불이 절대 불가".length();

		//sp.setSpan(new ForegroundColorSpan(Color.rgb(255, 255, 255)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dialog_title_text)), //
		startIndex, startIndex + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
		startIndex, startIndex + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		messageTextView1.setText(spannableStringBuilder);

		messageTextView2.setTypeface(FontManager.getInstance(context).getMediumTypeface());
		messageTextView3.setTypeface(FontManager.getInstance(context).getMediumTypeface());

		mDailySignatureView = (DailySignatureView) view.findViewById(R.id.signatureView);

		if (Util.isOverAPI21() == true)
		{
			LinearLayout messageLayout2 = (LinearLayout) view.findViewById(R.id.messageLayout2);
			LinearLayout messageLayout3 = (LinearLayout) view.findViewById(R.id.messageLayout3);

			LinearLayout.LayoutParams layoutParams2 = (android.widget.LinearLayout.LayoutParams) messageLayout2.getLayoutParams();
			layoutParams2.topMargin = Util.dpToPx(context, 17);

			LinearLayout.LayoutParams layoutParams3 = (android.widget.LinearLayout.LayoutParams) messageLayout3.getLayoutParams();
			layoutParams3.topMargin = Util.dpToPx(context, 17);
		}
	}

	public void setOnUserActionListener(DailySignatureView.OnUserActionListener listener)
	{
		if (mDailySignatureView != null)
		{
			mDailySignatureView.setOnUserActionListener(listener);
		}
	}
}