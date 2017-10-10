package com.adjust.sdk;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.Constants;

import java.util.Iterator;
import java.util.Map;

public class DailyAdjustEvent extends AdjustEvent
{
    private static final boolean DEBUG = Constants.DEBUG;

    Context mContext;

    public DailyAdjustEvent(String eventToken)
    {
        super(eventToken);
    }

    @Override
    public void setRevenue(double revenue, String currency)
    {
        super.setRevenue(revenue, currency);
    }

    @Override
    public void addCallbackParameter(String key, String value)
    {
        if (DailyTextUtils.isTextEmpty(value) == true)
        {
            value = "";
        }

        super.addCallbackParameter(key, value);
    }

    @Override
    public void addPartnerParameter(String key, String value)
    {
        if (DailyTextUtils.isTextEmpty(value) == true)
        {
            value = "";
        }

        super.addPartnerParameter(key, value);
        super.addCallbackParameter(key, value);
    }

    public void addPartnerParameter(Map<String, String> paramMap)
    {
        if (paramMap == null || paramMap.size() == 0)
        {
            return;
        }

        Iterator<Map.Entry<String, String>> iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext() == true)
        {
            Map.Entry<String, String> entry = iterator.next();

            addPartnerParameter(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setOrderId(String orderId)
    {
        super.setOrderId(orderId);
    }

    @Override
    public boolean isValid()
    {
        return super.isValid();
    }

    @Override
    public String toString()
    {
        String log = "eventToken : " + eventToken + ", revenue : " + revenue + ", currency : " + currency + ", orderId : " + orderId;

        if (callbackParameters != null)
        {
            log += ", callbackParameters : " + callbackParameters.toString();
        }

        if (partnerParameters != null)
        {
            log += ", partnerParameters : " + partnerParameters.toString();
        }

        return log;
    }
}
