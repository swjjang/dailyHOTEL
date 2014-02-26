package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.*;

import java.util.ArrayList;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.CreditListAdapter;
import com.twoheart.dailyhotel.adapter.RegionListAdapter;
import com.twoheart.dailyhotel.util.AppConstants;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RegionListFragment extends Fragment implements OnItemClickListener{
	private View view;
	private ArrayList<String> regionList;
	
	private ListView listView;
	private RegionListAdapter adapter;
	
	private SharedPreferences prefs;
	
//	public static RegionListFragment newInstance(ArrayList<String> list) {
//		regionList = list;
//		RegionListFragment fragment = new RegionListFragment();
//		return fragment;
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_region_list, null);
		
		regionList = getArguments().getStringArrayList("regionList");
		setListView();
		return view;
	}
	
	public void setListView() {
		listView = (ListView) view.findViewById(R.id.listview_region);
		adapter = new RegionListAdapter(view.getContext(), R.layout.list_row_region, regionList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}
	
	// 선택한 지역으로 switch
	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
		
		for(int i=0; i<clickState.length; i++) {
			clickState[i] = false;
		}
		clickState[position] = true;
		
		prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putString(PREFERENCE_REGION_SELECT, regionList.get(position));
		ed.putInt(PREFERENCE_REGION_INDEX, position);
		ed.commit();
		
		Fragment fragment = new HotelListFragment();
		MainActivity activity = (MainActivity) view.getContext();
		activity.switchContent(fragment);
	}
	
}
