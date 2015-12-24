package com.twoheart.dailyhotel.screen.region;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.DailyAnimatedExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegionListFragment extends BaseFragment
{
    public static final int CHILD_GRID_COLUMN = 2;
    private RegionListActivity.OnUserActionListener mOnUserActionListener;

    private DailyAnimatedExpandableListView mListView;
    private RegionAnimatedExpandableListAdapter mAdapter;

    private PlaceMainFragment.TYPE mType;
    private Province mSelectedProvince;

    private RegionListActivity.Region mRegion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mListView = (DailyAnimatedExpandableListView) inflater.inflate(R.layout.fragment_region_list, container, false);
        mListView.setOnGroupClickListener(mOnGroupClickListener);

        return mListView;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        onPageSelected();
    }

    public void onPageSelected()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing())
        {
            return;
        }

        switch (mType)
        {
            case HOTEL:
                DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mHotelRegionListJsonResponseListener, baseActivity);
                break;

            case FNB:
                DailyNetworkAPI.getInstance().requestGourmetRegionList(mNetworkTag, mGourmetRegionListJsonResponseListener, baseActivity);
                break;
        }
    }

    public void setInformation(PlaceMainFragment.TYPE type, RegionListActivity.Region region, Province province)
    {
        mType = type;
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

    private void selectedPreviousArea(Province province, ArrayList<RegionViewItem> arrayList)
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
                animation.setAnimationListener(new AnimationListener()
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
                        imageView.setImageResource(R.drawable.ic_details_menu_on);

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
                animation.setAnimationListener(new AnimationListener()
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
                        imageView.setImageResource(R.drawable.ic_details_menu_off);

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
                mAdapter.setSelected(mAdapter.getGroup(groupPosition));
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

    private ArrayList<RegionViewItem> makeAreaItemList(ArrayList<Province> provinceList, ArrayList<Area> areaList)
    {
        ArrayList<RegionViewItem> arrayList = new ArrayList<RegionViewItem>(provinceList.size());

        for (Province province : provinceList)
        {
            RegionViewItem item = new RegionViewItem();

            item.setProvince(province);

            int i = 0;
            Area[] areas = null;
            ArrayList<Area[]> areaArrayList = new ArrayList<>();

            for (Area area : areaList)
            {
                if (province.getProvinceIndex() == area.getProvinceIndex())
                {
                    if (areas == null)
                    {
                        areas = new Area[CHILD_GRID_COLUMN];
                    }

                    if (areaArrayList.size() == 0)
                    {
                        Area totalArea = new Area();

                        totalArea.index = -1;
                        totalArea.name = province.name + " 전체";
                        totalArea.setProvince(province);
                        totalArea.sequence = -1;
                        totalArea.tag = totalArea.name;
                        totalArea.setProvinceIndex(province.getProvinceIndex());

                        areas[i++] = totalArea;
                    }

                    area.setProvince(province);

                    if (i != 0 && i % CHILD_GRID_COLUMN == 1)
                    {
                        areas[i++] = area;
                        areaArrayList.add(areas);

                        i = 0;
                        areas = null;
                    } else
                    {
                        areas[i++] = area;
                    }
                }
            }

            if (areas != null)
            {
                areaArrayList.add(areas);
            }

            item.setAreaList(areaArrayList);
            arrayList.add(item);
        }

        return arrayList;
    }

    private DailyHotelJsonResponseListener mHotelRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    throw new NullPointerException("response == null");
                }

                JSONObject dataJSONObject = response.getJSONObject("data");

                JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                ArrayList<Province> provinceList = makeProvinceList(provinceArray);

                JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                ArrayList<RegionViewItem> regionViewItemList = makeAreaItemList(provinceList, areaList);

                if (mAdapter == null)
                {
                    mAdapter = new RegionAnimatedExpandableListAdapter(baseActivity);
                    mAdapter.setOnChildClickListener(mOnChildClickListener);
                }

                mAdapter.setData(regionViewItemList);
                mListView.setAdapter(mAdapter);

                selectedPreviousArea(mSelectedProvince, regionViewItemList);
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        private ArrayList<Area> makeAreaList(JSONArray jsonArray)
        {
            ArrayList<Area> areaList = new ArrayList<Area>();

            try
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Area area = new Area(jsonObject);

                        areaList.add(area);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return areaList;
        }

        private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
        {
            ArrayList<Province> provinceList = new ArrayList<Province>();

            try
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Province province = new Province(jsonObject);

                        provinceList.add(province);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return provinceList;
        }
    };

    private DailyHotelJsonResponseListener mGourmetRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    throw new NullPointerException("response == null");
                }

                JSONObject dataJSONObject = response.getJSONObject("data");

                JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                ArrayList<Province> provinceList = makeProvinceList(provinceArray);

                JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                ArrayList<RegionViewItem> regionViewItemList = makeAreaItemList(provinceList, areaList);

                if (mAdapter == null)
                {
                    mAdapter = new RegionAnimatedExpandableListAdapter(baseActivity);
                    mAdapter.setOnChildClickListener(mOnChildClickListener);
                }

                mAdapter.setData(regionViewItemList);
                mListView.setAdapter(mAdapter);

                selectedPreviousArea(mSelectedProvince, regionViewItemList);
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        private ArrayList<Area> makeAreaList(JSONArray jsonArray)
        {
            ArrayList<Area> areaList = new ArrayList<Area>();

            try
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Area area = new Area(jsonObject);

                        areaList.add(area);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return areaList;
        }

        private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
        {
            ArrayList<Province> provinceList = new ArrayList<Province>();

            try
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Province province = new Province(jsonObject);

                        provinceList.add(province);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return provinceList;
        }
    };
}