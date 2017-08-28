package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.pm.PackageManager;

import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppResearch
{
    private static final String yanolja = "com.cultsotry.yanolja.nativeapp";
    private static final String goodchoice = "kr.goodchoice.abouthere";
    private static final String interpark = "com.interpark.app.stay";
    private static final String hoteltime = "com.withweb.hoteltime";
    private static final String hotelscom = "com.hcom.android";
    private static final String expedia = "com.expedia.bookings";
    private static final String bookingcom = "com.booking";
    private static final String interparktour = "com.interpark.tour.mobile.main";
    private static final String timon = "com.tmon";
    private static final String coupang = "com.coupang.mobile";
    private static final String wemakeprice = "com.wemakeprice";
    private static final String hotelscombined = "com.hotelscombined";
    private static final String hotelnow = "com.hotelnow";
    private static final String facebook = "com.facebook.katana";
    private static final String instagram = "com.instagram.android";
    private static final String youtube = "com.google.android.youtube";

    private static final String[] WATCH_LIST = {yanolja, goodchoice, interpark, hoteltime, hotelscom//
        , expedia, bookingcom, interparktour, timon, coupang, wemakeprice, hotelscombined//
        , hotelnow, facebook, instagram, youtube};

    private Context mContext;
    private boolean mIsMyProcessInTheForeground;

    public AppResearch(Context context)
    {
        mContext = context;

        mIsMyProcessInTheForeground = true;
    }

    public void onResume(String placeType, int placeIndex)
    {
        if (VersionUtils.isOverAPI24() == true || mContext == null || mIsMyProcessInTheForeground == true)
        {
            return;
        }

        mIsMyProcessInTheForeground = true;

        Observable<List<String>> observable = new Observable<List<String>>()
        {
            @Override
            protected void subscribeActual(Observer<? super List<String>> observer)
            {
                List<AndroidAppProcess> androidAppProcessList = AndroidProcesses.getRunningAppProcesses();
                List<String> findList = new ArrayList<>();

                PackageManager packageManager = mContext.getPackageManager();
                int size = androidAppProcessList.size();
                for (AndroidAppProcess androidAppProcess : androidAppProcessList)
                {
                    for (String packageName : WATCH_LIST)
                    {
                        if (androidAppProcess.getPackageName().contains(packageName) == true)
                        {
                            try
                            {
                                findList.add(0, androidAppProcess.getPackageInfo(mContext, 0).applicationInfo.loadLabel(packageManager).toString());
                            } catch (Exception e)
                            {
                                ExLog.e(e.toString());
                            }
                        }
                    }
                }

                observer.onNext(findList);
                observer.onComplete();
            }
        };

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<String>>()
        {
            @Override
            public void accept(@NonNull List<String> packageList) throws Exception
            {
                if (packageList == null || packageList.size() == 0)
                {
                } else
                {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String packageName : packageList)
                    {
                        stringBuilder.append(packageName);
                        stringBuilder.append(',');
                    }

                    stringBuilder.setLength(stringBuilder.length() - 1);

                    if (Constants.DEBUG == true)
                    {
                        DailyToast.showToast(mContext, stringBuilder.toString(), DailyToast.LENGTH_LONG);
                    }

                    if (placeIndex > 0)
                    {
                        AnalyticsManager.getInstance(mContext).recordEvent("App_Switch_To_Foreground", placeType + '_' + placeIndex, stringBuilder.toString(), null);
                    } else
                    {
                        AnalyticsManager.getInstance(mContext).recordEvent("App_Switch_To_Foreground", placeType, stringBuilder.toString(), null);
                    }
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

    public void onPause(String placeType, int placeIndex)
    {
        if (VersionUtils.isOverAPI24() == true || mContext == null)
        {
            return;
        }

        Observable<List<String>> observable = new Observable<List<String>>()
        {
            @Override
            protected void subscribeActual(Observer<? super List<String>> observer)
            {
                List<AndroidAppProcess> androidAppProcessList = AndroidProcesses.getRunningAppProcesses();
                List<String> findList = new ArrayList<>();

                PackageManager packageManager = mContext.getPackageManager();
                int size = androidAppProcessList.size();
                for (AndroidAppProcess androidAppProcess : androidAppProcessList)
                {
                    for (String packageName : WATCH_LIST)
                    {
                        if (androidAppProcess.getPackageName().contains(packageName) == true)
                        {
                            try
                            {
                                if (androidAppProcess.foreground == true)
                                {
                                    findList.add(0, androidAppProcess.getPackageInfo(mContext, 0).applicationInfo.loadLabel(packageManager).toString());
                                }
                            } catch (Exception e)
                            {
                                ExLog.e(e.toString());
                            }
                        }
                    }
                }

                observer.onNext(findList);
                observer.onComplete();
            }
        };

        observable.delaySubscription(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<String>>()
        {
            @Override
            public void accept(@NonNull List<String> packageList) throws Exception
            {
                if (AndroidProcesses.isMyProcessInTheForeground() == true)
                {
                    mIsMyProcessInTheForeground = true;
                    return;
                }

                mIsMyProcessInTheForeground = false;

                if (packageList == null || packageList.size() == 0)
                {
                } else
                {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String packageName : packageList)
                    {
                        stringBuilder.append(packageName);
                        stringBuilder.append(',');
                    }

                    stringBuilder.setLength(stringBuilder.length() - 1);

                    if (Constants.DEBUG == true)
                    {
                        DailyToast.showToast(mContext, stringBuilder.toString(), DailyToast.LENGTH_LONG);
                    }

                    if (placeIndex > 0)
                    {
                        AnalyticsManager.getInstance(mContext).recordEvent("App_Switch_To_Background", placeType + '_' + placeIndex, stringBuilder.toString(), null);
                    } else
                    {
                        AnalyticsManager.getInstance(mContext).recordEvent("App_Switch_To_Background", placeType, stringBuilder.toString(), null);
                    }
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
}
