package com.twoheart.dailyhotel.screen.home.category;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 4. 12..
 */

public class HomeCategoryRegionListFragment extends BaseFragment
{
    private BaseActivity mBaseActivity;
    private HomeCategoryRegionListLayout mLayout;
    private OnFragmentListener mOnFragmentListener;

    private DailyCategoryType mDailyCategoryType;
    private Province mSelectedProvince;

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

    public void setSelectedProvince(Province province)
    {
        mSelectedProvince = province;

        if (mLayout != null)
        {
            mLayout.setSelectedProvince(province);
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
        Province selectedProvince = searchLastProvince(arrayList, mDailyCategoryType);
        setSelectedProvince(selectedProvince);

        if (mLayout != null)
        {
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

    public String getResultName()
    {
        return mDailyCategoryType == null ? DailyCategoryType.NONE.name() : mDailyCategoryType.name();
    }

    private Province searchLastProvince(List<RegionViewItem> regionViewItemList, DailyCategoryType categoryType)
    {
        if (regionViewItemList == null || regionViewItemList.size() == 0)
        {
            return null;
        }

        JSONObject saveRegionJsonObject = DailyPreference.getInstance(mBaseActivity).getDailyRegion(categoryType);
        if (saveRegionJsonObject == null)
        {
//            return regionViewItemList.get(0).getProvince();
            return null;
        }

        String oldProvinceName = Util.getDailyProvinceString(saveRegionJsonObject);
        String oldAreaName = Util.getDailyAreaString(saveRegionJsonObject);

        if (Util.isTextEmpty(oldProvinceName) == true)
        {
//            return regionViewItemList.get(0).getProvince();
            return null;
        }

        for (RegionViewItem regionViewItem : regionViewItemList)
        {
            Province province = regionViewItem.getProvince();

            // 저장된 대지역과 리스트의 대지역이 다르면 스킵
            if (oldProvinceName.equalsIgnoreCase(province.name) == false)
            {
                continue;
            }

            // 저장된 대지역과 같으나 저장된 소지역이 없으면 리턴
            if (Util.isTextEmpty(oldAreaName) == true)
            {
                return province;
            }

            ArrayList<Area[]> areasList = regionViewItem.getAreaList();

            // 저장된 대지역과 같으나 리스트의 소지역이 비었으면 대지역 리턴
            if (areasList == null || areasList.size() == 0)
            {
                return province;
            }

            for (Area[] areas : areasList)
            {

                // 저장된 대지역과 같으나 소지역 리스트의 내부가 비었으면 스킵
                if (areas == null || areas.length == 0)
                {
                    continue;
                }

                for (Area area : areas)
                {
                    // 저장된 소지역 이름과 리스트의 소지역 이름이 같으면 리턴
                    if (oldAreaName.equalsIgnoreCase(area.name) == true)
                    {
                        return area;
                    }
                }
            }

            // 소지역을 다 찾아봤으나 소지역이 저장된 소지역과 다르면 대지역 리턴
            return province;
        }

        // 다 찾아도 맞는게 없으면 리스트의 처음 대지역을 리턴 ??? 일단은 없는 상태로 리턴하기로 함
        //        return regionViewItemList.get(0).getProvince();
        return null;
    }


    //////////////////////////////////////////////

    private HomeCategoryRegionListLayout.OnEventListener mOnEventListener = new HomeCategoryRegionListLayout.OnEventListener()
    {
        @Override
        public boolean isLockUiComponent()
        {
            return isLockUiComponent();
        }

        @Override
        public void lockUiComponent()
        {
            lockUiComponent();
        }

        @Override
        public void releaseUiComponent()
        {
            releaseUiComponent();
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
