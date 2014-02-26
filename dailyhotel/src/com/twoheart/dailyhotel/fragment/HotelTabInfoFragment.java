package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.DETAIL;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_DAY;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_IDX;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_MONTH;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_YEAR;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.ui.DhManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HotelTabInfoFragment extends Fragment{

	private static final String TAG = "HotelTabInfoFragment";
	
	private View view;
	private LinearLayout layout;
	
	private SharedPreferences prefs;
	
	public static HotelTabInfoFragment newInstance() {
		HotelTabInfoFragment fragment = new HotelTabInfoFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_hotel_tab_info, null);
		
		loadResource();
		
		prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		String hotel_idx = prefs.getString(PREFERENCE_HOTEL_IDX, null);
		String year = prefs.getString(PREFERENCE_HOTEL_YEAR, null);
		String month = prefs.getString(PREFERENCE_HOTEL_MONTH, null);
		String day = prefs.getString(PREFERENCE_HOTEL_DAY, null);

		new GeneralHttpTask(infoListener, view.getContext()).execute(REST_URL + DETAIL + hotel_idx + "/" + year + "/" + month + "/" + day);
		
		return view;
	}
	
	public void loadResource() {
		layout = (LinearLayout) view.findViewById(R.id.layout_hotel_tab_info);
	}
	
	private void parseJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			JSONArray specArr = obj.getJSONArray("spec");
			
			for(int i=0; i<specArr.length(); i++) {
				
				ArrayList<String> contentList = new ArrayList<String>();
				
				JSONObject specObj = specArr.getJSONObject(i);
				String key = specObj.getString("key");
				JSONArray valueArr = specObj.getJSONArray("value");
				for(int j=0; j<valueArr.length(); j++) {
					JSONObject valueObj = valueArr.getJSONObject(j);
					String value = valueObj.getString("value");
					contentList.add(value);
				}
				addView(key, contentList);
			}
			
		} catch (Exception e) {
			Log.d(TAG, "TagDataParser" + "->" + e.toString());
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void addView(String subject, ArrayList<String> contentList) {
		LayoutInflater inflater = (LayoutInflater)view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
		View layout_view = inflater.inflate(R.layout.list_row_hotel_tab_info, layout, false);
		TextView tv_subject = (TextView) layout_view.findViewById(R.id.tv_hotel_tab_info_subject);
		
		for(int i=0; i<contentList.size(); i++) {
			View rowLayout = inflater.inflate(R.layout.list_row_hotel_tab_info_content, (LinearLayout)layout_view, false);
			TextView tv_content = (TextView) rowLayout.findViewById(R.id.tv_hotel_tab_info_content);
			tv_content.setText(contentList.get(i));
			((LinearLayout)layout_view).addView(rowLayout);
		}
		
		View line = new View(view.getContext());
		line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, DhManager.getPixels(1, view.getContext())));
//		line.setPadding(0, DhManager.getPixels(1, view.getContext()), 0, 0);
		line.setBackgroundResource(R.color.dh_gray50);
		
		tv_subject.setText(subject);
		
		layout.addView(layout_view);
		layout.addView(line);
	}
	
	protected OnCompleteListener infoListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "infoListener onTaskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};
}
