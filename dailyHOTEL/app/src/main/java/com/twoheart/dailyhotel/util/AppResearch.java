package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.pm.PackageManager;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppResearch
{
    boolean mIsMyProcessInTheForeground;
    String[] WATCH_LIST;

    public AppResearch()
    {
        mIsMyProcessInTheForeground = true;
    }

    String[] getWatchList(Context context)
    {
        if (WATCH_LIST != null)
        {
            return WATCH_LIST;
        }

        try
        {
            JSONArray jsonArray = new JSONArray(DailyRemoteConfigPreference.getInstance(context).getKeyRemoteConfigAppResearch());

            if (jsonArray != null && jsonArray.length() > 0)
            {
                int length = jsonArray.length();

                WATCH_LIST = new String[length];

                for (int i = 0; i < length; i++)
                {
                    WATCH_LIST[i] = jsonArray.getString(i);
                }
            }
        } catch (Exception e)
        {

        }

        return WATCH_LIST;
    }

    public void onResume(Context context, String placeType, int placeIndex)
    {
        if (VersionUtils.isOverAPI24() == true || context == null || mIsMyProcessInTheForeground == true)
        {
            return;
        }

        mIsMyProcessInTheForeground = true;

        Observable<List<Map<String, String>>> observable = new Observable<List<Map<String, String>>>()
        {
            @Override
            protected void subscribeActual(Observer<? super List<Map<String, String>>> observer)
            {
                List<AndroidAppProcess> androidAppProcessList = AndroidProcesses.getRunningAppProcesses();
                List<Map<String, String>> mapList = new ArrayList<>();

                Map<String, String> packageMap = new HashMap<>();
                PackageManager packageManager = context.getPackageManager();

                for (AndroidAppProcess androidAppProcess : androidAppProcessList)
                {
                    for (String packageName : getWatchList(context))
                    {
                        if (androidAppProcess.getPackageName().contains(packageName) == true)
                        {
                            try
                            {
                                packageMap.put(androidAppProcess.getPackageName(), androidAppProcess.getPackageInfo(context, 0).applicationInfo.loadLabel(packageManager).toString());
                            } catch (Exception e)
                            {
                                ExLog.e(e.toString());
                            }
                        }
                    }
                }

                mapList.add(packageMap);
                mapList.add(recentlyApp(context, androidAppProcessList));

                observer.onNext(mapList);
                observer.onComplete();
            }
        };

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Map<String, String>>>()
        {
            @Override
            public void accept(@NonNull List<Map<String, String>> mapList) throws Exception
            {
                if (mapList == null || mapList.size() == 0)
                {
                } else
                {
                    Map<String, String> knownPackageMap = mapList.get(0);
                    Map<String, String> unknownPackageMap = mapList.get(1);

                    // known
                    recordEvent(context, new ArrayList<>(knownPackageMap.values()), "App_Switch_To_Foreground", placeType, placeIndex);

                    // unknown
                    recordEvent(context, new ArrayList<>(unknownPackageMap.values()), "App_Switch", "Entire", placeIndex);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                ExLog.e(throwable.toString());
            }
        });
    }

    public void onPause(Context context, String placeType, int placeIndex)
    {
        if (VersionUtils.isOverAPI24() == true || context == null)
        {
            return;
        }

        Observable<List<Map<String, String>>> observable = new Observable<List<Map<String, String>>>()
        {
            @Override
            protected void subscribeActual(Observer<? super List<Map<String, String>>> observer)
            {
                List<AndroidAppProcess> androidAppProcessList = AndroidProcesses.getRunningAppProcesses();
                List<Map<String, String>> mapList = new ArrayList<>();

                Map<String, String> packageMap = new HashMap<>();

                PackageManager packageManager = context.getPackageManager();

                for (AndroidAppProcess androidAppProcess : androidAppProcessList)
                {
                    for (String packageName : getWatchList(context))
                    {
                        if (androidAppProcess.getPackageName().contains(packageName) == true)
                        {
                            try
                            {
                                if (androidAppProcess.foreground == true)
                                {
                                    packageMap.put(androidAppProcess.getPackageName(), androidAppProcess.getPackageInfo(context, 0).applicationInfo.loadLabel(packageManager).toString());
                                }
                            } catch (Exception e)
                            {
                                ExLog.e(e.toString());
                            }
                        }
                    }
                }

                mapList.add(packageMap);
                mapList.add(recentlyApp(context, androidAppProcessList));

                observer.onNext(mapList);
                observer.onComplete();
            }
        };

        observable.delaySubscription(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Map<String, String>>>()
        {
            @Override
            public void accept(@NonNull List<Map<String, String>> mapList) throws Exception
            {
                if (AndroidProcesses.isMyProcessInTheForeground() == true)
                {
                    mIsMyProcessInTheForeground = true;
                    return;
                }

                mIsMyProcessInTheForeground = false;

                if (mapList == null || mapList.size() == 0)
                {
                } else
                {
                    Map<String, String> knownPackageMap = mapList.get(0);
                    Map<String, String> unknownPackageMap = mapList.get(1);


                    // known
                    recordEvent(context, new ArrayList<>(knownPackageMap.values()), "App_Switch_To_Background", placeType, placeIndex);

                    // unknown
                    recordEvent(context, new ArrayList<>(unknownPackageMap.values()), "App_Switch", "Entire", placeIndex);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                ExLog.e(throwable.toString());
            }
        });
    }

    Map<String, String> recentlyApp(Context context, List<AndroidAppProcess> androidAppProcessList)
    {
        Map<String, String> unknownPackageMap = new HashMap<>();

        if (androidAppProcessList == null || androidAppProcessList.size() == 0)
        {
            return unknownPackageMap;
        }

        PackageManager packageManager = context.getPackageManager();

        int size = androidAppProcessList.size();
        int count = 0;

        for (int i = size - 1; i > 0; i--)
        {
            if (androidAppProcessList.get(i).foreground == false)
            {
                continue;
            }

            if (unknownPackageMap.size() == 5)
            {
                break;
            }

            try
            {
                unknownPackageMap.put(androidAppProcessList.get(i).getPackageName(), androidAppProcessList.get(i).getPackageInfo(context, 0).applicationInfo.loadLabel(packageManager).toString());
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        return unknownPackageMap;
    }

    void recordEvent(Context context, List<String> packageList, String categoryName, String placeType, int placeIndex)
    {
        if (packageList == null || packageList.size() == 0 || DailyTextUtils.isTextEmpty(categoryName) == true)
        {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String packageName : packageList)
        {
            stringBuilder.append(packageName);
            stringBuilder.append(',');
        }

        stringBuilder.setLength(stringBuilder.length() - 1);

        if (Constants.DEBUG == true)
        {
            DailyToast.showToast(context, stringBuilder.toString(), DailyToast.LENGTH_LONG);
        }

        if (placeIndex > 0)
        {
            AnalyticsManager.getInstance(context).recordEvent(categoryName, placeType + '_' + placeIndex, stringBuilder.toString(), null);
        } else
        {
            AnalyticsManager.getInstance(context).recordEvent(categoryName, placeType, stringBuilder.toString(), null);
        }
    }
}
