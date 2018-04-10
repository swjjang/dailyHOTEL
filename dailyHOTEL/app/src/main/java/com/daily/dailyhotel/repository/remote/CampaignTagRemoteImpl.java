package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.CampaignTagInterface;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.daily.dailyhotel.entity.StayCampaignTags;
import com.daily.dailyhotel.repository.remote.model.CampaignTagData;
import com.daily.dailyhotel.repository.remote.model.GourmetCampaignTagsData;
import com.daily.dailyhotel.repository.remote.model.StayCampaignTagsData;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 8. 8..
 */

public class CampaignTagRemoteImpl extends BaseRemoteImpl implements CampaignTagInterface
{
    @Override
    public Observable<ArrayList<CampaignTag>> getCampaignTagList(String serviceType)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/campaign/hashtag" //
            : "ODckMjIkODgkNTUkMzQkMyQzNyQyNyQ0JDY5JDcyJDAkNTMkODMkNTQkMjAk$QNEUTTxNjlCNDRCQTg1RHDBDRDCc0QW0Y5Qzg5NYDEJCM0ZCQkEwMTLKQ0NEM4MUYNGRTBDNTZVFZRjMzRkQ5JRDAyNEI1RDcwRA=JP=$";

        return mDailyMobileService.getCampaignTagList(Crypto.getUrlDecoderEx(URL), serviceType) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<CampaignTagData>, ArrayList<CampaignTag>>()
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
            }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<StayCampaignTags> getStayCampaignTags(int index, String checkInDate, int nights)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/campaign/hashtag" //
            : "NTAkOCQ4MSQyNiQxNyQ4NSQ5JDczJDYkMTkkNzQkNTckODIkNTkkNTgkODAk$OUVCMjUFFWVNkZGN0JCHIMEQ4QTNCRJTQ5N0EzMUFGNkJDNjIwQjVCMERULREBNzM2RDNENTQzQjIL5GJNkYwVMkNGNDhGDXRUVBNg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{idx}", Integer.toString(index));

        return mDailyMobileService.getStayCampaignTags(Crypto.getUrlDecoderEx(URL, urlParams), checkInDate, nights) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayCampaignTagsData>, StayCampaignTags>()
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
    public Observable<GourmetCampaignTags> getGourmetCampaignTags(Context context, int index, String visitDate)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/campaign/hashtag" //
            : "NTAkOCQ4MSQyNiQxNyQ4NSQ5JDczJDYkMTkkNzQkNTckODIkNTkkNTgkODAk$OUVCMjUFFWVNkZGN0JCHIMEQ4QTNCRJTQ5N0EzMUFGNkJDNjIwQjVCMERULREBNzM2RDNENTQzQjIL5GJNkYwVMkNGNDhGDXRUVBNg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{idx}", Integer.toString(index));

        return mDailyMobileService.getGourmetCampaignTags(Crypto.getUrlDecoderEx(URL, urlParams), visitDate) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<GourmetCampaignTagsData>, GourmetCampaignTags>()
            {
                @Override
                public GourmetCampaignTags apply(@io.reactivex.annotations.NonNull BaseDto<GourmetCampaignTagsData> gourmetCampaignTagsDataBaseDto) throws Exception
                {
                    GourmetCampaignTags gourmetCampaignTags = null;

                    if (gourmetCampaignTagsDataBaseDto != null)
                    {
                        if (gourmetCampaignTagsDataBaseDto.msgCode == 100 && gourmetCampaignTagsDataBaseDto.data != null)
                        {
                            gourmetCampaignTags = gourmetCampaignTagsDataBaseDto.data.getGourmetCampaignTags(context);
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
