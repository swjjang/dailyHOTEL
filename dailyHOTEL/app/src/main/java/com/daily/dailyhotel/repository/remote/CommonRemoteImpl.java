package com.daily.dailyhotel.repository.remote;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.CommonInterface;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Configurations;
import com.daily.dailyhotel.entity.NoticeAgreementMessage;
import com.daily.dailyhotel.entity.NoticeAgreementResultMessage;
import com.daily.dailyhotel.entity.Notification;
import com.daily.dailyhotel.repository.remote.model.NotificationData;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommonRemoteImpl extends BaseRemoteImpl implements CommonInterface
{
    @Override
    public Observable<CommonDateTime> getCommonDateTime()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/common/datetime"//
            : "NzgkMTUkMjckNTUkNjEkNjckNjckNDUkMTAkMjYkMTckMTkkNjckNTQkNjgkNDYk$ODE1MDI0NzMZGREQAJ0IMDFBNkIzTNTLUwNDAzMzY3MzQ0IMzZXEMkUT0RTZEMNkZGRUDAJE4MTkDSxOTVEMjBBQjRFQzMVDN0VDOA==$";

        return mDailyMobileService.getCommonDateTime(Crypto.getUrlDecoderEx(API)) //
            .subscribeOn(Schedulers.io()).map((commonDateTimeDataBaseDto) -> {
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
            .subscribeOn(Schedulers.io()).map((shortUrlData) -> {
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
            .subscribeOn(Schedulers.io()).map((BaseDto<NotificationData> notificationDataBaseDto) -> {
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
            .subscribeOn(Schedulers.io()).map((configurationDataBaseDto) -> {
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

    @Override
    public Observable<NoticeAgreementMessage> getNoticeAgreementMessage()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/confirm"//
            : "NTYkNjgkMTkkNyQ4JDI4JDUyJDE4JDc5JDg1JDY0JDg0JDY5JDc5JDYxJDk0JA==$ODdCM0QIL5RDY2RTRFNQjlFGRDhBRATk5ODlCMkEyQjVBQjYxRjc2YQkRENzUEzTOVUExMAjZENzkD1NVDEE3RTNFBHQjNRCOUYyNA==$";

        return mDailyMobileService.getNoticeAgreementMessage(Crypto.getUrlDecoderEx(API))//
            .subscribeOn(Schedulers.io()).map((baseDto) -> {
                NoticeAgreementMessage noticeAgreementMessage;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        noticeAgreementMessage = baseDto.data.getNoticeAgreementMessage();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return noticeAgreementMessage;
            });
    }


    @Override
    public Observable<NoticeAgreementResultMessage> updateNoticeAgreement(boolean agreed)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v1/notice/agreement/result"//
            : "NjgkOSQ4NSQ3NiQ3MCQ0NyQ1MCQzMiQ5NCQxNiQ5JDE3JDQxJDQ4JDk1JDUk$QTcxNVjkwRTOUEyOURMYBQTcyQzlBOTBCOUIS1OTg2VQ0JBREUI5NDTEwYRDA5M0RCNzAxMzM0ODIzXVNzc5NzRM3NjUxNEUODxMQQ==$";

        return mDailyMobileService.updateNoticeAgreement(Crypto.getUrlDecoderEx(API), agreed)//
            .subscribeOn(Schedulers.io()).map((baseDto) -> {
                NoticeAgreementResultMessage noticeAgreementResultMessage;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        noticeAgreementResultMessage = baseDto.data.getNoticeAgreementResultMessage();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return noticeAgreementResultMessage;
            });
    }
}
