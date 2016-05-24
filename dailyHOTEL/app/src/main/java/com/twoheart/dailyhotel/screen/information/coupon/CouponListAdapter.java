package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.FontManager;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by iseung-won on 2016. 5. 20..
 */
public class CouponListAdapter extends ArrayAdapter<Coupon>
{

	private Context mContext;
	private List<Coupon> mCouponList;
	private OnCouponItemListener mListener;

	public interface OnCouponItemListener
	{
		void showNotice(View view, int position);

		void onDownloadClick(View view, int position);
	}

	public CouponListAdapter(Context context, int resource, List<Coupon> list, OnCouponItemListener listener)
	{
		super(context, resource, list);
		mContext = context;
		mCouponList = list;
		mListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View view;

		if (convertView == null)
		{
			view = LayoutInflater.from(mContext).inflate(R.layout.list_row_coupon, parent, false);
		} else
		{
			view = convertView;
		}

		Coupon coupon = mCouponList.get(position);

		TextView couponPriceTextView = (TextView) view.findViewById(R.id.couponPriceTextView);
		TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
		TextView expireTextView = (TextView) view.findViewById(R.id.expireTextView);
		DailyTextView dueDateTextView = (DailyTextView) view.findViewById(R.id.dueDateTextView);
		TextView useablePlaceTextView = (TextView) view.findViewById(R.id.useablePlaceTextView);
		TextView minPriceTextView = (TextView) view.findViewById(R.id.minPriceTextView);
		TextView useIconView = (TextView) view.findViewById(R.id.useIconView);
		TextView downloadIconView = (TextView) view.findViewById(R.id.downloadIconView);
		TextView noticeTextView = (TextView) view.findViewById(R.id.noticeTextView);

		DecimalFormat decimalFormat = new DecimalFormat("###,##0");
		String strPrice = decimalFormat.format(coupon.price);
		couponPriceTextView.setText(strPrice + mContext.getResources().getString(R.string.currency));

		descriptionTextView.setText(coupon.description);
		expireTextView.setText(coupon.expiredTime);

		if (coupon.dueDate > 0)
		{
			dueDateTextView.setTypeface(FontManager.getInstance(mContext).getRegularTypeface());
			dueDateTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_expire_text));
			String strDueDate = mContext.getResources().getString(R.string.coupon_duedate_text, coupon.dueDate);
			dueDateTextView.setText(strDueDate);
		} else
		{
			// 오늘까지
			dueDateTextView.setTypeface(FontManager.getInstance(mContext).getMediumTypeface());
			dueDateTextView.setTextColor(mContext.getResources().getColor(R.color.coupon_red_wine_text));
			dueDateTextView.setText(mContext.getResources().getString(R.string.coupon_today_text));
		}

		if (coupon.minPrice > 0)
		{
			String strMinPrice = decimalFormat.format(coupon.minPrice);
			minPriceTextView.setText(strMinPrice + mContext.getResources().getString(R.string.currency));
		} else
		{
			minPriceTextView.setText("");
		}

		useablePlaceTextView.setText(coupon.useablePlace);

		if (coupon.state == 0)
		{
			//download
			downloadIconView.setVisibility(View.VISIBLE);
			useIconView.setVisibility(View.GONE);
		} else
		{
			//useable
			downloadIconView.setVisibility(View.GONE);
			useIconView.setVisibility(View.VISIBLE);
		}

		CharSequence charSequence = Util.isTextEmpty(noticeTextView.getText().toString()) ? "" : noticeTextView.getText().toString();
		SpannableString spannableString = new SpannableString(charSequence);
		spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		noticeTextView.setText(spannableString);
		noticeTextView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListener.showNotice(v, position);
			}
		});

		view.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListener.onDownloadClick(v, position);
			}
		});

		return view;
	}
}