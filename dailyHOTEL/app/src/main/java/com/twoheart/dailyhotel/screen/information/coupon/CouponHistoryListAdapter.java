package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by iseung-won on 2016. 5. 23..
 */
public class CouponHistoryListAdapter extends ArrayAdapter<Coupon>
{
	private Context mContext;
	private List<Coupon> mCouponList;

	public CouponHistoryListAdapter(Context context, int resource, List<Coupon> list)
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
			view = LayoutInflater.from(mContext).inflate(R.layout.list_row_coupon_history, parent, false);
		} else
		{
			view = convertView;
		}

		Coupon coupon = mCouponList.get(position);

		TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);
		TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
		TextView expireTextView = (TextView) view.findViewById(R.id.expireTextView);

		DecimalFormat decimalFormat = new DecimalFormat("###,##0");
		String strPrice = decimalFormat.format(coupon.price);
		priceTextView.setText(strPrice + mContext.getResources().getString(R.string.currency));

		descriptionTextView.setText(coupon.description);

		View upperLine = view.findViewById(R.id.upperLineView);
		upperLine.setVisibility((position == 0) ? View.VISIBLE : View.GONE);

		// 사용기간 및 사용일자 또는 만료일자 구현 필요

		return view;
	}
}
