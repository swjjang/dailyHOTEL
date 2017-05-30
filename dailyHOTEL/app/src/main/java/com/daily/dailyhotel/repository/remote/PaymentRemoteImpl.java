package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.PaymentInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.repository.remote.model.CardData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class PaymentRemoteImpl implements PaymentInterface
{
    private Context mContext;

    public PaymentRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutboundPayment> getStayOutBoundPayment(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, People people)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;

        try
        {
            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("rooms", getRooms(new People[]{people}));
            jsonObject.put("rateCode", rateCode);
            jsonObject.put("rateKey", rateKey);
            jsonObject.put("roomTypeCode", roomTypeCode);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getStayOutBoundPayment(index, jsonObject).map(stayOutboundPaymentDataBaseDto ->
        {
            StayOutboundPayment stayOutboundPayment = null;

            if (stayOutboundPaymentDataBaseDto != null)
            {
                if (stayOutboundPaymentDataBaseDto.msgCode == 100 && stayOutboundPaymentDataBaseDto.data != null)
                {
                    stayOutboundPayment = stayOutboundPaymentDataBaseDto.data.getStayOutboundPayment();
                } else
                {
                    throw new BaseException(stayOutboundPaymentDataBaseDto.msgCode, stayOutboundPaymentDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return stayOutboundPayment;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Card>> getSimpleCardList()
    {
        return DailyMobileAPI.getInstance(mContext).getSimpleCardList().map(new Function<BaseListDto<CardData>, List<Card>>()
        {
            @Override
            public List<Card> apply(@io.reactivex.annotations.NonNull BaseListDto<CardData> cardDataBaseListDto) throws Exception
            {
                List<Card> cardList = new ArrayList<>();

                StayOutboundPayment stayOutboundPayment = null;

                if (cardDataBaseListDto != null)
                {
                    if (cardDataBaseListDto.msgCode == 100 && cardDataBaseListDto.data != null)
                    {
                        for (CardData cardData : cardDataBaseListDto.data)
                        {
                            cardList.add(cardData.getCard());
                        }
                    } else
                    {
                        throw new BaseException(cardDataBaseListDto.msgCode, cardDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return cardList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private JSONArray getRooms(People[] peoples)
    {
        JSONArray roomJSONArray = new JSONArray();

        if (peoples == null || peoples.length == 0)
        {
            return roomJSONArray;
        }

        try
        {
            for (People people : peoples)
            {
                JSONObject roomJSONObject = new JSONObject();
                roomJSONObject.put("numberOfAdults", people.numberOfAdults);

                List<Integer> childAgeList = people.getChildAgeList();

                if (childAgeList != null && childAgeList.size() > 0)
                {
                    JSONArray childJSONArray = new JSONArray();

                    for (int age : childAgeList)
                    {
                        childJSONArray.put(Integer.toString(age));
                    }

                    roomJSONObject.put("numberOfChildren", childAgeList.size());
                    roomJSONObject.put("childAges", childJSONArray);
                } else
                {
                    roomJSONObject.put("numberOfChildren", 0);
                    roomJSONObject.put("childAges", null);
                }

                roomJSONArray.put(roomJSONObject);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return roomJSONArray;
    }
}
