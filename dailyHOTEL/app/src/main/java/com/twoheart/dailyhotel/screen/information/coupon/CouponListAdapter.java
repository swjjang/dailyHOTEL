package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;

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

		return super.getView(position, convertView, parent);
	}
}