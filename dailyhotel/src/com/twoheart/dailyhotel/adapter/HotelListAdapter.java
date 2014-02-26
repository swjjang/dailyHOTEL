package com.twoheart.dailyhotel.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.Hotel;
import com.twoheart.dailyhotel.util.ImageLoader;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HotelListAdapter extends ArrayAdapter<Hotel>{
	
	private Context context;
	private int resourceId;
	private ArrayList<Hotel> items;
	private ImageLoader imageLoader;
	
	public HotelListAdapter(Context context, int resourceId, ArrayList<Hotel> items) {
		super(context, resourceId,items);
		
		this.context = context;
		this.resourceId = resourceId;
		this.items = items;
		this.imageLoader = new ImageLoader(context);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		if(v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(resourceId, null);
			v.setTag(position);
		}
		
		Hotel element = items.get(position);
		
		if(element != null) {
			ImageView img = (ImageView) v.findViewById(R.id.iv_hotel_row_img);
			Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
			//Now Set your animation
			img.startAnimation(fadeInAnimation );
			
			TextView label = (TextView) v.findViewById(R.id.tv_hotel_row_label);
			TextView name = (TextView) v.findViewById(R.id.tv_hotel_row_name);
			TextView price = (TextView) v.findViewById(R.id.tv_hotel_row_price);
			TextView discount = (TextView) v.findViewById(R.id.tv_hotel_row_discount);
			TextView sold_out = (TextView) v.findViewById(R.id.tv_hotel_row_soldout);
			TextView address = (TextView) v.findViewById(R.id.tv_hotel_row_address);
			
			address.setText(element.getAddress());
			name.setText(element.getName());
			DecimalFormat comma = new DecimalFormat("###,##0");
			String strPrice = comma.format(Integer.parseInt(element.getPrice()));
			String strDiscount = comma.format(Integer.parseInt(element.getDiscount()));
			price.setText("￦" + strPrice);
			price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			discount.setText("￦" + strDiscount);
			
			
			ImageView grade = (ImageView) v.findViewById(R.id.iv_hotel_row_grade);
			//grade
			if(items.get(position).getCat().equals("biz")) {
				grade.setImageResource(R.drawable.dh_grademark_biz);
			} else if(items.get(position).getCat().equals("boutique")) {
				grade.setImageResource(R.drawable.dh_grademark_boutique);
			} else if(items.get(position).getCat().equals("residence")) {
				grade.setImageResource(R.drawable.dh_grademark_residence);
			} else if(items.get(position).getCat().equals("special")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			} else if(items.get(position).getCat().equals("hostel")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			} else if(items.get(position).getCat().equals("grade1")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			} else if(items.get(position).getCat().equals("grade2")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			} else if(items.get(position).getCat().equals("grade3")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			} else if(items.get(position).getCat().equals("resort")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			} else if(items.get(position).getCat().equals("pension")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			} else if(items.get(position).getCat().equals("condo ")) {
				grade.setImageResource(R.drawable.dh_grademark_special);
			}
			
			if(!items.get(position).getImg().equals("default"))
				imageLoader.DisplayImage(items.get(position).getImg(), img);
			else
				img.setImageResource(R.drawable.dh_no_image);
			
			// 객실이 1~2 개일때 label 표시
			int avail_cnt = items.get(position).getAvali_cnt();
			if(avail_cnt > 0 && avail_cnt < 3) {
				label.setText(avail_cnt + " 객실 남음");
				label.setVisibility(View.VISIBLE);
			} 
			else
				label.setVisibility(View.GONE);
			
			// SOLD OUT 표시
			if(avail_cnt == 0)
				sold_out.setVisibility(View.VISIBLE);
			else
				sold_out.setVisibility(View.GONE);
			
//			if(!items.get(position).isAvaliable()) {
//				sold_out.setBackgroundResource(R.drawable.dh_sold_out);
//				Drawable alpha1 = sold_out.getBackground();
//				alpha1.setAlpha(200);
//			}
		}
		
		return v;
	}
}
