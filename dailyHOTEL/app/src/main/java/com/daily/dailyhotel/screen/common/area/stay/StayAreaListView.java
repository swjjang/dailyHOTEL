package com.daily.dailyhotel.screen.common.area.stay;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.screen.common.area.stay.inbound.StayAreaExpandableListAdapter;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayAreaListDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRegionListLocationDataBinding;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

@Deprecated
public class StayAreaListView extends BaseDialogView<StayAreaListView.OnEventListener, ActivityStayAreaListDataBinding> implements StayAreaListInterface
{
    StayAreaExpandableListAdapter mStayAreaListAdapter;

    private LayoutRegionListLocationDataBinding mLayoutRegionListLocationDataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchClick();

        void onAreaGroupClick(int groupPosition);

        void onAreaClick(int groupPosition, StayArea stayTown);

        void onAroundSearchClick();
    }

    public StayAreaListView(BaseActivity baseActivity, StayAreaListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        //        initToolbar(viewDataBinding);
        //        initHeaderNFooterLayout(viewDataBinding.expandableListView);
        //
        //        viewDataBinding.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        //        {
        //            @Override
        //            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id)
        //            {
        //                getEventListener().onAreaGroupClick(groupPosition);
        //                return true;
        //            }
        //        });
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
    public void setAreaList(List<StayAreaGroup> areaList)
    {
        //        if (getViewDataBinding() == null || areaList == null || areaList.size() == 0)
        //        {
        //            return;
        //        }
        //
        //        if (mStayAreaListAdapter == null)
        //        {
        //            mStayAreaListAdapter = new StayAreaListAdapter(getContext());
        //
        //            mStayAreaListAdapter.setTablet(isTabletDevice());
        //            mStayAreaListAdapter.setOnChildClickListener(new View.OnClickListener()
        //            {
        //                @Override
        //                public void onClick(View view)
        //                {
        //                    Object tag = view.getTag();
        //
        //                    if (tag == null)
        //                    {
        //                        return;
        //                    }
        //
        //                    if (tag instanceof StayArea == false)
        //                    {
        //                        return;
        //                    }
        //
        //                    StayArea stayTown = (StayArea) tag;
        //                    Integer groupPosition = (Integer) view.getTag(view.getId());
        //
        //                    if (groupPosition == null)
        //                    {
        //                        return;
        //                    }
        //
        //                    getEventListener().onAreaClick(groupPosition, stayTown);
        //                }
        //            });
        //        }
        //
        //        mStayAreaListAdapter.setData(areaList);
        //        getViewDataBinding().expandableListView.setAdapter(mStayAreaListAdapter);
    }

    @Override
    public void setLocationText(String locationText)
    {
        if (getViewDataBinding() == null || mLayoutRegionListLocationDataBinding == null)
        {
            return;
        }

        mLayoutRegionListLocationDataBinding.myLocationTextView.setText(locationText);
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
        return null;

        //        if (getViewDataBinding() == null || groupPosition < 0)
        //        {
        //            return null;
        //        }
        //
        //        StayAreaGroup stayDistrict = mStayAreaListAdapter.getAreaGroup(groupPosition);
        //
        //        if (stayDistrict == null)
        //        {
        //            return null;
        //        }
        //
        //        Observable<Boolean> observable = new Observable<Boolean>()
        //        {
        //            @Override
        //            protected void subscribeActual(Observer<? super Boolean> observer)
        //            {
        //                if (animation == true)
        //                {
        //                    getViewDataBinding().expandableListView.collapseGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
        //                    {
        //                        @Override
        //                        public void onAnimationEnd()
        //                        {
        //                            observer.onNext(true);
        //                            observer.onComplete();
        //                        }
        //                    });
        //                } else
        //                {
        //                    getViewDataBinding().expandableListView.collapseGroup(groupPosition);
        //
        //                    observer.onNext(true);
        //                    observer.onComplete();
        //                }
        //            }
        //        };
        //
        //        Observable<Boolean> collapseObservable = collapseArrowAnimation(getGroupView(groupPosition), animation);
        //
        //        if (collapseObservable == null)
        //        {
        //            return observable.map(new Function<Boolean, Boolean>()
        //            {
        //                @Override
        //                public Boolean apply(Boolean aBoolean) throws Exception
        //                {
        //                    mStayAreaListAdapter.setSelectedGroupPosition(-1);
        //                    return true;
        //                }
        //            });
        //        } else
        //        {
        //            return Observable.zip(observable, collapseObservable, new BiFunction<Boolean, Boolean, Boolean>()
        //            {
        //                @Override
        //                public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception
        //                {
        //                    mStayAreaListAdapter.setSelectedGroupPosition(-1);
        //                    return true;
        //                }
        //            });
        //        }
    }

    @Override
    public Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation)
    {
        return null;

        //        if (getViewDataBinding() == null || groupPosition < 0)
        //        {
        //            return null;
        //        }
        //
        //        StayAreaGroup stayDistrict = mStayAreaListAdapter.getAreaGroup(groupPosition);
        //
        //        if (stayDistrict == null)
        //        {
        //            return null;
        //        }
        //
        //        Observable<Boolean> observable = new Observable<Boolean>()
        //        {
        //            @Override
        //            protected void subscribeActual(Observer<? super Boolean> observer)
        //            {
        //                if (animation == true)
        //                {
        //                    try
        //                    {
        //                        getViewDataBinding().expandableListView.expandGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
        //                        {
        //                            @Override
        //                            public void onAnimationEnd()
        //                            {
        //                                observer.onNext(true);
        //                                observer.onComplete();
        //                            }
        //                        });
        //                    } catch (Exception e)
        //                    {
        //                        Crashlytics.logException(e);
        //                        ExLog.e(e.toString());
        //
        //                        getViewDataBinding().expandableListView.expandGroup(groupPosition);
        //                    }
        //
        //                    // 마지막 리스트 목록은 애니메이션으로 안잡힌다.
        //                    if (groupPosition == mStayAreaListAdapter.getGroupCount() - 1)
        //                    {
        //                        getViewDataBinding().expandableListView.setOnScrollListener(new AbsListView.OnScrollListener()
        //                        {
        //                            @Override
        //                            public void onScrollStateChanged(AbsListView absListView, int scrollState)
        //                            {
        //                                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
        //                                {
        //                                    observer.onNext(true);
        //                                    observer.onComplete();
        //                                }
        //                            }
        //
        //                            @Override
        //                            public void onScroll(AbsListView absListView, int i, int i1, int i2)
        //                            {
        //
        //                            }
        //                        });
        //                    }
        //                } else
        //                {
        //                    getViewDataBinding().expandableListView.expandGroup(groupPosition);
        //
        //                    observer.onNext(true);
        //                    observer.onComplete();
        //                }
        //            }
        //        };
        //
        //        Observable<Boolean> arrowObservable = expandArrowAnimation(getGroupView(groupPosition));
        //
        //        if (arrowObservable == null)
        //        {
        //            arrowObservable = Observable.just(true);
        //        }
        //
        //        return Observable.zip(observable, arrowObservable, new BiFunction<Boolean, Boolean, Boolean>()
        //        {
        //            @Override
        //            public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception
        //            {
        //                mStayAreaListAdapter.setSelectedGroupPosition(groupPosition);
        //
        //                return true;
        //            }
        //        });
    }

    @Override
    public void setSelectedAreaGroup(int groupPosition)
    {
        //        if (getViewDataBinding() == null || mStayAreaListAdapter == null)
        //        {
        //            return;
        //        }
        //
        //        getViewDataBinding().expandableListView.setSelection(groupPosition);
        //
        //        if (mStayAreaListAdapter.getChildren(groupPosition) == null)
        //        {
        //            //            getViewDataBinding().expandableListView.setSelectedGroup(groupPosition);
        //        } else
        //        {
        //            getViewDataBinding().expandableListView.expandGroup(groupPosition);
        //        }
        //
        //        mStayAreaListAdapter.setSelectedGroupPosition(groupPosition);
    }

    private void initToolbar(ActivityStayAreaListDataBinding viewDataBinding)
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
        footerView.setBackgroundResource(R.drawable.layerlist_top_divider);
        AbsListView.LayoutParams footerLayoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 11));
        footerView.setLayoutParams(footerLayoutParams);

        listView.addFooterView(footerView);
    }

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
        //        if (getViewDataBinding() == null)
        //        {
        //            return null;
        //        }
        //
        //        int count = getViewDataBinding().expandableListView.getChildCount();
        //
        //        for (int i = 0; i < count; i++)
        //        {
        //            View childView = getViewDataBinding().expandableListView.getChildAt(i);
        //
        //            if (childView != null)
        //            {
        //                Object tag = childView.getTag();
        //
        //                if (tag != null && tag instanceof Integer == true)
        //                {
        //                    Integer childTag = (Integer) tag;
        //
        //                    if (childTag == groupPosition)
        //                    {
        //                        return childView;
        //                    }
        //                }
        //            }
        //        }

        return null;
    }

}
