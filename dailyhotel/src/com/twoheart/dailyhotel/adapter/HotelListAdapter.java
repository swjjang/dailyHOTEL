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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.util.lazy_image_loading.ImageLoader;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.widget.FadeInNetworkImageView;
import com.twoheart.dailyhotel.widget.HotelGradeView;
import com.twoheart.dailyhotel.widget.PinnedSectionListView.PinnedSectionListAdapter;

public class HotelListAdapter extends ArrayAdapter<HotelListViewItem> implements
		PinnedSectionListAdapter {

	private Context context;
	private int resourceId;
	private ImageLoader imageLoader;
	private LayoutInflater inflater;

	public HotelListAdapter(Context context, int resourceId,
			List<HotelListViewItem> hotelList) {
		super(context, resourceId, hotelList);	

		this.context = context;
		this.resourceId = resourceId;

		this.inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//		this.imageLoader = ImageLoader.getInstance();
//		this.imageLoader = VolleyImageLoader.getImageLoader();
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HotelListViewItem item = getItem(position);

		switch (item.getType()) {
		case HotelListViewItem.TYPE_SECTION:
			HeaderListViewHolder headerViewHolder = null;
			
			if (convertView != null) {
				if (convertView.getTag() != null)
					if (convertView.getTag() instanceof HeaderListViewHolder)
						headerViewHolder = (HeaderListViewHolder) convertView.getTag();
				
			} else {
				convertView = inflater.inflate(R.layout.list_row_hotel_section, parent, false);
				headerViewHolder = new HeaderListViewHolder();
				headerViewHolder.regionDetailName = (TextView) convertView
						.findViewById(R.id.hotelListRegionName);
				convertView.setTag(headerViewHolder);
			}

			headerViewHolder.regionDetailName.setText(item.getCategory());
			GlobalFont.apply((ViewGroup) convertView);

			break;
		case HotelListViewItem.TYPE_ENTRY:

			Hotel element = item.getItem();
			HotelListViewHolder viewHolder = null;
			
			if (convertView != null) {
				if (convertView.getTag() != null)
					if (convertView.getTag() instanceof HotelListViewHolder)
						viewHolder = (HotelListViewHolder) convertView.getTag();
			} else {
				convertView = inflater.inflate(resourceId, parent, false);

				viewHolder = new HotelListViewHolder();
				viewHolder.llHotelRowContent = (LinearLayout) convertView
						.findViewById(R.id.ll_hotel_row_content);
//				viewHolder.img = (ImageView) convertView
//						.findViewById(R.id.iv_hotel_row_img);
				viewHolder.img = (ImageView) convertView
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
				// viewHolder.gradeBackground = (FrameLayout) convertView
				// .findViewById(R.id.fl_hotel_row_grade);
				// viewHolder.gradeText = (TextView) convertView
				// .findViewById(R.id.tv_hotel_row_grade);
				viewHolder.grade = (HotelGradeView) convertView.findViewById(R.id.hv_hotel_grade);

				convertView.setTag(viewHolder);
				
			}
			
			DecimalFormat comma = new DecimalFormat("###,##0");
			String strPrice = comma
					.format(Integer.parseInt(element.getPrice()));
			String strDiscount = comma.format(Integer.parseInt(element
					.getDiscount()));

			// element.setAddress(element.getAddress().replace("I", ", ")); // i
			// element.setAddress(element.getAddress().replace("|", ", ")); //
			// pipe
			// element.setAddress(element.getAddress().replace("ㅣ", ", ")); // ㅣ
			// element.setAddress(element.getAddress().replace("l", ", ")); // l

			viewHolder.address.setText(element.getAddress());
			viewHolder.name.setText(element.getName());
			viewHolder.price.setText(strPrice + "원");
			viewHolder.price.setPaintFlags(viewHolder.price.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			viewHolder.discount.setText(strDiscount + "원");

			viewHolder.name.setSelected(true); // Android TextView marquee bug

//			viewHolder.name.setShadowLayer(Util.dpToPx(getContext(), 1), Util.dpToPx(getContext(), 6),
//					Util.dpToPx(getContext(), 6), android.R.color.black);
//			viewHolder.price.setShadowLayer(Util.dpToPx(getContext(), 1), Util.dpToPx(getContext(), 6),
//					Util.dpToPx(getContext(), 6), android.R.color.black);
//			viewHolder.discount.setShadowLayer(Util.dpToPx(getContext(), 1), Util.dpToPx(getContext(), 6),
//					Util.dpToPx(getContext(), 6), android.R.color.black);
//			viewHolder.address.setShadowLayer(Util.dpToPx(getContext(), 1), Util.dpToPx(getContext(), 6),
//					Util.dpToPx(getContext(), 6), android.R.color.black);

			final int colors[] = { Color.parseColor("#ED000000"),
					Color.parseColor("#E8000000"),
					Color.parseColor("#E2000000"),
					Color.parseColor("#66000000"),
					Color.parseColor("#00000000") };
			final float positions[] = { 0.0f, 0.01f, 0.02f, 0.17f, 0.38f };

			PaintDrawable p = new PaintDrawable();
			p.setShape(new RectShape());

			ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
				@Override
				public Shader resize(int width, int height) {
					return new LinearGradient(0, height, 0, 0, colors,
							positions, Shader.TileMode.CLAMP);
				}
			};

			p.setShaderFactory(sf);
			viewHolder.llHotelRowContent.setBackgroundDrawable(p);

			// grade
			viewHolder.grade.setHotelGradeCode(element.getCategory());
			
			GlobalFont.apply((ViewGroup) convertView);
			viewHolder.name.setTypeface(DailyHotel.getBoldTypeface());
			viewHolder.discount.setTypeface(DailyHotel.getBoldTypeface());

			if (!element.getImage().equals("default")) {
//				imageLoader.displayImage(element.getImage(), viewHolder.img);
				
//				viewHolder.img.setDefaultImageResId(R.drawable.img_placeholder);
//				viewHolder.img.setErrorImageResId(R.drawable.img_placeholder);
//				viewHolder.img.setImageUrl(element.getImage(), imageLoader);
				
				imageLoader.DisplayImage(element.getImage(), viewHolder.img);
				
			}

			// 객실이 1~2 개일때 label 표시
			int avail_cnt = element.getAvailableRoom();
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

			break;
		}

		return convertView;
	}

	private class HotelListViewHolder {
		LinearLayout llHotelRowContent;
//		ImageView img;
		ImageView img;
		TextView name;
		TextView price;
		TextView discount;
		TextView sold_out;
		TextView address;
		HotelGradeView grade;
		// FrameLayout gradeBackground;
		// TextView gradeText;
	}
	
	private class HeaderListViewHolder {
		TextView regionDetailName;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == HotelListViewItem.TYPE_SECTION;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getType();
	}

}
