package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Bank;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StayAutoRefundNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onBankList(List<Bank> bankList);

        void onAuthenticationAccount();

        void onRefundResult();
    }

    public StayAutoRefundNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestBankList()
    {
        final String bankList = "{msgCode=100, data=[\n" +
            "    {\n" +
            "        \"code\" : \"02\",\n" +
            "        \"name\" : \"한국산업은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"03\",\n" +
            "        \"name\" : \"기업은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"04\",\n" +
            "        \"name\" : \"국민은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"05\",\n" +
            "        \"name\" : \"외환은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"07\",\n" +
            "        \"name\" : \"수협중앙회\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"11\",\n" +
            "        \"name\" : \"농협중앙회\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"12\",\n" +
            "        \"name\" : \"단위농협\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"16\",\n" +
            "        \"name\" : \"축협중앙회\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"20\",\n" +
            "        \"name\" : \"우리은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"21\",\n" +
            "        \"name\" : \"신한은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"23\",\n" +
            "        \"name\" : \"제일은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"25\",\n" +
            "        \"name\" : \"하나은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"26\",\n" +
            "        \"name\" : \"신한은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"27\",\n" +
            "        \"name\" : \"한국씨티은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"31\",\n" +
            "        \"name\" : \"대구은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"32\",\n" +
            "        \"name\" : \"부산은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"34\",\n" +
            "        \"name\" : \"광주은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"35\",\n" +
            "        \"name\" : \"제주은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"37\",\n" +
            "        \"name\" : \"전북은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"38\",\n" +
            "        \"name\" : \"강원은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"39\",\n" +
            "        \"name\" : \"경남은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"41\",\n" +
            "        \"name\" : \"비씨카드\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"53\",\n" +
            "        \"name\" : \"씨티은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"54\",\n" +
            "        \"name\" : \"홍콩상하이은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"71\",\n" +
            "        \"name\" : \"우체국\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"81\",\n" +
            "        \"name\" : \"하나은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"83\",\n" +
            "        \"name\" : \"평화은행\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"87\",\n" +
            "        \"name\" : \"신세계\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"code\" : \"88\",\n" +
            "        \"name\" : \"신한은행\"\n" +
            "    }\n" +
            "]}";


        try
        {
            mBankListJsonResponseListener.onResponse(null, null, new JSONObject(bankList));
        } catch (JSONException e)
        {
        }
    }

    private DailyHotelJsonResponseListener mBankListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                List<Bank> bankList = new ArrayList<>(length);

                for (int i = 0; i < length; i++)
                {
                    bankList.add(new Bank(jsonArray.getJSONObject(i)));
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onBankList(bankList);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };
}
