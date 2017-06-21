package com.daily.dailyhotel.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
import com.twoheart.dailyhotel.model.HomeRecentParam;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by android_sam on 2017. 6. 8..
 */

public class RecentlyPlaceUtil
{
    public static final String RECENT_PLACE_DELIMITER = ",";
    public static final int MAX_RECENT_PLACE_COUNT = 30;
    public static final String SERVICE_TYPE_IB_STAY_NAME = "HOTEL";
    public static final String SERVICE_TYPE_OB_STAY_NAME = "OB_STAY";
    public static final String SERVICE_TYPE_GOURMET_NAME = "GOURMET";

    public enum ServiceType
    {
        IB_STAY,
        GOURMET,
        OB_STAY
    }

    public static void migrateRecentlyPlaces(Context context)
    {
        RecentPlaces recentPlaces = new RecentPlaces(context);
        ArrayList<HomeRecentParam> recentlyParamList = recentPlaces.getParamList(RecentPlaces.MAX_RECENT_PLACE_COUNT);

        if (recentlyParamList == null || recentlyParamList.size() == 0)
        {
            return;
        }

        RealmList<RecentlyRealmObject> realmObjectRealmList = new RealmList<>();

        int size = recentlyParamList.size();
        // 오래된 리스트부터 저장하기 위한 역순 계산 - 날짜 저장을 위하여...
        for (int i = size - 1; i >= 0; i--)
        {
            HomeRecentParam param = recentlyParamList.get(i);

            RecentlyRealmObject recentlyRealmObject = convertRecentlyRealmObject(param);
            if (recentlyRealmObject != null)
            {
                realmObjectRealmList.add(recentlyRealmObject);
            }
        }

        setRecentlyRealmListAsync(recentPlaces, realmObjectRealmList);
    }

