package com.twoheart.dailyhotel.credit;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.twoheart.dailyhotel.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CreditListAdapter extends ArrayAdapter<CreditListElement>{
	
	private ArrayList<CreditListElement> items;
	private Context context;
	private int resourceId;
	
	
	public CreditListAdapter(Context context, int resourceId, ArrayList<CreditListElement> items) {
		super(context, resourceId, items);
		this.items = items;
		this.context = context;
		this.resourceId = resourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(v == null	) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(resourceId, null);
			v.setTag(position);
		}
		
		CreditListElement element = items.get(position);
		
		if(element != null) {
			TextView bonus = (TextView) v.findViewById(R.id.list_row_credit_bonus);
			TextView content = (TextView) v.findViewById(R.id.list_row_credit_content);
			TextView expires = (TextView) v.findViewById(R.id.list_row_credit_expires);
			
			DecimalFormat comma = new DecimalFormat("###,##0");
			String strBonus = comma.format(Integer.parseInt(element.getBonus()));
			
			
			bonus.setText("￦" + strBonus);
			content.setText(element.getContent());
			expires.setText("유효기간\n" + "[" + element.getExpires() + "]");
		}
		
		return v;
	}
	
}
