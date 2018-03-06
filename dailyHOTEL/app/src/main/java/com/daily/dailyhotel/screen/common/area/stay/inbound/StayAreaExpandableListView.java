package com.daily.dailyhotel.screen.common.area.stay.inbound;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.AreaGroup;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutAreaSubwayTabDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRegionListLocationDataBinding;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

public class StayAreaExpandableListView extends DailyAnimatedExpandableListView
{
    private LayoutRegionListLocationDataBinding mLayoutRegionListLocationDataBinding;
    private LayoutAreaSubwayTabDataBinding mLayoutAreaSubwayTabDataBinding;

    private StayAreaExpandableListAdapter mStayAreaListAdapter;
    private OnStayAreaExpandableListener mAreaExpandableListener;

    public interface OnStayAreaExpandableListener
    {
        void onAroundSearchClick();

        void onAreaGroupClick(int groupPosition);

        void onAreaClick(int groupPosition, Area area);

        void onTabChanged(View view, int position);
    }

    public StayAreaExpandableListView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public StayAreaExpandableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public StayAreaExpandableListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        initHeaderLayout(context);
        initFooterLayout(context);
        initListAdapter(context);

        setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id)
            {
                if (mAreaExpandableListener != null)
                {
                    mAreaExpandableListener.onAreaGroupClick(groupPosition);
                }
                return true;
            }
        });
    }

    private void initHeaderLayout(Context context)
    {
        if (context == null)
        {
            return;
        }

        mLayoutAreaSubwayTabDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_area_subway_tab_data, this, false);
        addHeaderView(mLayoutAreaSubwayTabDataBinding.getRoot());
        mLayoutAreaSubwayTabDataBinding.subwayTabView.setOnTabChangeListener(new StaySubwayTabView.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(View view, int position)
            {
                if (mAreaExpandableListener != null)
                {
                    mAreaExpandableListener.onTabChanged(view, position);
                }
            }
        });

        mLayoutRegionListLocationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_region_list_location_data, this, false);
        addHeaderView(mLayoutRegionListLocationDataBinding.getRoot());
        mLayoutRegionListLocationDataBinding.getRoot().setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mAreaExpandableListener != null)
                {
                    mAreaExpandableListener.onAroundSearchClick();
                }
            }
        });

        mLayoutRegionListLocationDataBinding.myLocationTextView.setText(R.string.label_region_around_stay);
    }

    private void initFooterLayout(Context context)
    {
        if (context == null)
        {
            return;
        }

        View footerView = new View(getContext());
        footerView.setBackgroundResource(R.drawable.layerlist_top_divider);
        AbsListView.LayoutParams footerLayoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 11));
        footerView.setLayoutParams(footerLayoutParams);

        addFooterView(footerView);
    }

    private void initListAdapter(Context context)
    {
        mStayAreaListAdapter = new StayAreaExpandableListAdapter(context);
        mStayAreaListAdapter.setOnChildClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Object tag = view.getTag();

                if (tag == null || tag instanceof Area == false)
                {
                    return;
                }

                Area area = (Area) tag;
                Integer groupPosition = (Integer) view.getTag(view.getId());

                if (groupPosition == null)
                {
                    return;
                }

                if (mAreaExpandableListener != null)
                {
                    mAreaExpandableListener.onAreaClick(groupPosition, area);
                }
            }
        });
    }

    public void setOnAreaExpandableListener(OnStayAreaExpandableListener listener)
    {
        mAreaExpandableListener = listener;
    }

    public void removeTabLayout()
    {
        if (mLayoutAreaSubwayTabDataBinding == null)
        {
            return;
        }

        removeHeaderView(mLayoutAreaSubwayTabDataBinding.getRoot());
    }

    public void setTab(List<Area> areaList)
    {
        if (areaList == null || areaList.size() == 0)
        {
            return;
        }

        mLayoutAreaSubwayTabDataBinding.subwayTabView.clearTab();

        for (Area area : areaList)
        {
            mLayoutAreaSubwayTabDataBinding.subwayTabView.addTab(area.name, area);
        }
    }

    public void setTabSelected(int position)
    {
        if(position < 0)
        {
            return;
        }

        mLayoutAreaSubwayTabDataBinding.subwayTabView.setSelection(position);
    }

    public void setTabletDevice(boolean tablet)
    {
        if (mStayAreaListAdapter == null)
        {
            return;
        }

        mStayAreaListAdapter.setTablet(tablet);
    }

    public void setAreaList(List<? extends AreaGroup> areaList)
    {
        if (areaList == null || areaList.size() == 0)
        {
            return;
        }

        mStayAreaListAdapter.setAreaGroup(areaList);
        setAdapter(mStayAreaListAdapter);
    }

    public void setHeaderLocationText(String text)
    {
        if (mLayoutRegionListLocationDataBinding == null)
        {
            return;
        }

        mLayoutRegionListLocationDataBinding.myLocationTextView.setText(text);
    }

    public void setHeaderLocationTermVisible(boolean visible)
    {
        if (mLayoutRegionListLocationDataBinding == null)
        {
            return;
        }

        mLayoutRegionListLocationDataBinding.locationTermTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation)
    {
        if (groupPosition < 0)
        {
            return null;
        }

        AreaGroup areaGroup = mStayAreaListAdapter.getAreaGroup(groupPosition);

        if (areaGroup == null)
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
                    collapseGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
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
                    collapseGroup(groupPosition);

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
                public Boolean apply(Boolean result) throws Exception
                {
                    mStayAreaListAdapter.setSelectedGroupPosition(-1);
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
                    mStayAreaListAdapter.setSelectedGroupPosition(-1);
                    return true;
                }
            });
        }
    }

    public Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation)
    {
        if (groupPosition < 0)
        {
            return null;
        }

        AreaGroup areaGroup = mStayAreaListAdapter.getAreaGroup(groupPosition);

        if (areaGroup == null)
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
                    try
                    {
                        expandGroupWithAnimation(groupPosition, new DailyAnimatedExpandableListView.OnAnimationListener()
                        {
                            @Override
                            public void onAnimationEnd()
                            {
                                observer.onNext(true);
                                observer.onComplete();
                            }
                        });
                    } catch (Exception e)
                    {
                        Crashlytics.logException(e);
                        ExLog.e(e.toString());

                        expandGroup(groupPosition);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    // 마지막 리스트 목록은 애니메이션으로 안잡힌다.
                    if (groupPosition == mStayAreaListAdapter.getGroupCount() - 1)
                    {
                        setOnScrollListener(new AbsListView.OnScrollListener()
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
                    expandGroup(groupPosition);

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
                mStayAreaListAdapter.setSelectedGroupPosition(groupPosition);

                return true;
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    public void setAreaGroupSelected(int position)
    {
        if (mStayAreaListAdapter == null || position < 0)
        {
            return;
        }

        setSelection(position);

        if (mStayAreaListAdapter.getChildren(position) != null)
        {
            expandGroup(position);
        }

        mStayAreaListAdapter.setSelectedGroupPosition(position);
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
        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            View childView = getChildAt(i);

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
