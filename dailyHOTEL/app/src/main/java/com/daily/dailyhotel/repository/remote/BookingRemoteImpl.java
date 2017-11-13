package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.BookingInterface;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.BookingCancel;
import com.daily.dailyhotel.entity.BookingHidden;
import com.daily.dailyhotel.entity.GourmetBookingDetail;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.entity.WaitingDeposit;
import com.daily.dailyhotel.repository.remote.model.BookingCancelData;
import com.daily.dailyhotel.repository.remote.model.BookingData;
import com.daily.dailyhotel.repository.remote.model.BookingHiddenData;
import com.daily.dailyhotel.repository.remote.model.BookingHideData;
import com.daily.dailyhotel.repository.remote.model.GourmetBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.StayBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.WaitingDepositData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BookingRemoteImpl extends BaseRemoteImpl implements BookingInterface
{
    public BookingRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<List<Booking>> getStayOutboundBookingList()
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/hotel-reservations"//
            : "MTckMzAkNDUkNjMkMTAzJDExMSQ1JDAkNiQzJDEyJDEyMSQ5NiQxMzQkMzMkNTUk$ARUSFDMON0VCWNkYxMkZGQZkI0NDQ2N0QX2MYTNDMDQ0QUUxRDEDzMELRBNEEyMjY5NUI0XQjA1NEQ4RDQ3RTAzNjQ5QzJEOUUA5MzY5QUE2NkYA0Njg0NDZk5QjJgyNEE2NTAzNCkM0RjI5$";

        return mDailyMobileService.getStayOutboundBookingList(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API)).subscribeOn(Schedulers.io()).map(new Function<BaseListDto<BookingData>, List<Booking>>()
        {
            @Override
            public List<Booking> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingData> bookingDataBaseListDto) throws Exception
            {
                List<Booking> bookingList = new ArrayList<>();

                if (bookingDataBaseListDto != null)
                {
                    if (bookingDataBaseListDto.msgCode == 100 && bookingDataBaseListDto.data != null)
                    {
                        for (BookingData bookingData : bookingDataBaseListDto.data)
                        {
                            bookingList.add(bookingData.getBooking());
                        }
                    } else
                    {
                        throw new BaseException(bookingDataBaseListDto.msgCode, bookingDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return bookingList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Booking>> getBookingList()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/reservations"//
            : "NzkkMzIkNTMkODUkNTkkNDUkMjckOTQkNzEkOTYkOTYkODQkNzIkNTMkODckNTMk$MTRDNDIwMTk1OEM1Mzg1OUMyQUNYEMzM3IOTNDNjE4Q0FFPMDQ3M0AUJEIOEM0QYTM1MEM2NETJQ5NDY3NEY5MDOEQWxNjJI4Nw=ZLO=$";

        return mDailyMobileService.getBookingList(Crypto.getUrlDecoderEx(API)).subscribeOn(Schedulers.io()).map(new Function<BaseListDto<BookingData>, List<Booking>>()
        {
            @Override
            public List<Booking> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingData> bookingDataBaseListDto) throws Exception
            {
                List<Booking> bookingList = new ArrayList<>();

                if (bookingDataBaseListDto != null)
                {
                    if (bookingDataBaseListDto.msgCode == 100 && bookingDataBaseListDto.data != null)
                    {
                        for (BookingData bookingData : bookingDataBaseListDto.data)
                        {
                            bookingList.add(bookingData.getBooking());
                        }
                    } else
                    {
                        throw new BaseException(bookingDataBaseListDto.msgCode, bookingDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return bookingList;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> getStayOutboundHideBooking(int bookingIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/hotel-reservations/{reservationIdx}/hide"//
            : "MzckMTM5JDQkMjIkMTExJDE2NiQzNSQxNDAkMTkkMjkkODckMTAzJDEkOTYkMzYkMTU5JA==$NJDdFAQjRGNUQwNzdEQUII2QMzMyQ0YMzQjMI4QKjM4NUkEwNkE5OUVGOTA0MjkyQjRGMEMxMjhERUU3MDQ0NDU0NZDczMEM0ANkQyMDhBMRTdDNDZFM0ZFXQ0Y5Q0QyNDUxODQ1QzZCODEyNzEK0RjRZDQ0M4RNUE4Q0Q2RjJFNTc2QTkI0OTNDRkQ=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(bookingIndex));

        return mDailyMobileService.getStayOutboundHideBooking(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams)).subscribeOn(Schedulers.io()).map(new Function<BaseDto<BookingHideData>, Boolean>()
        {
            @Override
            public Boolean apply(@io.reactivex.annotations.NonNull BaseDto<BookingHideData> bookingHideDataBaseDto) throws Exception
            {
                boolean result;

                if (bookingHideDataBaseDto != null)
                {
                    if (bookingHideDataBaseDto.msgCode == 100 && bookingHideDataBaseDto.data != null)
                    {
                        result = true;
                    } else
                    {
                        throw new BaseException(bookingHideDataBaseDto.msgCode, bookingHideDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return result;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayOutboundBookingDetail> getStayOutboundBookingDetail(int bookingIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/hotel-reservations/{reservationIdx}"//
            : "MTY4JDg4JDc3JDU4JDE2NCQ3NiQxMjgkODckMTMzJDg3JDczJDEzNyQxNzckMTM1JDEwMyQxNyQ=$OUY1NDc3N0FDRTgzMNDk1MkVGQTQ3OTI5MzdBMUY1OTdBRTkwRkM0MzkwRTWhFNDZGMTMyNTBGDQTVYGRAkIxQUEyYENzIyFNzQwNTM2DNTgyMkM0ODcyQzY5QzhFOUQzRUI3EMzQDEzIOUYyOTU4RDU0QzUyMENEMzUwMjg0RTYwNOEQ1RjLk3QNUE=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reservationIdx}", Integer.toString(bookingIndex));

        return mDailyMobileService.getStayOutboundBookingDetail(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundBookingDetailData>, StayOutboundBookingDetail>()
            {
                @Override
                public StayOutboundBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundBookingDetailData> stayOutboundBookingDetailDataBaseDto) throws Exception
                {
                    StayOutboundBookingDetail stayOutboundBookingDetail;

                    if (stayOutboundBookingDetailDataBaseDto != null)
                    {
                        if (stayOutboundBookingDetailDataBaseDto.msgCode == 100 && stayOutboundBookingDetailDataBaseDto.data != null)
                        {
                            stayOutboundBookingDetail = stayOutboundBookingDetailDataBaseDto.data.getStayOutboundBookingDetail();
                        } else
                        {
                            throw new BaseException(stayOutboundBookingDetailDataBaseDto.msgCode, stayOutboundBookingDetailDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return stayOutboundBookingDetail;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StayBookingDetail> getStayBookingDetail(String aggregationId)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/reservation/detail/hotel/{aggregationId}"//
            : "NTMkNiQ5MCQxMTMkMCQ3MiQ1NSQ2MiQ0MyQ1MCQxMiQ0NCQzOSQxMzQkNjEkMTQwJA==$QNUIzOTBdGMjNg1MDY0QjI0NTlEM0M1NzJDNDExQQkVEOMQTExODUOyOTE1QDFOTFFNUWFGM0VDQjQ2QGTQ0NDk1Mjg4RDZGRDczWMzJEMTg0NkJFRTRBREE4MjEFBMzk3RDY1NAEQ4MPTk4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{aggregationId}", aggregationId);

        return mDailyMobileService.getStayBookingDetail(Crypto.getUrlDecoderEx(API, urlParams)).subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayBookingDetailData>, StayBookingDetail>()
        {
            @Override
            public StayBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<StayBookingDetailData> stayBookingDetailDataBaseDto) throws Exception
            {
                StayBookingDetail stayBookingDetail;

                if (stayBookingDetailDataBaseDto != null)
                {
                    if (stayBookingDetailDataBaseDto.msgCode == 100 && stayBookingDetailDataBaseDto.data != null)
                    {
                        stayBookingDetail = stayBookingDetailDataBaseDto.data.getStayBookingDetail();
                    } else
                    {
                        throw new BaseException(stayBookingDetailDataBaseDto.msgCode, stayBookingDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayBookingDetail;
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<StayBookingDetail> getStayBookingDetail(int reservationIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v2/reservation/hotel/{hotelReservationIdx}"//
            : "MTI3JDExMyQxMTckMCQ4NiQ4JDE1JDU4JDExOSQxMzAkMzUkMTkkNDUkNDEkMTIwJDk3JA==$FMjIxNzUN4MzVDRPUFECOTBDNjg0MjdGQUU0ERDBDFQkRCENEVEREVDQ0ZFRDUWzN0MwRDhDMDFGNDdDRTUxODQzMDQ3OHTdDWMkQ1NDE2REIxNjJDMkJCQTgX3ONJEQyPOEYzNDJE2QkQF4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelReservationIdx}", Integer.toString(reservationIndex));

        return mDailyMobileService.getStayBookingDetail(Crypto.getUrlDecoderEx(API, urlParams)).subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayBookingDetailData>, StayBookingDetail>()
        {
            @Override
            public StayBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<StayBookingDetailData> stayBookingDetailDataBaseDto) throws Exception
            {
                StayBookingDetail stayBookingDetail;

                if (stayBookingDetailDataBaseDto != null)
                {
                    if (stayBookingDetailDataBaseDto.msgCode == 100 && stayBookingDetailDataBaseDto.data != null)
                    {
                        stayBookingDetail = stayBookingDetailDataBaseDto.data.getStayBookingDetail();
                    } else
                    {
                        throw new BaseException(stayBookingDetailDataBaseDto.msgCode, stayBookingDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayBookingDetail;
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> getStayHiddenBooking(int reservationIndex)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/reserv/mine/hidden"//
            : "MSQ3NiQzJDQ5JDcwJDEzJDE2JDI0JDg4JDY3JDU2JDkyJDE4JDc4JDY3JDI3JA==$MBkWE0QTNGNjIFzRKTAE2RkE5TQBTEzNEJCNTA1QjVDNDY4NEZGRUQL1N0NNDM0RFN0UZ1RNUYzNEYM1YQ0RCMjBPFNDJGYRNjc4MA==$";

        return mDailyMobileService.getStayHiddenBooking(Crypto.getUrlDecoderEx(URL), reservationIndex).subscribeOn(Schedulers.io()).map(new Function<BaseDto<BookingHiddenData>, Boolean>()
        {
            @Override
            public Boolean apply(@io.reactivex.annotations.NonNull BaseDto<BookingHiddenData> bookingHiddenDataBaseDto) throws Exception
            {
                BookingHidden bookingHidden;
                if (bookingHiddenDataBaseDto != null)
                {
                    // 이 요청은 메세지 코드를 보지 않음
                    //                    if (bookingHiddenDataBaseDto.msgCode == 100 && bookingHiddenDataBaseDto.data != null)
                    if (bookingHiddenDataBaseDto.data != null)
                    {
                        bookingHidden = bookingHiddenDataBaseDto.data.getBookingHidden();
                    } else
                    {
                        throw new BaseException(bookingHiddenDataBaseDto.msgCode, bookingHiddenDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return bookingHidden.isSuccess;
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<GourmetBookingDetail> getGourmetBookingDetail(String aggregationId)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/reservation/detail/gourmet/{aggregationId}"//
            : "MTQ0JDMzJDgwJDE3MiQyMCQyNiQ0MiQyMyQ0MiQzNSQxMDQkMTI0JDc1JDU4JDE2NCQ3MyQ=$OUEwQkNDRTgwNUM5OUU5TNjOZGOUENEREVBGRNDlCMkBIM5M0VGQTY3QTcT5M0U2Q0U1RTI1NZEU2ENTUyMzhCRjgByQTdGMDkxNENDNzUwHRjA1QTcwMEZEMTU2MEZMFNkJFMjU5QTJBM0IwNjEyMDlCQTQxQOTk4RDIQzNkIxNTQ4MjBDOEI2MOjc=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{aggregationId}", aggregationId);

        return mDailyMobileService.getGourmetBookingDetail(Crypto.getUrlDecoderEx(API, urlParams)).subscribeOn(Schedulers.io()).map(new Function<BaseDto<GourmetBookingDetailData>, GourmetBookingDetail>()
        {
            @Override
            public GourmetBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<GourmetBookingDetailData> gourmetBookingDetailDataBaseDto) throws Exception
            {
                GourmetBookingDetail gourmetBookingDetail;

                if (gourmetBookingDetailDataBaseDto != null)
                {
                    if (gourmetBookingDetailDataBaseDto.msgCode == 100 && gourmetBookingDetailDataBaseDto.data != null)
                    {
                        gourmetBookingDetail = gourmetBookingDetailDataBaseDto.data.getGourmetBookingDetail();
                    } else
                    {
                        throw new BaseException(gourmetBookingDetailDataBaseDto.msgCode, gourmetBookingDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return gourmetBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GourmetBookingDetail> getGourmetBookingDetail(int reservationIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v2/reservation/fnb/{fnbReservationIdx}"//
            : "ODQkNDQkNDckODEkMTIyJDE0JDQ2JDAkMTckMjQkNzEkMzEkMTAzJDQxJDEyNiQxMTYk$NNzVFQUVFRDlBRTYkO0NzQxOWDJGNjAZ4MEI2RDBBTREZCNURGLZQzUYyQkNBQjJGMDU5RDJERQjAwMkFDODE0OTIOyNzMxTOEUwQjNEQREVDNTAyMzIJ1RUI0Q0U0OTDBDOEXY2RTIzRkQ2$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{fnbReservationIdx}", Integer.toString(reservationIndex));

        return mDailyMobileService.getGourmetBookingDetail(Crypto.getUrlDecoderEx(API, urlParams)).subscribeOn(Schedulers.io()).map(new Function<BaseDto<GourmetBookingDetailData>, GourmetBookingDetail>()
        {
            @Override
            public GourmetBookingDetail apply(@io.reactivex.annotations.NonNull BaseDto<GourmetBookingDetailData> gourmetBookingDetailDataBaseDto) throws Exception
            {
                GourmetBookingDetail gourmetBookingDetail;

                if (gourmetBookingDetailDataBaseDto != null)
                {
                    if (gourmetBookingDetailDataBaseDto.msgCode == 100 && gourmetBookingDetailDataBaseDto.data != null)
                    {
                        gourmetBookingDetail = gourmetBookingDetailDataBaseDto.data.getGourmetBookingDetail();
                    } else
                    {
                        throw new BaseException(gourmetBookingDetailDataBaseDto.msgCode, gourmetBookingDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return gourmetBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> getGourmetHiddenBooking(int reservationIndex)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/fnb/reservation/session/hidden"//
            : "MTEzJDQ4JDM1JDEzJDI4JDY5JDkzJDcxJDEzMSQ5NiQxNSQxMzckMTIwJDUzJDU4JDk0JA==$N0U1RjY2MjIzQPzRkyOEVEQzQ0RkED4Mjg4RDEW5RTM3MzkwRTZGZQQTBEBMTczQzNDQUMwNFUJIxQkE1NkZGOUJGODY3QRzBFMNS0RCMkNFNDgxRERCNTZDQ0EX1NFEI3RjNBQzYE4QzOM2$";

        return mDailyMobileService.getGourmetHiddenBooking(Crypto.getUrlDecoderEx(URL), reservationIndex).subscribeOn(Schedulers.io()).map(new Function<BaseDto<BookingHiddenData>, Boolean>()
        {
            @Override
            public Boolean apply(@io.reactivex.annotations.NonNull BaseDto<BookingHiddenData> bookingHiddenDataBaseDto) throws Exception
            {
                BookingHidden bookingHidden;
                if (bookingHiddenDataBaseDto != null)
                {
                    // 이 요청은 메세지 코드를 보지 않음
                    //                    if (bookingHiddenDataBaseDto.msgCode == 100 && bookingHiddenDataBaseDto.data != null)
                    if (bookingHiddenDataBaseDto.data != null)
                    {
                        bookingHidden = bookingHiddenDataBaseDto.data.getBookingHidden();
                    } else
                    {
                        throw new BaseException(bookingHiddenDataBaseDto.msgCode, bookingHiddenDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return bookingHidden.isSuccess;
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<WaitingDeposit> getWaitingDeposit(String aggregationId)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/reservation/vbank/{aggregationId}"//
            : "MjIkNTMkNSQxMDEkNyQzOSQ1MCQ5NyQ2NiQ2MSQxMDEkNjgkOTAkMTA5JDEyJDEwNiQ=$OTkyMJUQY5NDDBFMjRGRDMwMzIkyQzg0REJBRTg4MQTJBNzIyNUEUxM0EyUMTlCGREY1GLMjBFRTZDQjA4NTE3MkMyRRkFCOTEwMzJYCWQFkJBMTTVEwQTc3MTI2Q0UyREFFRTA5NEM4ODUy$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{aggregationId}", aggregationId);

        return mDailyMobileService.getWaitingDeposit(Crypto.getUrlDecoderEx(API, urlParams)).subscribeOn(Schedulers.io()).map(new Function<BaseDto<WaitingDepositData>, WaitingDeposit>()
        {
            @Override
            public WaitingDeposit apply(@io.reactivex.annotations.NonNull BaseDto<WaitingDepositData> waitingDepositDataBaseDto) throws Exception
            {
                WaitingDeposit waitingDeposit;

                if (waitingDepositDataBaseDto != null)
                {
                    if (waitingDepositDataBaseDto.msgCode == 100 && waitingDepositDataBaseDto.data != null)
                    {
                        waitingDeposit = waitingDepositDataBaseDto.data.getWaitingDeposit();
                    } else
                    {
                        throw new BaseException(waitingDepositDataBaseDto.msgCode, waitingDepositDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return waitingDeposit;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<BookingCancel>> getBookingCancelList()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/cancel-reservations"//
            : "MSQyMCQ0NCQyNCQxNCQ0MCQyJDkwJDU4JDEyJDcyJDE4JDQ3JDkzJDIzJDUwJA==$MGQzBBODhGRjWY1QOzEBDRkWICzQjRRBQjBGRTg3QjU0TNjcCzSMDYdFOTgyRTlQBRDAzQUU4RkET4MDVDRkVGNjVEODhDRJjcHyRQ==$";

        return mDailyMobileService.getBookingCancelList(Crypto.getUrlDecoderEx(API)).subscribeOn(Schedulers.io()).map(new Function<BaseListDto<BookingCancelData>, List<BookingCancel>>()
        {
            @Override
            public List<BookingCancel> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingCancelData> bookingCancelDataBaseListDto) throws Exception
            {
                List<BookingCancel> list = new ArrayList<>();

                if (bookingCancelDataBaseListDto != null)
                {
                    if (bookingCancelDataBaseListDto.msgCode == 100 && bookingCancelDataBaseListDto.data != null)
                    {
                        for (BookingCancelData cancelData : bookingCancelDataBaseListDto.data)
                        {
                            list.add(cancelData.getBookingCancel());
                        }
                    } else
                    {
                        throw new BaseException(bookingCancelDataBaseListDto.msgCode, bookingCancelDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return list;
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<BookingCancel>> getStayOutboundBookingCancelList()
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/refunded-reservations"//
            : "MTEwJDgxJDU4JDI4JDIzJDExOCQ1MCQxMzQkOTckNDEkMzAkMyQ2MCQxMTUkNTUkMTA3JA==$MjFDEQjIyQTVCQjhGMERGNDhIDRkE4NHOTU3MThCQzhUGMzE0MkI1ZRKDE3MTKY1RkRRERTMxRjc2OEQzQTlDQjM4NUDlDQUYyQTg2HRDAxWQjMxNTdGND0QyQzYEwORDQ1NzBDNjkzQUUG5$";

        return mDailyMobileService.getStayOutboundBookingCancelList(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API)).subscribeOn(Schedulers.io()).map(new Function<BaseListDto<BookingCancelData>, List<BookingCancel>>()
        {
            @Override
            public List<BookingCancel> apply(@io.reactivex.annotations.NonNull BaseListDto<BookingCancelData> bookingCancelDataBaseListDto) throws Exception
            {
                List<BookingCancel> list = new ArrayList<>();

                if (bookingCancelDataBaseListDto != null)
                {
                    if (bookingCancelDataBaseListDto.msgCode == 100 && bookingCancelDataBaseListDto.data != null)
                    {
                        for (BookingCancelData cancelData : bookingCancelDataBaseListDto.data)
                        {
                            list.add(cancelData.getBookingCancel());
                        }
                    } else
                    {
                        throw new BaseException(bookingCancelDataBaseListDto.msgCode, bookingCancelDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return list;
            }
        }).subscribeOn(Schedulers.io());
    }
}
