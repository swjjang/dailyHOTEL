package com.daily.dailyhotel.entity;

import java.util.List;

public class GourmetPayment
{
    public String restaurantName;
    public String businessName;
    public int totalPrice;
    private List<GourmetPaymentMenu> mGourmetPaymentMenuList;

    public GourmetPayment()
    {

    }

    public List<GourmetPaymentMenu> getGourmetPaymentMenuList()
    {
        return mGourmetPaymentMenuList;
    }

    public void setGourmetPaymentMenuList(List<GourmetPaymentMenu> gourmetPaymentMenuList)
    {
        mGourmetPaymentMenuList = gourmetPaymentMenuList;
    }
}