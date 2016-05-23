package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iseung-won on 2016. 5. 23..
 */
public class CouponHistoryLayout
		extends BaseLayout implements View.OnClickListener
{

	private ListView mListView;
	private View mEmptyView;
	private CouponHistoryListAdapter mListAdapter;

	public interface OnEventListener extends OnBaseEventListener
	{

	}

	public CouponHistoryLayout(Context context, OnBaseEventListener listener)
	{
		super(context, listener);
	}

	@Override
	protected void initLayout(View view)
	{
		initToolbar(view);
		mListView = (ListView) view.findViewById(R.id.listView);

		mEmptyView = view.findViewById(R.id.emptyView);

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

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			//			case R.id.couponHistoryTextView:
			//				((OnEventListener) mOnEventListener).startCouponHistory();
			//				break;
			//			case R.id.couponUseNoticeTextView:
			//				((OnEventListener) mOnEventListener).showNotice();
			//				break;
		}
	}

	public void setData(List<Coupon> list)
	{
		EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.over_scroll_edge));

		if (list != null && list.size() != 0)
		{
			mListAdapter = new CouponHistoryListAdapter(mContext, 0, list);
			mEmptyView.setVisibility(View.GONE);
		} else
		{
			mListAdapter = new CouponHistoryListAdapter(mContext, 0, new ArrayList<Coupon>());
			mEmptyView.setVisibility(View.VISIBLE);

		}

		mListView.setAdapter(mListAdapter);
	}
}
