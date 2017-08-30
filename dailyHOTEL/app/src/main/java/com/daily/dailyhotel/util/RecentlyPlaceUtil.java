package com.daily.dailyhotel.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.Nullable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.CarouselListItem;
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

    public static ArrayList<CarouselListItem> mergeCarouselListItemList(Context context, ArrayList<HomePlace> homePlacesList, StayOutbounds stayOutbounds)
    {

        ArrayList<CarouselListItem> carouselListItemList = new ArrayList<>();
        if (homePlacesList != null)
        {
            for (HomePlace homePlace : homePlacesList)
            {
                CarouselListItem item = new CarouselListItem(CarouselListItem.TYPE_HOME_PLACE, homePlace);
                carouselListItemList.add(item);
            }
        }

        List<StayOutbound> stayOutboundList = stayOutbounds.getStayOutbound();
        if (stayOutboundList != null)
        {
            for (StayOutbound stayOutbound : stayOutboundList)
            {
                CarouselListItem item = new CarouselListItem(CarouselListItem.TYPE_OB_STAY, stayOutbound);
                carouselListItemList.add(item);
            }
        }

        // sort list
        RecentlyPlaceUtil.sortCarouselListItemList(context, carouselListItemList, (Constants.ServiceType[]) null);

        return carouselListItemList;
    }

    public static ArrayList<CarouselListItem> mergeCarouselListItemList(Context context //
        , List<Stay> stayList, List<Gourmet> gourmetList, StayOutbounds stayOutbounds)
    {
        if (context == null)
        {
            return new ArrayList<>();
        }

        ArrayList<CarouselListItem> carouselListItemList = new ArrayList<>();

        if (stayList != null && stayList.size() > 0)
        {
            for (Stay stay : stayList)
            {
                CarouselListItem carouselListItem = new CarouselListItem(CarouselListItem.TYPE_IN_STAY, stay);
                carouselListItemList.add(carouselListItem);
            }
        }

        if (gourmetList != null && gourmetList.size() > 0)
        {
            for (Gourmet gourmet : gourmetList)
            {
                CarouselListItem carouselListItem = new CarouselListItem(CarouselListItem.TYPE_GOURMET, gourmet);
                carouselListItemList.add(carouselListItem);
            }
        }

        List<StayOutbound> stayOutboundList = stayOutbounds.getStayOutbound();
        if (stayOutboundList != null && stayOutboundList.size() > 0)
        {
            for (StayOutbound stayOutbound : stayOutboundList)
            {
                CarouselListItem carouselListItem = new CarouselListItem(CarouselListItem.TYPE_OB_STAY, stayOutbound);
                carouselListItemList.add(carouselListItem);
            }
        }

        sortCarouselListItemList(context, carouselListItemList, (Constants.ServiceType[]) null);

        return carouselListItemList;
    }

    public static void sortCarouselListItemList(Context context, ArrayList<CarouselListItem> actualList, Constants.ServiceType... serviceTypes)
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

        Collections.reverse(recentlyTypeList);

        if (recentlyTypeList == null || recentlyTypeList.size() == 0)
        {
            return;
        }

        ArrayList<Integer> expectedList = new ArrayList<>();
        for (RecentlyPlace recentlyPlace : recentlyTypeList)
        {
            expectedList.add(recentlyPlace.index);
        }

        Collections.sort(actualList, new Comparator<CarouselListItem>()
        {
            @Override
            public int compare(CarouselListItem item1, CarouselListItem item2)
            {
                Integer position1 = expectedList.indexOf(getCarouselListItemIndex(item1));
                Integer position2 = expectedList.indexOf(getCarouselListItemIndex(item2));
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

    public static int getCarouselListItemIndex(CarouselListItem carouselListItem)
    {
        int index = -1;
        switch (carouselListItem.mType)
        {
            case CarouselListItem.TYPE_HOME_PLACE:
            {
                HomePlace item = carouselListItem.getItem();
                index = item.index;
                break;
            }

            case CarouselListItem.TYPE_IN_STAY:
            {
                Stay item = carouselListItem.getItem();
                index = item.index;
                break;
            }

            case CarouselListItem.TYPE_OB_STAY:
            {
                StayOutbound item = carouselListItem.getItem();
                index = item.index;
                break;
            }

            case CarouselListItem.TYPE_GOURMET:
            {
                Gourmet item = carouselListItem.getItem();
                index = item.index;
                break;
            }
        }

        return index;
    }

    public static Constants.ServiceType getServiceType(CarouselListItem carouselListItem)
    {
        Constants.ServiceType serviceType = null;
        switch (carouselListItem.mType)
        {
            case CarouselListItem.TYPE_HOME_PLACE:
            {
                HomePlace place = carouselListItem.getItem();
                serviceType = RecentlyPlaceUtil.getServiceType(place.serviceType);
                break;
            }

            case CarouselListItem.TYPE_IN_STAY:
            {
                serviceType = Constants.ServiceType.HOTEL;
                break;
            }

            case CarouselListItem.TYPE_OB_STAY:
            {
                serviceType = Constants.ServiceType.OB_STAY;
                break;
            }

            case CarouselListItem.TYPE_GOURMET:
            {
                serviceType = Constants.ServiceType.GOURMET;
                break;
            }
        }

        return serviceType;
    }
}