    private static void setRecentlyRealmListAsync(RecentPlaces recentPlaces, RealmList<RecentlyRealmObject> list)
    {
        if (recentPlaces == null)
        {
            return;
        }

        if (list == null || list.size() == 0)
        {
            recentPlaces.clear();
            recentPlaces.savePreference();
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                realm.copyToRealmOrUpdate(list);
            }
        }, new Realm.Transaction.OnSuccess()
        {
            @Override
            public void onSuccess()
            {
                if (Constants.DEBUG == true)
                {
                    ExLog.w("realm Success");
                }

                recentPlaces.clear();
                recentPlaces.savePreference();

            }
        }, new Realm.Transaction.OnError()
        {
            @Override
            public void onError(Throwable error)
            {
                if (Constants.DEBUG == true)
                {
                    ExLog.w("realm Error , error : " + error.getMessage());
                }

                recentPlaces.clear();
            }
        });
    }

    private static RecentlyRealmObject convertRecentlyRealmObject(HomeRecentParam param)
    {
        if (param == null)
        {
            return null;
        }

        RecentlyRealmObject recentlyRealmObject = null;

        try
        {
            recentlyRealmObject = new RecentlyRealmObject();
            recentlyRealmObject.index = param.index;

            if (SERVICE_TYPE_IB_STAY_NAME.equalsIgnoreCase(param.serviceType) == true)
            {
                recentlyRealmObject.serviceType = ServiceType.IB_STAY.name();
            } else if (SERVICE_TYPE_GOURMET_NAME.equalsIgnoreCase(param.serviceType) == true)
            {
                recentlyRealmObject.serviceType = ServiceType.GOURMET.name();
            } else if (SERVICE_TYPE_OB_STAY_NAME.equalsIgnoreCase(param.serviceType) == true)
            {
                recentlyRealmObject.serviceType = ServiceType.OB_STAY.name();
            } else
            {
                // 지정 되지 않은 타입
                return null;
            }

            // 기존 단말 저장 시간은 그냥 순서이므로 무시하고 다시 시간을 설정 함
            Calendar calendar = DailyCalendar.getInstance();
            recentlyRealmObject.savingTime = calendar.getTimeInMillis();
        } catch (Exception e)
        {
            if (Constants.DEBUG == true)
            {
                ExLog.w(e.getMessage());
            }
        }

        return recentlyRealmObject;
    }

    public static JSONArray getRecentlyJsonArray(RealmResults<RecentlyRealmObject> list, int maxSize)
    {
        JSONArray jsonArray = new JSONArray();

        if (list == null || list.size() == 0 || maxSize == 0)
        {
            // dummy Data 생성
            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put("serviceType", "HOTEL");
                jsonObject.put("idx", 0);

                jsonArray.put(jsonObject);
            } catch (JSONException e)
            {
                ExLog.d(e.getMessage());
            }

            return jsonArray;
        }

        if (maxSize > list.size())
        {
            maxSize = list.size();
        }

        for (int i = 0; i < maxSize; i++)
        {
            RecentlyRealmObject realmObject = list.get(i);

            JSONObject jsonObject = new JSONObject();

            try
            {
                String typeString = null;

                ServiceType serviceType = ServiceType.valueOf(realmObject.serviceType);
                if (ServiceType.IB_STAY == serviceType)
                {
                    typeString = "HOTEL";
                } else if (ServiceType.GOURMET == serviceType)
                {
                    typeString = "GOURMET";
                } else
                {
                    continue;
                }

                jsonObject.put("serviceType", typeString);
                jsonObject.put("idx", realmObject.index);

                jsonArray.put(jsonObject);
            } catch (JSONException e)
            {
                ExLog.d(e.getMessage());
            }
        }

        return jsonArray;
    }

    @Nullable
    public static RealmResults<RecentlyRealmObject> getRecentlyTypeList(ServiceType... serviceTypes)
    {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery query = realm.where(RecentlyRealmObject.class);

        if (serviceTypes != null)
        {
            if (serviceTypes.length > 1)
            {
                query.beginGroup();

                for (int i = 0; i < serviceTypes.length; i++)
                {
                    if (i > 0)
                    {
                        query.or();
                    }

                    query.equalTo("serviceType", serviceTypes[i].name());
                }

                query.endGroup();
            } else
            {
                query.equalTo("serviceType", serviceTypes[0].name());
            }
        }

        RealmResults<RecentlyRealmObject> realmResults = query.findAllSorted("savingTime", Sort.DESCENDING);
        return realmResults;
    }

    public static String getTargetIndices(ServiceType serviceType, int maxSize)
    {
        RealmResults<RecentlyRealmObject> recentlyList = RecentlyPlaceUtil.getRecentlyTypeList(serviceType);

        if (recentlyList == null || recentlyList.size() == 0 || maxSize <= 0)
        {
            return "";
        }

        if (maxSize > recentlyList.size())
        {
            maxSize = recentlyList.size();
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < maxSize; i++)
        {
            RecentlyRealmObject realmObject = recentlyList.get(i);

            if (i != 0)
            {
                builder.append(RECENT_PLACE_DELIMITER);
            }

            builder.append(realmObject.index);
        }

        return builder.toString();
    }

    public static ArrayList<Integer> getRecentlyIndexList(ServiceType... serviceTypes)
    {
        RealmResults<RecentlyRealmObject> recentlyList = RecentlyPlaceUtil.getRecentlyTypeList(serviceTypes);

        if (recentlyList == null || recentlyList.size() == 0)
        {
            return null;
        }

        ArrayList<Integer> indexList = new ArrayList<>();
        for (RecentlyRealmObject realmObject : recentlyList)
        {
            indexList.add(realmObject.index);
        }

        return indexList;
    }

    public static void addRecentlyItemAsync(final ServiceType serviceType, int index, String name //
        , String englishName, String imageUrl, boolean isUpdateDate)
    {
        if (serviceType == null || index <= 0)
        {
            return;
        }

        RecentlyRealmObject realmObject = new RecentlyRealmObject();
        realmObject.index = index;
        realmObject.serviceType = serviceType.name();

        if (DailyTextUtils.isTextEmpty(name) == false)
        {
            realmObject.name = name;
        }

        if (DailyTextUtils.isTextEmpty(englishName) == false)
        {
            realmObject.englishName = englishName;
        }

        if (DailyTextUtils.isTextEmpty(imageUrl) == false)
        {
            realmObject.imageUrl = imageUrl;
        }

        if (isUpdateDate == true)
        {
            Calendar calendar = DailyCalendar.getInstance();
            realmObject.savingTime = calendar.getTimeInMillis();
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                if (isUpdateDate == false)
                {
                    RecentlyRealmObject firstResultObject = realm.where(RecentlyRealmObject.class) //
                        .beginGroup().equalTo("serviceType", serviceType.name()).equalTo("index", index).endGroup() //
                        .findFirst();

                    if (firstResultObject != null)
                    {
                        realmObject.savingTime = firstResultObject.savingTime;
                    }
                }

                realm.copyToRealmOrUpdate(realmObject);
            }
        }, new Realm.Transaction.OnSuccess()
        {
            @Override
            public void onSuccess()
            {
                maintainMaxRecentlyItem(serviceType);
            }
        });
    }

    public static void deleteRecentlyItemAsync(ServiceType serviceType, int index)
    {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                RealmResults<RecentlyRealmObject> resultList = realm.where(RecentlyRealmObject.class) //
                    .beginGroup().equalTo("serviceType", serviceType.name()).equalTo("index", index).endGroup() //
                    .findAll();
                resultList.deleteAllFromRealm();
            }
        });
    }

    private static void maintainMaxRecentlyItem(ServiceType serviceType)
    {
        RealmResults<RecentlyRealmObject> realmResults = RecentlyPlaceUtil.getRecentlyTypeList(serviceType);
        if (realmResults == null)
        {
            return;
        }

        int size = realmResults.size();
        if (size <= MAX_RECENT_PLACE_COUNT)
        {
            return;
        }

        long deleteStartDate = realmResults.get(MAX_RECENT_PLACE_COUNT).savingTime;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                RealmResults<RecentlyRealmObject> deleteList = realm.where(RecentlyRealmObject.class) //
                    .beginGroup().equalTo("serviceType", serviceType.name()) //
                    .lessThanOrEqualTo("savingTime", deleteStartDate).endGroup().findAllSorted("savingTime", Sort.DESCENDING);
                deleteList.deleteAllFromRealm();
            }
        });
    }

    public static List<HomePlace> mergeHomePlaceList(Context context, List<HomePlace> homePlacesList, StayOutbounds stayOutbounds)
    {
        List<StayOutbound> stayOutboundList = stayOutbounds.getStayOutbound();
        if (stayOutboundList == null || stayOutboundList.size() == 0)
        {
            return homePlacesList;
        }

        ArrayList<HomePlace> resultList = new ArrayList<HomePlace>();
        if (homePlacesList != null)
        {
            resultList.addAll(homePlacesList);
        }

        for (StayOutbound stayOutbound : stayOutboundList)
        {
            resultList.add(convertHomePlace(context, stayOutbound));
        }

        // sort list
        RecentlyPlaceUtil.sortHomePlaceList(resultList, (RecentlyPlaceUtil.ServiceType[]) null);

        return resultList;
    }

    private static HomePlace convertHomePlace(Context context, StayOutbound stayOutbound)
    {
        if (context == null || stayOutbound == null)
        {
            return null;
        }

        HomePlace homePlace = null;

        try
        {
            homePlace = new HomePlace();
            homePlace.index = stayOutbound.index;
            homePlace.title = stayOutbound.name;
            homePlace.serviceType = RecentlyPlaceUtil.SERVICE_TYPE_OB_STAY_NAME;
            homePlace.regionName = stayOutbound.city;
            homePlace.prices = null;
            homePlace.imgPathMain = null;
            homePlace.details = null;
            homePlace.placeType = null;
            homePlace.isSoldOut = false;

            ImageMap imageMap = stayOutbound.getImageMap();
            String url;

            if (ScreenUtils.getScreenWidth(context) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
            {
                if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
                {
                    url = imageMap.smallUrl;
                } else
                {
                    url = imageMap.bigUrl;
                }
            } else
            {
                if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
                {
                    url = imageMap.smallUrl;
                } else
                {
                    url = imageMap.mediumUrl;
                }
            }

            homePlace.imageUrl = url;
        } catch (Exception e)
        {
            ExLog.d(stayOutbound.index + " , " + stayOutbound.name + " , " + e.toString());
        }

        return homePlace;
    }

    private static void sortHomePlaceList(ArrayList<HomePlace> actualList, RecentlyPlaceUtil.ServiceType... serviceTypes)
    {
        if (actualList == null || actualList.size() == 0)
        {
            return;
        }

        ArrayList<Integer> expectedList = RecentlyPlaceUtil.getRecentlyIndexList(serviceTypes);
        if (expectedList == null || expectedList.size() == 0)
        {
            return;
        }

        Collections.sort(actualList, new Comparator<HomePlace>()
        {
            @Override
            public int compare(HomePlace place1, HomePlace place2)
            {
                Integer position1 = expectedList.indexOf(place1.index);
                Integer position2 = expectedList.indexOf(place2.index);
                return position1.compareTo(position2);
            }
        });
    }
}