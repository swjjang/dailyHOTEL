package com.twoheart.dailyhotel.hotel;

import static com.twoheart.dailyhotel.AppConstants.HOTEL;
import static com.twoheart.dailyhotel.AppConstants.LOCATION_LIST;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_REGION_DEFALUT;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_REGION_SELECT;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_SELECTED_MENU;
import static com.twoheart.dailyhotel.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.AppConstants.SALE_TIME;
import static com.twoheart.dailyhotel.AppConstants.SHARED_PREFERENCES_NAME;
import static com.twoheart.dailyhotel.AppConstants.TIME;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.twoheart.dailyhotel.ErrorFragment;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.asynctask.GeneralHttpTask;
import com.twoheart.dailyhotel.asynctask.onCompleteListener;
import com.twoheart.dailyhotel.booking.BookingListFragment;
import com.twoheart.dailyhotel.utils.LoadingDialog;

public class HotelListFragment extends SherlockFragment implements OnItemClickListener{
	
	private final static String TAG = "HotelListFragment";
	
	private final static int HOTEL_LIST_FRAGMENT = 1;
	
	private ArrayList<HotelListElement> items;
	private PullToRefreshListView listView;
	private HotelListAdapter adapter;
	
	private View view;

	private String currentYear;
	private String currentMon;
	private String currentDay;
	
	private int selectedPosition;
	
	private ArrayList<String> regionList;
	
	private SharedPreferences prefs;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	private long openTime;
	private long closeTime;
	
	// Jason | Google analytics
	@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Hotel View");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		view = inflater.inflate(R.layout.fragment_hotel_list, null);
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(view.getContext());
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");
		
		// Actionbar setting
		MainActivity activity = (MainActivity)view.getContext();
		activity.hideMenuItem();
		activity.addMenuItem("지역");
		
		// Right Sliding setting
//		activity.getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
//		activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//		activity.getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_right);
//		activity.getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadow_right);
		
		getCalendar();
		
//		LoadingDialog.showLoading(view.getContext());
//		new GeneralHttpTask(timerListener, view.getContext()).execute(REST_URL + TIME);
		
