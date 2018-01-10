package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.CommonInterface;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Configurations;
import com.daily.dailyhotel.entity.Notification;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.repository.remote.model.NotificationData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommonRemoteImpl extends BaseRemoteImpl implements CommonInterface
{
    public CommonRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<CommonDateTime> getCommonDateTime()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/common/datetime"//
            : "NzgkMTUkMjckNTUkNjEkNjckNjckNDUkMTAkMjYkMTckMTkkNjckNTQkNjgkNDYk$ODE1MDI0NzMZGREQAJ0IMDFBNkIzTNTLUwNDAzMzY3MzQ0IMzZXEMkUT0RTZEMNkZGRUDAJE4MTkDSxOTVEMjBBQjRFQzMVDN0VDOA==$";

        return mDailyMobileService.getCommonDateTime(Crypto.getUrlDecoderEx(API)) //
            .subscribeOn(Schedulers.io()).map((commonDateTimeDataBaseDto) ->
            {
                CommonDateTime commonDateTime;

                if (commonDateTimeDataBaseDto != null)
                {
                    if (commonDateTimeDataBaseDto.msgCode == 100 && commonDateTimeDataBaseDto.data != null)
                    {
                        commonDateTime = commonDateTimeDataBaseDto.data.getCommonDateTime();
                    } else
                    {
                        throw new BaseException(commonDateTimeDataBaseDto.msgCode, commonDateTimeDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return commonDateTime;
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> getShortUrl(String longUrl)
    {
        final String URL;

        if (Constants.UNENCRYPTED_URL == true)
        {
            URL = Constants.DEBUG ? "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyAYwC5Y-1h3inzttzRF7JN-aJhwR1fFCtU"//
                : "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyDjhmWw3dUuqYB9E9bbykjh53RFFdKiSuQ";
        } else
        {
            URL = Constants.DEBUG ? "NDYkMTY1JDExJDUxJDg0JDE5OSQxOCQxOSQ4NCQyNjAkMjYkMTU3JDY4JDE3MSQyMCQzNyQ=$MDlEQkI0REQSwMTVBNLFEzlFRDVPCMTUxQTM5BRkE0OUMxMzlENzAFCRUjY1REQzQUJBMDKA3MjczOUFERDBDMDQD1RXTRDMkQzQkUzRjY2NEJFMkU0MzlDQzZBMzZFRTMwNDQ0MzdCN0U4QTE0MTIzMkZENTIyOXTkzRUMxOTY5QOzY3JNjU4RkZCMDRBMEMyQzU1RkFGRkYzNDNIzQzdDMDZGRkNERDAxQTQyQjNGRjBBQzU1NjlDQTgyMjVBOUFEMUVGODUK5NUJG$"//
                : "NTEkMTE0JDI0OCQyNTAkMjM3JDE5NSQxNDckODgkODQkMTIyJDIyJDEyMyQxNTYkMjQ2JDEzMyQyNTAk$RDFFNjcwRDUyRENGREQ3QjNA4REE3M0M5QjBDQzQ2QTczQzUyRUEB3QThDM0VBNzA5NzE0NkZERDM5NTgwQUML4M0ZJCRjM0MTUyNjg1RjYwMTc2MkY1NG0VBMjYFJGMzI1ODQQwN0NERUY5RkYwRUFCNPkI1QQUE0RkQxMkZGMzY5NENBMDgwQkY1MjA2RDAzMDkxQjJDRYUFCOTYwOTc1QzkwMURCNzE0NjFGQzA0OUNGQTcxMTMWExQRkY2NTIzQ0FQJ2Q0RENkUy$";
        }

        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("longUrl", longUrl);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return mDailyMobileService.getShortUrl(Crypto.getUrlDecoderEx(URL), jsonObject) //
            .subscribeOn(Schedulers.io()).map((shortUrlData) ->
            {
                String shortUrl;

                if (shortUrlData != null && DailyTextUtils.isTextEmpty(shortUrlData.id) == false)
                {
                    shortUrl = shortUrlData.id;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return shortUrl;
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Notification> updateNotification(boolean agreed)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v1/notice/benefit"//
            : "ODckNDIkMzUkNzYkMzAkNjEkOTAkNTgkNTIkODEkNDckOSQ2MiQzMiQ3NyQ2OSQ=$NjdCRTNCQBTczOUY4RTJGMzY5RDA2NEWTRCMTkX5NDA3NEOE1HRDNBNVjc3NzMAA1NkAQRyNTgwNjBWDNEY3REIXM1NkRCREQ4MVQ=K=$";

        return mDailyMobileService.updateNotification(Crypto.getUrlDecoderEx(API), agreed)//
            .subscribeOn(Schedulers.io()).map((BaseDto<NotificationData> notificationDataBaseDto) ->
            {
                Notification notification;

                if (notificationDataBaseDto != null)
                {
                    if (notificationDataBaseDto.msgCode == 100 && notificationDataBaseDto.data != null)
                    {
                        notification = new Notification();
                        notification.serverDate = DailyCalendar.convertDateFormatString(notificationDataBaseDto.data.serverDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
                        notification.agreed = agreed;
                    } else
                    {
                        throw new BaseException(notificationDataBaseDto.msgCode, notificationDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return notification;
            });
    }

    public Observable<Configurations> getConfigurations()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/common/configurations"//
            : "NzYkMTMkNDIkODQkNjQkMCQ5MyQzMiQyNSQ0NyQ3MyQ4MCQ5OSQ2JDEwMSQ3OSQ=$FREQ3QETU4QTIzMHTVGMjE5NjUPzMUVBRTVUzQkY2MTlCMIDAY5NkQ3MDA5RDlEMTBDNDSM5Q0BQyMzUJGIRDY3VMDBGOIDNDOA=FQT=$";

        return mDailyMobileService.getConfigurations(Crypto.getUrlDecoderEx(API))//
            .subscribeOn(Schedulers.io()).map((configurationDataBaseDto) ->
            {
                Configurations configuration;

                if (configurationDataBaseDto != null)
                {
                    if (configurationDataBaseDto.msgCode == 100 && configurationDataBaseDto.data != null)
                    {
                        configuration = configurationDataBaseDto.data.getConfigurations();
                    } else
                    {
                        throw new BaseException(configurationDataBaseDto.msgCode, configurationDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return configuration;
            });
    }
}
