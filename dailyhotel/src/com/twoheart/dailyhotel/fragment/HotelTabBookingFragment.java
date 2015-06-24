/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * HotelTabBookingFragment (호텔 예약 탭)
 * 
 * 호텔 탭 중 예약 탭 프래그먼트
 * 
 */
package com.twoheart.dailyhotel.fragment;

import java.text.DecimalFormat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.adapter.HotelImageFragmentPagerAdapter;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ABTestPreferences;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public class HotelTabBookingFragment extends BaseFragment implements OnTouchListener
{
	private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;
	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";

	private HotelDetail mHotelDetail;
	private HotelImageFragmentPagerAdapter mAdapter;
	private HotelViewPager mViewPager;
	private CirclePageIndicator mIndicator;
	private TextView tvBedType, tvAddress, tvPrice, tvDiscount, tvPriceTitle;

	private HotelTabActivity.OnUserActionListener mOnUserActionListener;

	private Handler mHandler;
	private int mCurrentPage = 0;

	public static HotelTabBookingFragment newInstance(HotelDetail hotelDetail, String title)
	{

		HotelTabBookingFragment newFragment = new HotelTabBookingFragment();
		Bundle arguments = new Bundle();

		// 관련정보는 HotelTabActivity에서 넘겨받음. 
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		newFragment.setArguments(arguments);
		newFragment.setTitle(title);

		return newFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mHotelDetail = (HotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_hotel_tab_booking, container, false);

		tvBedType = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_bed_type);
		tvAddress = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_address);
		tvPriceTitle = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_price_title);
		tvPrice = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_price);
		tvDiscount = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_discount);
		mViewPager = (HotelViewPager) view.findViewById(R.id.vp_hotel_tab_booking_img);
		mIndicator = (CirclePageIndicator) view.findViewById(R.id.cp_hotel_tab_booking_indicator);

		tvBedType.setText(mHotelDetail.getHotel().getBedType());
		tvAddress.setText(mHotelDetail.getHotel().getAddress());
		tvAddress.setSelected(true);

		Spanned currency = Html.fromHtml(getString(R.string.currency));

		String priceTitle = getString(R.string.frag_hotel_tab_price);

		tvPriceTitle.setText(priceTitle + "");

		DecimalFormat comma = new DecimalFormat("###,##0");

		tvDiscount.setText(comma.format(mHotelDetail.getHotel().getDiscount()) + currency);

		int price = mHotelDetail.getHotel().getPrice();

		if (price <= 0)
		{
			tvPrice.setVisibility(View.INVISIBLE);

			tvPrice.setText(null);
		} else
		{
			tvPrice.setVisibility(View.VISIBLE);

			tvPrice.setText(comma.format(price) + currency);
			tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}

		if (mAdapter == null)
		{
			mAdapter = new HotelImageFragmentPagerAdapter(getChildFragmentManager(), mHotelDetail);
			mAdapter.setOnUserActionListener(mOnUserActionListener);

			mViewPager.setAdapter(mAdapter);
		}

		mCurrentPage = mHotelDetail.getImageUrl().size();

		if (mCurrentPage != 0)
		{
			mViewPager.setBoundaryCaching(true);
			mViewPager.setOnTouchListener(this);
		}

		mViewPager.setCurrentItem(0);

		mIndicator.setViewPager(mViewPager);
		mIndicator.setSnap(true);

		if (mCurrentPage != 0)
		{
			mHandler = new Handler()
			{
				public void handleMessage(Message msg)
				{
					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null || baseActivity.isFinishing() == true)
					{
						return;
					}

					mCurrentPage = mViewPager.getCurrentItem();
					mCurrentPage++;
					mViewPager.setCurrentItem(mCurrentPage, true);
					this.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
				}
			};
		}

		// 문의 하기 기능.
		int state = ABTestPreferences.getInstance(getActivity()).getKakaotalkConsult();

		setKakaotalkConsultVisible(view, state == 1);

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		releaseUiComponent();

		tvDiscount.setTypeface(DailyHotel.getBoldTypeface());
		if (mHandler != null)
		{
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (mHandler != null)
			mHandler.removeMessages(0);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if (mHandler != null)
			mHandler.removeMessages(0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (v.getId() == mViewPager.getId())
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					mHandler.removeMessages(0);
					break;

				case MotionEvent.ACTION_MOVE:
					mHandler.removeMessages(0);
					break;

				case MotionEvent.ACTION_UP:
					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null)
					{
						return false;
					}

					mHandler.removeMessages(0);
					RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordEvent("scroll", "photos", mHotelDetail.getHotel().getName(), null);
					mHandler.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
				default:
					break;
			}
		}

		return false;
	}

	public void setOnUserActionListener(HotelTabActivity.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	public void setHotelDetail(HotelDetail hotelDetail)
	{
		if (hotelDetail == null)
		{
			return;
		}

		mHotelDetail = hotelDetail;

		Spanned currency = Html.fromHtml(getString(R.string.currency));

		String priceTitle = getString(R.string.frag_hotel_tab_price);

		tvPriceTitle.setText(priceTitle + "");

		DecimalFormat comma = new DecimalFormat("###,##0");
		int discount = hotelDetail.getHotel().getDiscount();
		tvDiscount.setText(comma.format(discount) + currency);

		try
		{
			int price = hotelDetail.getHotel().getPrice();

			if (price <= 0)
			{
				tvPrice.setVisibility(View.INVISIBLE);
				tvPrice.setText(null);
			} else
			{
				tvPrice.setVisibility(View.VISIBLE);

				tvPrice.setText(comma.format(price) + currency);
				tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			}
		} catch (Exception e)
		{
			ExLog.d(e.toString());

			tvPrice.setVisibility(View.INVISIBLE);
			tvPrice.setText(null);
		}
	}

	private void setKakaotalkConsultVisible(View view, boolean visible)
	{
		View kakaoConsultLayout = view.findViewById(R.id.kakaoConsultLayout);

		if (visible == false)
		{
			kakaoConsultLayout.setVisibility(View.GONE);

		} else
		{
			kakaoConsultLayout.setVisibility(View.VISIBLE);

			// 카톡 1:1 실시간 상담
			View consultKakaoButton = view.findViewById(R.id.consultKakaoButton);
			consultKakaoButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (isLockUiComponent() == true)
					{
						return;
					}

					lockUiComponent();

					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null || baseActivity.isFinishing() == true)
					{
						return;
					}

					// ABTest
					ABTestPreferences.getInstance(baseActivity).feedbackKakaotalkConsult(baseActivity, mQueue, null);

					try
					{
						startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94")));
					} catch (ActivityNotFoundException e)
					{
						try
						{
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
						} catch (ActivityNotFoundException e1)
						{
							Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
							marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
							startActivity(marketLaunch);
						}
					}
				}
			});
		}

	}
}
