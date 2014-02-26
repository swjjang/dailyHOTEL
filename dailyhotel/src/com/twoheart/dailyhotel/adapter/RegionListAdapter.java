package com.twoheart.dailyhotel.adapter;

import static com.twoheart.dailyhotel.util.AppConstants.*;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.AppConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegionListAdapter extends ArrayAdapter<String>{
	private ArrayList<String> items;
	private Context context;
	private int resourceId;
	
	private SharedPreferences prefs;
	
	public RegionListAdapter(Context context, int resourceId, ArrayList<String> items) {
		super(context, resourceId, items);
		this.items = items;
		this.context = context;
		this.resourceId = resourceId;
		
		prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		if(prefs.getString(PREFERENCE_REGION_SELECT, null) == null) {
			AppConstants.clickState = new Boolean[items.size()] ;
			for(int i=0; i<items.size(); i++) {
				if(i==0)
					AppConstants.clickState[i] = true;
				else
					AppConstants.clickState[i] = false;
			}
		} else {
			AppConstants.clickState = new Boolean[items.size()] ;
			for(int i=0; i<items.size(); i++) {
				if(i== prefs.getInt(PREFERENCE_REGION_INDEX, 0))
					AppConstants.clickState[i] = true;
				else
					AppConstants.clickState[i] = false;
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(v == null	) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(resourceId, null);
			v.setTag(position);
		}
		
		String name = items.get(position);
		
		if(name != null) {
			TextView tv_name = (TextView) v.findViewById(R.id.tv_list_row_region_name);
			tv_name.setText(name);
		}
		
		if(AppConstants.clickState[position]) { 	// 선택된 상태
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.layout_list_row_region);
			layout.setBackgroundResource(R.drawable.list_region_press);
		} else {		// 선택안됨
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.layout_list_row_region);
			layout.setBackgroundDrawable(null);
		}
		
		return v;
	}
}