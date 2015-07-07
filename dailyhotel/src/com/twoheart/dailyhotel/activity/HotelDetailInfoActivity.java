package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

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

		TextView titleTextView = (TextView) textLayout.findViewById(R.id.titleTextView);
		titleTextView.setText(information.title);

		List<String> contentsList = information.getContentsList();

		if (contentsList != null)
		{
			int size = contentsList.size();

			StringBuffer stringBuffer = new StringBuffer();

			for (int i = 0; i < size; i++)
			{
				stringBuffer.append(contentsList.get(i));

				if (i != size - 1)
				{
					stringBuffer.append("\n");
				}
			}

			TextView contentsTextView = (TextView) textLayout.findViewById(R.id.contentsTextView);
			contentsTextView.setText(stringBuffer.toString());

			viewGroup.addView(textLayout);
		}
	}

	@Override
	public void finish()
	{
		super.finish();

		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}
}
