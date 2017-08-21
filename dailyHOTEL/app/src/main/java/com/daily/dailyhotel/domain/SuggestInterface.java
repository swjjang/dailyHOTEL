package com.daily.dailyhotel.domain;

import android.support.v4.util.Pair;

import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.network.model.StayKeyword;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public interface SuggestInterface
{
    Observable<List<Suggest>> getSuggestsByStayOutbound(String keyword);

    Observable<Pair<String, ArrayList<StayKeyword>>> getSuggestsByStayInbound(String checkInDate, int stays, String keyword);

    Observable<Pair<String, ArrayList<GourmetKeyword>>> getSuggestsByGourmet(String visitDate, String keyword);
}
