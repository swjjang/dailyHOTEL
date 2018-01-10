package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Configurations;
import com.daily.dailyhotel.entity.Notification;
import com.daily.dailyhotel.entity.Review;

import io.reactivex.Observable;

public interface CommonInterface
{
    Observable<CommonDateTime> getCommonDateTime();

    Observable<String> getShortUrl(String longUrl);

    Observable<Notification> updateNotification(boolean agreed);

    Observable<Configurations> getConfigurations();
}
