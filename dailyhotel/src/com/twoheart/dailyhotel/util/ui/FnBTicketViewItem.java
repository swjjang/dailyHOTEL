package com.twoheart.dailyhotel.util.ui;

import com.twoheart.dailyhotel.model.FnBTicketDto;
import com.twoheart.dailyhotel.model.TicketDto;

public class FnBTicketViewItem extends TicketViewItem
{
	private FnBTicketDto mFnBDto;

	public FnBTicketViewItem(String title)
	{
		super(title);
	}

	public FnBTicketViewItem(FnBTicketDto fnbDto)
	{
		super();

		mFnBDto = fnbDto;
	}

	@Override
	public TicketDto getTicketDto()
	{
		return mFnBDto;
	}
}