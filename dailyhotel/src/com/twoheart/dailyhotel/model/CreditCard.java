package com.twoheart.dailyhotel.model;

public class CreditCard
{
	public final String name;
	public final String number;
	public final String billingkey;

	public CreditCard(String name, String number, String billkey)
	{
		this.name = name;
		this.number = number;
		this.billingkey = billkey;
	}
}
