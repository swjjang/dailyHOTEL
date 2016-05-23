package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iseung-won on 2016. 5. 20..
 */
public class CouponListLayout extends BaseLayout implements View.OnClickListener
{

	private DailyTextView mHeaderTextView;
	private ListView mListView;
	private View mEmptyView;
	private CouponListAdapter mListAdapter;


	public interface OnEventListener extends OnBaseEventListener
	{
		void startCouponHistory();

		void startNotice();

		void showListItemNotice(View view, int position, Coupon coupon);

		void onListItemDownLoadClick(View view, int position, Coupon coupon);

	}

	public CouponListLayout(Context context, OnBaseEventListener listener)
	{
		super(context, listener);
	}

	@Override
	protected void initLayout(View view)
	{
		initToolbar(view);
		initListView(view);

		mHeaderTextView = (DailyTextView) view.findViewById(R.id.couponTextView);
		mEmptyView = view.findViewById(R.id.emptyView);

		View couponHistoryView = view.findViewById(R.id.couponHistoryTextView);
		couponHistoryView.setOnClickListener(this);

		updateHeaderTextView(0);

		setData(new ArrayList<Coupon>());
	}

	private void initToolbar(View view)
	{
		View toolbar = view.findViewById(R.id.toolbar);

		DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
		dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_coupon_list), new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mOnEventListener.finish();
			}
		});
	}

	private void initListView(View view)
	{
		mListView = (ListView) view.findViewById(R.id.listView);

		View header = LayoutInflater.from(mContext).inflate(R.layout.list_row_couponlist_header, mListView, false);
		mListView.addHeaderView(header);

		TextView useNoticeTextView = (TextView) header.findViewById(R.id.couponUseNoticeTextView);
		useNoticeTextView.setOnClickListener(this);

		SpannableString spannableString = new SpannableString(useNoticeTextView.getText());
		spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		useNoticeTextView.setText(spannableString);
	}

	private void updateHeaderTextView(int count)
	{
		if (mContext == null)
		{
			return;
		}

		if (mHeaderTextView == null)
		{
			return;
		}

		String text = mContext.getString(R.string.coupon_header_text, count);
		mHeaderTextView.setText(text);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.couponHistoryTextView:
				((OnEventListener) mOnEventListener).startCouponHistory();
				break;
			case R.id.couponUseNoticeTextView:
				((OnEventListener) mOnEventListener).startNotice();
				break;
		}
	}

	public void setData(List<Coupon> list)
	{
		EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.over_scroll_edge));

		if (list != null && list.size() != 0)
		{
			mListAdapter = new CouponListAdapter(mContext, 0, list, mCouponItemListener);
			mEmptyView.setVisibility(View.GONE);
		} else
		{
			mListAdapter = new CouponListAdapter(mContext, 0, new ArrayList<Coupon>(), mCouponItemListener);
			mEmptyView.setVisibility(View.VISIBLE);

		}

		mListView.setAdapter(mListAdapter);
	}

	private CouponListAdapter.OnCouponItemListener mCouponItemListener = new CouponListAdapter.OnCouponItemListener()
	{
		@Override
		public void showNotice(View view, int position)
		{
			Coupon coupon = mListAdapter.getItem(position);
			((OnEventListener) mOnEventListener).showListItemNotice(view, position, coupon);
		}

		@Override
		public void onDownloadClick(View view, int position)
		{
			Coupon coupon = mListAdapter.getItem(position);
			((OnEventListener) mOnEventListener).onListItemDownLoadClick(view, position, coupon);
		}
	};

}
