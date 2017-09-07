package com.daily.dailyhotel.screen.common.images;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.BaseDetailImage;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ImageListPresenter extends BaseExceptionPresenter<ImageListActivity, ImageListInterface> implements ImageListView.OnEventListener
{
    private ImageListAnalyticsInterface mAnalytics;

    private String mTitle;
    private List<BaseDetailImage> mImageList;
    private int mIndex;
    private boolean mTouchMoving;

    public interface ImageListAnalyticsInterface extends BaseAnalyticsInterface
    {
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

        setAnalytics(new ImageListAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (ImageListAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTitle = intent.getStringExtra(ImageListActivity.INTENT_EXTRA_DATA_TITLE);

        List<Map<String, String>> imageMapList = (List<Map<String, String>>) intent.getSerializableExtra(ImageListActivity.INTENT_EXTRA_DATA_IMAGE_LIST);

        if (imageMapList != null)
        {
            mImageList = new ArrayList<>();

            if (equalsCallingActivity(GourmetDetailActivity.class) == true)
            {
                for (Map<String, String> imageMap : imageMapList)
                {
                    mImageList.add(new DetailImageInformation(imageMap));
                }
            } else if (equalsCallingActivity(StayOutboundDetailActivity.class) == true)
            {
                for (Map<String, String> imageMap : imageMapList)
                {
                    mImageList.add(new StayOutboundDetailImage(imageMap));
                }
            }
        }

        mIndex = intent.getIntExtra(ImageListActivity.INTENT_EXTRA_DATA_INDEX, 0);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitle);

        getViewInterface().setImageList(mImageList, mIndex);
    }

    @Override
    public void onStart()
    {
        super.onStart();

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
    public void onFinish()
    {
        super.onFinish();

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
