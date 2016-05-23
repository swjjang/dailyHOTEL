package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;

/**
 * Created by iseung-won on 2016. 5. 19..
 */
public class CouponListActivity extends BaseActivity
{
	private CouponListLayout mCouponListLayout;
	private CouponListNetworkController mCouponListNetworkController;

	public static Intent newInstance(Context context)
	{
		Intent intent = new Intent(context, CouponListActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCouponListLayout = new CouponListLayout(this, mOnEventListener);
		mCouponListNetworkController = new CouponListNetworkController(this, mNetworkTag, mNetworkControllerListener);

		setContentView(mCouponListLayout.onCreateView(R.layout.activity_coupon_list));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void finish()
	{
		super.finish();

		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	// ////////////////////////////////////////////////////////
	// EventListener
	// ////////////////////////////////////////////////////////
	private CouponListLayout.OnEventListener mOnEventListener = new CouponListLayout.OnEventListener()
	{

		@Override
		public void startCouponHistory()
		{
			// 쿠폰 사용내역 이동
		}

		@Override
		public void startNotice()
		{
			// 쿠폰 사용시 유의사항 안내
		}

		@Override
		public void startListItemNotice(View view, int position, Coupon coupon)
		{
			// 리스트 아이템 쿠폰 유의사항 팝업
		}

		@Override
		public void onListItemDownLoadClick(View view, int position, Coupon coupon)
		{
			// 리스트 아이템 쿠폰 다운로드
		}

		@Override
		public void finish()
		{
			CouponListActivity.this.finish();
		}
	};

	// ///////////////////////////////////////////////////
	// NetworkController
	// ///////////////////////////////////////////////////
	private CouponListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new CouponListNetworkController.OnNetworkControllerListener()
	{

		@Override
		public void onErrorResponse(VolleyError volleyError)
		{
			CouponListActivity.this.onErrorResponse(volleyError);
		}

		@Override
		public void onError(Exception e)
		{
			CouponListActivity.this.onError(e);
		}

		@Override
		public void onErrorPopupMessage(int msgCode, String message)
		{
			CouponListActivity.this.onErrorPopupMessage(msgCode, message);
		}

		@Override
		public void onErrorToastMessage(String message)
		{
			CouponListActivity.this.onErrorToastMessage(message);
		}
	};

}
