package com.twoheart.dailyhotel.screen.home.category.region;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by android_sam on 2017. 4. 12..
 */

public class HomeCategoryRegionListFragment extends BaseFragment
{
    BaseActivity mBaseActivity;
    private HomeCategoryRegionListLayout mLayout;
    OnFragmentListener mOnFragmentListener;

    private DailyCategoryType mDailyCategoryType;

    public interface OnFragmentListener
    {
        void onActivityCreated(HomeCategoryRegionListFragment homeCategoryRegionListFragment);

        void onRegionClick(Province province);

        void onAroundSearchClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mLayout = new HomeCategoryRegionListLayout(mBaseActivity, mOnEventListener);

        return mLayout.onCreateView(R.layout.fragment_region_list);
    }

    @Override
    public void onResume()
    {
        if (mLayout != null && mLayout.getAdapter() != null)
        {
            recordAnalyticsScreen(mDailyCategoryType);
        }

        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (mOnFragmentListener != null)
        {
            mOnFragmentListener.onActivityCreated(this);
        }
    }

    public void setDailyCategoryType(DailyCategoryType categoryType)
    {
        mDailyCategoryType = categoryType;

        if (mLayout != null)
        {
            mLayout.setDailyCategoryType(mDailyCategoryType);
        }
    }

    public void updateTermsOfLocationView()
    {
        if (mLayout != null)
        {
            mLayout.updateTermsOfLocationView(DailyPreference.getInstance(mBaseActivity).isAgreeTermsOfLocation());
        }
    }

    public void setRegionViewList(BaseActivity baseActivity, List<RegionViewItem> arrayList, boolean isAgreed)
    {
        if (mLayout != null)
        {
            Province previousProvince = searchPreviousProvince(arrayList, mDailyCategoryType);
            mLayout.setSelectedProvince(previousProvince);

            mLayout.setDailyCategoryType(mDailyCategoryType);

            mLayout.setRegionViewList(baseActivity, arrayList, isAgreed);
        }
    }

    public void setOnFragmentListener(OnFragmentListener listener)
    {
        mOnFragmentListener = listener;
    }

    protected void recordAnalyticsScreen(DailyCategoryType categoryType)
    {
        //        switch (categoryType)
        //        {
        //            case DOMESTIC:
        //                AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null);
        //                break;
        //
        //            case GLOBAL:
        //                AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL, null);
        //                break;
        //        }
    }

    private Province searchPreviousProvince(List<RegionViewItem> regionViewItemList, DailyCategoryType categoryType)
    {
        if (regionViewItemList == null || regionViewItemList.size() == 0)
        {
            return null;
        }

        PreferenceRegion preferenceRegion = DailyPreference.getInstance(mBaseActivity).getDailyRegion(categoryType);
        if (preferenceRegion == null || DailyTextUtils.isTextEmpty(preferenceRegion.areaGroupName) == true)
        {
            return regionViewItemList.get(0).getProvince();
        }

        for (RegionViewItem regionViewItem : regionViewItemList)
        {
            Province province = regionViewItem.getProvince();

            // 저장된 대지역과 리스트의 대지역이 같으면 리턴
            if (preferenceRegion.areaGroupName.equalsIgnoreCase(province.name) == true)
            {
                return province;
            }
        }

        // 다 찾아도 맞는게 없으면 리스트의 처음 대지역을 리턴
        return regionViewItemList.get(0).getProvince();
    }

    //////////////////////////////////////////////

    private HomeCategoryRegionListLayout.OnEventListener mOnEventListener = new HomeCategoryRegionListLayout.OnEventListener()
    {
        @Override
        public boolean isLockUiComponent()
        {
            return mBaseActivity.isLockUiComponent();
        }

        @Override
        public void lockUiComponent()
        {
            mBaseActivity.lockUiComponent();
        }

        @Override
        public void releaseUiComponent()
        {
            mBaseActivity.releaseUiComponent();
        }

        @Override
        public void onAroundSearchClick()
        {
            if (mOnFragmentListener != null)
            {
                mOnFragmentListener.onAroundSearchClick();
            }
        }

        @Override
        public void onRegionClick(Province province)
        {
            if (mOnFragmentListener != null)
            {
                mOnFragmentListener.onRegionClick(province);
            }
        }

        @Override
        public void finish()
        {
            mBaseActivity.unLockUI();
            mBaseActivity.finish();
        }
    };
}
