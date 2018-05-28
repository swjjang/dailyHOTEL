package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.util.Pair;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.domain.StayInterface;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.RoomImageInformation;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetailk;
import com.daily.dailyhotel.entity.StayFilterCount;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.daily.dailyhotel.entity.Stays;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.model.RoomImageInformationData;
import com.daily.dailyhotel.repository.remote.model.SubwayAreasData;
import com.daily.dailyhotel.repository.remote.model.TrueVRData;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class StayRemoteImpl extends BaseRemoteImpl implements StayInterface
{
    @Override
    public Observable<Stays> getList(Context context, DailyCategoryType categoryType, Map<String, Object> queryMap, String abTestType)
    {
        return mDailyMobileService.getStayList(getBaseUrl(context) + getListApiUrl(context, categoryType) + toStringQueryParams(queryMap, abTestType)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                Stays stays;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stays = baseDto.data.getStays();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stays;
            });
    }

    @Override
    public Observable<Stays> getLocalPlusList(Context context, Map<String, Object> queryMap)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales/local-plus" //
            : "NjYkNjckMjgkMjQkMzIkNzYkNjIkMjEkNTQkODAkNjYkMTQkNTMkODgkODAkNzQk$MDQzOThGREU2NzXZGMjFGNJjhFWRTg5AOEYI1MTIxRUZBNTdFQUU4FNESRCRDkyRkRNCVQTNCNTzZXlBQZ0RRKDNEYGxRjZBN0VFMQ==$";

        return mDailyMobileService.getStayList(getBaseUrl(context) + Crypto.getUrlDecoderEx(API) + toStringQueryParams(queryMap, null)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                Stays stays;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stays = baseDto.data.getStays();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stays;
            });
    }

    @Override
    public Observable<StayFilterCount> getListCountByFilter(Context context, DailyCategoryType categoryType, Map<String, Object> queryMap, String abTestType)
    {
        return mDailyMobileService.getStayListCountByFilter(getBaseUrl(context) + getListApiUrl(context, categoryType) + toStringQueryParams(queryMap, abTestType)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                StayFilterCount stayFilterCount;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stayFilterCount = baseDto.data.getFilterCount();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayFilterCount;
            });
    }

    @Override
    public Observable<StayFilterCount> getLocalPlusListCountByFilter(Context context, Map<String, Object> queryMap)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales/local-plus" //
            : "NjYkNjckMjgkMjQkMzIkNzYkNjIkMjEkNTQkODAkNjYkMTQkNTMkODgkODAkNzQk$MDQzOThGREU2NzXZGMjFGNJjhFWRTg5AOEYI1MTIxRUZBNTdFQUU4FNESRCRDkyRkRNCVQTNCNTzZXlBQZ0RRKDNEYGxRjZBN0VFMQ==$";

        return mDailyMobileService.getStayListCountByFilter(getBaseUrl(context) + Crypto.getUrlDecoderEx(API) + toStringQueryParams(queryMap, null)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                StayFilterCount stayFilterCount;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stayFilterCount = baseDto.data.getFilterCount();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayFilterCount;
            });
    }

    private String getListApiUrl(Context context, DailyCategoryType categoryType)
    {
        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            final String API = Constants.UNENCRYPTED_URL ? "api/v4/hotels/sales"//
                : "NjAkMzUkMjAkMzQkOSQ2NiQzMiQxMiQ3MSQzNCQ5MyQzOSQ1MyQ5MiQ2NiQzMiQ=$MUEzNjQ3NMUQE2RkFCM0IxHMDFERDA0MYDNLU3OEWTEF2Mjg4MDAxMYDk3QzdENTQ4MWTdDANKzYE5NDhBQUQyQTI5N0FFLOTcN4OA==$";

            return Crypto.getUrlDecoderEx(API);
        } else
        {
            final String API = Constants.UNENCRYPTED_URL ? "api/v5/hotels/category/{categoryAsPath}/sales"//
                : "NyQ4NyQyMCQyOCQ1JDgkMTA3JDk5JDczJDg1JDUxJDQkMzEkODMkNCQyNCQ=$MjJEHNQETVAXFMDk0NDlDNzgXX4OUZFMUVZU0RjI2NTI2NDIyNkRDODSRCQzM3OTRFMTVDQzczMzJDKQjg5QzYNCOTlKFREI4MDMk2REMwMU0Q3OUU1OSUIzODc4MkYyOERCMENDRDMwQUNF$";

            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("{categoryAsPath}", categoryType.getCodeString(context));

            return Crypto.getUrlDecoderEx(API, urlParams);
        }
    }

    @Override
    public Observable<StayDetailk> getDetail(int stayIndex, StayBookDateTime stayBookDateTime)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/hotel/{stayIndex}"//
            : "MzkkMTckNTgkNDMkOTAkNDgkMTckMzMkOSQ3MyQ3OCQyNSQyOCQyJDc1JDUwJA==$NkVMxNTg1NUzg0MzU4QIL0U1OUJY2DOEYyOTgS0MkVBMjkYwMCFzIyMJ0RBNTMzQzZFYMzlCNzA4BRNThCQDkEwN0I1M0JFQzI3MAW==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getStayDetail(Crypto.getUrlDecoderEx(API, urlParams) //
            , stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"), stayBookDateTime.getNights()) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                StayDetailk stayDetail;

                if (baseDto != null)
                {
                    if (baseDto.data != null)
                    {
                        // 100	성공
                        // 4	데이터가 없을시
                        // 5	판매 마감시
                        switch (baseDto.msgCode)
                        {
                            case 5:
                                stayDetail = baseDto.data.getStayDetail();
                                break;

                            case 100:
                                stayDetail = baseDto.data.getStayDetail();
                                break;

                            default:
                                throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayDetail;
            });
    }

    @Override
    public Observable<List<RoomImageInformation>> getRoomImages(int stayIndex, int roomIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/hotel/{stayIndex}/{roomIndex}/images"//
            : "MTAwJDEwOSQyNCQxMTIkNTIkODUkNzgkMTUkMzYkMTE1JDEwNSQxMjEkMzAkMTIwJDcwJDExMCQ=$QkQ4RTcyQzdGODgU0NERDODhFQQUExRNkVCQjNg4RTc3NzI4MEMyNkZLDNDU5QzNGMzk4MLEE2QzYxQjY1KOEQyODkKwNUUwNUQ5RjhCODJEBECMTcxOEJDEFNSBEVU2QkM4QUREREI2REM4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));
        urlParams.put("{roomIndex}", Integer.toString(roomIndex));

        return mDailyMobileService.getRoomImages(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                List<RoomImageInformation> roomImageInformationList = new ArrayList<>();

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        roomImageInformationList = baseDto.data.getRoomImageList();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return roomImageInformationList;
            });
    }

    @Override
    public Observable<Boolean> getHasCoupon(int stayIndex, StayBookDateTime stayBookDateTime)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{stayIndex}/coupons/exist"//
            : "OTgkMTI0JDExMyQ4NCQ4NCQyMSQxMjAkMTMxJDM0JDU1JDcwJDUyJDEzMiQxMTEkMzkkMTAwJA==$QTRFMkM0NkU2MjhBRjc1NTTIyODUxREQ3RFTIyMZjVENzA0Q0VCNDZk1QK0E2MEFEN0E4N0MW1MEMzNEE3QzJDQ0REXXQzM2NjYwYMzE5MkMVGN0NTBMERDMjFCOOEVQGMzlFMUGVLCXMDM4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getStayHasCoupon(Crypto.getUrlDecoderEx(API, urlParams) //
            , stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"), stayBookDateTime.getNights()) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                boolean hasCoupon = false;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        hasCoupon = baseDto.data.existCoupons;
                    }
                }

                return hasCoupon;
            });
    }

    @Override
    public Observable<WishResult> addWish(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/hotel/add/{stayIndex}"//
            : "NjkkNzkkNzkkOTAkNzckMTE4JDY1JDE3JDEzMyQxMDEkNDgkMTEkOTAkMTYkOTIkNDgk$OTM0RTg5QzkA0RkVBDOMDZFNzRFQkFGNzYxMTcwMjU5NzEzMCzAF3NjgyQzZGOUQxNTFCRAEMyNITQ1M0I3UNjGEE0QjHXI3RUFVFRDc0OEWQxQjlBNDNEQzc4NzI2RATYyRjM0MEE2RNDE4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.addWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<WishResult> removeWish(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/hotel/remove/{stayIndex}"//
            : "MTA3JDgxJDk4JDY2JDQkODMkODMkMjQkNTUkNzUkMzUkMjckMTM4JDEwMSQxMzkkMTM1JA==$QjQzUN0UzODUyNkFBM0FBQkJECMZDlFMTNFMKUJDMTE2MzE0MDYyMTZGMBkQ1QkQ5NDU1NTUc3M0QTyOEVGNkI4NQKCTQ4Qjc1QzUGwMjYyMM0U3REY1QTgX2MkYwMkQyRTkxNENM4NzDMQ3$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.removeWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<ReviewScores> getReviewScores(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/{stayIndex}/statistic"//
            : "NzckNTkkMTgkODgkMTA4JDExOSQyMyQ2JDE4JDI3JDI3JDEzJDE2JDEzMyQxMTckMTAwJA==$QzQ2OTOMxNUNBJNTBBDQNTDg2RUPFGYGMzZDNzMyNzQxMzA5MzdBM0EzNkI3NEVBNTkE1MjUyMjY5RjA4QUE4MYjMzQjgxQV0Y2NBjk2RjI4MEFCOTU4TOXERCRTI2MzYEwOERDRMzkxQkRC$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getReviewScores(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                ReviewScores reviewScores;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        reviewScores = baseDto.data.getReviewScores();
                    } else
                    {
                        reviewScores = new ReviewScores();
                    }
                } else
                {
                    reviewScores = new ReviewScores();
                }

                return reviewScores;
            });
    }

    @Override
    public Observable<TrueReviews> getTrueReviews(int stayIndex, int page, int limit)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/{stayIndex}"//
            : "MjUkNjUkNjAkNjYkMzIkMzYkMjIkNzMkOTEkODkkNjMkNSQzOCQzNSQ1NyQ1NSQ=$ODhEOWTEyMTk0RDU1ODkxODSY0OOEVGQzVRDCRUXEM3MTY4QkNGNDc2UN0DU2NzRGODkGVzM0MwXAMzMM4QTg0MDJEODc0MZTVXCNg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getTrueReviews(Crypto.getUrlDecoderEx(API, urlParams), page, limit, "createdAt", "DESC")//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                TrueReviews trueReviews;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        trueReviews = baseDto.data.getTrueReviews();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return trueReviews;
            });
    }

    @Override
    public Observable<List<TrueVR>> getTrueVR(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{stayIndex}/vr-list"//
            : "NzAkNDIkOCQ2JDg0JDQyJDY5JDU2JDExNSQxMzUkMTEyJDY0JDQxJDk1JDEzNCQxMTIk$NEM4MzEUzSMTg4MzU3NDA4RDQ0NjAxNzVBRkM2RDkN3ZQzJQ4QjAzOThFFREREQ0QAwRTgyRIDU5M0BExNjI4ODI4WMUI2NIjYzNUM3ODQxNEU2NLTFFHOTFWCNkY4RTJEMjA0MKzVFRERQ4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getTrueReviews(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseListDto -> {
                List<TrueVR> trueVR = new ArrayList<>();

                if (baseListDto != null)
                {
                    if (baseListDto.msgCode == 100 && baseListDto.data != null)
                    {
                        for (TrueVRData trueVRData : baseListDto.data)
                        {
                            trueVR.add(trueVRData.getTrueVR());
                        }
                    } else
                    {
                    }
                } else
                {
                }

                return trueVR;
            });
    }

    @Override
    public Observable<List<StayAreaGroup>> getAreaList(Context context, DailyCategoryType categoryType)
    {
        final String API;

        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            API = Constants.UNENCRYPTED_URL ? "api/v3/hotel/region"//
                : "MjMkNjQkMjEkMCQ2MCQ1MiQ0NCQzMiQzMSQyMiQ3MSQ4NiQ2OCQxMyQ0NyQ2OCQ=$PRUM3NTRGQzA5RMEVBMjZFNPQEEN0MTgzYMVzcyQ0VERDUzOOJDQyRTQ1NYzkxNkM0MBNEUG1RUTFOGMDExRDVEMEMExRTEwMDExNw==$";

            return mDailyMobileService.getStayAreaList(Crypto.getUrlDecoderEx(API))//
                .subscribeOn(Schedulers.io()).map(baseDto -> {
                    List<StayAreaGroup> areaGroupList;

                    if (baseDto != null)
                    {
                        if (baseDto.msgCode == 100 && baseDto.data != null)
                        {
                            areaGroupList = baseDto.data.getAreaGroupList();
                        } else
                        {
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return areaGroupList;
                });
        } else
        {
            API = Constants.UNENCRYPTED_URL ? "api/v4/hotels/category/{category}/regions"//
                : "OTkkNTIkMTIyJDEzJDI3JDE0JDg2JDcwJDUyJDM5JDI3JDExOSQxMjUkODkkMTIwJDExMSQ=$QjAyMTQ1MUIzQKLkE0OTAzNUQ3MXjIhENTQwQjY1KQjZFQTIyMDFFRWDk0PQUZGNEUyMUZBODVI5QjcxMDg4ODU1OLEVZGRUU3MThGNTQ4OUJCGPMTQ4REVDMLEUJCMDENCQ0ZCWRDZFQUM4$";

            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("{category}", categoryType.getCodeString(context));

            return mDailyMobileService.getStayCategoryAreaList(Crypto.getUrlDecoderEx(API, urlParams))//
                .subscribeOn(Schedulers.io()).map(baseDto -> {
                    List<StayAreaGroup> areaGroupList;

                    if (baseDto != null)
                    {
                        if (baseDto.msgCode == 100 && baseDto.data != null)
                        {
                            areaGroupList = baseDto.data.getAreaGroupList();
                        } else
                        {
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return areaGroupList;
                });
        }
    }

    @Override
    public Observable<LinkedHashMap<Area, List<StaySubwayAreaGroup>>> getSubwayAreaList(Context context, DailyCategoryType categoryType)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/hotels/subway"//
            : "NTkkMjQkODkkMTgkNzMkMSQzMSQzOCQzMCQ3NCQyMyQ1MiQ0OCQ5JDEwMCQ4MSQ=$NP0RCOUIyZN0JEMjNDRjKVEMYDgwBQzYS3HN0Y2MTMVERkJFQPzJCQFjY0NTlBNzMzMzNUGOUFGQTgHyNCDVhEMjAzQjA1MTNBQg=SS=$";

        String category;

        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            category = null;
        } else
        {
            category = categoryType.getCodeString(context);
        }

        return mDailyMobileService.getStaySubwayAreaList(Crypto.getUrlDecoderEx(API), category)//
            .subscribeOn(Schedulers.io()).map(baseListDto -> {
                LinkedHashMap<Area, List<StaySubwayAreaGroup>> subwayHashMap = new LinkedHashMap<>();

                if (baseListDto != null)
                {
                    if (baseListDto.msgCode == 100 && baseListDto.data != null)
                    {
                        for (SubwayAreasData subwayAreasData : baseListDto.data)
                        {
                            Pair<Area, List<StaySubwayAreaGroup>> pair = subwayAreasData.getAreaGroup();

                            subwayHashMap.put(pair.first, pair.second);
                        }
                    } else
                    {
                        throw new BaseException(baseListDto.msgCode, baseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return subwayHashMap;
            });
    }

    @Override
    public Observable<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>> getRegionList(Context context, DailyCategoryType categoryType)
    {
        return Observable.zip(getAreaList(context, categoryType), getSubwayAreaList(context, categoryType), new BiFunction<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>, Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>()
        {
            @Override
            public Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>> apply(List<StayAreaGroup> areaGroupList, LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaListLinkedHashMap) throws Exception
            {
                return new Pair(areaGroupList, areaListLinkedHashMap);
            }
        });
    }

    private String toStringQueryParams(Map<String, Object> queryMap, String abTestType)
    {
        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append('?');

        for (Map.Entry<String, Object> entry : queryMap.entrySet())
        {
            String entryKey = entry.getKey();
            if (DailyTextUtils.isTextEmpty(entryKey) == true)
            {
                continue;
            }

            Object entryValue = entry.getValue();
            if (entryValue == null)
            {
                continue;
            }

            if (entryValue instanceof List)
            {
                if (stringBuilder.length() > 1)
                {
                    stringBuilder.append('&');
                }

                stringBuilder.append(toStringListQueryParams(entryKey, (List) entryValue, stringBuilder.length() > 1));
            } else
            {
                String convertedEntryValue = entryValue.toString();

                if (DailyTextUtils.isTextEmpty(convertedEntryValue) == true)
                {
                    continue;
                }

                stringBuilder.append(toStringQueryParams(entryKey, convertedEntryValue, stringBuilder.length() > 1));
            }
        }

        if (DailyTextUtils.isTextEmpty(abTestType) == false)
        {
            toStringQueryParams("abtest", abTestType, stringBuilder.length() > 1);
        }

        return stringBuilder.toString();
    }

    private String toStringListQueryParams(String entryKey, List entryValue, boolean addBeginAmpersand)
    {
        if (entryKey == null || entryValue == null)
        {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        int size = entryValue.size();

        for (int i = 0; i < size; i++)
        {
            String convertedEntryValue = entryValue.get(i).toString();

            if (DailyTextUtils.isTextEmpty(convertedEntryValue) == true)
            {
                continue;
            }

            if (i > 0)
            {
                stringBuilder.append('&');
            }

            stringBuilder.append(entryKey);
            stringBuilder.append("=");
            stringBuilder.append(convertedEntryValue);
        }

        if (addBeginAmpersand && stringBuilder.length() > 0)
        {
            stringBuilder.insert(0, '&');
        }

        return stringBuilder.toString();
    }

    private String toStringQueryParams(String entryKey, String entryValue, boolean addAmpersand)
    {
        if (entryKey == null || entryValue == null)
        {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (addAmpersand == true)
        {
            stringBuilder.append('&');
        }

        stringBuilder.append(entryKey);
        stringBuilder.append("=");
        stringBuilder.append(entryValue);

        return stringBuilder.toString();
    }
}
