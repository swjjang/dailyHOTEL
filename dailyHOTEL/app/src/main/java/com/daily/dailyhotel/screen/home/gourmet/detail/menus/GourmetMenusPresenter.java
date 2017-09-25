package com.daily.dailyhotel.screen.home.gourmet.detail.menus;


import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.parcel.GourmetMenuParcel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetMenusPresenter extends BaseExceptionPresenter<GourmetMenusActivity, GourmetMenusInterface> implements GourmetMenusView.OnEventListener
{
    GourmetMenusAnalyticsInterface mAnalytics;

    private List<GourmetMenu> mGourmetMenuList;
    private int mIndex;
    private int mCenterPosition = -1;

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

        ArrayList<GourmetMenuParcel> gourmetMenuParcelList = intent.getParcelableArrayListExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_MENU_LIST);

        if (gourmetMenuParcelList == null || gourmetMenuParcelList.size() == 0)
        {
            return false;
        }

        mGourmetMenuList = new ArrayList<>();

        for (GourmetMenuParcel gourmetMenuParcel : gourmetMenuParcelList)
        {
            mGourmetMenuList.add(gourmetMenuParcel.getGourmetMenu());
        }

        mIndex = intent.getIntExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_INDEX, 0);

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

        getViewInterface().setGourmetMenus(mGourmetMenuList, mIndex);

        onScrolled(mIndex, false);
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

        getViewInterface().hideGuideAnimation(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                unLockAll();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
    }

    @Override
    public void onReservationClick(int index)
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_INDEX, index);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScrolled(int position, boolean real)
    {
        if (mCenterPosition == position)
        {
            return;
        }

        mCenterPosition = position;

        getViewInterface().setSubTitle(String.format(Locale.KOREA, "%d / %d", position + 1, mGourmetMenuList.size()));

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

        addCompositeDisposable(Observable.just(gourmetMenuImageList).subscribeOn(Schedulers.newThread()).map(new Function<List<DetailImageInformation>, List<com.twoheart.dailyhotel.network.model.ImageInformation>>()
        {
            @Override
            public List<com.twoheart.dailyhotel.network.model.ImageInformation> apply(@io.reactivex.annotations.NonNull List<DetailImageInformation> detailImageInformations) throws Exception
            {
                List<com.twoheart.dailyhotel.network.model.ImageInformation> imageInformationList = new ArrayList<>();

                for (DetailImageInformation gourmetMenuImage : gourmetMenuImageList)
                {
                    com.twoheart.dailyhotel.network.model.ImageInformation imageInformation = new com.twoheart.dailyhotel.network.model.ImageInformation();
                    imageInformation.description = gourmetMenuImage.caption;
                    imageInformation.setImageUrl(gourmetMenuImage.url);

                    imageInformationList.add(imageInformation);
                }

                return imageInformationList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<com.twoheart.dailyhotel.network.model.ImageInformation>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<com.twoheart.dailyhotel.network.model.ImageInformation> imageInformations) throws Exception
            {
                Intent intent = ImageDetailListActivity.newInstance(getActivity(), Constants.PlaceType.FNB//
                    , gourmetMenu.name, imageInformations, 0);
                startActivityForResult(intent, GourmetMenusActivity.REQUEST_CODE_IMAGE_LIST);

                mAnalytics.onEventImageClick(getActivity(), Integer.toString(gourmetMenu.index));
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }
}
