package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.DETAIL;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_DAY;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_IDX;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_MONTH;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_YEAR;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.ImageViewAdapter;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class HotelTabBookingFragment extends Fragment implements OnTouchListener{
	
	private static final String TAG = "HotelTabBookingFragment"; 
	
	private View view;
	private ImageViewAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;
	private ArrayList<String> urlList;
	
	private TextView tv_name, tv_address, tv_price, tv_discount;
	
	private ImageView iv_lowest;
	
	// sliding img
	private Handler handler;
	private Timer swipeTimer;
	private int currentPage = 0;
	
	private SharedPreferences prefs;
	
	
	public static HotelTabBookingFragment newInstance() {
		HotelTabBookingFragment fragment = new HotelTabBookingFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_hotel_tab_booking, null);
		loadResource();
		
		prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		String hotel_idx = prefs.getString(PREFERENCE_HOTEL_IDX, null);
		String year = prefs.getString(PREFERENCE_HOTEL_YEAR, null);
		String month = prefs.getString(PREFERENCE_HOTEL_MONTH, null);
		String day = prefs.getString(PREFERENCE_HOTEL_DAY, null);

		new GeneralHttpTask(bookingListener, view.getContext()).execute(REST_URL + DETAIL + hotel_idx + "/" + year + "/" + month + "/" + day);
		
		return view;
	}
	
	public void loadResource() {
		tv_name = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_name);
		tv_address = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_address);
		tv_price = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_price);
		tv_discount = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_discount);
		
		iv_lowest = (ImageView) view.findViewById(R.id.iv_hotel_tab_booking_lowest);
		iv_lowest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	dialog.dismiss();     //닫기
				    }
				});
				alert.setMessage("다른 곳을 통해 더 저렴한 가격으로 숙박하셨을 경우 그 차액의 두 배를 보상해드립니다.");
				alert.show();
			}
		});
	}
	
	public void parseJson(String str) {
		
		try {
			JSONObject obj = new JSONObject(str);
			JSONArray bookingArr = obj.getJSONArray("detail");
			JSONObject detailObj =  bookingArr.getJSONObject(0);
			
			DecimalFormat comma = new DecimalFormat("###,##0");
			String strDiscount = comma.format(Integer.parseInt(detailObj.getString("discount")));
			String strPrice = comma.format(Integer.parseInt(detailObj.getString("price")));
			
			tv_name.setText(detailObj.getString("hotel_name"));
			tv_address.setText(detailObj.getString("address"));
			tv_discount.setText("￦" + strDiscount);
			tv_price.setText("￦" + strPrice);
			tv_price.setPaintFlags(tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			
			//grade
			String cat = detailObj.getString("cat");
			ImageView grade = (ImageView) view.findViewById(R.id.iv_hotel_tab_booking_grade);
			
			if(cat.equals("5")) {
				grade.setImageResource(R.drawable.dh_grademark_biz);
			} else if(cat.equals("2")) {
				grade.setImageResource(R.drawable.dh_grademark_boutique);
			} else if(cat.equals("3")) {
				grade.setImageResource(R.drawable.dh_grademark_residence);
			} else {
				grade.setImageResource(R.drawable.dh_grademark_special);
			}
			
			JSONArray imgArr = detailObj.getJSONArray("img");
			urlList = new ArrayList<String>();
			
			for(int i=0; i<imgArr.length(); i++) {
				if(i==0)
					continue;
				JSONObject imgObj = imgArr.getJSONObject(i);
				urlList.add(imgObj.getString("path"));
			}
			
			adapter = new ImageViewAdapter(getActivity().getSupportFragmentManager(), urlList);
			pager = (ViewPager) view.findViewById(R.id.vp_hotel_tab_booking_img);
			pager.setAdapter(adapter);
			pager.setOnTouchListener(this);
			CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.cp_hotel_tab_booking_indicator);
			this.indicator = indicator;
			indicator.setViewPager(pager);
			indicator.setSnap(true);
			
			
//			handler = new Handler();
//			final Runnable Update = new Runnable() {
//                public void run() {
//                	
//                	currentPage = pager.getCurrentItem();
//                	currentPage++;
//                    if (currentPage == urlList.size()) {
//                        currentPage = 0;
//                    }
//                    pager.setCurrentItem(currentPage, true);
//                }
//            };
//
//            swipeTimer = new Timer();
//            swipeTimer.schedule(new TimerTask() {
//
//                @Override
//                public void run() {
//                	handler.post(Update);
//                }
//            }, 5000, 5000);
			
			handler = new Handler() {
				public void handleMessage(Message msg) {
					
					currentPage = pager.getCurrentItem();
                	currentPage++;
                    if (currentPage == urlList.size()) {
                        currentPage = 0;
                    }
                    pager.setCurrentItem(currentPage, true);
					this.sendEmptyMessageDelayed(0, 5000);
				}
			};
			
			handler.sendEmptyMessageDelayed(0, 5000);
			
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(v.getId() == pager.getId()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				handler.removeMessages(0);
				break;
			case MotionEvent.ACTION_MOVE:
				handler.removeMessages(0);
				break;
			
			case MotionEvent.ACTION_UP:
				Log.d("Asdadasd", "action up");
				handler.sendEmptyMessageDelayed(0, 5000);
			default:
				break;
			}
		}
		
		return false;
	}
	
	protected OnCompleteListener bookingListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d("TAG", "bookingListener onTaskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};
}
