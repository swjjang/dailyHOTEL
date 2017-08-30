package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.WaitingDeposit;

import java.util.List;

@JsonObject
public class WaitingDepositData
{
    @JsonField(name = "accountHolder")
    public String accountHolder;

    @JsonField(name = "accountNumber")
    public String accountNumber;

    @JsonField(name = "bankName")
    public String bankName;

    @JsonField(name = "bonusAmount")
    public int bonusAmount;

    @JsonField(name = "couponAmount")
    public int couponAmount;

    @JsonField(name = "depositWaitingAmount")
    public int depositWaitingAmount;

    @JsonField(name = "totalPrice")
    public int totalPrice;

    @JsonField(name = "expiredAt")
    public String expiredAt;

    @JsonField(name = "message1")
    public List<String> message1List;

    @JsonField(name = "message2")
    public String message2;

    public WaitingDepositData()
    {

    }

    public WaitingDeposit getWaitingDeposit()
    {
        WaitingDeposit waitingDeposit = new WaitingDeposit();
        waitingDeposit.accountHolder = accountHolder;
        waitingDeposit.accountNumber = accountNumber;
        waitingDeposit.bankName = bankName;
        waitingDeposit.bonusAmount = bonusAmount;
        waitingDeposit.couponAmount = couponAmount;
        waitingDeposit.depositWaitingAmount = depositWaitingAmount;
        waitingDeposit.totalPrice = totalPrice;
        waitingDeposit.expiredAt = expiredAt;
        waitingDeposit.setMessageList(message1List);
        waitingDeposit.message2 = message2;

        return waitingDeposit;
    }
}
