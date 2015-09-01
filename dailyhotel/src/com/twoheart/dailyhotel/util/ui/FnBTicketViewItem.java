package com.twoheart.dailyhotel.util.ui;

import com.twoheart.dailyhotel.model.BaseTicketDto;
import com.twoheart.dailyhotel.model.FnBTicketDto;

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
	public BaseTicketDto getTicketDto()
	{
		return mFnBDto;
	}
}