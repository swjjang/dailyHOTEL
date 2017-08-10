package com.daily.dailyhotel.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.Nullable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.repository.local.DailyDb;
import com.daily.dailyhotel.repository.local.DailyDbHelper;
import com.daily.dailyhotel.repository.local.model.RecentlyList;
import com.daily.dailyhotel.repository.local.model.RecentlyPlace;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.HomeRecentParam;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.model.HomeDetails;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.Prices;
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

    public static void migrateRecentlyPlaces(Context context)
    {
        RecentPlaces recentPlaces = new RecentPlaces(context);
        ArrayList<HomeRecentParam> recentlyParamList = recentPlaces.getParamList(RecentPlaces.MAX_RECENT_PLACE_COUNT);

        changeServiceType();

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

    private static void changeServiceType()
    {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                RealmResults<RecentlyRealmObject> results = realm.where(RecentlyRealmObject.class).equalTo("serviceType", "IB_STAY").findAll();
                if (results == null || results.size() == 0)
                {
                    return;
                }

                RealmList<RecentlyRealmObject> list = new RealmList<RecentlyRealmObject>();

                for (RecentlyRealmObject realmObject : results)
                {
                    realmObject.serviceType = Constants.ServiceType.HOTEL.name();
                    list.add(realmObject);
                }

                realm.copyToRealmOrUpdate(list);
            }
        });
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
                recentlyRealmObject.serviceType = Constants.ServiceType.HOTEL.name();
            } else if (SERVICE_TYPE_GOURMET_NAME.equalsIgnoreCase(param.serviceType) == true)
            {
                recentlyRealmObject.serviceType = Constants.ServiceType.GOURMET.name();
            } else if (SERVICE_TYPE_OB_STAY_NAME.equalsIgnoreCase(param.serviceType) == true)
            {
                recentlyRealmObject.serviceType = Constants.ServiceType.OB_STAY.name();
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

    public static JSONArray getDbRecentlyJsonArray(ArrayList<RecentlyPlace> list, int maxSize)
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

        int size = list.size();

        if (maxSize > size)
        {
            maxSize = size;
        }

        for (int i = 0; i < maxSize; i++)
        {
            JSONObject jsonObject = new JSONObject();

            RecentlyPlace recentlyPlace = list.get(i);

            try
            {
                String serviceTypeString = recentlyPlace.serviceType.name();

                if (Constants.ServiceType.HOTEL.name().equalsIgnoreCase(serviceTypeString) == true //
                    || Constants.ServiceType.GOURMET.name().equalsIgnoreCase(serviceTypeString) == true)
                {
                    int index = recentlyPlace.index;

                    jsonObject.put("serviceType", serviceTypeString);
                    jsonObject.put("idx", index);

                    jsonArray.put(jsonObject);
                }
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        }

        return jsonArray;
    }

    public static ArrayList<RecentlyPlace> getDbRecentlyTypeList(Context context, Constants.ServiceType... serviceTypes)
    {
        if (context == null)
        {
            return null;
        }

        DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

        Cursor cursor = null;

        ArrayList<RecentlyPlace> recentlyList = new ArrayList<>();

        try
        {
            cursor = dailyDb.getRecentlyPlaces(-1, serviceTypes);

            if (cursor == null || cursor.getCount() == 0)
            {
                return null;
            }

            for (int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToPosition(i);

                try
                {
                    RecentlyPlace recentlyPlace = new RecentlyPlace();
                    recentlyPlace.index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));
                    recentlyPlace.name = cursor.getString(cursor.getColumnIndex(RecentlyList.NAME));
                    recentlyPlace.englishName = cursor.getString(cursor.getColumnIndex(RecentlyList.ENGLISH_NAME));
                    recentlyPlace.savingTime = cursor.getLong(cursor.getColumnIndex(RecentlyList.SAVING_TIME));

                    Constants.ServiceType serviceType;

                    try
                    {
                        serviceType = Constants.ServiceType.valueOf(cursor.getString(cursor.getColumnIndex(RecentlyList.SERVICE_TYPE)));
                    } catch (Exception e)
                    {
                        serviceType = null;
                    }

                    recentlyPlace.serviceType = serviceType;
                    recentlyPlace.imageUrl = cursor.getString(cursor.getColumnIndex(RecentlyList.IMAGE_URL));

                    recentlyList.add(recentlyPlace);
                } catch (Exception e)
                {
                    ExLog.w("index : " + i + " , e : " + e.toString());
                }
            }

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                cursor.close();
            } catch (Exception e)
            {
            }
        }

        DailyDbHelper.getInstance().close();

        return recentlyList;
    }

    @Nullable
    public static RealmResults<RecentlyRealmObject> getRealmRecentlyTypeList(Constants.ServiceType... serviceTypes)
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

        realm.close();
        return realmResults;
    }

    public static long getOldestSavingTime(Constants.ServiceType... serviceTypes)
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

        RealmResults<RecentlyRealmObject> realmResults = query.findAllSorted("savingTime", Sort.ASCENDING);

        realm.close();

        if (realmResults == null || realmResults.size() == 0)
        {
            return -1;
        }

        return realmResults.get(0).savingTime;
    }

    public static RecentlyRealmObject getRecentlyPlace(Constants.ServiceType serviceType, int index)
    {
        Realm realm = Realm.getDefaultInstance();

        RealmQuery query = realm.where(RecentlyRealmObject.class);

        query.equalTo("serviceType", serviceType.name());
        query.equalTo("index", index);

        RealmResults<RecentlyRealmObject> realmResults = query.findAllSorted("savingTime", Sort.DESCENDING);

        realm.close();

        if (realmResults != null && realmResults.size() > 0)
        {
            return realmResults.get(0);
        }

        return null;
    }

    public static String getDbTargetIndices(Context context, Constants.ServiceType serviceType, int maxSize)
    {
        if (context == null || serviceType == null || maxSize <= 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

        Cursor cursor = null;

        try
        {
            cursor = dailyDb.getRecentlyPlaces(-1, serviceType);

            if (cursor == null || cursor.getCount() == 0 || maxSize <= 0)
            {
                return "";
            }

            int size = cursor.getCount();
            if (maxSize > size)
            {
                maxSize = size;
            }

            for (int i = 0; i < maxSize; i++)
            {
                cursor.moveToPosition(i);

                int index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));

                if (i != 0)
                {
                    builder.append(RECENT_PLACE_DELIMITER);
                }

                builder.append(index);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                cursor.close();
            } catch (Exception e)
            {
            }
        }

        DailyDbHelper.getInstance().close();

        return builder.toString();
    }

    public static String getRealmTargetIndices(Constants.ServiceType serviceType, int maxSize)
    {
        RealmResults<RecentlyRealmObject> recentlyList = RecentlyPlaceUtil.getRealmRecentlyTypeList(serviceType);

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

    public static ArrayList<Integer> getDbRecentlyIndexList(Context context, Constants.ServiceType... serviceTypes)
    {
        if (context == null || serviceTypes == null || serviceTypes.length == 0)
        {
            return null;
        }

        ArrayList<Integer> indexList = new ArrayList<>();

        DailyDb dailyDb = DailyDbHelper.getInstance().open(context);

        Cursor cursor = null;

        try
        {
            cursor = dailyDb.getRecentlyPlaces(-1, serviceTypes);

            if (cursor == null || cursor.getCount() == 0)
            {
                return null;
            }

            int size = cursor.getCount();
            if (MAX_RECENT_PLACE_COUNT < size)
            {
                size = MAX_RECENT_PLACE_COUNT;
            }

            for (int i = 0; i < size; i++)
            {
                cursor.moveToPosition(i);

                int index = cursor.getInt(cursor.getColumnIndex(RecentlyList.PLACE_INDEX));

                indexList.add(index);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                cursor.close();
            } catch (Exception e)
            {
            }
        }

        DailyDbHelper.getInstance().close();

        return indexList;
    }

    public static ArrayList<Integer> getRealmRecentlyIndexList(Constants.ServiceType... serviceTypes)
    {
        RealmResults<RecentlyRealmObject> recentlyList = RecentlyPlaceUtil.getRealmRecentlyTypeList(serviceTypes);

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

    public static void addRecentlyItem(Context context, final Constants.ServiceType serviceType, int index, String name //
        , String englishName, String imageUrl, boolean isUpdateDate)
    {
        if (serviceType == null || index <= 0 || context == null)
        {
            return;
        }

        DailyDb dailyDb = DailyDbHelper.getInstance().open(context);
        dailyDb.addRecentlyPlace(serviceType, index, name, englishName, imageUrl, isUpdateDate);

        DailyDbHelper.getInstance().close();
    }

    public static void deleteRecentlyItem(Context context, Constants.ServiceType serviceType, int index)
    {
        if (serviceType == null || index <= 0 || context == null)
        {
            return;
        }

        DailyDb dailyDb = DailyDbHelper.getInstance().open(context);
        dailyDb.deleteRecentlyItem(serviceType, index);

        DailyDbHelper.getInstance().close();
    }

    public static ArrayList<HomePlace> mergeHomePlaceList(Context context, List<Stay> stayList, List<Gourmet> gourmetList, StayOutbounds stayOutbounds)
    {
        if (context == null)
        {
            return null;
        }

        ArrayList<HomePlace> homePlaceList = new ArrayList<>();

        if (stayList != null && stayList.size() > 0)
        {
            for (Stay stay : stayList)
            {
                homePlaceList.add(convertHomePlace(context, stay));
            }
        }

        if (gourmetList != null && gourmetList.size() > 0)
        {
            for (Gourmet gourmet : gourmetList)
            {
                homePlaceList.add(convertHomePlace(context, gourmet));
            }
        }

        List<StayOutbound> stayOutboundList = stayOutbounds.getStayOutbound();
        if (stayOutboundList != null && stayOutboundList.size() > 0)
        {
            for (StayOutbound stayOutbound : stayOutboundList)
            {
                homePlaceList.add(convertHomePlace(context, stayOutbound));
            }
        }

        sortHomePlaceList(context, homePlaceList, (Constants.ServiceType[]) null);

        return homePlaceList;
    }

    public static ArrayList<HomePlace> mergeHomePlaceList(Context context, ArrayList<HomePlace> homePlacesList, StayOutbounds stayOutbounds)
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
        RecentlyPlaceUtil.sortHomePlaceList(context, resultList, (Constants.ServiceType[]) null);

        return resultList;
    }

    private static HomePlace convertHomePlace(Context context, Stay stay)
    {
        if (context == null || stay == null)
        {
            return null;
        }

        HomePlace homePlace = null;

        try
        {
            homePlace = new HomePlace();
            homePlace.index = stay.index;
            homePlace.title = stay.name;
            homePlace.serviceType = RecentlyPlaceUtil.SERVICE_TYPE_IB_STAY_NAME;
            homePlace.regionName = stay.districtName;

            Prices prices = new Prices();
            prices.discountPrice = stay.discountPrice;
            prices.normalPrice = stay.price;

            homePlace.prices = prices;
            homePlace.imageUrl = stay.imageUrl;
            homePlace.placeType = Constants.PlaceType.HOTEL;
            homePlace.isSoldOut = stay.isSoldOut;
            homePlace.distance = stay.distance;

            HomeDetails details = new HomeDetails();
            details.category = stay.categoryCode;
            details.stayGrade = stay.getGrade();

            homePlace.details = details;

        } catch (Exception e)
        {
            ExLog.d(stay.index + " , " + stay.name + " , " + e.toString());
        }

        return homePlace;
    }

    private static HomePlace convertHomePlace(Context context, Gourmet gourmet)
    {
        if (context == null || gourmet == null)
        {
            return null;
        }

        HomePlace homePlace = null;

        try
        {
            homePlace = new HomePlace();
            homePlace.index = gourmet.index;
            homePlace.title = gourmet.name;
            homePlace.serviceType = RecentlyPlaceUtil.SERVICE_TYPE_GOURMET_NAME;
            homePlace.regionName = gourmet.districtName;

            Prices prices = new Prices();
            prices.discountPrice = gourmet.discountPrice;
            prices.normalPrice = gourmet.price;

            homePlace.prices = prices;
            homePlace.imageUrl = gourmet.imageUrl;
            homePlace.placeType = Constants.PlaceType.FNB;
            homePlace.isSoldOut = gourmet.isSoldOut;
            homePlace.distance = gourmet.distance;

            HomeDetails details = new HomeDetails();
            details.category = gourmet.category;
            details.grade = gourmet.grade.getName(context);
            details.persons = gourmet.persons;

            homePlace.details = details;
        } catch (Exception e)
        {
            ExLog.w(gourmet.index + " | " + gourmet.name + " :: " + e.getMessage());
        }

        return homePlace;
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
            homePlace.imageUrl = imageMap.smallUrl;
        } catch (Exception e)
        {
            ExLog.d(stayOutbound.index + " , " + stayOutbound.name + " , " + e.toString());
        }

        return homePlace;
    }

    public static void sortHomePlaceList(Context context, ArrayList<HomePlace> actualList, Constants.ServiceType... serviceTypes)
    {
        if (context == null || actualList == null || actualList.size() == 0)
        {
            return;
        }

        ArrayList<RecentlyPlace> recentlyTypeList = RecentlyPlaceUtil.getDbRecentlyTypeList(context, serviceTypes);
        RealmResults<RecentlyRealmObject> results = RecentlyPlaceUtil.getRealmRecentlyTypeList(serviceTypes);
        if (results != null && results.size() > 0)
        {
            if (recentlyTypeList == null)
            {
                recentlyTypeList = new ArrayList<>();
            }

            for (RecentlyRealmObject object : results)
            {
                RecentlyPlace recentlyPlace = new RecentlyPlace();
                recentlyPlace.index = object.index;
                recentlyPlace.name = object.name;
                recentlyPlace.englishName = object.englishName;
                recentlyPlace.serviceType = object.serviceType == null ? null : Constants.ServiceType.valueOf(object.serviceType);
                recentlyPlace.savingTime = object.savingTime;
                recentlyPlace.imageUrl = object.imageUrl;
                recentlyTypeList.add(recentlyPlace);
            }
        }

        Collections.sort(recentlyTypeList, new Comparator<RecentlyPlace>()
        {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(RecentlyPlace o1, RecentlyPlace o2)
            {
                return Long.compare(o1.savingTime, o2.savingTime);
            }
        });

        if (recentlyTypeList == null || recentlyTypeList.size() == 0)
        {
            return;
        }

        ArrayList<Integer> expectedList = new ArrayList<>();
        for (RecentlyPlace recentlyPlace : recentlyTypeList)
        {
            expectedList.add(recentlyPlace.index);
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

    public static Constants.ServiceType getServiceType(String serviceTypeString)
    {
        if (DailyTextUtils.isTextEmpty(serviceTypeString) == true)
        {
            return null;
        }

        Constants.ServiceType serviceType = null;

        if (SERVICE_TYPE_OB_STAY_NAME.equalsIgnoreCase(serviceTypeString) //
            || Constants.ServiceType.OB_STAY.name().equalsIgnoreCase(serviceTypeString))
        {
            serviceType = Constants.ServiceType.OB_STAY;
        } else if (SERVICE_TYPE_IB_STAY_NAME.equalsIgnoreCase(serviceTypeString) //
            || Constants.ServiceType.HOTEL.name().equalsIgnoreCase(serviceTypeString))
        {
            serviceType = Constants.ServiceType.HOTEL;
        } else if (SERVICE_TYPE_GOURMET_NAME.equalsIgnoreCase(serviceTypeString) //
            || Constants.ServiceType.GOURMET.name().equalsIgnoreCase(serviceTypeString))
        {
            serviceType = Constants.ServiceType.GOURMET;
        }

        return serviceType;
    }
}
