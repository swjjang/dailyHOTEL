package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbounds;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 6. 14..
 */

public interface RecentlyInterface
{
    Observable<StayOutbounds> getStayOutboundRecentlyList(Context context, String targetIndices, int numberOfResults);

    Observable<ArrayList<RecentlyPlace>> getInboundRecentlyList(JSONObject recentJsonObject);
}
