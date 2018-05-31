package com.daily.dailyhotel.screen.common.images;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.parcel.DetailImageInformationParcel;
import com.daily.dailyhotel.parcel.analytics.ImageListAnalyticsParam;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ImageListPresenter extends BaseExceptionPresenter<ImageListActivity, ImageListInterface> implements ImageListView.OnEventListener
{
    private ImageListAnalyticsInterface mAnalytics;

    private String mTitle;
    private List<DetailImageInformation> mImageList;
    private int mIndex;
    private String mImageUrl;
    private boolean mTouchMoving;

    public interface ImageListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(ImageListAnalyticsParam analyticsParam);

        void onScreen(Activity activity);

        void onEventBackClick(Activity activity);

        void onEventSwipe(Activity activity);

        void onEventCloseClick(Activity activity);
    }

    public ImageListPresenter(@NonNull ImageListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected ImageListInterface createInstanceViewInterface()
    {
        return new ImageListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(ImageListActivity activity)
    {
        setContentView(R.layout.activity_image_list_data);

        mAnalytics = new ImageListAnalyticsImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTitle = intent.getStringExtra(ImageListActivity.INTENT_EXTRA_DATA_TITLE);
        mImageUrl = intent.getStringExtra(ImageListActivity.INTENT_EXTRA_DATA_BASE_IMAGE_URL);

        if (DailyTextUtils.isTextEmpty(mImageUrl) == true)
        {
            mImageUrl = "";
        }

        List<DetailImageInformationParcel> imageList = (List<DetailImageInformationParcel>) intent.getSerializableExtra(ImageListActivity.INTENT_EXTRA_DATA_IMAGE_LIST);

        if (imageList != null)
        {
            mImageList = new ArrayList<>();

            for (DetailImageInformationParcel detailImageInformationParcel : imageList)
            {
                mImageList.add(detailImageInformationParcel.getDetailImageInformation());
            }
        }

        mIndex = intent.getIntExtra(ImageListActivity.INTENT_EXTRA_DATA_INDEX, 0);
        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitle);

        getViewInterface().setImageList(mImageUrl, mImageList, mIndex);
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
    public void onPostFinish()
    {
        super.onPostFinish();

        if (mTouchMoving == true)
        {
            getActivity().overridePendingTransition(R.anim.hold, R.anim.fade_out);
        } else
        {
            getActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
        }
    }

    @Override
    public boolean onBackPressed()
    {
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
    public void onTouchMoving(boolean moving)
    {
        mTouchMoving = moving;
    }
}
