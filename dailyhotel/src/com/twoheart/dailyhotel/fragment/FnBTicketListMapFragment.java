package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.adapter.FnBTicketViewPagerAdapter;
import com.twoheart.dailyhotel.adapter.TicketViewPagerAdapter;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

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
