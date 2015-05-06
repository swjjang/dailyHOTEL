package com.twoheart.dailyhotel.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;

public class HotelInfoWindowAdapter implements InfoWindowAdapter, View.OnTouchListener
{
	private static final int MAX_CHILD_VIEW = 4;

	private ArrayList<Hotel> mHotelList;
	private Context mContext;
	private boolean mRefreshingInfoWindow;
	private View mRootView;
	private View[] mChildView;
	private OnInfoWindowClickListener mOnInfoWindowClickListener;
	private Handler mHandler = new Handler();
	private boolean mIsPressed;
	private ViewConfiguration mViewConfiguration;

	private View mSelectedView;

	private int prev_x;
	private int prev_y;

	public interface OnInfoWindowClickListener
	{
		public void onInfoWindowClickListener(Hotel selectedHotel);

		public void onCancelInfoWindowClickListener();
	}

	public HotelInfoWindowAdapter(Context context)
	{
		mContext = context;

		mViewConfiguration = ViewConfiguration.get(context);

		// Max 4
		mChildView = new View[MAX_CHILD_VIEW];
	}

	public void setHotelListViewItems(ArrayList<Hotel> arrayList)
	{
		if (arrayList == null)
		{
			return;
		}

		int size = arrayList.size();
		mHotelList = new ArrayList<Hotel>(MAX_CHILD_VIEW);

		for (int i = 0; i < MAX_CHILD_VIEW && i < size; i++)
		{
			mHotelList.add(arrayList.get(i));
		}

		int hotelSize = mHotelList.size();

		mRefreshingInfoWindow = false;

		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (hotelSize == 1)
		{
			if (mRootView == null || mRootView.getId() != R.layout.view_map_popup)
			{
				mRootView = layoutInflater.inflate(R.layout.view_map_popup, null);
			}

			mRootView.setOnTouchListener(this);
			mRootView.setTag(mHotelList.get(0));

			ImageView imageView = (ImageView) mRootView.findViewById(R.id.iv_hotel_row_img);
			imageView.setBackgroundResource(R.drawable.img_placeholder);

			ImageView cancelView = (ImageView) mRootView.findViewById(R.id.cancelView);
			cancelView.setOnTouchListener(mCancelTouch);

		} else
		{
			switch (hotelSize)
			{
				case 2:
				{
					if (mRootView == null || mRootView.getId() != R.layout.views_map_popup02)
					{
						mRootView = layoutInflater.inflate(R.layout.views_map_popup02, null);
					}

					mChildView[0] = mRootView.findViewById(R.id.view01);
					mChildView[1] = mRootView.findViewById(R.id.view02);
					break;
				}

				case 3:
				{
					if (mRootView == null || mRootView.getId() != R.layout.views_map_popup03)
					{
						mRootView = layoutInflater.inflate(R.layout.views_map_popup03, null);
					}

					mChildView[0] = mRootView.findViewById(R.id.view01);
					mChildView[1] = mRootView.findViewById(R.id.view02);
					mChildView[2] = mRootView.findViewById(R.id.view03);
					break;
				}

				case 4:
				{
					if (mRootView == null || mRootView.getId() != R.layout.views_map_popup04)
					{
						mRootView = layoutInflater.inflate(R.layout.views_map_popup04, null);
					}

					mChildView[0] = mRootView.findViewById(R.id.view01);
					mChildView[1] = mRootView.findViewById(R.id.view02);
					mChildView[2] = mRootView.findViewById(R.id.view03);
					mChildView[3] = mRootView.findViewById(R.id.view04);
					break;
				}
			}

			ImageView cancelView = (ImageView) mRootView.findViewById(R.id.cancelView);
			cancelView.setOnTouchListener(mCancelTouch);

			for (int i = 0; i < hotelSize; i++)
			{
				if (i < hotelSize)
				{
					mChildView[i].setVisibility(View.VISIBLE);
					mChildView[i].setTag(mHotelList.get(i));
					mChildView[i].setOnTouchListener(mChildTouch);

					ImageView imageView = (ImageView) mChildView[i].findViewById(R.id.iv_hotel_row_img);
					imageView.setBackgroundResource(R.drawable.img_placeholder);
				} else
				{
					mChildView[i].setVisibility(View.GONE);
					mChildView[i].setTag(null);
					mChildView[i].setOnTouchListener(null);
				}
			}
		}
	}

