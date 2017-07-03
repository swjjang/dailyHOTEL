package com.daily.dailyhotel.domain;

import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetParams;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 6. 30..
 */

public interface GourmetListInterface
{
    Observable<List<Gourmet>> getGourmetList(GourmetParams gourmetParams);
}
