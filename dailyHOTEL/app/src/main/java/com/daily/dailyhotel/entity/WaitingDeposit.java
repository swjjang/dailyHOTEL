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
    public String message2;

    public WaitingDeposit()
    {

    }

    public void setMessageList(List<String> messageList)
    {
        mMessage1List = messageList;
    }

    public List<String> getMessageList()
    {
        return mMessage1List;
    }
}