package com.twoheart.dailyhotel.screen.home.category;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceRegionAnimatedExpandableListAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView;

import java.util.List;

/**
 * Created by android_sam on 2017. 4. 12..
 */

public class HomeCategoryRegionListLayout extends BaseLayout
{
    private View mTermsOfLocationView;
    private DailyAnimatedExpandableListView mListView;

    private PlaceRegionAnimatedExpandableListAdapter mAdapter; // TODO : 임시로 어뎁터 생성 - 서버에서 어떤 타입이 올지 몰라 생성 안하고 씀

    private DailyCategoryType mDailyCategoryType;
    private Province mSelectedProvince;

    public interface OnEventListener extends OnBaseEventListener
    {
        boolean isLockUiComponent();

        void lockUiComponent();

        void releaseUiComponent();

        void onRegionClick(Province province);

        void onAroundSearchClick();
    }

    public HomeCategoryRegionListLayout(Context context, @NonNull OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mListView = (DailyAnimatedExpandableListView) view.findViewById(R.id.listView);
        mListView.setOnGroupClickListener(mOnGroupClickListener);
    }

    public PlaceRegionAnimatedExpandableListAdapter getAdapter()
    {
        return mAdapter;
    }

    private View getHeaderLayout(boolean isAgreed)
    {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.layout_region_around_search_header, null);

        View searchAroundLayout = headerView.findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(mOnHeaderClickListener);

        TextView text01View = (TextView) headerView.findViewById(R.id.text01View);
        text01View.setText(getAroundPlaceText());

        mTermsOfLocationView = headerView.findViewById(R.id.text02View);
        updateTermsOfLocationView(isAgreed);

        return headerView;
    }

    public void updateTermsOfLocationView(boolean isAgreed)
    {
        if (isAgreed == true)
        {
            mTermsOfLocationView.setVisibility(View.GONE);
        } else
        {
            mTermsOfLocationView.setVisibility(View.VISIBLE);
        }
    }

    public void setRegionViewList(BaseActivity baseActivity, List<RegionViewItem> arrayList, boolean isAgreed)
    {
        if (mAdapter == null)
        {
            mAdapter = new PlaceRegionAnimatedExpandableListAdapter(baseActivity);
            mAdapter.setIsTablet(Util.isTabletDevice(baseActivity));
            mAdapter.setOnChildClickListener(mOnChildClickListener);
        }

        mAdapter.setData(arrayList);

        if (mListView == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (mListView.getHeaderViewsCount() == 0)
        {
            mListView.addHeaderView(getHeaderLayout(isAgreed));
        }

        mListView.setAdapter(mAdapter);
        selectedPreviousArea(mSelectedProvince, arrayList);
    }

    public void setDailyCategoryType(DailyCategoryType categoryType)
    {
        mDailyCategoryType = categoryType;
    }

    public void setSelectedProvince(Province province)
    {
        mSelectedProvince = province;
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

    private String getAroundPlaceText()
    {
        if (mDailyCategoryType == null || mDailyCategoryType == DailyCategoryType.NONE)
        {
            mDailyCategoryType = DailyCategoryType.STAY_ALL;
        }

        String categoryName = mContext.getResources().getString(mDailyCategoryType.getNameResId());

        return mContext.getResources().getString(R.string.label_select_area_daily_category_format, categoryName);
    }

    private View getGroupView(int groupPosition)
    {
        int count = mListView.getChildCount();

        for (int i = 0; i < count; i++)
        {
            View childView = mListView.getChildAt(i);

            if (childView != null)
            {
                Object tag = childView.getTag();

                if (tag != null && tag instanceof Integer == true)
                {
                    Integer childTag = (Integer) tag;

                    if (childTag == groupPosition)
                    {
                        return childView;
                    }
                }
            }
        }

        return null;
    }

    public void onGroupExpand(View view, final RegionViewItem regionViewItem)
    {
        if (view.getVisibility() != View.VISIBLE)
        {
            ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

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
                        ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();
                        imageView.setAnimation(null);
                        imageView.setImageResource(R.drawable.region_ic_sub_v_top);

                        regionViewItem.isExpandGroup = true;
                    }
                });

                imageView.startAnimation(animation);
            } else
            {
                ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

                regionViewItem.isExpandGroup = true;
            }
        } else
        {
            ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

            regionViewItem.isExpandGroup = true;
        }
    }

    private void onGroupCollapse(View view, final RegionViewItem regionViewItem)
    {
        if (view.getVisibility() != View.VISIBLE)
        {
            ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

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
                        ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

                        imageView.setAnimation(null);
                        imageView.setImageResource(R.drawable.region_ic_sub_v);

                        regionViewItem.isExpandGroup = false;
                    }
                });

                imageView.startAnimation(animation);
            } else
            {
                ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

                regionViewItem.isExpandGroup = false;
            }
        } else
        {
            ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

            regionViewItem.isExpandGroup = false;
        }
    }

    private void expandGroupWidthAnimation(int groupPosition, final RegionViewItem regionViewItem)
    {
        mListView.expandGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
        {
            @Override
            public void onAnimationEnd()
            {
                ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();

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


    private DailyAnimatedExpandableListView.OnGroupClickListener //
        mOnGroupClickListener = new DailyAnimatedExpandableListView.OnGroupClickListener()
    {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id)
        {
            if (((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).isLockUiComponent() == true)
            {
                return true;
            }

            ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).lockUiComponent();

            //
            if (mAdapter.getChildrenCount(groupPosition) == 0)
            {
                if (mOnEventListener != null)
                {
                    ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).onRegionClick(mAdapter.getGroup(groupPosition));
                }
                return true;
            }

            Object tag = mListView.getTag();

            int previousGroupPosition = -1;

            if (tag != null && tag instanceof Integer == true)
            {
                previousGroupPosition = (Integer) tag;

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
                ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).releaseUiComponent();
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
            Object tag = view.getTag();

            if (tag == null)
            {
                return;
            }

            if (tag instanceof Area == false || mOnEventListener == null)
            {
                return;
            }

            Area area = (Area) tag;

            if (area.index == -1)
            {
                Integer groupPosition = (Integer) view.getTag(view.getId());

                if (groupPosition != null)
                {
                    ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).onRegionClick(mAdapter.getGroup(groupPosition));
                }
            } else
            {
                ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).onRegionClick(area);
            }
        }
    };

    private View.OnClickListener mOnHeaderClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            ((HomeCategoryRegionListLayout.OnEventListener) mOnEventListener).onAroundSearchClick();
        }
    };
}
