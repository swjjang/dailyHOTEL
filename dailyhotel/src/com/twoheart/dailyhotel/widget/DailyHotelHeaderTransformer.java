package com.twoheart.dailyhotel.widget;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.AbcDefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DailyHotelHeaderTransformer extends AbcDefaultHeaderTransformer
{
	@Override
	public void onReset()
	{
		super.onReset();

		View view = getHeaderView();

		if (view != null)
		{
			view.setBackgroundColor(android.R.color.transparent);
			TextView mHeaderTextView = (TextView) view.findViewById(R.id.ptr_text);
			ViewGroup mContentLayout = (ViewGroup) view.findViewById(R.id.ptr_content);

			mHeaderTextView.setVisibility(View.INVISIBLE);
			mContentLayout.setVisibility(View.INVISIBLE);
		}
	}
}
