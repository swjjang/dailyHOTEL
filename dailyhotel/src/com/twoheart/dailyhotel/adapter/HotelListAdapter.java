package com.twoheart.dailyhotel.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.Hotel;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.widget.FadeInNetworkImageView;

public class HotelListAdapter extends ArrayAdapter<Hotel> {

	private Context context;
	private int resourceId;
	private List<Hotel> items;
	private ImageLoader imageLoader;
	private LayoutInflater inflater;

	public HotelListAdapter(Context context, int resourceId,
			List<Hotel> mHotelList) {
		super(context, resourceId, mHotelList);

		this.context = context;
		this.resourceId = resourceId;
		this.items = mHotelList;
		
		this.inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.imageLoader = VolleyImageLoader.getImageLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HotelListViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = inflater.inflate(resourceId, parent, false);

			viewHolder = new HotelListViewHolder();
			viewHolder.llHotelRowContent = (LinearLayout) convertView.findViewById(R.id.ll_hotel_row_content);
			viewHolder.img = (FadeInNetworkImageView) convertView
					.findViewById(R.id.iv_hotel_row_img);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_hotel_row_name);
			viewHolder.price = (TextView) convertView
					.findViewById(R.id.tv_hotel_row_price);
			viewHolder.discount = (TextView) convertView
					.findViewById(R.id.tv_hotel_row_discount);
			viewHolder.sold_out = (TextView) convertView
					.findViewById(R.id.tv_hotel_row_soldout);
			viewHolder.address = (TextView) convertView
					.findViewById(R.id.tv_hotel_row_address);
			viewHolder.gradeBackground = (FrameLayout) convertView
					.findViewById(R.id.fl_hotel_row_grade);
			viewHolder.gradeText = (TextView) convertView
					.findViewById(R.id.tv_hotel_row_grade);
			viewHolder.rlHotelUnder = (RelativeLayout) convertView
					.findViewById(R.id.rl_hotel_under);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (HotelListViewHolder) convertView.getTag();

		}

		Hotel element = items.get(position);

		DecimalFormat comma = new DecimalFormat("###,##0");
		String strPrice = comma.format(Integer.parseInt(element.getPrice()));
		String strDiscount = comma.format(Integer.parseInt(element
				.getDiscount()));

		// element.setAddress(element.getAddress().replace("I", ", ")); // i
		// element.setAddress(element.getAddress().replace("|", ", ")); // pipe
		// element.setAddress(element.getAddress().replace("ㅣ", ", ")); // ㅣ
		// element.setAddress(element.getAddress().replace("l", ", ")); // l

		viewHolder.address.setText(element.getAddress());
		viewHolder.name.setText(element.getName());
		viewHolder.price.setText(strPrice + "원");
		viewHolder.price.setPaintFlags(viewHolder.price.getPaintFlags()
				| Paint.STRIKE_THRU_TEXT_FLAG);
		viewHolder.discount.setText(strDiscount + "원");

		viewHolder.name.setSelected(true); // Android TextView marquee bug

		viewHolder.name.setShadowLayer(2, 0, 2, android.R.color.black);
		viewHolder.price.setShadowLayer(2, 0, 2, android.R.color.black);
		viewHolder.discount.setShadowLayer(2, 0, 2, android.R.color.black);
		viewHolder.address.setShadowLayer(2, 0, 2, android.R.color.black);
		
		final int colors[] = { Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), 
				Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000") };
		final float positions[] = { 0.0f, 0.01f, 0.02f, 0.17f, 0.38f };

		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());
		
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
		    @Override
		    public Shader resize(int width, int height) {
		        return new LinearGradient(0, height,
		        		0,
		        		0, colors, positions,
						Shader.TileMode.CLAMP);
		    }
		};
		
		p.setShaderFactory(sf);
		viewHolder.llHotelRowContent.setBackgroundDrawable(p);

//		viewHolder.name.setTypeface(BaseActivity.mTypefaceBold);
//		viewHolder.discount.setTypeface(BaseActivity.mTypefaceBold);
//		viewHolder.price.setTypeface(BaseActivity.mTypefaceCommon);
//		viewHolder.address.setTypeface(BaseActivity.mTypefaceCommon);

		// grade
		if (element.getCat().equals("biz")) {
			viewHolder.gradeBackground.setBackgroundColor(Color
					.parseColor("#055870"));
			viewHolder.gradeText.setText("비지니스");

		} else if (element.getCat().equals("boutique")) {
			viewHolder.gradeBackground.setBackgroundColor(Color
					.parseColor("#9f2d58"));
			viewHolder.gradeText.setText("부띠끄");

		} else if (element.getCat().equals("residence")) {
			viewHolder.gradeBackground.setBackgroundColor(Color
					.parseColor("#407f67"));
			viewHolder.gradeText.setText("레지던스");

		} else if (element.getCat().equals("resort")) {
			viewHolder.gradeBackground.setBackgroundColor(Color
					.parseColor("#cf8d14"));
			viewHolder.gradeText.setText("리조트");

		} else if (element.getCat().equals("special")) {
			viewHolder.gradeBackground.setBackgroundColor(Color
					.parseColor("#ab380a"));
			viewHolder.gradeText.setText("특급");
		
		} else {
			viewHolder.gradeBackground.setBackgroundColor(Color
					.parseColor("#808080"));
			viewHolder.gradeText.setText("미정");
		}

		if (!element.getImg().equals("default")) {
			viewHolder.img.setDefaultImageResId(R.drawable.img_placeholder);
			viewHolder.img.setErrorImageResId(R.drawable.img_placeholder);
			viewHolder.img.setImageUrl(element.getImg(), imageLoader);
		}

		// 객실이 1~2 개일때 label 표시
		int avail_cnt = element.getAvali_cnt();
		// if(avail_cnt > 0 && avail_cnt < 3) {
		// label.setText(avail_cnt + " 객실 남음");
		// label.setVisibility(View.VISIBLE);
		// }
		// else
		// label.setVisibility(View.GONE);

		// SOLD OUT 표시
		if (avail_cnt == 0)
			viewHolder.sold_out.setVisibility(View.VISIBLE);
		else
			viewHolder.sold_out.setVisibility(View.GONE);

		// if(!items.get(position).isAvaliable()) {
		// sold_out.setBackgroundResource(R.drawable.dh_sold_out);
		// Drawable alpha1 = sold_out.getBackground();
		// alpha1.setAlpha(200);
		// }

		return convertView;
	}

	private class HotelListViewHolder {
		LinearLayout llHotelRowContent;
		FadeInNetworkImageView img;
		TextView name;
		TextView price;
		TextView discount;
		TextView sold_out;
		TextView address;
		FrameLayout gradeBackground;
		TextView gradeText;
		RelativeLayout rlHotelUnder;
	}
}
