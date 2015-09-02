package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.FnBTicketViewPagerAdapter;
import com.twoheart.dailyhotel.adapter.TicketViewPagerAdapter;

public class FnBTicketListMapFragment extends TicketListMapFragment
{
	private FnBTicketViewPagerAdapter mTicketViewPagerAdapter;

	public FnBTicketListMapFragment()
	{
		super();
	}

	protected TicketViewPagerAdapter getViewPagerAdapter(BaseActivity baseActivity)
	{
		if (mTicketViewPagerAdapter == null)
		{
			mTicketViewPagerAdapter = new FnBTicketViewPagerAdapter(baseActivity);
		}

		return mTicketViewPagerAdapter;
	}
}
