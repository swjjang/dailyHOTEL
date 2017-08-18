package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.PaymentInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.DomesticGuest;
import com.daily.dailyhotel.entity.OverseasGuest;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.StayPayment;
import com.daily.dailyhotel.entity.StayRefundPolicy;
import com.daily.dailyhotel.repository.remote.model.CardData;
import com.daily.dailyhotel.repository.remote.model.StayRefundPolicyData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.util.DailyCalendar;

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
    public Observable<StayOutboundPayment> getStayOutboundPayment(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;

        try
        {
            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("rooms", getRooms(new People[]{people}, new int[]{roomBedTypeId}));
            jsonObject.put("rateCode", rateCode);
            jsonObject.put("rateKey", rateKey);
            jsonObject.put("roomTypeCode", roomTypeCode);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getStayOutboundPayment(index, jsonObject).map(stayOutboundPaymentDataBaseDto ->
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
    public Observable<StayPayment> getStayPayment(StayBookDateTime stayBookDateTime, int index)
    {
        int nights = 1;

        try
        {
            nights = stayBookDateTime.getNights();
        } catch (Exception e)
        {
            nights = 1;
        }

        return DailyMobileAPI.getInstance(mContext).getStayPayment(index, stayBookDateTime.getCheckInDateTime("yyyyMMdd")//
            , nights).map(stayPaymentDataBaseDto ->
        {
            StayPayment stayPayment = null;

            if (stayPaymentDataBaseDto != null)
            {
                if (stayPaymentDataBaseDto.msgCode == 0 && stayPaymentDataBaseDto.data != null)
                {
                    stayPayment = stayPaymentDataBaseDto.data.getStayPayment();
                } else
                {
                    throw new BaseException(stayPaymentDataBaseDto.msgCode, stayPaymentDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return stayPayment;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Card>> getEasyCardList()
    {
        return DailyMobileAPI.getInstance(mContext).getEasyCardList().map(new Function<BaseListDto<CardData>, List<Card>>()
        {
            @Override
            public List<Card> apply(@io.reactivex.annotations.NonNull BaseListDto<CardData> cardDataBaseListDto) throws Exception
            {
                List<Card> cardList = new ArrayList<>();

                StayOutboundPayment stayOutboundPayment = null;

                if (cardDataBaseListDto != null)
                {
                    if (cardDataBaseListDto.msgCode == 0 && cardDataBaseListDto.data != null)
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

    @Override
    public Observable<PaymentResult> getStayOutboundPaymentTypeEasy(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , boolean usedBonus, int bonus, OverseasGuest guest, int totalPrice, String billingKey)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;
        final String PAYMENT_TYPE = "ONE_CLICK";

        try
        {
            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("rooms", getRooms(new People[]{people}, new int[]{roomBedTypeId}));
            jsonObject.put("rateCode", rateCode);
            jsonObject.put("rateKey", rateKey);
            jsonObject.put("roomTypeCode", roomTypeCode);

            if (usedBonus == true)
            {
                jsonObject.put("bonusAmount", bonus);
            }

            jsonObject.put("firstName", guest.firstName);
            jsonObject.put("lastName", guest.lastName);
            jsonObject.put("email", guest.email);
            jsonObject.put("phoneNumber", guest.phone.replace("-", ""));
            jsonObject.put("paymentType", PAYMENT_TYPE);
            jsonObject.put("total", totalPrice);
            jsonObject.put("billingKey", billingKey);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getOutboundPaymentTypeEasy(index, jsonObject).map(paymentResultDataBaseDto ->
        {
            PaymentResult paymentResult = null;

            if (paymentResultDataBaseDto != null)
            {
                if (paymentResultDataBaseDto.msgCode == 100 && paymentResultDataBaseDto.data != null)
                {
                    paymentResult = paymentResultDataBaseDto.data.getPaymentTypeEasy();
                } else
                {
                    throw new BaseException(paymentResultDataBaseDto.msgCode, paymentResultDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return paymentResult;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PaymentResult> getStayOutboundPaymentTypeBonus(StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , boolean usedBonus, int bonus, OverseasGuest guest, int totalPrice)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;
        final String PAYMENT_TYPE = "BONUS";

        try
        {
            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("rooms", getRooms(new People[]{people}, new int[]{roomBedTypeId}));
            jsonObject.put("rateCode", rateCode);
            jsonObject.put("rateKey", rateKey);
            jsonObject.put("roomTypeCode", roomTypeCode);

            if (usedBonus == true)
            {
                jsonObject.put("bonusAmount", bonus > totalPrice ? totalPrice : bonus);
            }

            jsonObject.put("firstName", guest.firstName);
            jsonObject.put("lastName", guest.lastName);
            jsonObject.put("email", guest.email);
            jsonObject.put("phoneNumber", guest.phone.replace("-", ""));
            jsonObject.put("paymentType", PAYMENT_TYPE);
            jsonObject.put("total", totalPrice);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getOutboundPaymentTypeBonus(index, jsonObject).map(paymentResultDataBaseDto ->
        {
            PaymentResult paymentResult = null;

            if (paymentResultDataBaseDto != null)
            {
                if (paymentResultDataBaseDto.msgCode == 100 && paymentResultDataBaseDto.data != null)
                {
                    paymentResult = paymentResultDataBaseDto.data.getPaymentTypeEasy();
                } else
                {
                    throw new BaseException(paymentResultDataBaseDto.msgCode, paymentResultDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return paymentResult;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PaymentResult> getStayPaymentTypeEasy(StayBookDateTime stayBookDateTime, int roomIndex//
        , boolean usedBonus, int bonus, boolean usedCoupon, String couponCode, DomesticGuest guest//
        , int totalPrice, String transportation, String billingKey)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("billingKey", billingKey);

            if (usedBonus == true)
            {
                jsonObject.put("bonusAmount", bonus > totalPrice ? totalPrice : bonus);
            } else
            {
                jsonObject.put("bonusAmount", 0);
            }

            jsonObject.put("checkInDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("days", stayBookDateTime.getNights());

            if (usedCoupon == true)
            {
                jsonObject.put("couponCode", couponCode);
            }

            jsonObject.put("roomIdx", roomIndex);

            JSONObject bookingGuestJSONObject = new JSONObject();
            bookingGuestJSONObject.put("arrivalDateTime", stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));

            if (DailyTextUtils.isTextEmpty(transportation) == false)
            {
                bookingGuestJSONObject.put("arrivalType", transportation);
            }

            bookingGuestJSONObject.put("email", guest.email);
            bookingGuestJSONObject.put("name", guest.name);
            bookingGuestJSONObject.put("phone", guest.phone);

            jsonObject.put("bookingGuest", bookingGuestJSONObject);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getPaymentTypeEasy(jsonObject).map(paymentResultDataBaseDto ->
        {
            PaymentResult paymentResult = null;

            if (paymentResultDataBaseDto != null)
            {
                if (paymentResultDataBaseDto.msgCode == 100 && paymentResultDataBaseDto.data != null)
                {
                    paymentResult = paymentResultDataBaseDto.data.getPaymentTypeEasy();
                } else
                {
                    throw new BaseException(paymentResultDataBaseDto.msgCode, paymentResultDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return paymentResult;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PaymentResult> getStayPaymentTypeBonus(StayBookDateTime stayBookDateTime, int roomIndex//
        , boolean usedBonus, int bonus, boolean usedCoupon, String couponCode, DomesticGuest guest, int totalPrice, String transportation)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            if (usedBonus == true)
            {
                jsonObject.put("bonusAmount", bonus > totalPrice ? totalPrice : bonus);
            } else
            {
                jsonObject.put("bonusAmount", 0);
            }

            jsonObject.put("checkInDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("days", stayBookDateTime.getNights());

            if (usedCoupon == true)
            {
                jsonObject.put("couponCode", couponCode);
            }

            jsonObject.put("roomIdx", roomIndex);

            JSONObject bookingGuestJSONObject = new JSONObject();
            bookingGuestJSONObject.put("arrivalDateTime", stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));

            if (DailyTextUtils.isTextEmpty(transportation) == false)
            {
                bookingGuestJSONObject.put("arrivalType", transportation);
            }

            bookingGuestJSONObject.put("email", guest.email);
            bookingGuestJSONObject.put("name", guest.name);
            bookingGuestJSONObject.put("phone", guest.phone);

            jsonObject.put("bookingGuest", bookingGuestJSONObject);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return DailyMobileAPI.getInstance(mContext).getPaymentTypeBonus(jsonObject).map(paymentResultDataBaseDto ->
        {
            PaymentResult paymentResult = null;

            if (paymentResultDataBaseDto != null)
            {
                if (paymentResultDataBaseDto.msgCode == 100 && paymentResultDataBaseDto.data != null)
                {
                    paymentResult = paymentResultDataBaseDto.data.getPaymentTypeEasy();
                } else
                {
                    throw new BaseException(paymentResultDataBaseDto.msgCode, paymentResultDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return paymentResult;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayRefundPolicy> getStayRefundPolicy(StayBookDateTime stayBookDateTime, int stayIndex, int roomIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayRefundPolicy(stayIndex, roomIndex, stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)).map(new Function<BaseDto<StayRefundPolicyData>, StayRefundPolicy>()
        {
            @Override
            public StayRefundPolicy apply(@io.reactivex.annotations.NonNull BaseDto<StayRefundPolicyData> stayRefundPolicyDataBaseDto) throws Exception
            {
                StayRefundPolicy stayRefundPolicy = null;

                if (stayRefundPolicyDataBaseDto != null)
                {
                    if (stayRefundPolicyDataBaseDto.msgCode == 100 && stayRefundPolicyDataBaseDto.data != null)
                    {
                        stayRefundPolicy = stayRefundPolicyDataBaseDto.data.getStayRefundPolicy();
                    } else
                    {
                        throw new BaseException(stayRefundPolicyDataBaseDto.msgCode, stayRefundPolicyDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayRefundPolicy;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }


    private JSONArray getRooms(People[] peoples, int[] roomBedTypeIds)
    {
        JSONArray roomJSONArray = new JSONArray();

        if (peoples == null || peoples.length == 0 || roomBedTypeIds == null || roomBedTypeIds.length == 0//
            || peoples.length != roomBedTypeIds.length)
        {
            return roomJSONArray;
        }

        try
        {
            int length = peoples.length;

            for (int i = 0; i < length; i++)
            {
                JSONObject roomJSONObject = new JSONObject();

                roomJSONObject.put("numberOfAdults", peoples[i].numberOfAdults);
                roomJSONObject.put("roomBedTypeId", roomBedTypeIds[i]);

                List<Integer> childAgeList = peoples[i].getChildAgeList();

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
