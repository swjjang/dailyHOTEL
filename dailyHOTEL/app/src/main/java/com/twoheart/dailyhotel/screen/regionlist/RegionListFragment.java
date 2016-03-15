package com.twoheart.dailyhotel.screen.regionlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.common.BaseFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.DailyAnimatedExpandableListView;

import java.util.List;

public class RegionListFragment extends BaseFragment
{
    private RegionListActivity.OnUserActionListener mOnUserActionListener;

    private DailyAnimatedExpandableListView mListView;
    private RegionAnimatedExpandableListAdapter mAdapter;

    private Constants.PlaceType mPlaceType;
    private Province mSelectedProvince;

    private RegionListActivity.Region mRegion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mListView = (DailyAnimatedExpandableListView) inflater.inflate(R.layout.fragment_region_list, container, false);
        mListView.setOnGroupClickListener(mOnGroupClickListener);

        return mListView;
    }

    private void recordAnalyticsScreen()
    {
        switch (mPlaceType)
        {
            case HOTEL:
                switch (mRegion)
                {
                    case DOMESTIC:
                        AnalyticsManager.getInstance(getContext()).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null);
                        break;

                    case GLOBAL:
                        AnalyticsManager.getInstance(getContext()).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL, null);
                        break;
                }
                break;

            case FNB:
                AnalyticsManager.getInstance(getContext()).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, null);
                break;
        }
    }

    @Override
    public void onResume()
    {
        if (mAdapter != null)
        {
            recordAnalyticsScreen();
        }

        super.onResume();
    }

    public void setRegionViewList(BaseActivity baseActivity, List<RegionViewItem> arrayList)
    {
        if (mAdapter == null)
        {
            mAdapter = new RegionAnimatedExpandableListAdapter(baseActivity);
            mAdapter.setOnChildClickListener(mOnChildClickListener);
        }

        mAdapter.setData(arrayList);
        mListView.setAdapter(mAdapter);
        selectedPreviousArea(mSelectedProvince, arrayList);
    }

    public void setInformation(Constants.PlaceType type, RegionListActivity.Region region, Province province)
    {
        mPlaceType = type;
        mRegion = region;
        mSelectedProvince = province;
    }

    public void setOnUserActionListener(RegionListActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    private View getGroupView(int groupPosition)
    {
        int count = mListView.getChildCount();

        for (int i = 0; i < count; i++)
        {
            View childView = mListView.getChildAt(i);

            if (childView != null)
            {
                Integer childTag = (Integer) childView.getTag();

                if (childTag != null && childTag.intValue() == groupPosition)
                {
                    return childView;
                }
            }
        }

        return null;
    }

    private void expandGroupWidthAnimation(int groupPosition, final RegionViewItem regionViewItem)
    {
        mListView.expandGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
        {
            @Override
            public void onAnimationEnd()
            {
                releaseUiComponent();

                regionViewItem.isExpandGroup = true;
            }
        });

        mListView.setTag(groupPosition);

        View groupView = getGroupView(groupPosition);

        if (groupView != null)
        {
            onGroupExpand(groupView, regionViewItem);
        }
    }

    private void postExpandGroupWithAnimation(final int groupPosition)
    {
        mListView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mListView.isGroupExpanded(groupPosition))
                {
                    RegionViewItem regionViewItem = mAdapter.getAreaItem(groupPosition);

                    mListView.collapseGroupWithAnimation(groupPosition);

                    View groupView = getGroupView(groupPosition);

                    if (groupView != null)
                    {
                        onGroupCollapse(groupView, regionViewItem);
                    }
                } else
                {
                    final RegionViewItem regionViewItem = mAdapter.getAreaItem(groupPosition);

                    try
                    {
                        expandGroupWidthAnimation(groupPosition, regionViewItem);
                    } catch (Exception e)
                    {
                        mListView.setSelection(groupPosition);

                        postExpandGroupWithAnimation(groupPosition);
                    }
                }
            }
        }, 100);
    }

    private void selectedPreviousArea(Province province, List<RegionViewItem> arrayList)
    {
        if (province == null || arrayList == null)
        {
            return;
        }

        int size = arrayList.size();

        for (int i = 0; i < size; i++)
        {
            RegionViewItem regionViewItem = arrayList.get(i);

            if (province.getProvinceIndex() == regionViewItem.getProvince().getProvinceIndex())
            {
                if (regionViewItem.getAreaList().size() == 0)
                {
                    // 상세 지역이 없는 경우.
                    mListView.setSelection(i);
                    mListView.setSelectedGroup(i);

                    regionViewItem.isExpandGroup = false;
                } else
                {
                    mListView.setSelection(i);
                    mListView.expandGroup(i);
                    mListView.setTag(i);

                    regionViewItem.isExpandGroup = true;
                }
                break;
            }
        }
    }

    public void onGroupExpand(View view, final RegionViewItem regionViewItem)
    {
        if (view.getVisibility() != View.VISIBLE)
        {
            releaseUiComponent();

            regionViewItem.isExpandGroup = true;
            return;
        }

        if (Util.isOverAPI11() == true)
        {
            final ImageView imageView = (ImageView) view.findViewById(R.id.updownArrowImageView);

            RotateAnimation animation = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setFillBefore(true);
            animation.setFillAfter(true);
            animation.setDuration(250);

            if (imageView != null)
            {
                animation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        releaseUiComponent();
                        imageView.setAnimation(null);
                        imageView.setImageResource(R.drawable.region_ic_sub_v_top);

                        regionViewItem.isExpandGroup = true;
                    }
                });

                imageView.startAnimation(animation);
            } else
            {
                releaseUiComponent();

                regionViewItem.isExpandGroup = true;
            }
        } else
        {
            releaseUiComponent();

            regionViewItem.isExpandGroup = true;
        }
    }

    public void onGroupCollapse(View view, final RegionViewItem regionViewItem)
    {
        if (view.getVisibility() != View.VISIBLE)
        {
            releaseUiComponent();

            regionViewItem.isExpandGroup = false;
            return;
        }

        if (Util.isOverAPI11() == true)
        {
            final ImageView imageView = (ImageView) view.findViewById(R.id.updownArrowImageView);

            RotateAnimation animation = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setFillBefore(true);
            animation.setFillAfter(true);
            animation.setDuration(250);

            if (imageView != null)
            {
                animation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        releaseUiComponent();

                        imageView.setAnimation(null);
                        imageView.setImageResource(R.drawable.region_ic_sub_v);

                        regionViewItem.isExpandGroup = false;
                    }
                });

                imageView.startAnimation(animation);
            } else
            {
                releaseUiComponent();

                regionViewItem.isExpandGroup = false;
            }
        } else
        {
            releaseUiComponent();

            regionViewItem.isExpandGroup = false;
        }
    }

    private OnGroupClickListener mOnGroupClickListener = new OnGroupClickListener()
    {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id)
        {
            if (isLockUiComponent() == true || v.getTag() == null)
            {
                return true;
            }

            lockUiComponent();

            //
            if (mAdapter.getChildrenCount(groupPosition) == 0)
            {
                mAdapter.notifyDataSetChanged();

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onRegionClick(mAdapter.getGroup(groupPosition));
                }

                return true;
            }

            Integer tag = (Integer) mListView.getTag();

            int previousGroupPosition = -1;

            if (tag != null)
            {
                previousGroupPosition = tag.intValue();

                RegionViewItem regionViewItem = mAdapter.getAreaItem(previousGroupPosition);

                if (mListView.isGroupExpanded(previousGroupPosition))
                {
                    if (previousGroupPosition == groupPosition)
                    {
                        mListView.collapseGroupWithAnimation(previousGroupPosition);

                        View preGroupView = getGroupView(previousGroupPosition);

                        if (preGroupView == null)
                        {
                            regionViewItem.isExpandGroup = false;
                        } else
                        {
                            onGroupCollapse(preGroupView, regionViewItem);
                        }
                    } else
                    {
                        mListView.collapseGroup(previousGroupPosition);
                        regionViewItem.isExpandGroup = false;
                    }
                } else
                {
                    previousGroupPosition = -1;
                }
            }

            if (previousGroupPosition == groupPosition)
            {
                releaseUiComponent();
                return true;
            }

            postExpandGroupWithAnimation(groupPosition);

            return true;
        }
    };

    private View.OnClickListener mOnChildClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Area area = (Area) view.getTag();

            if (area == null)
            {
                return;
            }

            view.setSelected(true);

            Intent intent = new Intent();

            if (area.index == -1)
            {
                if (mOnUserActionListener != null)
                {
                    Integer groupPosition = (Integer) view.getTag(view.getId());

                    if (groupPosition != null)
                    {
                        mOnUserActionListener.onRegionClick(mAdapter.getGroup(groupPosition.intValue()));
                    }
                }
            } else
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onRegionClick(area);
                }
            }
        }
    };
}