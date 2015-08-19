package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.FontManager;

public class HotelDetailInfoActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		ArrayList<DetailInformation> arrayList = null;

		if (intent != null)
		{
			arrayList = intent.getParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_MOREINFORMATION);
		}

		if (arrayList == null)
		{
			Util.restartApp(this);
			return;
		}

		initLayout(arrayList);
	}

	private void initLayout(ArrayList<DetailInformation> arrayList)
	{
		setContentView(R.layout.activity_hoteldetail_info);
		setActionBar(R.string.actionbar_title_hoteldetailinfo_activity);

		LinearLayout moreLayout = (LinearLayout) findViewById(R.id.moreLayout);

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (DetailInformation information : arrayList)
		{
			makeInformationLayout(layoutInflater, moreLayout, information);
		}
	}

	private void makeInformationLayout(LayoutInflater layoutInflater, ViewGroup viewGroup, DetailInformation information)
	{
		if (information == null)
		{
			return;
		}

		View textLayout = layoutInflater.inflate(R.layout.list_row_detailmore_text, null, false);

		LinearLayout contentsLayout = (LinearLayout) textLayout.findViewById(R.id.contentsList);
		contentsLayout.removeAllViews();

		TextView titleTextView = (TextView) textLayout.findViewById(R.id.titleTextView);
		titleTextView.setText(information.title);

		List<String> contentsList = information.getContentsList();

		if (contentsList != null)
		{
			int size = contentsList.size();

			for (int i = 0; i < size; i++)
			{
				View subTextLayout = layoutInflater.inflate(R.layout.list_row_detail_text, null, false);
				TextView textView = (TextView) subTextLayout.findViewById(R.id.textView);
				textView.setText(contentsList.get(i));
				textView.setTypeface(FontManager.getInstance(HotelDetailInfoActivity.this).getDemiLightTypeface());

				if (Util.isOverAPI21() == true)
				{
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					layoutParams.bottomMargin = Util.dpToPx(this, 5);
					contentsLayout.addView(subTextLayout, layoutParams);
				} else
				{
					contentsLayout.addView(subTextLayout);
				}
			}
		}

		viewGroup.addView(textLayout);
	}

	@Override
	public void finish()
	{
		super.finish();

		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}
}
