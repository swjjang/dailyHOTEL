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

		View couponHistoryView = view.findViewById(R.id.couponHistoryTextView);
		couponHistoryView.setOnClickListener(this);

		updateHeaderTextView(0);

		setData(new ArrayList<Coupon>());
//		setData(setDummyData());
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
		EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.over_scroll_edge));

		View emptyView = LayoutInflater.from(mContext).inflate(R.layout.view_empty_coupon_list, mListView, false);
		mListView.setEmptyView(emptyView);

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
		if (list != null && list.size() != 0)
		{
			mListAdapter = new CouponListAdapter(mContext, 0, list, mCouponItemListener);
		} else
		{
			mListAdapter = new CouponListAdapter(mContext, 0, new ArrayList<Coupon>(), mCouponItemListener);
		}

		mListView.setAdapter(mListAdapter);
	}

//	private ArrayList<Coupon> setDummyData() {
//		ArrayList<Coupon> list = new ArrayList<Coupon>();
//
//		list.add(new Coupon("name", 5000, "test coupon1", "2015.05.15 ~ 2017.06.15", 0, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 100000, "test coupon2", "2015.05.15 ~ 2017.06.15", 1, 10000, 1, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 53000, "test coupon3", "2015.05.15 ~ 2017.06.15", 0, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 5111000, "test coupon4", "2015.05.15 ~ 2017.06.15", 4, 10000, 1, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 665000, "test coupon5", "2015.05.15 ~ 2017.06.15", 123, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 566678000, "test coupon6 - ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ", "2015.05.15 ~ 2017.06.15", 1, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 5000, "test coupon7", "2015.05.15 ~ 2017.06.15", 0, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 100000, "test coupon8", "2015.05.15 ~ 2017.06.15", 1, 10000, 1, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 53000, "test coupon9", "2015.05.15 ~ 2017.06.15", 0, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 5111000, "test coupon10", "2015.05.15 ~ 2017.06.15", 4, 10000, 1, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 665000, "test coupon11", "2015.05.15 ~ 2017.06.15", 123, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		list.add(new Coupon("name", 566678000, "test coupon12 - ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ", "2015.05.15 ~ 2017.06.15", 1, 10000, 0, "호텔 , 펜션 등", "주의사항 ~~~~" ));
//		return list;
//	}

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
