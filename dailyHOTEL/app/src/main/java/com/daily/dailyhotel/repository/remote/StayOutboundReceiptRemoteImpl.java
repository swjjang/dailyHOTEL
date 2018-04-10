package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.StayOutboundReceiptInterface;
import com.daily.dailyhotel.entity.StayOutboundReceipt;
import com.daily.dailyhotel.repository.remote.model.StayOutboundEmailReceiptData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundReceiptData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class StayOutboundReceiptRemoteImpl extends BaseRemoteImpl implements StayOutboundReceiptInterface
{
    @Override
    public Observable<StayOutboundReceipt> getReceipt(Context context, int bookingIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/reservations/{reservationIdx}/sales-receipt"//
            : "MTM4JDE0NCQxNDckNzgkMTEzJDE1NSQ1NiQxNzYkNzYkOTgkMTQyJDE1NSQzMCQyJDE1MCQ5NCQ=$RjLI5NUVFQjcyQzUzRkQ3NEU4OTc3NTEc5NzlDNjg4RTI4OTgyMzZFNTcwBMzQ4NTY2NjU5N0NBNzMQ4MTHgzNEQzMkMxRVDg2NDlXCMjY3MjU5NUZDMTBDFQjZBODhFQzA4ODcwOTgzMjUyMLDNgwRPEUB0RZUMI4RjZUyODc4RTBERjlDRTc0RADI=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(bookingIndex));

        return mDailyMobileService.getStayOutboundReceipt(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundReceiptData>, StayOutboundReceipt>()
            {
                @Override
                public StayOutboundReceipt apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundReceiptData> stayOutboundReceiptDataBaseDto) throws Exception
                {
                    StayOutboundReceipt stayOutboundReceipt;

                    if (stayOutboundReceiptDataBaseDto != null)
                    {
                        if (stayOutboundReceiptDataBaseDto.msgCode == 100 && stayOutboundReceiptDataBaseDto.data != null)
                        {
                            stayOutboundReceipt = stayOutboundReceiptDataBaseDto.data.getStayOutboundReceipt();
                        } else
                        {
                            throw new BaseException(stayOutboundReceiptDataBaseDto.msgCode, stayOutboundReceiptDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return stayOutboundReceipt;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> getEmailReceipt(Context context, int bookingIndex, String email)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/reservations/{reservationIdx}/sales-receipt/request"//
            : "MTcxJDEzNCQxMDAkNzAkMzgkOTUkNTUkMTgzJDYzJDE2MCQyMTgkMjIkMTk1JDIwMyQxODUkMjA1JA==$ODgwNUIyMTY5NEU3QTUwRTCJENTQzMzg0NDA2MTHNGOTExMjg5MDZFMEUQ4MkY2MNUM4MTcyMkSEwREQ0NjdDMDdBOTMwMDYyOCDRBODE2SMUE4NDc0MEQ5Nzc1Mzg3OUVEM0Y1QjIwMTEc2MEMyRDdBMkNCRERFOMUQyNzRDMjAwREQzNDhVBOUJZFWMDYzRDMxDOUExN0RBDENTEzM0M1MDFCRDY1AQ0I0Qg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(bookingIndex));

        return mDailyMobileService.getStayOutboundEmailReceipt(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams), email) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundEmailReceiptData>, String>()
            {
                @Override
                public String apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundEmailReceiptData> stayOutboundEmailReceiptDataBaseDto) throws Exception
                {
                    String message;

                    if (stayOutboundEmailReceiptDataBaseDto != null)
                    {
                        if (stayOutboundEmailReceiptDataBaseDto.msgCode == 100 && stayOutboundEmailReceiptDataBaseDto.data != null)
                        {
                            message = stayOutboundEmailReceiptDataBaseDto.data.message;
                        } else
                        {
                            throw new BaseException(stayOutboundEmailReceiptDataBaseDto.msgCode, stayOutboundEmailReceiptDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return message;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }
}
