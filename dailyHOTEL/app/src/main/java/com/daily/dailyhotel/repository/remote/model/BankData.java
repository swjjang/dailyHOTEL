package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Bank;

/**
 * Created by android_sam on 2018. 1. 11..
 */
@JsonObject
public class BankData
{
    @JsonField(name = "name")
    public String name;

    @JsonField(name = "code")
    public String code;

    public Bank getBank()
    {
        Bank bank = new Bank();

        bank.code = this.code;
        bank.name = this.name;

        return bank;
    }
}
