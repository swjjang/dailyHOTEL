package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class SelectAreaActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectarea);
		setActionBar(R.string.label_selectarea_area);

		initLayout();
	}

	private void initLayout()
	{

	}

	@Override
	protected void onResume()
	{
		super.onResume();

	}
}
