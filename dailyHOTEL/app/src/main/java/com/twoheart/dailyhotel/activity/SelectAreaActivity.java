package com.twoheart.dailyhotel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
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
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.DailyAnimatedExpandableListView;
import com.twoheart.dailyhotel.view.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectAreaActivity extends BaseActivity
{
    private DailyAnimatedExpandableListView mListView;
    private AreaAnimatedExpandableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectarea);
        setActionBar(R.string.label_selectarea_area);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        Province province = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        ArrayList<AreaItem> arrayList = intent.getParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_AREAITEMLIST);

        if (province == null || arrayList == null)
        {
            Util.restartApp(getApplicationContext());
            return;
        }

        initLayout(province, arrayList);

        selectedPreviousArea(province, arrayList);
    }

    private void initLayout(Province province, ArrayList<AreaItem> arrayList)
    {
        AreaItem footerItem = new AreaItem();
        arrayList.add(footerItem);

        mAdapter = new AreaAnimatedExpandableListAdapter(this);
        mAdapter.setData(arrayList);
        mAdapter.setSelected(province);

        mListView = (DailyAnimatedExpandableListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);

        mListView.setOnGroupClickListener(new OnGroupClickListener()
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

                    Intent intent = new Intent();
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mAdapter.getGroup(groupPosition));
                    setResult(RESULT_OK, intent);
                    finish();
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
                                mOnUserActionListener.onGroupCollapse(preGroupView, areaItem);
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

//                mListView.postDelayed(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        ExLog.d("groupPosition : " + groupPosition);
//
//                        if (mListView.isGroupExpanded(groupPosition))
//                        {
//                            AreaItem areaItem = mAdapter.getAreaItem(groupPosition);
//
//                            mListView.collapseGroupWithAnimation(groupPosition);
//
//                            View groupView = getGroupView(groupPosition);
//
//                            if (groupView != null)
//                            {
//                                mOnUserActionListener.onGroupCollapse(groupView, areaItem);
//                            }
//                        } else
//                        {
//                            final AreaItem areaItem = mAdapter.getAreaItem(groupPosition);
//
//                            try
//                            {
//                                mListView.expandGroupWithAnimation(groupPosition);
//                                mListView.setTag(groupPosition);
//
//                                View groupView = getGroupView(groupPosition);
//
//                                if (groupView != null)
//                                {
//                                    mOnUserActionListener.onGroupExpand(groupView, areaItem);
//                                }
//                            } catch (Exception e)
//                            {
//                                mListView.setSelection(groupPosition);
//                                mListView.postDelayed(new Runnable()
//                                {
//                                    @Override
//                                    public void run()
//                                    {
//                                        try
//                                        {
//                                            mListView.expandGroupWithAnimation(groupPosition);
//                                            mListView.setTag(groupPosition);
//
//                                            View groupView = getGroupView(groupPosition);
//
//                                            if (groupView != null)
//                                            {
//                                                mOnUserActionListener.onGroupExpand(groupView, areaItem);
//                                            }
//                                        } catch (Exception e)
//                                        {
//                                            mListView.expandGroup(groupPosition);
//                                            mListView.setTag(groupPosition);
//                                            areaItem.isExpandGroup = true;
//                                            releaseUiComponent();
//                                        }
//                                    }
//                                }, 100);
//                            }
//                        }
//                    }
//                }, 100);

                return true;
            }
        });

        mListView.setOnChildClickListener(new OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                Intent intent = new Intent();

                if (childPosition == 0)
                {
                    mAdapter.setSelected(mAdapter.getChildren(groupPosition).get(childPosition));
                    mAdapter.notifyDataSetChanged();

                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mAdapter.getGroup(groupPosition));

                } else
                {
                    mAdapter.setSelected(mAdapter.getChildren(groupPosition).get(childPosition));
                    mAdapter.notifyDataSetChanged();

                    intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, mAdapter.getChildren(groupPosition).get(childPosition));
                }

                setResult(RESULT_OK, intent);
                finish();

                return false;
            }
        });
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

    private void expandGroupWidthAnimation(int groupPosition, AreaItem areaItem)
    {
        mListView.expandGroupWithAnimation(groupPosition);
        mListView.setTag(groupPosition);

        View groupView = getGroupView(groupPosition);

        if (groupView != null)
        {
            mOnUserActionListener.onGroupExpand(groupView, areaItem);
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
                        mOnUserActionListener.onGroupCollapse(groupView, areaItem);
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

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    public interface OnUserActionListener
    {
        public void onGroupExpand(View view, AreaItem areaItem);

        public void onGroupCollapse(View view, AreaItem areaItem);
    }

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
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
                animation.setDuration(350);

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

        @Override
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
                animation.setDuration(350);

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
    };

    private class AreaAnimatedExpandableListAdapter extends AnimatedExpandableListAdapter
    {
        private LayoutInflater inflater;
        private Province mSelectedProvince;
        private List<AreaItem> items;

        public AreaAnimatedExpandableListAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<AreaItem> items)
        {
            this.items = items;
        }

        public void setSelected(Province province)
        {
            mSelectedProvince = province;
        }

        public ArrayList<Area> getChildren(int groupPosition)
        {
            return items.get(groupPosition).getAreaList();
        }

        @Override
        public Area getChild(int groupPosition, int childPosition)
        {
            return items.get(groupPosition).getAreaList().get(childPosition);
        }

        public AreaItem getAreaItem(int groupPosition)
        {
            return items.get(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            Area area = getChild(groupPosition, childPosition);

            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.list_row_area, parent, false);
            } else
            {
                Integer resourceId = (Integer) convertView.getTag(parent.getId());

                if (resourceId == null || resourceId.intValue() != R.layout.list_row_area)
                {
                    convertView = inflater.inflate(R.layout.list_row_area, parent, false);
                }
            }

            convertView.setTag(parent.getId(), R.layout.list_row_area);

            TextView textView = (TextView) convertView.findViewById(R.id.areaTextView);

            textView.setText(area.tag);

            boolean isSelected = false;

            if (mSelectedProvince != null)
            {
                if (mSelectedProvince instanceof Area)
                {
                    if (mSelectedProvince.index == area.index && mSelectedProvince.name.equalsIgnoreCase(area.name) == true)
                    {
                        isSelected = true;
                    }
                } else
                {
                    if (mSelectedProvince.getProvinceIndex() == ((Province) area).getProvinceIndex() && area.index == -1)
                    {
                        isSelected = true;
                    }
                }
            }

            if (isSelected == true)
            {
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setBackgroundColor(getResources().getColor(R.color.dh_theme_color));
            } else
            {
                try
                {
                    XmlResourceParser parser = getResources().getXml(R.drawable.selector_textview_selectarea_childcolor);
                    ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
                    textView.setTextColor(colors);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                textView.setBackgroundResource(R.drawable.selector_background_area);
            }

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition)
        {
            return items.get(groupPosition).getAreaList().size();
        }

        @Override
        public Province getGroup(int groupPosition)
        {
            return items.get(groupPosition).getProvince();
        }

        @Override
        public int getGroupCount()
        {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            Province province = getGroup(groupPosition);

            if (province == null)
            {
                convertView = inflater.inflate(R.layout.list_row_region_footer, parent, false);
                convertView.setTag(null);
                convertView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });

                return convertView;
            } else
            {
                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.list_row_province, parent, false);
                } else
                {
                    Integer resourceId = (Integer) convertView.getTag(parent.getId());

                    if (resourceId == null || resourceId.intValue() != R.layout.list_row_province)
                    {
                        convertView = inflater.inflate(R.layout.list_row_province, parent, false);
                    }
                }
            }

            convertView.setTag(parent.getId(), R.layout.list_row_province);
            convertView.setTag(groupPosition);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.updownArrowImageView);
            TextView textView = (TextView) convertView.findViewById(R.id.provinceTextView);

            textView.setText(province.name);

            boolean hasChildren = getRealChildrenCount(groupPosition) > 0;
            boolean isSelected = false;

            // 우측 위아래 화살펴 표시 여부.
            if (hasChildren == true)
            {
                imageView.setVisibility(View.VISIBLE);
            } else
            {
                imageView.setVisibility(View.GONE);
            }

            if (mSelectedProvince instanceof Area == false && mSelectedProvince.index == province.index)
            {
                if (hasChildren == false)
                {
                    isSelected = true;
                }
            }

            if (isSelected == true)
            {
                textView.setBackgroundColor(getResources().getColor(R.color.dh_theme_color));
                textView.setTextColor(getResources().getColor(R.color.white));
            } else
            {
                if (hasChildren == true)
                {
                    textView.setTextColor(getResources().getColor(R.color.selectarea_text_group));
                    textView.setBackgroundColor(getResources().getColor(R.color.white));
                } else
                {
                    try
                    {
                        XmlResourceParser parser = getResources().getXml(R.drawable.selector_textview_selectarea_groupcolor);
                        ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
                        textView.setTextColor(colors);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }

                    textView.setBackgroundResource(R.drawable.selector_background_province);
                }
            }

            if (hasChildren == true)
            {
                if (getAreaItem(groupPosition).isExpandGroup == true)
                {
                    imageView.setImageResource(R.drawable.ic_details_menu_on);
                } else
                {
                    imageView.setImageResource(R.drawable.ic_details_menu_off);
                }
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1)
        {
            return true;
        }
    }
}