package com.daily.dailyhotel.screen.home.stay.inbound.region;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Region;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayRegionListDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRegionListLocationDataBinding;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.BiFunction;

public class StayRegionListView extends BaseDialogView<StayRegionListView.OnEventListener, ActivityStayRegionListDataBinding> implements StayRegionListInterface
{
    private StayRegionListAdapter mStayRegionListAdapter;

    private LayoutRegionListLocationDataBinding mLayoutRegionListLocationDataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchClick();

        void onProvinceClick(int groupPosition);

        void onAreaClick(int groupPosition, Area area);
    }

    public StayRegionListView(BaseActivity baseActivity, StayRegionListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayRegionListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initLocationTermLayout(viewDataBinding.expandableListView);

        viewDataBinding.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id)
            {
                getEventListener().onProvinceClick(groupPosition);
                return true;
            }
        });


    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setRegionList(List<Region> regionList)
    {
        if (getViewDataBinding() == null || regionList == null || regionList.size() == 0)
        {
            return;
        }

        if (mStayRegionListAdapter == null)
        {
            mStayRegionListAdapter = new StayRegionListAdapter(getContext());

            mStayRegionListAdapter.setTablet(isTabletDevice());
            mStayRegionListAdapter.setOnChildClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Object tag = view.getTag();

                    if (tag == null)
                    {
                        return;
                    }

                    if (tag instanceof Area == false)
                    {
                        return;
                    }

                    Area area = (Area) tag;
                    Integer groupPosition = (Integer) view.getTag(view.getId());

                    if (groupPosition == null)
                    {
                        return;
                    }

                    if (area.index == -1)
                    {
                        getEventListener().onProvinceClick(groupPosition);
                    } else
                    {

                        getEventListener().onAreaClick(groupPosition, area);
                    }
                }
            });
        }

        mStayRegionListAdapter.setData(regionList);
        getViewDataBinding().expandableListView.setAdapter(mStayRegionListAdapter);
    }

    @Override
    public void setLocationTermVisible(boolean visible)
    {
        if (getViewDataBinding() == null || mLayoutRegionListLocationDataBinding == null)
        {
            return;
        }

        mLayoutRegionListLocationDataBinding.locationTermTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public Observable<Boolean> collapseGroupWithAnimation(int groupPosition)
    {
        if (getViewDataBinding() == null || groupPosition < 0)
        {
            return null;
        }

        Region region = mStayRegionListAdapter.getRegion(groupPosition);

        if (region == null)
        {
            return null;
        }

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                getViewDataBinding().expandableListView.collapseGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
                {
                    @Override
                    public void onAnimationEnd()
                    {
                        observer.onNext(true);
                        observer.onComplete();
                    }
                });
            }
        };

        return Observable.zip(observable, collapseArrowAnimation(getGroupView(groupPosition)), new BiFunction<Boolean, Boolean, Boolean>()
        {
            @Override
            public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception
            {
                region.expandGroup = false;
                return true;
            }
        });
    }

    @Override
    public Observable<Boolean> expandGroupWidthAnimation(int groupPosition)
    {
        if (getViewDataBinding() == null || groupPosition < 0)
        {
            return null;
        }

        Region region = mStayRegionListAdapter.getRegion(groupPosition);

        if (region == null)
        {
            return null;
        }

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                getViewDataBinding().expandableListView.expandGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
                {
                    @Override
                    public void onAnimationEnd()
                    {
                        observer.onNext(true);
                        observer.onComplete();
                    }
                });
            }
        };

        return Observable.zip(observable, expandArrowAnimation(getGroupView(groupPosition)), new BiFunction<Boolean, Boolean, Boolean>()
        {
            @Override
            public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception
            {
                region.expandGroup = true;

                return true;
            }
        });
    }

    private void initToolbar(ActivityStayRegionListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.SEARCH, null, v -> getEventListener().onSearchClick());
    }

    private void initLocationTermLayout(ListView listView)
    {
        if (listView == null || listView.getHeaderViewsCount() > 0)
        {
            return;
        }

        mLayoutRegionListLocationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_region_list_location_data, listView, false);
        listView.addHeaderView(mLayoutRegionListLocationDataBinding.getRoot());

        mLayoutRegionListLocationDataBinding.myLocationTextView.setText(R.string.label_region_around_stay);
    }

    //    void postExpandGroupWithAnimation(final int groupPosition)
    //    {
    //        if (getViewDataBinding() == null)
    //        {
    //            return;
    //        }
    //
    //        mListView.postDelayed(new Runnable()
    //        {
    //            @Override
    //            public void run()
    //            {
    //                if (mListView.isGroupExpanded(groupPosition))
    //                {
    //                    RegionViewItem regionViewItem = mAdapter.getAreaItem(groupPosition);
    //
    //                    mListView.collapseGroupWithAnimation(groupPosition);
    //
    //                    View groupView = getGroupView(groupPosition);
    //
    //                    if (groupView != null)
    //                    {
    //                        onGroupCollapse(groupView, regionViewItem);
    //                    }
    //                } else
    //                {
    //                    final RegionViewItem regionViewItem = mAdapter.getAreaItem(groupPosition);
    //
    //                    try
    //                    {
    //                        expandGroupWidthAnimation(groupPosition, regionViewItem);
    //                    } catch (Exception e)
    //                    {
    //                        mListView.setSelection(groupPosition);
    //
    //                        postExpandGroupWithAnimation(groupPosition);
    //                    }
    //                }
    //            }
    //        }, 100);
    //    }
    //
//        private void selectedPreviousArea(Province province, List<RegionViewItem> arrayList)
//        {
//            if (province == null || arrayList == null)
//            {
//                return;
//            }
//
//            int size = arrayList.size();
//
//            for (int i = 0; i < size; i++)
//            {
//                RegionViewItem regionViewItem = arrayList.get(i);
//
//                if (province.getProvinceIndex() == regionViewItem.getProvince().getProvinceIndex())
//                {
//                    if (regionViewItem.getAreaList().size() == 0)
//                    {
//                        // 상세 지역이 없는 경우.
//                        mListView.setSelection(i);
//                        mListView.setSelectedGroup(i);
//
//                        regionViewItem.isExpandGroup = false;
//                    } else
//                    {
//                        mListView.setSelection(i);
//                        mListView.expandGroup(i);
//                        mListView.setTag(i);
//
//                        regionViewItem.isExpandGroup = true;
//                    }
//                    break;
//                }
//            }
//        }

    private Observable<Boolean> expandArrowAnimation(View view)
    {
        if (view == null)
        {
            return null;
        }

        final ImageView imageView = view.findViewById(R.id.arrowImageView);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0f, 180f);
        objectAnimator.setDuration(250);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                objectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator)
                    {
                        imageView.setImageResource(R.drawable.ic_region_ic_sub_v_top);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator)
                    {

                    }
                });

                objectAnimator.start();
            }
        };

        return observable;
    }

    private Observable<Boolean> collapseArrowAnimation(View view)
    {
        if (view == null)
        {
            return null;
        }

        final ImageView imageView = (ImageView) view.findViewById(R.id.arrowImageView);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 180f, 0f);
        objectAnimator.setDuration(250);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                objectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator)
                    {
                        imageView.setImageResource(R.drawable.ic_region_ic_sub_v);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator)
                    {

                    }
                });

                objectAnimator.start();
            }
        };

        return observable;
    }

    private View getGroupView(int groupPosition)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        int count = getViewDataBinding().expandableListView.getChildCount();

        for (int i = 0; i < count; i++)
        {
            View childView = getViewDataBinding().expandableListView.getChildAt(i);

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

}
