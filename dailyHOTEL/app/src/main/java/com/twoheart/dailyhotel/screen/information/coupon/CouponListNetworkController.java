package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

/**
 * Created by iseung-won on 2016. 5. 20..
 */
public class CouponListNetworkController extends BaseNetworkController
{
	protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
	{



	}


	public CouponListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
	{
		super(context, networkTag, listener);
	}

	@Override
	public void onErrorResponse(VolleyError volleyError)
	{
		mOnNetworkControllerListener.onErrorResponse(volleyError);
	}
}
