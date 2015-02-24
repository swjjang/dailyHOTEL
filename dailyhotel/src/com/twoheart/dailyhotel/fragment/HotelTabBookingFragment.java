package com.twoheart.dailyhotel.fragment;

import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.HotelImageFragmentPagerAdapter;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.LoopCirclePageIndicator;

public class HotelTabBookingFragment extends BaseFragment implements
OnTouchListener {

	private static final String TAG = "HotelTabBookingFragment";
	
	private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;
	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";

	private HotelDetail mHotelDetail;
	private HotelImageFragmentPagerAdapter mAdapter;
	private HotelViewPager mViewPager;
	private LoopCirclePageIndicator mIndicator;
	private TextView tvBedType, tvAddress, tvPrice, tvDiscount, tvPriceTitle;

	private Handler mHandler;
	private int mCurrentPage = 0;
	

	public static HotelTabBookingFragment newInstance(HotelDetail hotelDetail, String title) {

		HotelTabBookingFragment newFragment = new HotelTabBookingFragment();
		Bundle arguments = new Bundle();

		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		newFragment.setArguments(arguments);
		newFragment.setTitle(title);

		return newFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHotelDetail = (HotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_tab_booking, container,
				false);

		tvBedType = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_bed_type);
		tvAddress = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_address);
		tvPriceTitle = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_price_title);
		tvPrice = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_price);
		tvDiscount = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_discount);
		mViewPager = (HotelViewPager) view.findViewById(R.id.vp_hotel_tab_booking_img);
		mIndicator = (LoopCirclePageIndicator) view.findViewById(R.id.cp_hotel_tab_booking_indicator);

		tvBedType.setText(mHotelDetail.getHotel().getBedType());
		tvAddress.setText(mHotelDetail.getHotel().getAddress());
		tvAddress.setSelected(true);
		
		String currency = getString(R.string.currency);
		
		String priceTitle = getString(R.string.frag_hotel_tab_price);
		
		//영어 버전에서 괄호부분의 텍스트 사이즈를 줄이기 위함
		String locale = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_LOCALE, null);
		if (locale.equals("English")) {
			final SpannableStringBuilder sps = new SpannableStringBuilder(priceTitle);
			sps.setSpan(new AbsoluteSizeSpan(25), 5, 34, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 
			tvPriceTitle.append(sps);
			tvDiscount.setText(currency + mHotelDetail.getHotel().getDiscount());
			tvPrice.setText(currency + mHotelDetail.getHotel().getPrice());
		} else {
			tvPriceTitle.setText(priceTitle + "");
			tvDiscount.setText(mHotelDetail.getHotel().getDiscount() + currency);
			tvPrice.setText(mHotelDetail.getHotel().getPrice() + currency);
		}
//		tvPriceTitle.setText(priceTitle + "");
//		tvDiscount.setText(mHotelDetail.getHotel().getDiscount() + currency);
//		tvPrice.setText(mHotelDetail.getHotel().getPrice() + currency);
		
		tvPrice.setPaintFlags(tvPrice.getPaintFlags()
				| Paint.STRIKE_THRU_TEXT_FLAG);

		if (mAdapter == null) {
			mAdapter = new HotelImageFragmentPagerAdapter(getChildFragmentManager(), mHotelDetail);
			mViewPager.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		mViewPager.setOnTouchListener(this);
		mViewPager.setCurrentItem((mHotelDetail.getImageUrl().size() * 10000)); // 페이지를 큰 수의 배수로 설정하여 루핑을 하게 함 
		mIndicator.setViewPager(mViewPager);
		mIndicator.setSnap(true);

		mCurrentPage = mHotelDetail.getImageUrl().size();
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				mCurrentPage = mViewPager.getCurrentItem();
				mCurrentPage++;
				mViewPager.setCurrentItem(mCurrentPage, true);
				this.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
			}
		};

		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		tvDiscount.setTypeface(DailyHotel.getBoldTypeface());
		if (mHandler != null) { 
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mHandler != null) mHandler.removeMessages(0);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mHandler != null) mHandler.removeMessages(0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (v.getId() == mViewPager.getId()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mHandler.removeMessages(0);
				break;
			case MotionEvent.ACTION_MOVE:
				mHandler.removeMessages(0);
				break;

			case MotionEvent.ACTION_UP:
				mHandler.removeMessages(0);
				RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordEvent("scroll", "photos", mHotelDetail.getHotel().getName(), null);
				mHandler.sendEmptyMessageDelayed(0,
						DURATION_HOTEL_IMAGE_SHOW);
			default:
				break;
			}
		}

		return false;
	}

}
