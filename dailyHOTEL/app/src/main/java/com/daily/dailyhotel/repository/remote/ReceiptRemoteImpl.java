package com.daily.dailyhotel.repository.remote;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ReceiptInterface;
import com.daily.dailyhotel.entity.GourmetReceipt;
import com.daily.dailyhotel.entity.StayReceipt;
import com.daily.dailyhotel.repository.remote.model.GourmetReceiptData;
import com.daily.dailyhotel.repository.remote.model.StayReceiptData;
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
                    String message;

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
                    String message;

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
    public Observable<StayReceipt> getStayReceipt(int reservationIdx)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/receipt"//
            : "NTIkMzckNDYkNDQkNjEkMzIkNzMkNDQkMjUkODQkODMkMiQ2NiQyNCQ4NyQ3MSQ=$ODPIyNDhGMzU1QTQzNzRBQjgR1QD0E2MTJBLNzRFRWDVDRkNQY0NRTEzRTk1PNzY3OTODUzZRDU1RjlTEMzUxRDCFFSGMzNDNzM0QQ==$";

        return mDailyMobileService.getStayReceipt(Crypto.getUrlDecoderEx(URL), Integer.toString(reservationIdx)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayReceiptData>, StayReceipt>()
            {
                @Override
                public StayReceipt apply(BaseDto<StayReceiptData> stayReceiptDataBaseDto) throws Exception
                {
                    StayReceipt stayReceipt;

                    if (stayReceiptDataBaseDto != null)
                    {
                        if (stayReceiptDataBaseDto.msgCode == 0 && stayReceiptDataBaseDto.data != null) // 아주 오래된 API라서 메시지코드가 100이 아니라 0입니다. 유의 하세요.
                        {
                            stayReceipt = stayReceiptDataBaseDto.data.getStayReceipt();
                        } else
                        {
                            throw new BaseException(stayReceiptDataBaseDto.msgCode, stayReceiptDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return stayReceipt;
                }
            });
    }

    @Override
    public Observable<StayReceipt> getStayReceipt(String aggregationId)
    {
        // not yet used.
        return null;
    }

    @Override
    public Observable<String> getStayReceiptByEmail(int reservationIdx, String email)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/reservations/{kind}/{reservationIdx}/receipts" //
            : "ODQkOTgkMTY5JDckNTYkNjkkNTkkOTUkNjQkMTczJDEyOCQ3NiQ0NCQ0NCQ3OSQxMTEk$OUIxOEUQwMUJFREVGQkMwOENEOEE5MkY5REY2NjE0QTcFC2REU0RDk2MzEVyOZURCMXDQ1Q0UOzRUEPYzOENFMzZGREMwNQUEwQUHFDNkVDQRzIQxRjFBNzJGQUQwQTJCMDg5NOEU2NTc1QzgyRkE1QkFCQ0E0NzQ0ODlBRTYzN0ZGMjhFMYzFMDMDQ=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{kind}", "stay");
        urlParams.put("{reservationIdx}", Integer.toString(reservationIdx));

        return mDailyMobileService.getStayReceiptByEmail(Crypto.getUrlDecoderEx(URL, urlParams), email) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, String>()
            {
                @Override
                public String apply(BaseDto<Object> objectBaseDto) throws Exception
                {
                    String message;

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
    public Observable<String> getStayReceiptByEmail(String aggregationId, String email)
    {
        // not yet used;
        return null;
    }
}
