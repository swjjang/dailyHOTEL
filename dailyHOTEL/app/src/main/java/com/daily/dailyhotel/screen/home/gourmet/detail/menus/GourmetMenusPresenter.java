package com.daily.dailyhotel.screen.home.gourmet.detail.menus;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.parcel.GourmetMenuParcel;
import com.daily.dailyhotel.parcel.analytics.ImageListAnalyticsParam;
import com.daily.dailyhotel.screen.common.images.ImageListActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailPresenter;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetMenusPresenter extends BaseExceptionPresenter<GourmetMenusActivity, GourmetMenusInterface> implements GourmetMenusView.OnEventListener
{
    GourmetMenusAnalyticsInterface mAnalytics;

    private GourmetBookDateTime mGourmetBookDateTime;
    private List<GourmetMenu> mGourmetMenuList;
    private List<GourmetMenu> mGourmetMenuVisibleList;

    private int mPosition;
    private int mCenterPosition = -1;

    private int mGourmetIndex;
    private String mGourmetName;
    private List<Integer> mOperationTimeList;
    private int mVisitTime;
    private GourmetCart mGourmetCart;
    private boolean mShowOperationTimeView;

    public interface GourmetMenusAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventBackClick(Activity activity);

        void onEventFlicking(Activity activity);

        void onEventImageClick(Activity activity, String label);
    }

    public GourmetMenusPresenter(@NonNull GourmetMenusActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetMenusInterface createInstanceViewInterface()
    {
        return new GourmetMenusView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetMenusActivity activity)
    {
        setContentView(R.layout.activity_gourmet_menus_data);

        setAnalytics(new GourmetMenusAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (GourmetMenusAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        setGourmetBookDateTime(intent.getStringExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME));
        mGourmetIndex = intent.getIntExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_GOURMET_INDEX, 0);
        mGourmetName = intent.getStringExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_GOURMET_NAME);

        ArrayList<GourmetMenuParcel> gourmetMenuParcelList = intent.getParcelableArrayListExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_MENU_LIST);

        if (gourmetMenuParcelList == null || gourmetMenuParcelList.size() == 0)
        {
            return false;
        }

        mGourmetMenuList = new ArrayList<>();
        mGourmetMenuVisibleList = new ArrayList<>();

        for (GourmetMenuParcel gourmetMenuParcel : gourmetMenuParcelList)
        {
            mGourmetMenuList.add(gourmetMenuParcel.getGourmetMenu());
        }

        mPosition = intent.getIntExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_POSITION, 0);
        mOperationTimeList = intent.getIntegerArrayListExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_OPERATION_TIMES);
        mVisitTime = intent.getIntExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_VISIT_TIME, GourmetDetailPresenter.FULL_TIME);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        if (DailyPreference.getInstance(getActivity()).getGourmetProductDetailGuide() == true)
        {
            DailyPreference.getInstance(getActivity()).setGourmetProductDetailGuide(false);
            getViewInterface().setGuideVisible(true);
        }

        setToolbarTitle(mVisitTime);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        mAnalytics.onEventBackClick(getActivity());

        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        // 카트 데이터를 받는다.
        //        addCompositeDisposable(mGourmetCartImpl.getGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetCart>()
        //        {
        //            @Override
        //            public void accept(GourmetCart gourmetCart) throws Exception
        //            {
        //                mGourmetCart = gourmetCart;
        //
        //                notifyOperationTimeChanged();
        //                getViewInterface().setOperationTimes(mOperationTimeList);
        //
        //                onScrolled(mPosition, false);
        //            }
        //        }, new Consumer<Throwable>()
        //        {
        //            @Override
        //            public void accept(Throwable throwable) throws Exception
        //            {
        mGourmetCart = new GourmetCart();

        getViewInterface().setOperationTimes(mOperationTimeList);

        notifyOperationTimeChanged();


        onScrolled(mPosition, false);
        //            }
        //        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onCloseClick()
    {
        onBackClick();
    }

    @Override
    public void onGuideClick()
    {
        if (lock() == true)
        {
            return;
        }

        Observable<Boolean> observable = getViewInterface().hideGuideAnimation();

        if (observable == null)
        {
            unLockAll();
            return;
        }

        addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onReservationClick(int index)
    {
        if (lock() == true)
        {
            return;
        }

        //        Intent intent = new Intent();
        //        intent.putExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_INDEX, index);
        //        setResult(Activity.RESULT_OK, intent);
        //        finish();
    }

    @Override
    public void onScrolled(int position, boolean real)
    {
        if (mCenterPosition == position)
        {
            return;
        }

        mCenterPosition = position;

        getViewInterface().setMenuIndicator(position + 1, mGourmetMenuVisibleList.size());

        if (real == true)
        {
            mAnalytics.onEventFlicking(getActivity());
        }
    }

    @Override
    public void onMoreImageClick(int position)
    {
        if (lock() == true)
        {
            return;
        }

        GourmetMenu gourmetMenu = mGourmetMenuList.get(position);
        List<DetailImageInformation> gourmetMenuImageList = gourmetMenu.getImageList();


        if (gourmetMenuImageList == null || gourmetMenuImageList.size() == 0)
        {
            unLockAll();
            return;
        }

        ImageListAnalyticsParam analyticsParam = new ImageListAnalyticsParam();
        analyticsParam.serviceType = Constants.ServiceType.GOURMET;

        startActivityForResult(ImageListActivity.newInstance(getActivity(), gourmetMenu.name//
            , gourmetMenuImageList, 0, analyticsParam), GourmetMenusActivity.REQUEST_CODE_IMAGE_LIST);

        mAnalytics.onEventImageClick(getActivity(), Integer.toString(gourmetMenu.index));
    }

    @Override
    public void onOperationTimeClick()
    {
        if (lock() == true)
        {
            return;
        }

        if (mShowOperationTimeView == true)
        {
            mShowOperationTimeView = false;

            addCompositeDisposable(getViewInterface().hideOperationTimes().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    setToolbarTitle(mVisitTime);

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        } else
        {
            mShowOperationTimeView = true;

            setToolbarTitle(getString(R.string.label_gourmet_product_detail_view_operation_time_list));

            addCompositeDisposable(getViewInterface().showOperationTimes(mVisitTime).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        }
    }

    @Override
    public void onVisitTimeClick(int time)
    {
        if (lock() == true)
        {
            return;
        }

        mVisitTime = time;

        mPosition = 0;

        notifyOperationTimeChanged();

        mShowOperationTimeView = false;

        addCompositeDisposable(getViewInterface().hideOperationTimes().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                setToolbarTitle(mVisitTime);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onVisitTimeClick(int time, int menuIndex)
    {
        if (lock() == true)
        {
            return;
        }

        mVisitTime = time;

        notifyOperationTimeChanged();

        setToolbarTitle(mVisitTime);

        plusMenu(menuIndex);

        unLockAll();
    }

    @Override
    public void onHideOperationTimesClick()
    {
        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(getViewInterface().hideOperationTimes().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                setToolbarTitle(mVisitTime);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onMenuOderCountPlusClick(int position)
    {
        if (position < 0 || mGourmetCart == null || lock() == true)
        {
            return;
        }

        GourmetMenu gourmetMenu = mGourmetMenuVisibleList.get(position);

        if (gourmetMenu == null)
        {
            unLockAll();
            return;
        }

        // 처음 메뉴 개수가 0인경우
        if (mGourmetCart.getCount() == 0)
        {
            if (mVisitTime > 0)
            {
                mGourmetCart.setGourmetInformation(mGourmetIndex, mGourmetName, mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), mVisitTime);

                plusMenu(gourmetMenu.index);
            } else
            {
                onChangeTimeClick(gourmetMenu.index);
            }
        } else
        {
            try
            {
                // 기존에 메뉴가 있고 처음 넣는 티켓인 경우, 기존 메뉴와 날짜와 시간이 일치하는지 비교한다.
                if (mGourmetCart.equalsDay(mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)) == true//
                    && mGourmetCart.visitTime == mVisitTime)//
                {
                    //                    plusMenu(mViewTime, position);
                } else
                {
                    // 티켓 시간표를 띄운다
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_gourmet_product_detail_add_menu_after_initialization)//
                        , getString(R.string.label_gourmet_product_detail_add_new_menu), getString(R.string.label_gourmet_product_detail_existing_menu), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                // 장바구니 초기화 후 시간 변경 화면
                                mGourmetCart.clear();
                                mGourmetCart.setGourmetInformation(mGourmetIndex, mGourmetName, mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), mVisitTime);

                                plusMenu(position);
                            }
                        }, null);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        unLockAll();
    }

    @Override
    public void onMenuOderCountMinusClick(int position)
    {
        if (position < 0 || mGourmetCart == null || mGourmetCart.getCount() <= 0 || lock() == true)
        {
            return;
        }

        GourmetMenu gourmetMenu = mGourmetMenuVisibleList.get(position);

        if (gourmetMenu == null)
        {
            unLockAll();
            return;
        }

        minusMenu(gourmetMenu.index);

        if (mGourmetCart.getCount() == 0)
        {
            mGourmetCart.clear();
        }

        unLockAll();
    }

    private void setToolbarTitle(String text)
    {
        getViewInterface().setToolbarTitle(text);
    }

    private void setToolbarTitle(int visitTime)
    {
        if (visitTime == GourmetDetailPresenter.FULL_TIME)
        {
            getViewInterface().setToolbarTitle(getString(R.string.label_gourmet_product_detail_operation_time_list));
        } else
        {
            getViewInterface().setToolbarTitle(String.format(Locale.KOREA, "%02d:%02d ", visitTime / 100, visitTime % 100) + getString(R.string.label_menu));
        }
    }

    private void setGourmetBookDateTime(String visitDateTime)
    {
        if (DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        if (mGourmetBookDateTime == null)
        {
            mGourmetBookDateTime = new GourmetBookDateTime();
        }

        try
        {
            mGourmetBookDateTime.setVisitDateTime(visitDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onChangeTimeClick(int menuIndex)
    {
        if (menuIndex < 0)
        {
            return;
        }

        GourmetMenu gourmetMenu = getGourmetMenu(menuIndex);

        if (gourmetMenu == null)
        {
            return;
        }

        List<Integer> operationTimeList = gourmetMenu.getOperationTimeList();

        getViewInterface().showTimePickerDialog(operationTimeList, menuIndex);
    }

    private void plusMenu(int menuIndex)
    {
        if (menuIndex < 0 || mGourmetCart == null || mGourmetMenuVisibleList == null || mGourmetMenuVisibleList.size() == 0)
        {
            return;
        }

        GourmetMenu gourmetMenu = getGourmetMenu(menuIndex);
        mGourmetCart.plus(gourmetMenu);

        getViewInterface().setMenuOrderCount(menuIndex, mGourmetCart.getCount(menuIndex));

    }

    private void minusMenu(int menuIndex)
    {
        if (menuIndex < 0 || mGourmetCart == null || mGourmetMenuVisibleList == null || mGourmetMenuVisibleList.size() == 0)
        {
            return;
        }

        GourmetMenu gourmetMenu = getGourmetMenu(menuIndex);
        mGourmetCart.minus(gourmetMenu.index);

        getViewInterface().setMenuOrderCount(menuIndex, mGourmetCart.getCount(gourmetMenu.index));

    }

    private void setCartInformation(int time)
    {

    }

    private GourmetMenu getGourmetMenu(int menuIndex)
    {
        if (menuIndex > 0)
        {
            for (GourmetMenu gourmetMenu : mGourmetMenuList)
            {
                if (gourmetMenu.index == menuIndex)
                {
                    return gourmetMenu;
                }
            }
        }

        return null;
    }

    private void notifyOperationTimeChanged()
    {
        if (mOperationTimeList == null)
        {
            return;
        }

        getViewInterface().setVisitTime(mVisitTime);

        mGourmetMenuVisibleList.clear();

        for (GourmetMenu gourmetMenu : mGourmetMenuList)
        {
            gourmetMenu.orderCount = 0;
            gourmetMenu.visible = true;
        }

        if (mVisitTime == GourmetDetailPresenter.FULL_TIME)
        {
            mGourmetMenuVisibleList.addAll(mGourmetMenuList);
        } else
        {
            for (GourmetMenu gourmetMenu : mGourmetMenuList)
            {
                gourmetMenu.visible = false;

                for (int operationTime : gourmetMenu.getOperationTimeList())
                {
                    if (operationTime == mVisitTime)
                    {
                        gourmetMenu.visible = true;

                        mGourmetMenuVisibleList.add(gourmetMenu);

                        if (mGourmetCart != null && mGourmetCart.visitTime == mVisitTime)
                        {
                            gourmetMenu.orderCount = mGourmetCart.getCount(gourmetMenu.index);
                        }
                        break;
                    }
                }
            }
        }

        getViewInterface().setGourmetMenus(mGourmetMenuVisibleList, mPosition);
        getViewInterface().setMenuIndicator(mPosition + 1, mGourmetMenuVisibleList.size());
    }
}
