package com.daily.dailyhotel.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
import com.twoheart.dailyhotel.model.HomeRecentParam;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

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

    public enum ServiceType
    {
        IB_STAY,
        GOURMET,
        OB_STAY,
        ALL_STAY,
        ALL
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

            if ("HOTEL".equalsIgnoreCase(param.serviceType) == true)
            {
                recentlyRealmObject.serviceType = RecentlyPlaceUtil.ServiceType.IB_STAY.name();
            } else if ("GOURMET".equalsIgnoreCase(param.serviceType) == true)
            {
                recentlyRealmObject.serviceType = RecentlyPlaceUtil.ServiceType.GOURMET.name();
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
        if (list == null || list.size() == 0 || maxSize == 0)
        {
            return null;
        }

        JSONArray jsonArray = new JSONArray();

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

    public static String getPlaceIndexList(ServiceType serviceType, int maxSize)
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
}
