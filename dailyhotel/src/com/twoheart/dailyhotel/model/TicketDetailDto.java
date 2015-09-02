package com.twoheart.dailyhotel.model;

import java.util.ArrayList;

import org.json.JSONObject;

public abstract class TicketDetailDto
{
	public int index;
	public String grade;
	public String name;
	public String address;
	public String addressDetail;
	public String benefit;
	public double latitude;
	public double longitude;
	protected ArrayList<String> mImageUrlList;
	protected ArrayList<DetailInformation> mInformationList;
	protected ArrayList<TicketInformation> mTicketInformationList;

	public abstract void setData(JSONObject jsonObject) throws Exception;

	public TicketDetailDto(int index)
	{
		this.index = index;
	}

	public ArrayList<String> getImageUrlList()
	{
		return mImageUrlList;
	}

	public ArrayList<TicketInformation> getTicketInformation()
	{
		return mTicketInformationList;
	}

	public ArrayList<DetailInformation> getInformation()
	{
		return mInformationList;
	}
}
