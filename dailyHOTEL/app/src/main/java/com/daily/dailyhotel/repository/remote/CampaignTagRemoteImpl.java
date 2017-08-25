package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.CampaignTagInterface;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.daily.dailyhotel.entity.StayCampaignTags;
import com.daily.dailyhotel.repository.remote.model.CampaignTagData;
import com.daily.dailyhotel.repository.remote.model.GourmetCampaignTagsData;
import com.daily.dailyhotel.repository.remote.model.StayCampaignTagsData;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by iseung-won on 2017. 8. 8..
 */

public class CampaignTagRemoteImpl implements CampaignTagInterface
{
    private Context mContext;

    public CampaignTagRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<ArrayList<CampaignTag>> getCampaignTagList(String serviceType)
    {
        return DailyMobileAPI.getInstance(mContext).getCampaignTagList(serviceType).map(new Function<BaseListDto<CampaignTagData>, ArrayList<CampaignTag>>()
        {
            @Override
            public ArrayList<CampaignTag> apply(@io.reactivex.annotations.NonNull BaseListDto<CampaignTagData> campaignTagDataBaseListDto) throws Exception
            {
                ArrayList<CampaignTag> campaignTagList = new ArrayList<>();

                if (campaignTagDataBaseListDto != null)
                {
                    if (campaignTagDataBaseListDto.msgCode == 100 && campaignTagDataBaseListDto.data != null)
                    {
                        List<CampaignTagData> list = campaignTagDataBaseListDto.data;

                        for (CampaignTagData campaignTagData : list)
                        {
                            campaignTagList.add(campaignTagData.getCampaignTag());
                        }
                    } else
                    {
                        throw new BaseException(campaignTagDataBaseListDto.msgCode, campaignTagDataBaseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return campaignTagList;
            }
        });
    }

    @Override
    public Observable<StayCampaignTags> getStayCampaignTags(int index, StayBookingDay stayBookingDay)
    {
        String checkInDate = stayBookingDay.getCheckInDay("yyyy-MM-dd");

        int nights = 1;
        try
        {
            nights = stayBookingDay.getNights();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return DailyMobileAPI.getInstance(mContext).getStayCampaignTags(index, checkInDate, nights).map(new Function<BaseDto<StayCampaignTagsData>, StayCampaignTags>()
        {
            @Override
            public StayCampaignTags apply(@io.reactivex.annotations.NonNull BaseDto<StayCampaignTagsData> stayCampaignTagsDataBaseDto) throws Exception
            {
                StayCampaignTags stayCampaignTags = null;

                if (stayCampaignTagsDataBaseDto != null)
                {
                    if (stayCampaignTagsDataBaseDto.msgCode == 100 && stayCampaignTagsDataBaseDto.data != null)
                    {
                        stayCampaignTags = stayCampaignTagsDataBaseDto.data.getStayCampaigns();

                        if (stayCampaignTags == null)
                        {
                            stayCampaignTags = new StayCampaignTags();
                        }

                        stayCampaignTags.msgCode = stayCampaignTagsDataBaseDto.msgCode;
                    } else if (stayCampaignTagsDataBaseDto.msgCode == -101)
                    {
                        if (stayCampaignTagsDataBaseDto.data != null)
                        {
                            stayCampaignTags = stayCampaignTagsDataBaseDto.data.getStayCampaigns();
                        }

                        // 조회된 데이터가 없을때 - KRQA-1630 요청으로 emptyView 생성
                        if (stayCampaignTags == null)
                        {
                            stayCampaignTags = new StayCampaignTags();
                        }

                        stayCampaignTags.msgCode = stayCampaignTagsDataBaseDto.msgCode;
                    } else if (stayCampaignTagsDataBaseDto.msgCode == 200)
                    {
                        // 종료된 캠페인 태그
                        stayCampaignTags = new StayCampaignTags();
                        stayCampaignTags.msgCode = stayCampaignTagsDataBaseDto.msgCode;
                    } else
                    {
                        throw new BaseException(stayCampaignTagsDataBaseDto.msgCode, stayCampaignTagsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayCampaignTags;
            }
        });
    }

    @Override
    public Observable<GourmetCampaignTags> getGourmetCampaignTags(int index, GourmetBookingDay gourmetBookingDay)
    {
        String visitDate = gourmetBookingDay.getVisitDay("yyyy-MM-dd");

        return DailyMobileAPI.getInstance(mContext).getGourmetCampaignTags(index, visitDate).map(new Function<BaseDto<GourmetCampaignTagsData>, GourmetCampaignTags>()
        {
            @Override
            public GourmetCampaignTags apply(@io.reactivex.annotations.NonNull BaseDto<GourmetCampaignTagsData> gourmetCampaignTagsDataBaseDto) throws Exception
            {
                GourmetCampaignTags gourmetCampaignTags = null;

                if (gourmetCampaignTagsDataBaseDto != null)
                {
                    if (gourmetCampaignTagsDataBaseDto.msgCode == 100 && gourmetCampaignTagsDataBaseDto.data != null)
                    {
                        gourmetCampaignTags = gourmetCampaignTagsDataBaseDto.data.getGourmetCampaignTags(mContext);

                        if (gourmetCampaignTags == null)
                        {
                            gourmetCampaignTags = new GourmetCampaignTags();
                        }

                        gourmetCampaignTags.msgCode = gourmetCampaignTagsDataBaseDto.msgCode;
                    } else if (gourmetCampaignTagsDataBaseDto.msgCode == -101)
                    {
                        if (gourmetCampaignTagsDataBaseDto.data != null)
                        {
                            gourmetCampaignTags = gourmetCampaignTagsDataBaseDto.data.getGourmetCampaignTags(mContext);
                        }

                        if (gourmetCampaignTags == null)
                        {
                            // 조회된 데이터가 없을때 - KRQA-1630 요청으로 emptyView 생성
                            gourmetCampaignTags = new GourmetCampaignTags();
                        }

                        gourmetCampaignTags.msgCode = gourmetCampaignTagsDataBaseDto.msgCode;
                    } else if (gourmetCampaignTagsDataBaseDto.msgCode == 200)
                    {
                        // 종료된 캠페인 태그
                        gourmetCampaignTags = new GourmetCampaignTags();
                        gourmetCampaignTags.msgCode = gourmetCampaignTagsDataBaseDto.msgCode;
                    } else
                    {
                        throw new BaseException(gourmetCampaignTagsDataBaseDto.msgCode, gourmetCampaignTagsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return gourmetCampaignTags;
            }
        });
    }
}
