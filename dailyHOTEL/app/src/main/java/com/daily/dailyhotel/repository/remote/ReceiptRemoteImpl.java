package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ReceiptInterface;
import com.daily.dailyhotel.entity.GourmetReceipt;
import com.daily.dailyhotel.repository.remote.model.GourmetReceiptData;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 12. 7..
 */

public class ReceiptRemoteImpl extends BaseRemoteImpl implements ReceiptInterface
{
    public ReceiptRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<GourmetReceipt> getGourmetReceipt(int reservationIdx)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/receipt/app/gourmet/legacy/{reservationIdx}"//
            : "NzQkMTgkMTE3JDEwNSQ4MSQyNyQxMTgkNzUkMTcyJDEwOCQxMjQkNzckNDckMTU5JDE3NyQ3OSQ=$RkJDRjM1QTBBMjEyOTEhEQzgxNDYgzQzZEMTk3NkZBMEI0NCjY0NTMzMDFGQzZFQjFEQTI0NzdBMJUKOGZENTJIDQjE3MjQ5QkNBRUYwNUY1MjZPSGNTRGM0RFQPTIJFxMjcyRTM4MkE3RDVENDk2RkZFOEE5M0VKCQjhBNzYwRDYxQURGHCRkQ5RUY=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(reservationIdx));

        return mDailyMobileService.getGourmetReceipt(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<GourmetReceiptData>, GourmetReceipt>()
            {
                @Override
                public GourmetReceipt apply(BaseDto<GourmetReceiptData> gourmetReceiptDataBaseDto) throws Exception
                {
                    GourmetReceipt gourmetReceipt;

                    if (gourmetReceiptDataBaseDto != null)
                    {
                        if (gourmetReceiptDataBaseDto.msgCode == 100 && gourmetReceiptDataBaseDto.data != null)
                        {
                            gourmetReceipt = gourmetReceiptDataBaseDto.data.getGourmetReceipt();
                        } else
                        {
                            throw new BaseException(gourmetReceiptDataBaseDto.msgCode, gourmetReceiptDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return gourmetReceipt;
                }
            });
    }

    @Override
    public Observable<GourmetReceipt> getGourmetReceipt(String aggregationId)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/receipt/app/gourmet/{aggregationId}"//
            : "ODYkMTAyJDg1JDEwNCQxMTkkMTAkODQkMTM0JDAkMTMzJDEwOCQxMjEkMSQyOCQzMiQxMDAk$UOOUQ4NTYyQjLQ4QzY2RTRBM0M3NDTNEAQ0JBMTJCMzA1REEwNzNBNDlBN0NENkRBRjdERTBENzRGQzlCRjVFMTZAGQCTKBEOTM5CRUExRjMyNLZQjM2MjExMTgwQGjYB2Q0E5NDkwMODNZE$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{aggregationId}", aggregationId);

        return mDailyMobileService.getGourmetReceipt(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<GourmetReceiptData>, GourmetReceipt>()
            {
                @Override
                public GourmetReceipt apply(BaseDto<GourmetReceiptData> gourmetReceiptDataBaseDto) throws Exception
                {
                    GourmetReceipt gourmetReceipt;

                    if (gourmetReceiptDataBaseDto != null)
                    {
                        if (gourmetReceiptDataBaseDto.msgCode == 100 && gourmetReceiptDataBaseDto.data != null)
                        {
                            gourmetReceipt = gourmetReceiptDataBaseDto.data.getGourmetReceipt();
                        } else
                        {
                            throw new BaseException(gourmetReceiptDataBaseDto.msgCode, gourmetReceiptDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return gourmetReceipt;
                }
            });
    }

    @Override
    public Observable<String> getGourmetReceiptByEmail(int reservationIdx, String email)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/receipt/email/gourmet/legacy/{reservationIdx}"//
            : "MTYwJDg4JDE0MiQxMTUkNjMkMTczJDE2JDY3JDgwJDc1JDExOSQxMDAkMTU1JDYyJDIxJDYk$QUY2MTSQwODU5RjU2RODc3GQkIwNjU4NzU1Q0QyNDUzQTg4OUY2N0UzNTI2NjM2QXkYQ1NK0Y5NThBHRTJGNV0ZGRjM2QTY2ANjRDMkBFFNTRCRDY5RjIwM0RCRODQlGQzU1RkVCRjcxNjkzQkQwMzRCMEUVCMG0Q3MUY0MzRFQjMzDNjcwNDcxTNjk=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(reservationIdx));

        return mDailyMobileService.getGourmetReceiptByEmail(Crypto.getUrlDecoderEx(API, urlParams), email) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, String>()
            {
                @Override
                public String apply(BaseDto<Object> objectBaseDto) throws Exception
                {
                    String message = null;

                    if (objectBaseDto != null)
                    {
                        if (objectBaseDto.msgCode == 100)
                        {
                            message = objectBaseDto.msg;
                        } else
                        {
                            throw new BaseException(objectBaseDto.msgCode, objectBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return message;
                }
            });
    }

    @Override
    public Observable<String> getGourmetReceiptByEmail(String aggregationId, String email)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/receipt/email/gourmet/{aggregationId}"//
            : "OSQ2JDg1JDYzJDQ3JDM5JDg3JDYkNTIkMTIwJDExNyQ4MCQ2MCQ1MiQ0NCQxNDAk$NzJERDJGI5MM0MwRDcwQTNBQUY2NUNGQUY1Mzk4MBzlDGN0FFMX0VUVEQTQ5RTNA1NzE0NWkIyNTg0MEMyMNDYzNUZDRTOUCxMUUwRTNFNjg5NEVDQUYyMEZDBMzMH2QkQwMEMwRkUxNQTBF$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{aggregationId}", aggregationId);

        return mDailyMobileService.getGourmetReceiptByEmail(Crypto.getUrlDecoderEx(API, urlParams), email) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, String>()
            {
                @Override
                public String apply(BaseDto<Object> objectBaseDto) throws Exception
                {
                    String message = null;

                    if (objectBaseDto != null)
                    {
                        if (objectBaseDto.msgCode == 100)
                        {
                            message = objectBaseDto.msg;
                        } else
                        {
                            throw new BaseException(objectBaseDto.msgCode, objectBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return message;
                }
            });
    }
}