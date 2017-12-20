package com.daily.dailyhotel.entity;

import java.util.List;

public class WaitingDeposit
{
    public String accountHolder;
    public String accountNumber;
    public String bankName;
    public int bonusAmount;
    public int couponAmount;
    public int depositWaitingAmount;
    public int totalPrice;
    public String expiredAt;
    private List<String> mMessage1List;
    private List<String> mMessage2List;

    public WaitingDeposit()
    {

    }

    public void setMessage1List(List<String> messageList)
    {
        mMessage1List = messageList;
    }

    public List<String> getMessage1List()
    {
        return mMessage1List;
    }

    public void setMessage2List(List<String> messageList)
    {
        mMessage2List = messageList;
    }

    public List<String> getMessage2List()
    {
        return mMessage2List;
    }
}