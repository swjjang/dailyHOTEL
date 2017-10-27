package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.domain.CommonInterface;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Configurations;
import com.daily.dailyhotel.entity.Notification;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.repository.remote.model.NotificationData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.DailyCalendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class CommonRemoteImpl implements CommonInterface
{
    private Context mContext;

    public CommonRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<CommonDateTime> getCommonDateTime()
    {
        return DailyMobileAPI.getInstance(mContext).getCommonDateTime().map((commonDateTimeDataBaseDto) ->
        {
            CommonDateTime commonDateTime = null;

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
    public Observable<Review> getReview(String placeType, int reservationIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getReview(placeType, reservationIndex).map((reviewDataBaseDto) ->
        {
            Review review = null;

            if (reviewDataBaseDto != null)
            {
                if (reviewDataBaseDto.msgCode == 100 && reviewDataBaseDto.data != null)
                {
                    review = reviewDataBaseDto.data.getReview();
                } else
                {
                    throw new BaseException(reviewDataBaseDto.msgCode, reviewDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return review;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> getShortUrl(String longUrl)
    {
        return DailyMobileAPI.getInstance(mContext).getShortUrl(longUrl).map((shortUrlData) ->
        {
            String shortUrl = null;

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
        return DailyMobileAPI.getInstance(mContext).updateNotification(agreed).map((BaseDto<NotificationData> notificationDataBaseDto) ->
        {
            Notification notification = null;

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
        return DailyMobileAPI.getInstance(mContext).getConfigurations().map((configurationDataBaseDto) ->
        {
            Configurations configuration;

            if (configurationDataBaseDto != null)
            {
                if (configurationDataBaseDto.msgCode == 100 && configurationDataBaseDto.data != null)
                {
                    configuration = configurationDataBaseDto.data.getConfiguration();
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
