package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseFragmentDialogView;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.screen.common.area.stay.StayAreaListAdapter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentStayAreaListDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRegionListLocationDataBinding;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAreaFragmentView extends BaseFragmentDialogView<StayAreaFragmentInterface.OnEventListener, FragmentStayAreaListDataBinding>//
    implements StayAreaFragmentInterface.ViewInterface
{
    private LayoutRegionListLocationDataBinding mLayoutRegionListLocationDataBinding;
    private StayAreaListAdapter mStayAreaListAdapter;

    public StayAreaFragmentView(StayAreaFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initHeaderNFooterLayout(viewDataBinding);

        viewDataBinding.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id)
            {
                getEventListener().onAreaGroupClick(groupPosition);
                return true;
            }
        });
    }

    @Override
    public void setAreaList(List<StayAreaGroup> areaList)
    {
        if (getViewDataBinding() == null || areaList == null || areaList.size() == 0)
        {
            return;
        }

        if (mStayAreaListAdapter == null)
        {
            mStayAreaListAdapter = new StayAreaListAdapter(getContext());

            mStayAreaListAdapter.setTablet(isTabletDevice());
            mStayAreaListAdapter.setOnChildClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Object tag = view.getTag();

                    if (tag == null)
                    {
                        return;
                    }

                    if (tag instanceof StayArea == false)
                    {
                        return;
                    }

                    StayArea stayTown = (StayArea) tag;
                    Integer groupPosition = (Integer) view.getTag(view.getId());

                    if (groupPosition == null)
                    {
                        return;
                    }

                    getEventListener().onAreaClick(groupPosition, stayTown);
                }
            });
        }

        mStayAreaListAdapter.setData(areaList);
        getViewDataBinding().expandableListView.setAdapter(mStayAreaListAdapter);
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
    public Completable collapseGroupWithAnimation(int groupPosition, boolean animation)
    {
        if (getViewDataBinding() == null || groupPosition < 0)
        {
            return null;
        }

        StayAreaGroup stayDistrict = mStayAreaListAdapter.getAreaGroup(groupPosition);

        if (stayDistrict == null)
        {
            return null;
        }

        Completable collapseGroupCompletable = new Completable()
        {
            @Override
            protected void subscribeActual(CompletableObserver observer)
            {
                if (animation == true)
                {
                    getViewDataBinding().expandableListView.collapseGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
                    {
                        @Override
                        public void onAnimationEnd()
                        {
                            observer.onComplete();
                        }
                    });
                } else
                {
                    getViewDataBinding().expandableListView.collapseGroup(groupPosition);

                    observer.onComplete();
                }
            }
        };

        Completable collapseArrowCompletable = collapseArrowAnimation(getGroupView(groupPosition), animation);

        if (collapseArrowCompletable == null)
        {
            return collapseGroupCompletable.doOnComplete(() -> mStayAreaListAdapter.setSelectedGroupPosition(-1)).subscribeOn(AndroidSchedulers.mainThread());
        } else
        {
            return Completable.mergeArray(collapseGroupCompletable, collapseArrowCompletable).doOnComplete(() -> mStayAreaListAdapter.setSelectedGroupPosition(-1)) .subscribeOn(AndroidSchedulers.mainThread());
        }
    }

    @Override
    public Completable expandGroupWithAnimation(int groupPosition, boolean animation)
    {
        if (getViewDataBinding() == null || groupPosition < 0)
        {
            return null;
        }

        StayAreaGroup stayDistrict = mStayAreaListAdapter.getAreaGroup(groupPosition);

        if (stayDistrict == null)
        {
            return null;
        }

        Completable expandGroupCompletable = new Completable()
        {
            @Override
            protected void subscribeActual(CompletableObserver observer)
            {
                if (animation == true)
                {
                    try
                    {
                        getViewDataBinding().expandableListView.expandGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
                        {
                            @Override
                            public void onAnimationEnd()
                            {
                                observer.onComplete();
                            }
                        });
                    } catch (Exception e)
                    {
                        Crashlytics.logException(e);
                        ExLog.e(e.toString());

                        getViewDataBinding().expandableListView.expandGroup(groupPosition);
                    }

                    // 마지막 리스트 목록은 애니메이션으로 안잡힌다.
                    if (groupPosition == mStayAreaListAdapter.getGroupCount() - 1)
                    {
                        getViewDataBinding().expandableListView.setOnScrollListener(new AbsListView.OnScrollListener()
                        {
                            @Override
                            public void onScrollStateChanged(AbsListView absListView, int scrollState)
                            {
                                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                                {
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

                    observer.onComplete();
                }
            }
        };

        Completable expandArrowCompletable = expandArrowAnimation(getGroupView(groupPosition));

        if (expandArrowCompletable == null)
        {
            return expandGroupCompletable.doOnComplete(() -> mStayAreaListAdapter.setSelectedGroupPosition(groupPosition)).subscribeOn(AndroidSchedulers.mainThread());
        } else
        {
            return Completable.mergeArray(expandGroupCompletable, expandArrowCompletable).doOnComplete(() -> mStayAreaListAdapter.setSelectedGroupPosition(groupPosition)).subscribeOn(AndroidSchedulers.mainThread());
        }
    }

    @Override
    public void setSelectedAreaGroup(int groupPosition)
    {
        if (getViewDataBinding() == null || mStayAreaListAdapter == null)
        {
            return;
        }

        getViewDataBinding().expandableListView.setSelection(groupPosition);

        if (mStayAreaListAdapter.getChildren(groupPosition) == null)
        {
            //            getViewDataBinding().expandableListView.setSelectedGroup(groupPosition);
        } else
        {
            getViewDataBinding().expandableListView.expandGroup(groupPosition);
        }

        mStayAreaListAdapter.setSelectedGroupPosition(groupPosition);
    }

    private void initHeaderNFooterLayout(FragmentStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        // 헤더
        mLayoutRegionListLocationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_region_list_location_data, viewDataBinding.expandableListView, false);
        viewDataBinding.expandableListView.addHeaderView(mLayoutRegionListLocationDataBinding.getRoot());
        mLayoutRegionListLocationDataBinding.getRoot().setOnClickListener(v -> getEventListener().onAroundSearchClick());

        mLayoutRegionListLocationDataBinding.myLocationTextView.setText(R.string.label_region_around_stay);

        // 푸터
        View footerView = new View(getContext());
        footerView.setBackgroundResource(R.drawable.layerlist_top_divider);
        AbsListView.LayoutParams footerLayoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 11));
        footerView.setLayoutParams(footerLayoutParams);

        viewDataBinding.expandableListView.addFooterView(footerView);
    }

    private Completable expandArrowAnimation(View view)
    {
        if (view == null)
        {
            return null;
        }

        final ImageView imageView = view.findViewById(R.id.arrowImageView);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0f, -180f);
        objectAnimator.setDuration(250);

        Completable observable = new Completable()
        {
            @Override
            protected void subscribeActual(CompletableObserver observer)
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

    private Completable collapseArrowAnimation(View view, boolean animation)
    {
        if (view == null)
        {
            return null;
        }

        Completable completable;
        final ImageView imageView = view.findViewById(R.id.arrowImageView);

        if (animation == true)
        {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0, 180f);
            objectAnimator.setDuration(250);

            completable = new Completable()
            {
                @Override
                protected void subscribeActual(CompletableObserver observer)
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

            completable = Completable.complete();
        }

        return completable;
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
