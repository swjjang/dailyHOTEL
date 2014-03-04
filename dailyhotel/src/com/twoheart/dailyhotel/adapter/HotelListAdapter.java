package com.twoheart.dailyhotel.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.Hotel;
import com.twoheart.dailyhotel.util.ImageLoader;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

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
		Log.d("HotelListAdapter", element.getAddress() + " " + element.getAvali_cnt() + " " 
		+ element.getCat() + " " + element.getDiscount() + " " + element.getIdx() + " " +
				element.getImg() + " " + element.getName() + " " + element.getPrice() + " " + element.getSeq());
		
		
		if(element != null) {
			ImageView img = (ImageView) v.findViewById(R.id.iv_hotel_row_img);
			
//			TextView label = (TextView) v.findViewById(R.id.tv_hotel_row_label);
			TextView name = (TextView) v.findViewById(R.id.tv_hotel_row_name);
			TextView price = (TextView) v.findViewById(R.id.tv_hotel_row_price);
			TextView discount = (TextView) v.findViewById(R.id.tv_hotel_row_discount);
			TextView sold_out = (TextView) v.findViewById(R.id.tv_hotel_row_soldout);
			TextView address = (TextView) v.findViewById(R.id.tv_hotel_row_address);
			
			FrameLayout gradeBackground = (FrameLayout) v.findViewById(R.id.fl_hotel_row_grade);
			TextView gradeText = (TextView) v.findViewById(R.id.tv_hotel_row_grade);
			
			DecimalFormat comma = new DecimalFormat("###,##0");
			String strPrice = comma.format(Integer.parseInt(element.getPrice()));
			String strDiscount = comma.format(Integer.parseInt(element.getDiscount()));
			
//			element.setAddress(element.getAddress().replace("I", ", ")); // i
//			element.setAddress(element.getAddress().replace("|", ", ")); // pipe
//			element.setAddress(element.getAddress().replace("ㅣ", ", ")); // ㅣ
//			element.setAddress(element.getAddress().replace("l", ", ")); // l
			
			address.setText(element.getAddress());
			name.setText(element.getName());
			price.setText(strPrice + "원");
			price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			discount.setText(strDiscount + "원");
			
			name.setSelected(true); 	//Android TextView marquee bug
			
			name.setShadowLayer(2, 0, 2, android.R.color.black);
			price.setShadowLayer(2, 0, 2, android.R.color.black);
			discount.setShadowLayer(2, 0, 2, android.R.color.black);
			address.setShadowLayer(2, 0, 2, android.R.color.black);
			
			name.setTypeface(BaseActivity.mTypefaceBold);
			discount.setTypeface(BaseActivity.mTypefaceBold);
			price.setTypeface(BaseActivity.mTypefaceCommon);
			address.setTypeface(BaseActivity.mTypefaceCommon);
			
			//grade
			if(items.get(position).getCat().equals("biz")) {
//				grade.setImageResource(R.drawable.dh_grademark_biz);
				gradeBackground.setBackgroundColor(Color.parseColor("#055870"));
				gradeText.setText("비지니스");
				
			} else if(items.get(position).getCat().equals("boutique")) {
//				grade.setImageResource(R.drawable.dh_grademark_boutique);
				gradeBackground.setBackgroundColor(Color.parseColor("#9f2d58"));
				gradeText.setText("부띠끄");
				
			} else if(items.get(position).getCat().equals("residence")) {
//				grade.setImageResource(R.drawable.dh_grademark_residence);
				gradeBackground.setBackgroundColor(Color.parseColor("#407f67"));
				gradeText.setText("레지던스");
				
			} else if(items.get(position).getCat().equals("resort")) {
//				grade.setImageResource(R.drawable.dh_grademark_residence);
				gradeBackground.setBackgroundColor(Color.parseColor("#cf8d14"));
				gradeText.setText("리조트");
				
			} else {
//				grade.setImageResource(R.drawable.dh_grademark_special);
				gradeBackground.setBackgroundColor(Color.parseColor("#ab380a"));
				gradeText.setText("특급");
			}
			
			if(!element.getImg().equals("default")) {
				imageLoader.DisplayImage(element.getImg(), img);
				Log.d("img_path", element.getImg());
			}
			
			// 객실이 1~2 개일때 label 표시
			int avail_cnt = items.get(position).getAvali_cnt();
//			if(avail_cnt > 0 && avail_cnt < 3) {
//				label.setText(avail_cnt + " 객실 남음");
//				label.setVisibility(View.VISIBLE);
//			} 
//			else
//				label.setVisibility(View.GONE);
			
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
