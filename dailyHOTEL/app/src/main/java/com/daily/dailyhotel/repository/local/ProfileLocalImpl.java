package com.daily.dailyhotel.repository.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.domain.ProfileInterface;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.twoheart.dailyhotel.util.DailyPreference;

import io.reactivex.Observable;

public class ProfileLocalImpl implements ProfileInterface
{
    private Context mContext;

    public ProfileLocalImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<User> getProfile()
    {
        return null;
    }

    @Override
    public Observable<UserBenefit> getBenefit()
    {
        return null;
    }

    @Override
    public void setVerified(boolean verify)
    {
        DailyPreference.getInstance(mContext).setVerification(verify);
    }

    @Override
    public boolean isVerified()
    {
        return DailyPreference.getInstance(mContext).isVerification();
    }
}
