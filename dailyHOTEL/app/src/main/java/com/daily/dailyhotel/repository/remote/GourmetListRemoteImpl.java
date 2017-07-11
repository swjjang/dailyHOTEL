package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.GourmetListInterface;
import com.daily.dailyhotel.repository.remote.model.GourmetListData;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by android_sam on 2017. 6. 30..
 */

public class GourmetListRemoteImpl implements GourmetListInterface
{
    private Context mContext;

    public GourmetListRemoteImpl(Context mContext)
    {
        this.mContext = mContext;
    }

    @Override
    public Observable<List<Gourmet>> getGourmetList(GourmetParams gourmetParams)
    {
        return DailyMobileAPI.getInstance(mContext) //
            .getGourmetList(gourmetParams.toParamsMap(), gourmetParams.getCategoryList(), gourmetParams.getTimeList(), gourmetParams.getLuxuryList()) //
            .map(new Function<BaseDto<GourmetListData>, List<Gourmet>>()
            {
                @Override
                public List<Gourmet> apply(@NonNull BaseDto<GourmetListData> gourmetListDataBaseDto) throws Exception
                {
                    List<Gourmet> gourmetList = null;

                    if (gourmetListDataBaseDto != null)
                    {
                        if (gourmetListDataBaseDto.msgCode == 100 && gourmetListDataBaseDto.data != null)
                        {
                            gourmetList = gourmetListDataBaseDto.data.getGourmetList(mContext);
                            if (gourmetList == null || gourmetList.size() == 0)
                            {
                                gourmetList = new ArrayList<>();
                            }
                        } else
                        {
                            throw new BaseException(gourmetListDataBaseDto.msgCode, gourmetListDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return gourmetList;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }
}