	public void setOnInfoWindowClickListener(OnInfoWindowClickListener listener)
	{
		mOnInfoWindowClickListener = listener;
	}

	public View getInfoWindow()
	{
		return mRootView;
	}

	private final Runnable mConfirmClickRunnable = new Runnable()
	{
		public void run()
		{
			if (mIsPressed == true)
			{
				mIsPressed = false;

				mHandler.removeCallbacks(mConfirmClickRunnable);

				if (mSelectedView != null)
				{
					mOnInfoWindowClickListener.onInfoWindowClickListener((Hotel) mSelectedView.getTag());
				}
			}
		}
	};

	private final Runnable mConfirmCancelClickRunnable = new Runnable()
	{
		public void run()
		{
			if (mIsPressed == true)
			{
				mHandler.removeCallbacks(mConfirmClickRunnable);
				mOnInfoWindowClickListener.onCancelInfoWindowClickListener();
			}
		}
	};

	private OnTouchListener mChildTouch = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					if (mIsPressed == false)
					{
						prev_x = (int) event.getX();
						prev_y = (int) event.getY();

						mSelectedView = null;
						mIsPressed = true;
						mHandler.removeCallbacks(mConfirmClickRunnable);
					}
					break;

				case MotionEvent.ACTION_UP:
					mSelectedView = v;
					mHandler.postDelayed(mConfirmClickRunnable, 150);
					break;

				case MotionEvent.ACTION_MOVE:
				{
					if (mIsPressed == true)
					{
						int x = (int) (prev_x - event.getX());
						int y = (int) (prev_y - event.getY());

						int distance = x * x + y * y;
						if (distance > (mViewConfiguration.getScaledWindowTouchSlop() >> 1))
						{
							event.setAction(MotionEvent.ACTION_CANCEL);
							onTouch(v, event);
						}
					}
					break;
				}

