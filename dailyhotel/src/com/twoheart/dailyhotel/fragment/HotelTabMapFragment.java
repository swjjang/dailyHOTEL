package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.GMapActivity;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HotelTabMapFragment extends Fragment implements OnMapClickListener{
	
	public static HotelTabMapFragment newInstance() {
		HotelTabMapFragment fragment = new HotelTabMapFragment();
		return fragment;
	}
	private static final String TAG = "HotelTabMapFragment";
	
	private View view;
	private GoogleMap googleMap;
	
	private TextView tv_name, tv_address; 
	
	private SharedPreferences prefs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_hotel_tab_map, null);
		
		loadResource();
		prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		String hotel_idx = prefs.getString(PREFERENCE_HOTEL_IDX, null);
		String year = prefs.getString(PREFERENCE_HOTEL_YEAR, null);
		String month = prefs.getString(PREFERENCE_HOTEL_MONTH, null);
		String day = prefs.getString(PREFERENCE_HOTEL_DAY, null);

		new GeneralHttpTask(mapListener, view.getContext()).execute(REST_URL + DETAIL + hotel_idx + "/" + year + "/" + month + "/" + day);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		try {
			googleMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_map)).getMap();
			googleMap.setOnMapClickListener(this);
		} catch (Exception e) {
			Log.d(TAG, "google map null");
		}
		super.onActivityCreated(savedInstanceState);
	}
	
	public void loadResource() {
		tv_name = (TextView) view.findViewById(R.id.tv_hotel_tab_map_name);
		tv_address = (TextView) view.findViewById(R.id.tv_hotel_tab_map_address);
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		Intent i = new Intent(view.getContext(), GMapActivity.class);
		startActivity(i);
	}

	// 마커 추가
	public void addMarker(Double lat, Double lng, String hotel_name) {
		if(googleMap != null) {
			googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
			LatLng address = new LatLng(lat,lng);
			CameraPosition cp = new CameraPosition.Builder().target((address )).zoom(15).build();
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			googleMap.setMyLocationEnabled(false);
		}
	}

	public void parseJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			JSONArray arr =  obj.getJSONArray("detail");
			JSONObject mapObj = arr.getJSONObject(0);
			
			String lat = Double.toString(mapObj.getDouble("lat"));
			String lng = Double.toString(mapObj.getDouble("lng"));
			String name = mapObj.getString("hotel_name");
			String address = mapObj.getString("address");
			String cat = mapObj.getString("cat");
				
			// map detail activity에 사용할 data 저장
			prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putString(PREFERENCE_HOTEL_LNG, lng);
			ed.putString(PREFERENCE_HOTEL_LAT, lat);
			ed.putString(PREFERENCE_HOTEL_NAME, name);
			ed.commit();
			
			tv_name.setText(name);
			tv_address.setText(address);
			
			ImageView grade = (ImageView) view.findViewById(R.id.iv_hotel_tab_map_grade);
			//grade
			if(cat.equals("biz")) {
				grade.setImageResource(R.drawable.dh_grademark_biz);
			} else if(cat.equals("boutique")) {
				grade.setImageResource(R.drawable.dh_grademark_boutique);
			} else if(cat.equals("residence")) {
				grade.setImageResource(R.drawable.dh_grademark_residence);
			} else if(cat.equals("special")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			}
			
			addMarker(mapObj.getDouble("lat"), mapObj.getDouble("lng"), name);
			
		} catch (Exception e) {
			Log.d(TAG, "parseJson " + e.toString());
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected OnCompleteListener mapListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "mapListener onTaskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};
}
