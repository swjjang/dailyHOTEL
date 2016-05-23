package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
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

	public CouponListAdapter(Context context, int resource, List<Coupon> list)
	{
		super(context, resource, list);
		mContext = context;
		mCouponList = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
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
		couponPriceTextView.setText(strPrice);

		descriptionTextView.setText(coupon.description);
		expireTextView.setText(coupon.expiredTime);

		if (coupon.dueDate > 0) {
			dueDateTextView.setTypeface(FontManager.getInstance(mContext).getRegularTypeface());
		} else {
			// 오늘까지
			dueDateTextView.setTypeface(FontManager.getInstance(mContext).getMediumTypeface());
		}

		dueDateTextView.setText(coupon.dueDate);



		useablePlaceTextView.setText(coupon.useablePlace);

		if (coupon.minPrice > 0) {
			minPriceTextView.setText(coupon.minPrice);
		} else {
			minPriceTextView.setText("");
		}

		return view;
	}
}