				case MotionEvent.ACTION_CANCEL:
					if (mIsPressed == true)
					{
						mSelectedView = null;
						mIsPressed = false;
						mHandler.removeCallbacks(mConfirmClickRunnable);
					}
					break;
				default:
					break;
			}

			return true;
		}
	};

	private OnTouchListener mCancelTouch = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					if (mIsPressed == false)
					{
						prev_x = (int) event.getX();
						prev_y = (int) event.getY();

						mIsPressed = true;
						mHandler.removeCallbacks(mConfirmCancelClickRunnable);
					}
					break;

				case MotionEvent.ACTION_UP:
					if (mIsPressed == true)
					{
						mHandler.postDelayed(mConfirmCancelClickRunnable, 150);
					}
					break;

				case MotionEvent.ACTION_MOVE:
				{
					if (mIsPressed == true)
					{
						int x = (int) (prev_x - event.getX());
						int y = (int) (prev_y - event.getY());

						int distance = x * x + y * y;
						if (distance > (mViewConfiguration.getScaledWindowTouchSlop() >> 1))
						{
							event.setAction(MotionEvent.ACTION_CANCEL);
							onTouch(v, event);
						}
					}
					break;
				}

				case MotionEvent.ACTION_CANCEL:
					if (mIsPressed == true)
					{
						mIsPressed = false;
						mHandler.removeCallbacks(mConfirmCancelClickRunnable);
					}
					break;

				default:
					break;
			}

			return true;
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		final int cancelButtonWidth = Util.dpToPx(mContext, 30);

		// 상단 취소 버튼.
		if (v.getWidth() - cancelButtonWidth <= event.getX() && event.getX() <= v.getWidth() && v.getHeight() - cancelButtonWidth <= event.getY() && event.getY() <= v.getHeight())
		{

		} else if (0 <= event.getX() && event.getX() <= v.getWidth() && 0 <= event.getY() && event.getY() <= v.getHeight())
		{
			mChildTouch.onTouch(v, event);
		}

		return false;
	}

	@Override
	public View getInfoWindow(Marker marker)
	{
		if (mRefreshingInfoWindow == true)
		{

		} else
		{
			if (mHotelList == null)
			{
				return null;
			}

			int size = mHotelList.size();

			if (size == 1)
			{
				makeLayout(marker, mRootView, mHotelList.get(0));
			} else
			{
				for (int i = 0; i < size; i++)
				{
					makeLayout(marker, mChildView[i], mHotelList.get(i));
				}
			}
		}

		return mRootView;
	}

	private void makeLayout(final Marker marker, View view, Hotel hotel)
	{
		RelativeLayout llHotelRowContent = (RelativeLayout) view.findViewById(R.id.ll_hotel_row_content);
		final ImageView img = (ImageView) view.findViewById(R.id.iv_hotel_row_img);
		TextView name = (TextView) view.findViewById(R.id.tv_hotel_row_name);
		TextView price = (TextView) view.findViewById(R.id.tv_hotel_row_price);
		TextView discount = (TextView) view.findViewById(R.id.tv_hotel_row_discount);
		TextView sold_out = (TextView) view.findViewById(R.id.tv_hotel_row_soldout);
		TextView address = (TextView) view.findViewById(R.id.tv_hotel_row_address);
		TextView grade = (TextView) view.findViewById(R.id.hv_hotel_grade);

		DecimalFormat comma = new DecimalFormat("###,##0");
		String strPrice = comma.format(Integer.parseInt(hotel.getPrice()));
		String strDiscount = comma.format(Integer.parseInt(hotel.getDiscount()));

		address.setText(hotel.getAddress());
		name.setText(hotel.getName());

		Spanned currency = Html.fromHtml(mContext.getResources().getString(R.string.currency));
		price.setText(strPrice + currency);
		price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		discount.setText(strDiscount + currency);

		name.setSelected(true); // Android TextView marquee bug

		final int colors[] = { Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000") };
		final float positions[] = { 0.0f, 0.01f, 0.02f, 0.17f, 0.38f };

		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());

		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
		{
			@Override
			public Shader resize(int width, int height)
			{
				return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
			}
		};

		p.setShaderFactory(sf);
		llHotelRowContent.setBackgroundDrawable(p);

		// grade
		grade.setText(hotel.getCategory().getName(mContext));
		grade.setBackgroundResource(hotel.getCategory().getColorResId());

		name.setTypeface(DailyHotel.getBoldTypeface());
		discount.setTypeface(DailyHotel.getBoldTypeface());

		if (mRefreshingInfoWindow == false)
		{
			// Used AQuery
			AQuery aq = new AQuery(view);
			Bitmap cachedImg = VolleyImageLoader.getCache(hotel.getImage());

			if (cachedImg == null)
			{ // 힛인 밸류가 없다면 이미지를 불러온 후 캐시에 세이브
				BitmapAjaxCallback cb = new BitmapAjaxCallback()
				{
					@Override
					protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
					{
						VolleyImageLoader.putCache(url, bm);
						super.callback(url, iv, bm, status);

						mRefreshingInfoWindow = true;
						marker.showInfoWindow();
					}
				};

				cb.url(hotel.getImage());
				aq.id(img).image(cb);

			} else
			{
				aq.id(img).image(cachedImg);
				//				cachedImg.recycle();

				mRefreshingInfoWindow = true;
				marker.showInfoWindow();
			}
		}

		// 객실이 1~2 개일때 label 표시
		int avail_cnt = hotel.getAvailableRoom();

		// SOLD OUT 표시
		if (avail_cnt == 0)
		{
			sold_out.setVisibility(View.VISIBLE);
		} else
		{
			sold_out.setVisibility(View.GONE);
		}
	}

	@Override
	public View getInfoContents(Marker marker)
	{
		return null;
	}

}
