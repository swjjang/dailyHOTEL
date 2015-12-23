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
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Province;
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

        DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
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

    private void expandGroupWidthAnimation(int groupPosition, final AreaItem areaItem)
    {
        mListView.expandGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
        {
            @Override
            public void onAnimationEnd()
            {
                releaseUiComponent();

                areaItem.isExpandGroup = true;
            }
        });

        mListView.setTag(groupPosition);

        View groupView = getGroupView(groupPosition);

        if (groupView != null)
        {
            onGroupExpand(groupView, areaItem);
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
                    AreaItem areaItem = mAdapter.getAreaItem(groupPosition);

                    mListView.collapseGroupWithAnimation(groupPosition);

                    View groupView = getGroupView(groupPosition);

                    if (groupView != null)
                    {
                        onGroupCollapse(groupView, areaItem);
                    }
                } else
                {
                    final AreaItem areaItem = mAdapter.getAreaItem(groupPosition);

                    try
                    {
                        expandGroupWidthAnimation(groupPosition, areaItem);
                    } catch (Exception e)
                    {
                        mListView.setSelection(groupPosition);

                        postExpandGroupWithAnimation(groupPosition);
                    }
                }
            }
        }, 100);
    }

    private void selectedPreviousArea(Province province, ArrayList<AreaItem> arrayList)
    {
        if (province == null || arrayList == null)
        {
            return;
        }

        if (province instanceof Area)
        {
            int size = arrayList.size();
            Area selectedArea = (Area) province;

            for (int i = 0; i < size; i++)
            {
                AreaItem areaItem = arrayList.get(i);

                if (selectedArea.getProvinceIndex() == areaItem.getProvince().getProvinceIndex())
                {
                    if (areaItem.getAreaList().size() == 0)
                    {
                        // 상세 지역이 없는 경우.
                        mListView.setSelection(i);
                        mListView.setSelectedGroup(i);

                        areaItem.isExpandGroup = false;
                    } else
                    {
                        ArrayList<Area> areaList = areaItem.getAreaList();
                        int areaSize = areaList.size();

                        for (int j = 0; j < areaSize; j++)
                        {
                            Area area = areaList.get(j);

                            if (area.index == selectedArea.index)
                            {
                                mListView.setSelection(i);
                                mListView.expandGroup(i);
                                mListView.setTag(i);

                                areaItem.isExpandGroup = true;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        } else
        {
            int size = arrayList.size();

            for (int i = 0; i < size; i++)
            {
                AreaItem areaItem = arrayList.get(i);

                if (province.getProvinceIndex() == areaItem.getProvince().getProvinceIndex())
                {
                    if (areaItem.getAreaList().size() == 0)
                    {
                        // 상세 지역이 없는 경우.
                        mListView.setSelection(i);
                        mListView.setSelectedGroup(i);

                        areaItem.isExpandGroup = false;
                    } else
                    {
                        mListView.setSelection(i);
                        mListView.expandGroup(i);
                        mListView.setTag(i);

                        areaItem.isExpandGroup = true;
                    }
                    break;
                }
            }
        }
    }

    public void onGroupExpand(View view, final AreaItem areaItem)
    {
        if (view.getVisibility() != View.VISIBLE)
        {
            releaseUiComponent();

            areaItem.isExpandGroup = true;
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

                        areaItem.isExpandGroup = true;
                    }
                });

                imageView.startAnimation(animation);
            } else
            {
                releaseUiComponent();

                areaItem.isExpandGroup = true;
            }
        } else
        {
            releaseUiComponent();

            areaItem.isExpandGroup = true;
        }
    }

    public void onGroupCollapse(View view, final AreaItem areaItem)
    {
        if (view.getVisibility() != View.VISIBLE)
        {
            releaseUiComponent();

            areaItem.isExpandGroup = false;
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

                        areaItem.isExpandGroup = false;
                    }
                });

                imageView.startAnimation(animation);
            } else
            {
                releaseUiComponent();

                areaItem.isExpandGroup = false;
            }
        } else
        {
            releaseUiComponent();

            areaItem.isExpandGroup = false;
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

                AreaItem areaItem = mAdapter.getAreaItem(previousGroupPosition);

                if (mListView.isGroupExpanded(previousGroupPosition))
                {
                    if (previousGroupPosition == groupPosition)
                    {
                        mListView.collapseGroupWithAnimation(previousGroupPosition);

                        View preGroupView = getGroupView(previousGroupPosition);

                        if (preGroupView == null)
                        {
                            areaItem.isExpandGroup = false;
                        } else
                        {
                            onGroupCollapse(preGroupView, areaItem);
                        }
                    } else
                    {
                        mListView.collapseGroup(previousGroupPosition);
                        areaItem.isExpandGroup = false;
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

    private OnChildClickListener mOnChildClickListener = new OnChildClickListener()
    {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
        {
            Intent intent = new Intent();

            if (childPosition == 0)
            {
                mAdapter.setSelected(mAdapter.getChildren(groupPosition).get(childPosition));
                mAdapter.notifyDataSetChanged();

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onRegionClick(mAdapter.getGroup(groupPosition));
                }
            } else
            {
                mAdapter.setSelected(mAdapter.getChildren(groupPosition).get(childPosition));
                mAdapter.notifyDataSetChanged();

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onRegionClick(mAdapter.getChildren(groupPosition).get(childPosition));
                }
            }

            return false;
        }
    };

    private ArrayList<AreaItem> makeAreaItemList(ArrayList<Province> provinceList, ArrayList<Area> areaList)
    {
        ArrayList<AreaItem> arrayList = new ArrayList<AreaItem>(provinceList.size());

        for (Province province : provinceList)
        {
            AreaItem item = new AreaItem();

            item.setProvince(province);
            item.setAreaList(new ArrayList<Area>());

            for (Area area : areaList)
            {
                if (province.getProvinceIndex() == area.getProvinceIndex())
                {
                    ArrayList<Area> areaArrayList = item.getAreaList();

                    if (areaArrayList.size() == 0)
                    {
                        Area totalArea = new Area();

                        totalArea.index = -1;
                        totalArea.name = province.name + " 전체";
                        totalArea.setProvince(province);
                        totalArea.sequence = -1;
                        totalArea.tag = totalArea.name;
                        totalArea.setProvinceIndex(province.getProvinceIndex());

                        areaArrayList.add(totalArea);
                    }

                    area.setProvince(province);
                    areaArrayList.add(area);
                }
            }

            arrayList.add(item);
        }

        return arrayList;
    }

    private DailyHotelJsonResponseListener mRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
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

                ArrayList<AreaItem> areaItemList = makeAreaItemList(provinceList, areaList);

                if (mAdapter == null)
                {
                    mAdapter = new RegionAnimatedExpandableListAdapter(baseActivity);
                }

                mAdapter.setData(areaItemList);
                mListView.setAdapter(mAdapter);
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