package com.daily.dailyhotel.screen.common.region.stay;

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
import com.daily.base.util.ScreenUtils;
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
import io.reactivex.functions.Function;

public class StayRegionListView extends BaseDialogView<StayRegionListView.OnEventListener, ActivityStayRegionListDataBinding> implements StayRegionListInterface
{
    private StayRegionListAdapter mStayRegionListAdapter;

    private LayoutRegionListLocationDataBinding mLayoutRegionListLocationDataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchClick();

        void onProvinceClick(int groupPosition);

        void onAreaClick(int groupPosition, Area area);

        void onAroundSearchClick();
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
        initHeaderNFooterLayout(viewDataBinding.expandableListView);

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
    public Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation)
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
                if (animation == true)
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
                } else
                {
                    getViewDataBinding().expandableListView.collapseGroup(groupPosition);

                    observer.onNext(true);
                    observer.onComplete();
                }
            }
        };

        Observable<Boolean> collapseObservable = collapseArrowAnimation(getGroupView(groupPosition), animation);

        if (collapseObservable == null)
        {
            return observable.map(new Function<Boolean, Boolean>()
            {
                @Override
                public Boolean apply(Boolean aBoolean) throws Exception
                {
                    region.expandGroup = false;
                    return true;
                }
            });
        } else
        {
            return Observable.zip(observable, collapseObservable, new BiFunction<Boolean, Boolean, Boolean>()
            {
                @Override
                public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception
                {
                    region.expandGroup = false;
                    return true;
                }
            });
        }
    }

    @Override
    public Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation)
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
                if (animation == true)
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

                    // 마지막 리스트 목록은 애니메이션으로 안잡힌다.
                    if (groupPosition == mStayRegionListAdapter.getGroupCount() - 1)
                    {
                        getViewDataBinding().expandableListView.setOnScrollListener(new AbsListView.OnScrollListener()
                        {
                            @Override
                            public void onScrollStateChanged(AbsListView absListView, int scrollState)
                            {
                                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                                {
                                    observer.onNext(true);
                                    observer.onComplete();
                                }
                            }

                            @Override
                            public void onScroll(AbsListView absListView, int i, int i1, int i2)
                            {

                            }
                        });
                    }
                } else
                {
                    getViewDataBinding().expandableListView.expandGroup(groupPosition);

                    observer.onNext(true);
                    observer.onComplete();
                }
            }
        };

        Observable<Boolean> arrowObservable = expandArrowAnimation(getGroupView(groupPosition));

        if (arrowObservable == null)
        {
            arrowObservable = Observable.just(true);
        }

        return Observable.zip(observable, arrowObservable, new BiFunction<Boolean, Boolean, Boolean>()
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

    private void initHeaderNFooterLayout(ListView listView)
    {
        if (listView == null || listView.getHeaderViewsCount() > 0 || listView.getFooterViewsCount() > 0)
        {
            return;
        }

        // 헤더
        mLayoutRegionListLocationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_region_list_location_data, listView, false);
        listView.addHeaderView(mLayoutRegionListLocationDataBinding.getRoot());
        mLayoutRegionListLocationDataBinding.getRoot().setOnClickListener(v -> getEventListener().onAroundSearchClick());

        mLayoutRegionListLocationDataBinding.myLocationTextView.setText(R.string.label_region_around_stay);

        // 푸터
        View footerView = new View(getContext());
        footerView.setBackgroundResource(R.drawable.layerlist_divider);
        AbsListView.LayoutParams footerLayoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 12));
        footerView.setLayoutParams(footerLayoutParams);

        listView.addFooterView(footerView);
    }

    //    public void setArea(Province province, List<RegionViewItem> arrayList)
    //    {
    //        if (province == null || arrayList == null)
    //        {
    //            return;
    //        }
    //
    //        int size = arrayList.size();
    //
    //        for (int i = 0; i < size; i++)
    //        {
    //            RegionViewItem regionViewItem = arrayList.get(i);
    //
    //            if (province.getProvinceIndex() == regionViewItem.getProvince().getProvinceIndex())
    //            {
    //                if (regionViewItem.getAreaList().size() == 0)
    //                {
    //                    // 상세 지역이 없는 경우.
    //                    mListView.setSelection(i);
    //                    mListView.setSelectedGroup(i);
    //
    //                    regionViewItem.isExpandGroup = false;
    //                } else
    //                {
    //                    mListView.setSelection(i);
    //                    mListView.expandGroup(i);
    //                    mListView.setTag(i);
    //
    //                    regionViewItem.isExpandGroup = true;
    //                }
    //                break;
    //            }
    //        }
    //    }

    private Observable<Boolean> expandArrowAnimation(View view)
    {
        if (view == null)
        {
            return null;
        }

        final ImageView imageView = view.findViewById(R.id.arrowImageView);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0f, -180f);
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
                        imageView.setRotation(0f);
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

    private Observable<Boolean> collapseArrowAnimation(View view, boolean animation)
    {
        if (view == null)
        {
            return null;
        }

        Observable<Boolean> observable;
        final ImageView imageView = view.findViewById(R.id.arrowImageView);

        if (animation == true)
        {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0, 180f);
            objectAnimator.setDuration(250);

            observable = new Observable<Boolean>()
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
                            imageView.setRotation(0f);
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
        } else
        {
            imageView.setRotation(0f);
            imageView.setImageResource(R.drawable.ic_region_ic_sub_v);

            observable = Observable.just(true);
        }

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
