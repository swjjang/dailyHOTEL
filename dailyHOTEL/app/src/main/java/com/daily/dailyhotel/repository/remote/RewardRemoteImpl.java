package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.CouponInterface;
import com.daily.dailyhotel.domain.RewardInterface;
import com.daily.dailyhotel.entity.RewardHistory;
import com.daily.dailyhotel.repository.remote.model.CouponsData;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class RewardRemoteImpl implements RewardInterface
{
    private Context mContext;

    public RewardRemoteImpl(Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<RewardHistory> getRewardHistoryList()
    {
        return null;
    }
}
