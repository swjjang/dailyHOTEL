/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 * 
 */
package com.twoheart.dailyhotel.activity;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.FnBDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import android.content.Intent;
import android.widget.Toast;

public class FnBDetailActivity extends PlaceDetailActivity
{
	@Override
	protected PlaceDetail createPlaceDetail(Intent intent)
	{
		if (intent == null)
		{
			return null;
		}

		int index = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);

		return new FnBDetail(index);
	}

	@Override
	protected void shareKakao(PlaceDetail placeDetail, String imageUrl, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
	{
		KakaoLinkManager.newInstance(this).shareFnB(placeDetail.name, placeDetail.index, //
		imageUrl, //
		checkInSaleTime.getDailyTime(), //
		checkInSaleTime.getOffsetDailyDay());
	}

	@Override
	protected void requestPlaceDetailInformation(PlaceDetail placeDetail, SaleTime checkInSaleTime)
	{
		// 호텔 정보를 가져온다.
		String params = String.format("?restaurant_idx=%d&sday=%s", placeDetail.index, checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

		if (DEBUG == true)
		{
			showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
		}

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_SALE_RESTAURANT_INFO).append(params).toString(), null, mFnBDetailJsonResponseListener, this));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mFnBDetailJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					if (response.has("msg") == true)
					{
						String msg = response.getString("msg");

						DailyToast.showToast(FnBDetailActivity.this, msg, Toast.LENGTH_SHORT);
						finish();
						return;
					} else
					{
						throw new NullPointerException("response == null");
					}
				}

				JSONObject dataJSONObject = response.getJSONObject("data");

				mPlaceDetail.setData(dataJSONObject);

				if (mIsStartByShare == true)
				{
					mIsStartByShare = false;
					initLayout(mPlaceDetail.name, null);
				}

				if (mPlaceDetailLayout != null)
				{
					mPlaceDetailLayout.setDetail(mPlaceDetail, mCurrentImage);
				}
			} catch (Exception e)
			{
				onError(e);
				finish();
			} finally
			{
				unLockUI();
			}
		}
	};
}