		return view;
	}
	
	@Override
	public void onResume() {
		LoadingDialog.showLoading(view.getContext());
//		new GeneralHttpTask(timerListener, view.getContext()).execute(REST_URL + TIME);
		new GeneralHttpTask(saleTimeListener, view.getContext()).execute(REST_URL + SALE_TIME);
		super.onResume();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	// get current calendar
	public void getCalendar() {
		Calendar c = Calendar.getInstance();
		currentYear = Integer.toString(c.get(Calendar.YEAR));
		currentYear = currentYear.substring(2, 4);
		currentMon = Integer.toString(c.get(Calendar.MONTH) + 1);
		if( (c.get(Calendar.MONTH) + 1) < 10)
			currentMon = "0" + currentMon;
		currentDay = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		if(c.get(Calendar.DAY_OF_MONTH) < 10)
			currentDay = "0" + currentDay;
	}
	
	public void setListView() {
		listView = (PullToRefreshListView)view.findViewById(R.id.listview_hotel_list);
		adapter = new HotelListAdapter(view.getContext(), R.layout.list_row_hotel, items);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			
			// listview 끌어서 새로고침
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(view.getContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				new GeneralHttpTask(refreshTimerListener, view.getContext()).execute(REST_URL + TIME);
				
			}
		});
		
		// footer 추가
		final ListView lv = listView.getRefreshableView();
		LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
		View footer = inflater.inflate(R.layout.footer_hotel_list, null);
		lv.addFooterView(footer);
		Button btn_footer = (Button) view.findViewById(R.id.btn_footer);
		btn_footer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(view.getContext(), EventWebActivity.class);
				MainActivity activity = (MainActivity) view.getContext();
				activity.startActivity(i);
				activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
			}
		});
	}
	
	public void parseJson(String str) {
		if(listView == null) {
			setListView();
		}
		items.clear();
		
		try {
			JSONObject json = new JSONObject(str);
			JSONArray hotelArr = json.getJSONArray("hotels");
			
			for(int i=0; i<hotelArr.length(); i++) {
				JSONObject obj = hotelArr.getJSONObject(i);
				
				String name = obj.getString("name");
				String price = obj.getString("price");
				String discount = obj.getString("discount");
				String address = obj.getString("addr_summary");
				String cat = obj.getString("cat");
				int idx =  obj.getInt("idx");
				int avail_cnt = obj.getInt("avail_room_count");
				int seq = obj.getInt("seq");
				
				JSONArray arr = obj.getJSONArray("img");
				String img = "default";
				if(arr.length() != 0) {
					JSONObject arrObj = arr.getJSONObject(0);
					img = arrObj.getString("path");
				}
				items.add(new HotelListElement(img, name, price, discount, address, idx, avail_cnt, seq, cat));
			}
			
			listSort();
			listHide();
			
			// listview notify
			adapter = new HotelListAdapter(view.getContext(), R.layout.list_row_hotel, items);
			adapter.notifyDataSetChanged();
			listView.setAdapter(adapter);
			listView.invalidate();
			
			// get region list
			new GeneralHttpTask(regionListListener, view.getContext()).execute(REST_URL + LOCATION_LIST);
			
		} catch (Exception e) {
			Log.d(TAG, "JSON Parsing Error : " + e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseRegionJson(String str) {
		try {
			
			regionList = new ArrayList<String>();
			
			JSONArray arr = new JSONArray(str);
			for(int i=0; i<arr.length(); i++) {
				JSONObject obj =  arr.getJSONObject(i);
				String name = obj.getString("name"	);
				regionList.add(name);
			}
			
			setRegionList();
			
		} catch (Exception e) {
			Log.d(TAG, "parseRegionJson " + e.toString());
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}

	public boolean checkTimer(String str) {
		boolean result = false;
		
		Date now = new Date(Long.parseLong(str.trim()));
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		String time = format.format(now);
		long hour = Integer.parseInt(time.substring(0, 2));
		long min = Integer.parseInt(time.substring(2,4));
		long sec = Integer.parseInt(time.substring(4,6));
		
		long curTime = (hour * 60 * 60 ) + (min * 60) + sec;
//		long targetTime = 11 * 60 * 60;
		
//		long waitTime = openTime - curTime;
		
//		if(waitTime < 0)
//			result  = false;
//		else 
//			result = true;
		
		Log.d("aaaaaaaaaaaaa", curTime + "   " + openTime + "   " + closeTime);
		// true 면 호텔판매시간 아님
		if( (openTime < curTime) && (curTime < closeTime)) {
			result = false;
		}
			
		else if((curTime < closeTime) && (closeTime < openTime)) {
			result = false;
		}
		else {
			result = true;
		}
		
		return result;
		// Open anytime...
//		return false;
	}
	
	public void setRegionList() {
		Bundle argument = new Bundle();
		argument.putStringArrayList("regionList", regionList);
//		RegionListFragment fragment =  new RegionListFragment();
//		fragment.setArguments(argument);
//		MainActivity activity = (MainActivity) view.getContext();
//		FragmentTransaction t = activity.getSupportFragmentManager().beginTransaction();
//		activity.getSupportFragmentManager()
//		.beginTransaction()
//		.replace(R.id.menu_frame_right, fragment)
//		.commitAllowingStateLoss();
	}
	
	// sold out 밑으로
	public void listSort() {
//		for(int i=0; i < items.size(); i++) {
//			if(items.get(i).getAvali_cnt() <= 0 ) {
//				items.get(i).setSeq(items.get(i).getSeq() * 100);
//			}
//		}
		Comparator<HotelListElement> comparator = new Comparator<HotelListElement>() {
	         public int compare(HotelListElement o1, HotelListElement o2) {
	            // 숫자정렬
	            return Integer.parseInt(o1.getSeq() + "")
	                  - Integer.parseInt(o2.getSeq() + "");
	         }
	      };
	      Collections.sort(items, comparator);
	}
	
	// 음수제거 (숨김기능)
	public void listHide() {
		for(Iterator<HotelListElement> iterator = items.iterator(); iterator.hasNext(); ) {
			HotelListElement element = iterator.next();
			if(element.getSeq() < 0) {
				iterator.remove();
			}
		}
	}
	
	// 호텔 클릭시
	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
		
		selectedPosition = position-1;
		
		LoadingDialog.showLoading(view.getContext());
		new GeneralHttpTask(clickTimerListener, view.getContext()).execute(REST_URL + TIME);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		menu.add("지역").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == HOTEL_LIST_FRAGMENT) {
			if(resultCode == getActivity().RESULT_OK) {
				
				prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
				SharedPreferences.Editor ed = prefs.edit();
				ed.putString(PREFERENCE_SELECTED_MENU, "booking");
				ed.commit();
				
				MainActivity activity = (MainActivity) view.getContext();
				activity.changeMenu();
				
				Fragment fragment = new BookingListFragment();
		        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		        ft.replace(R.id.content_frame, fragment);
		        ft.commitAllowingStateLoss();
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	protected onCompleteListener saleTimeListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "saleTimeListener onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			try {
				JSONObject obj = new JSONObject(result);
				String open = obj.getString("open");
				String close = obj.getString("close");
			
				openTime = (Long.parseLong(open.substring(0, 2)) * 60 * 60) + (Long.parseLong(open.substring(3, 5)) * 60);
				closeTime = (Long.parseLong(close.substring(0, 2)) * 60 * 60) + (Long.parseLong(close.substring(3, 5)) * 60);
				
				new GeneralHttpTask(timerListener, view.getContext()).execute(REST_URL + TIME);
			} catch (Exception e) {
				Log.d(TAG, "JSON Parsing Error : " + e.toString());
				LoadingDialog.hideLoading();
				Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	protected onCompleteListener timerListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			
			MainActivity activity = (MainActivity) view.getContext();
			
			if(checkTimer(result)) {
				LoadingDialog.hideLoading();
				activity.switchContent(new WaitTimerFragment());
			} else {
				prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
				String select = prefs.getString(PREFERENCE_REGION_SELECT, null);
				if(select == null) {	// 지역 선택 하기전 DEFAULT
					String region = prefs.getString(PREFERENCE_REGION_DEFALUT, "서울");
					activity.changeTitle(region);
					region = region.replace(" ", "%20");
					region = region.replace("|", "%7C");
					new GeneralHttpTask(hotelListListener, view.getContext()).execute(REST_URL+ HOTEL + region + "/near/0/0/0/1000/" + currentYear + "/" + currentMon + "/" + currentDay);
				} else {		// 지역 선택시
					activity.changeTitle(select);
					select = select.replace(" ", "%20");
					select = select.replace("|", "%7C");
					new GeneralHttpTask(hotelListListener, view.getContext()).execute(REST_URL+ HOTEL + select + "/near/0/0/0/1000/" + currentYear + "/" + currentMon + "/" + currentDay);
				}
			}
		}
	};

	// hotel list callback
	protected onCompleteListener hotelListListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			// list 초기화
			items = new ArrayList<HotelListElement>();
			parseJson(result);
		}
	};
	
	// refresh list callback
	protected onCompleteListener refreshListListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			((PullToRefreshListView) listView).onRefreshComplete();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			((PullToRefreshListView) listView).onRefreshComplete();
			items = new ArrayList<HotelListElement>();
			parseJson(result);
		}
	};
	
	protected onCompleteListener regionListListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseRegionJson(result);
			LoadingDialog.hideLoading();
		}
	};
	
	protected onCompleteListener refreshTimerListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			((PullToRefreshListView) listView).onRefreshComplete();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			
			MainActivity activity = (MainActivity) view.getContext();
			
			if(checkTimer(result)) {
				LoadingDialog.hideLoading();
				activity.switchContent(new WaitTimerFragment());
				
			} else {
				prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
				String select = prefs.getString(PREFERENCE_REGION_SELECT, null);
				
				if(select == null) {	// 지역 선택 하기전 DEFAULT
					String region = prefs.getString(PREFERENCE_REGION_DEFALUT, "서울");
					region = region.replace(" ", "%20");
					region = region.replace("|", "%7C");
					new GeneralHttpTask(refreshListListener, view.getContext()).execute(REST_URL+ HOTEL + region + "/near/0/0/0/1000/" + currentYear + "/" + currentMon + "/" + currentDay);
				} else {		// 지역 선택시
					select = select.replace(" ", "%20");
					select = select.replace("|", "%7C");
					new GeneralHttpTask(refreshListListener, view.getContext()).execute(REST_URL+ HOTEL + select + "/near/0/0/0/1000/" + currentYear + "/" + currentMon + "/" + currentDay);
				}
			}
		}
	};
	
	protected onCompleteListener clickTimerListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			MainActivity activity = (MainActivity) view.getContext();
			
			if(checkTimer(result)) {
				LoadingDialog.hideLoading();
				activity.switchContent(new WaitTimerFragment());
				
			} else {
				
				LoadingDialog.hideLoading();
				
				Intent i = new Intent(view.getContext(), HotelTabActivity.class);
				i.putExtra("hotel_name", items.get(selectedPosition).getName());
				i.putExtra("hotel_idx", items.get(selectedPosition).getIdx());
				i.putExtra("available_cnt", items.get(selectedPosition).getAvali_cnt());
				i.putExtra("year", currentYear);
				i.putExtra("month", currentMon);
				i.putExtra("day", currentDay);
				
				startActivityForResult(i, HOTEL_LIST_FRAGMENT);
			}
		}
	};
	
	
}
