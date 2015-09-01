package com.twoheart.dailyhotel.util.ui;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.twoheart.dailyhotel.model.BaseTicketDto;

public class TicketClusterItem implements ClusterItem
{
	private final BaseTicketDto mTicketDto;
	private final LatLng mPosition;

	public TicketClusterItem(BaseTicketDto ticketDto)
	{
		mTicketDto = ticketDto;
		mPosition = new LatLng(ticketDto.latitude, ticketDto.longitude);
	}

	@Override
	public LatLng getPosition()
	{
		return mPosition;
	}

	public BaseTicketDto getTicketDto()
	{
		return mTicketDto;
	}
}
