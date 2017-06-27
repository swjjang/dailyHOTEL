package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDetailDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.DialogStayOutboundMapDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail02DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail03DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail04DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail05DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailAmenityDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailAmenityMoreDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailConcierageDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailInformationDataBinding;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.TextTransition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class StayOutboundDetailView extends BaseDialogView<StayOutboundDetailView.OnEventListener, ActivityStayOutboundDetailDataBinding>//
    implements StayOutboundDetailViewInterface, View.OnClickListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener
{
    private static final int ANIMATION_DEALY = 250;

    private DailyToolbarLayout mDailyToolbarLayout;

    private StayOutboundDetailEmptyView mStayOutboundDetailEmptyView;

    private StayOutboundDetailImageViewPagerAdapter mImageViewPagerAdapter;
    private StayOutboundDetailRoomListAdapter mRoomTypeListAdapter;

    private AnimatorSet mRoomAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onShareClick();

        void onShareKakaoClick();

        void onShareSmsClick();

        void onImageClick(int position);

        void onImageSelected(int position);

        void onCalendarClick();

        void onPeopleClick();

        void onMapClick();

        void onClipAddressClick(String address);

        void onNavigatorClick();

        void onConciergeClick();

        void onHideRoomListClick(boolean animation);

        void onActionButtonClick();

        void onAmenityMoreClick();

        void onPriceTypeClick(StayOutboundDetailPresenter.PriceType priceType);

        void onConciergeFaqClick();

        void onConciergeHappyTalkClick();

        void onConciergeCallClick();

        void onShareMapClick();

        void onRoomClick(StayOutboundRoom stayOutboundRoom);
    }

    public StayOutboundDetailView(BaseActivity baseActivity, StayOutboundDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (getViewDataBinding().scrollLayout.getChildCount() < 2)
                {
                    mDailyToolbarLayout.setToolbarVisibility(false, false);
                    return;
                }

                View titleLayout = getViewDataBinding().scrollLayout.getChildAt(1);
                final int TOOLBAR_HEIGHT = getDimensionPixelSize(R.dimen.toolbar_height);

                if (titleLayout.getY() - TOOLBAR_HEIGHT > scrollY)
                {
                    mDailyToolbarLayout.setToolbarVisibility(false, true);
                } else
                {
                    mDailyToolbarLayout.setToolbarVisibility(true, true);
                }
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge));

        mImageViewPagerAdapter = new StayOutboundDetailImageViewPagerAdapter(getContext());
        viewDataBinding.imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        viewDataBinding.viewpagerIndicator.setViewPager(viewDataBinding.imageLoopViewPager);

        viewDataBinding.imageLoopViewPager.setOnPageChangeListener(this);
        viewDataBinding.viewpagerIndicator.setOnPageChangeListener(this);

        ViewGroup.LayoutParams layoutParams = viewDataBinding.imageLoopViewPager.getLayoutParams();
        layoutParams.height = ScreenUtils.getDetailScreenImageLayoutHeight(getContext());
        viewDataBinding.imageLoopViewPager.setLayoutParams(layoutParams);

        // 객실 초기화
        viewDataBinding.productTypeTextView.setText(R.string.act_hotel_search_room);
        viewDataBinding.productTypeTextView.setClickable(true);
        viewDataBinding.priceOptionLayout.setVisibility(View.GONE);

        viewDataBinding.productTypeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.productTypeRecyclerView, getColor(R.color.default_over_scroll_edge));
        viewDataBinding.productTypeLayout.setVisibility(View.INVISIBLE);

        viewDataBinding.productTypeBackgroundView.setOnClickListener(this);
        viewDataBinding.closeView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.backView:
                getEventListener().onBackClick();
                break;

            case R.id.menu1View:
            case R.id.shareView:
                getEventListener().onShareClick();
                break;

            case R.id.closeView:
            case R.id.productTypeBackgroundView:
                getEventListener().onHideRoomListClick(true);
                break;

            case R.id.dateLayout:
                getEventListener().onCalendarClick();
                break;

            case R.id.peopleLayout:
                getEventListener().onPeopleClick();
                break;

            case R.id.bookingTextView:
                getEventListener().onActionButtonClick();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        getEventListener().onImageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
    {
        switch (checkedId)
        {
            case R.id.averageRadioButton:
                getEventListener().onPriceTypeClick(StayOutboundDetailPresenter.PriceType.AVERAGE);
                break;

            case R.id.totalRadioButton:
                getEventListener().onPriceTypeClick(StayOutboundDetailPresenter.PriceType.TOTAL);
                break;
        }
    }

    @Override
    public Observable<Boolean> showRoomList(boolean animation)
    {
        if (getViewDataBinding() == null && mRoomAnimatorSet != null && mRoomAnimatorSet.isRunning() == true)
        {
            return null;
        }

        Observable<Boolean> observable;

        if (animation == true)
        {
            observable = new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    final float fromAnimationY = getViewDataBinding().bottomLayout.getTop();

                    // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
                    int height = getViewDataBinding().productTypeLayout.getHeight();
                    int toolbarHeight = getDimensionPixelSize(R.dimen.toolbar_height);
                    int maxHeight = getViewDataBinding().getRoot().getHeight() - getViewDataBinding().bottomLayout.getHeight() - toolbarHeight;

                    float toAnimationY = fromAnimationY - Math.min(height, maxHeight);

                    int startTransY = ScreenUtils.dpToPx(getContext(), height);
                    getViewDataBinding().productTypeLayout.setTranslationY(startTransY);

                    ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().productTypeLayout, "y", fromAnimationY, toAnimationY);
                    ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().productTypeBackgroundView, "alpha", 0.0f, 1.0f);

                    mRoomAnimatorSet = new AnimatorSet();
                    mRoomAnimatorSet.playTogether(transObjectAnimator, alphaObjectAnimator);
                    mRoomAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    mRoomAnimatorSet.setDuration(ANIMATION_DEALY);

                    mRoomAnimatorSet.addListener(new Animator.AnimatorListener()
                    {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {
                            getViewDataBinding().productTypeBackgroundView.setVisibility(View.VISIBLE);
                            getViewDataBinding().productTypeLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            mRoomAnimatorSet.removeAllListeners();
                            mRoomAnimatorSet = null;

                            observer.onNext(true);
                            observer.onComplete();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation)
                        {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation)
                        {

                        }
                    });

                    mRoomAnimatorSet.start();
                }
            };
        } else
        {
            observable = new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    getViewDataBinding().productTypeBackgroundView.setVisibility(View.VISIBLE);
                    getViewDataBinding().productTypeLayout.setVisibility(View.VISIBLE);

                    observer.onNext(true);
                    observer.onComplete();
                }
            };

        }

        return observable;
    }

    @Override
    public Observable<Boolean> hideRoomList(boolean animation)
    {
        if (getViewDataBinding() == null && mRoomAnimatorSet != null && mRoomAnimatorSet.isRunning() == true)
        {
            return null;
        }

        Observable<Boolean> observable;

        if (animation == true)
        {
            observable = new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    final float y = getViewDataBinding().productTypeLayout.getY();

                    ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().productTypeLayout, "y", y, getViewDataBinding().bottomLayout.getTop());
                    ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().productTypeBackgroundView, "alpha", 1.0f, 0.0f);

                    mRoomAnimatorSet = new AnimatorSet();
                    mRoomAnimatorSet.playTogether(transObjectAnimator, alphaObjectAnimator);
                    mRoomAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    mRoomAnimatorSet.setDuration(ANIMATION_DEALY);

                    mRoomAnimatorSet.addListener(new Animator.AnimatorListener()
                    {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            mRoomAnimatorSet.removeAllListeners();
                            mRoomAnimatorSet = null;

                            getViewDataBinding().productTypeBackgroundView.setVisibility(View.GONE);
                            getViewDataBinding().productTypeLayout.setVisibility(View.INVISIBLE);

                            observer.onNext(true);
                            observer.onComplete();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation)
                        {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation)
                        {
                        }
                    });

                    mRoomAnimatorSet.start();
                }
            };
        } else
        {
            observable = new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    getViewDataBinding().productTypeBackgroundView.setVisibility(View.GONE);
                    getViewDataBinding().productTypeLayout.setVisibility(View.INVISIBLE);

                    observer.onNext(true);
                    observer.onComplete();
                }
            };
        }

        return observable;
    }

    @Override
    public void setStayDetail(StayBookDateTime stayBookDateTime, People people, StayOutboundDetail stayOutboundDetail)
    {
        if (getViewDataBinding() == null || stayBookDateTime == null || stayOutboundDetail == null)
        {
            return;
        }

        getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);

        setImageList(stayOutboundDetail.getImageList());

        getViewDataBinding().scrollLayout.removeAllViews();

        mStayOutboundDetailEmptyView = null;

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        // 이미지 상단에 빈화면 넣기
        addEmptyView(getContext(), getViewDataBinding().scrollLayout);

        // 호텔 등급과 이름 / 체크인 체크아웃
        setTitleView(layoutInflater, getViewDataBinding().scrollLayout, stayBookDateTime, people, stayOutboundDetail);

        // 주소 및 맵
        setAddressView(layoutInflater, getViewDataBinding().scrollLayout, stayBookDateTime, stayOutboundDetail);

        // Amenity
        SparseArray<String> stringSparseArray = stayOutboundDetail.getAmenityList();

        if (stringSparseArray != null && stringSparseArray.size() > 0)
        {
            setAmenitiesView(layoutInflater, getViewDataBinding().scrollLayout, stringSparseArray);
        }

        // 베네핏이 없으면 정보화면의 상단 라인으로 대체한다.
        View view = new View(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 1));
        view.setLayoutParams(layoutParams);
        view.setBackgroundResource(R.color.default_line_cf0f0f0);
        getViewDataBinding().scrollLayout.addView(view);

        // 정보 화면
        setInformationView(layoutInflater, getViewDataBinding().scrollLayout, stayOutboundDetail.getInformationMap());

        // 카카오톡 문의
        setConciergeView(layoutInflater, getViewDataBinding().scrollLayout);

        // 객실
        try
        {
            if (stayBookDateTime.getNights() > 1)
            {
                getViewDataBinding().priceRadioGroup.check(R.id.averageRadioButton);
                getViewDataBinding().priceOptionLayout.setVisibility(View.VISIBLE);
                getViewDataBinding().priceRadioGroup.setOnCheckedChangeListener(this);
            } else
            {
                getViewDataBinding().priceOptionLayout.setVisibility(View.GONE);
                getViewDataBinding().priceRadioGroup.setOnCheckedChangeListener(null);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            getViewDataBinding().priceOptionLayout.setVisibility(View.GONE);
            getViewDataBinding().priceRadioGroup.setOnCheckedChangeListener(null);
        }

        // 객실 세팅
        setRoomList(stayBookDateTime, stayOutboundDetail.getRoomList());
    }


    @TargetApi(value = 21)
    @Override
    public Observable<Boolean> getSharedElementTransition()
    {
        TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
        Transition inTextTransition = new TextTransition(getColor(R.color.white), getColor(R.color.default_text_c323232)//
            , 17, 18, new LinearInterpolator());
        inTextTransition.addTarget(getString(R.string.transition_place_name));
        inTransitionSet.addTransition(inTextTransition);

        Transition inBottomAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
        inBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
        inTransitionSet.addTransition(inBottomAlphaTransition);

        Transition inTopAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
        inTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
        inTransitionSet.addTransition(inTopAlphaTransition);

        getWindow().setSharedElementEnterTransition(inTransitionSet);

        TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
        Transition outTextTransition = new TextTransition(getColor(R.color.default_text_c323232), getColor(R.color.white)//
            , 18, 17, new LinearInterpolator());
        outTextTransition.addTarget(getString(R.string.transition_place_name));
        outTransitionSet.addTransition(outTextTransition);

        Transition outBottomAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
        outBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
        outTransitionSet.addTransition(outBottomAlphaTransition);

        Transition outTopAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
        outTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
        outTransitionSet.addTransition(outTopAlphaTransition);

        outTransitionSet.setDuration(200);

        getWindow().setSharedElementReturnTransition(outTransitionSet);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener()
                {
                    @Override
                    public void onTransitionStart(Transition transition)
                    {
                    }

                    @Override
                    public void onTransitionEnd(Transition transition)
                    {
                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onTransitionCancel(Transition transition)
                    {
                    }

                    @Override
                    public void onTransitionPause(Transition transition)
                    {
                    }

                    @Override
                    public void onTransitionResume(Transition transition)
                    {
                    }
                });
            }
        };

        return observable;
    }

    @Override
    public void setInitializedImage(String url)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            setViewPagerLineIndicatorVisible(false);
            return;
        }

        setViewPagerLineIndicatorVisible(true);

        if (mImageViewPagerAdapter == null)
        {
            mImageViewPagerAdapter = new StayOutboundDetailImageViewPagerAdapter(getContext());
        }

        StayOutboundDetailImage detailImage = new StayOutboundDetailImage();
        ImageMap imageMap = new ImageMap();
        imageMap.smallUrl = url;
        imageMap.mediumUrl = url;
        imageMap.bigUrl = url;
        detailImage.setImageMap(imageMap);

        List<StayOutboundDetailImage> imageList = new ArrayList<>();
        imageList.add(detailImage);

        mImageViewPagerAdapter.setData(imageList);
        getViewDataBinding().imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        getViewDataBinding().viewpagerIndicator.setViewPager(getViewDataBinding().imageLoopViewPager);
    }

    @Override
    public void setInitializedTransLayout(String name, String url, boolean callFromMap)
    {
        if (getViewDataBinding() == null || DailyTextUtils.isTextEmpty(name, url) == true)
        {
            return;
        }

        setInitializedImage(url);


        getViewDataBinding().transImageView.setImageURI(Uri.parse(url));
        getViewDataBinding().transNameTextView.setText(name);

        if (callFromMap == true)
        {
            getViewDataBinding().transGradientView.setBackgroundResource(R.color.black_a28);
        }

        if (mImageViewPagerAdapter == null)
        {
            mImageViewPagerAdapter = new StayOutboundDetailImageViewPagerAdapter(getContext());
        }

        StayOutboundDetailImage detailImage = new StayOutboundDetailImage();
        ImageMap imageMap = new ImageMap();
        imageMap.smallUrl = url;
        imageMap.mediumUrl = url;
        imageMap.bigUrl = url;
        detailImage.setImageMap(imageMap);

        List<StayOutboundDetailImage> imageList = new ArrayList<>();
        imageList.add(detailImage);

        mImageViewPagerAdapter.setData(imageList);
        getViewDataBinding().imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        getViewDataBinding().viewpagerIndicator.setViewPager(getViewDataBinding().imageLoopViewPager);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setSharedElementTransitionEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (enabled == true)
        {
            getViewDataBinding().transImageView.setVisibility(View.VISIBLE);
            getViewDataBinding().transGradientView.setVisibility(View.VISIBLE);
            getViewDataBinding().transGradientTopView.setVisibility(View.VISIBLE);
            getViewDataBinding().transTitleLayout.setVisibility(View.VISIBLE);

            getViewDataBinding().transImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDetailScreenImageLayoutHeight(getContext())));
            getViewDataBinding().transImageView.setTransitionName(getString(R.string.transition_place_image));

            getViewDataBinding().transGradientView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDetailScreenImageLayoutHeight(getContext())));
            getViewDataBinding().transGradientView.setTransitionName(getString(R.string.transition_gradient_bottom_view));
            getViewDataBinding().transGradientView.setBackground(makeShaderFactory());

            getViewDataBinding().transGradientTopView.setTransitionName(getString(R.string.transition_gradient_top_view));
            getViewDataBinding().transNameTextView.setTransitionName(getString(R.string.transition_place_name));
        } else
        {
            getViewDataBinding().transImageView.setVisibility(View.GONE);
            getViewDataBinding().transGradientView.setVisibility(View.GONE);
            getViewDataBinding().transGradientTopView.setVisibility(View.GONE);
            getViewDataBinding().transTitleLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setBottomButtonLayout(int status)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (status)
        {
            case StayOutboundDetailPresenter.STATUS_NONE:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);
                break;
            }

            case StayOutboundDetailPresenter.STATUS_ROOM_LIST:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.act_hotel_search_room);
                break;
            }

            case StayOutboundDetailPresenter.STATUS_BOOKING:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.act_hotel_booking);
                break;
            }

            case StayOutboundDetailPresenter.STATUS_SOLD_OUT:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.GONE);
                getViewDataBinding().soldoutTextView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void setDetailImageCaption(String caption)
    {
        if (DailyTextUtils.isTextEmpty(caption) == false)
        {
            getViewDataBinding().descriptionTextView.setVisibility(View.VISIBLE);
            getViewDataBinding().descriptionTextView.setText(caption);
        } else
        {
            getViewDataBinding().descriptionTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setPriceType(StayOutboundDetailPresenter.PriceType priceType)
    {
        if (mRoomTypeListAdapter == null)
        {
            return;
        }

        mRoomTypeListAdapter.setPriceType(priceType);
        mRoomTypeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPeopleText(String peopleText)
    {

    }

    @Override
    public void setCalendarText(String peopleText)
    {

    }

    @Override
    public void showConciergeDialog(Dialog.OnDismissListener listener)
    {
        DialogConciergeDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_concierge_data, null, false);

        // 버튼
        dataBinding.contactUs02Layout.setVisibility(View.GONE);

        dataBinding.contactUs01TextView.setText(R.string.frag_faqs);
        dataBinding.contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0);

        dataBinding.contactUs01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeFaqClick();
            }
        });

        dataBinding.kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeHappyTalkClick();
            }
        });

        dataBinding.callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeCallClick();
            }
        });

        dataBinding.closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    @Override
    public void showNavigatorDialog(Dialog.OnDismissListener listener)
    {
        DialogStayOutboundMapDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_stay_outbound_map_data, null, false);

        dataBinding.googleMapTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onShareMapClick();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    @Override
    public void showShareDialog(Dialog.OnDismissListener listener)
    {
        DialogShareDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_share_data, null, false);

        dataBinding.kakaoShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onShareKakaoClick();
            }
        });

        dataBinding.smsShareLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onShareSmsClick();
            }
        });

        dataBinding.closeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    @Override
    public void scrollTop()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().nestedScrollView.fullScroll(View.FOCUS_UP);
    }

    private void initToolbar(ActivityStayOutboundDetailDataBinding viewDataBinding)
    {
        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));
        mDailyToolbarLayout.initToolbar(null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        }, false);

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_share_01_black, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(this);
        mDailyToolbarLayout.setToolbarVisibility(false, false);

        viewDataBinding.backView.setOnClickListener(this);
        viewDataBinding.shareView.setOnClickListener(this);
    }

    private void setImageList(List<StayOutboundDetailImage> imageList)
    {
        if (getViewDataBinding() == null || imageList == null)
        {
            return;
        }

        if (imageList.size() == 1)
        {
            setViewPagerLineIndicatorVisible(false);
        } else
        {
            setViewPagerLineIndicatorVisible(true);
        }

        setDetailImageCaption(imageList.get(0).caption);

        if (mImageViewPagerAdapter == null)
        {
            mImageViewPagerAdapter = new StayOutboundDetailImageViewPagerAdapter(getContext());
        }

        mImageViewPagerAdapter.setData(imageList);
        getViewDataBinding().imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        getViewDataBinding().viewpagerIndicator.setViewPager(getViewDataBinding().imageLoopViewPager);
        mImageViewPagerAdapter.notifyDataSetChanged();
    }

    /**
     * @param context
     * @param viewGroup
     */
    private void addEmptyView(Context context, ViewGroup viewGroup)
    {
        if (context == null || viewGroup == null)
        {
            return;
        }

        mStayOutboundDetailEmptyView = new StayOutboundDetailEmptyView(context, new StayOutboundDetailEmptyView.OnEventListener()
        {
            @Override
            public void onStopMove(MotionEvent event)
            {
                getViewDataBinding().nestedScrollView.setScrollingEnabled(false);

                try
                {
                    getViewDataBinding().imageLoopViewPager.onTouchEvent(event);
                } catch (Exception e)
                {
                }
            }

            @Override
            public void onHorizontalMove(MotionEvent event)
            {
                try
                {
                    getViewDataBinding().imageLoopViewPager.onTouchEvent(event);
                } catch (Exception e)
                {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    event.setLocation(getViewDataBinding().imageLoopViewPager.getScrollX(), getViewDataBinding().imageLoopViewPager.getScrollY());
                    getViewDataBinding().imageLoopViewPager.onTouchEvent(event);
                }
            }

            @Override
            public void onVerticalMove(MotionEvent event)
            {
                getViewDataBinding().nestedScrollView.setScrollingEnabled(true);
            }

            @Override
            public void onCancelMove(MotionEvent event)
            {
                try
                {
                    getViewDataBinding().imageLoopViewPager.onTouchEvent(event);
                } catch (Exception e)
                {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    event.setLocation(getViewDataBinding().imageLoopViewPager.getScrollX(), getViewDataBinding().imageLoopViewPager.getScrollY());
                    getViewDataBinding().imageLoopViewPager.onTouchEvent(event);
                }

                getViewDataBinding().nestedScrollView.setScrollingEnabled(true);
            }

            @Override
            public void onImageClick()
            {
                getEventListener().onImageClick(getViewDataBinding().imageLoopViewPager.getCurrentItem());
            }
        });

        mStayOutboundDetailEmptyView.setContentView(R.layout.layout_stay_outbound_detail_01_data, viewGroup, true);
    }

    /**
     * 호텔 등급 및 이름
     *
     * @param layoutInflater
     * @param viewGroup
     * @param stayBookDateTime
     * @param stayOutboundDetail
     */
    private void setTitleView(LayoutInflater layoutInflater, ViewGroup viewGroup//
        , StayBookDateTime stayBookDateTime, People people, StayOutboundDetail stayOutboundDetail)
    {
        if (layoutInflater == null || viewGroup == null || stayBookDateTime == null || stayOutboundDetail == null)
        {
            return;
        }

        LayoutStayOutboundDetail02DataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_02_data, viewGroup, true);

        // 등급
        viewDataBinding.gradeTextView.setVisibility(View.VISIBLE);
        viewDataBinding.gradeTextView.setText(getString(R.string.label_stay_outbound_detail_grade, stayOutboundDetail.grade));
        viewDataBinding.gradeTextView.setBackgroundResource(R.color.default_background_c929292);

        // 호텔명
        viewDataBinding.nameTextView.setText(stayOutboundDetail.name);
        viewDataBinding.nameEngTextView.setText("(" + stayOutboundDetail.nameEng + ")");

        // tripAdvisor
        if (stayOutboundDetail.tripAdvisorRating == 0.0f)
        {
            viewDataBinding.tripAdvisorLayout.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.tripAdvisorLayout.setVisibility(View.VISIBLE);

            viewDataBinding.tripAdvisorRatingBar.setRating(stayOutboundDetail.tripAdvisorRating);
            viewDataBinding.tripAdvisorRatingTextView.setText(getString(R.string.label_stay_outbound_tripadvisor_rating, Float.toString(stayOutboundDetail.tripAdvisorRating)));

            // 별등급이 기본이 5개 이기 때문에 빈공간에도 내용이 존재한다.
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewDataBinding.tripAdvisorRatingTextView.getLayoutParams();
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 3) - ScreenUtils.dpToPx(getContext(), (5 - (int) Math.ceil(stayOutboundDetail.tripAdvisorRating)) * 10);
            viewDataBinding.tripAdvisorRatingTextView.setLayoutParams(layoutParams);
        }

        // 날짜, 인원
        try
        {
            String dateFormat = String.format(Locale.KOREA, "%s - %s, %s", stayBookDateTime.getCheckInDateTime("M.d(EEE)"), stayBookDateTime.getCheckOutDateTime("M.d(EEE)"), getString(R.string.label_nights, stayBookDateTime.getNights()));

            viewDataBinding.dateTextView.setText(dateFormat);
            viewDataBinding.peopleTextView.setText(people.toShortString(getContext()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        viewDataBinding.dateLayout.setOnClickListener(this);
        viewDataBinding.peopleLayout.setOnClickListener(this);
    }

    /**
     * 호텔 주소 및 맵
     *
     * @param layoutInflater
     * @param viewGroup
     * @param stayBookDateTime
     * @param stayOutboundDetail
     */
    private void setAddressView(LayoutInflater layoutInflater, ViewGroup viewGroup//
        , StayBookDateTime stayBookDateTime, StayOutboundDetail stayOutboundDetail)
    {
        if (layoutInflater == null || viewGroup == null || stayBookDateTime == null || stayOutboundDetail == null)
        {
            return;
        }

        LayoutStayOutboundDetail03DataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_03_data, getViewDataBinding().scrollLayout, true);

        // 주소지
        viewDataBinding.addressTextView.setText(stayOutboundDetail.address);

        // 주소 복사
        viewDataBinding.copyAddressView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onClipAddressClick(stayOutboundDetail.address);
            }
        });

        // 길찾기
        viewDataBinding.navigatorView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onNavigatorClick();
            }
        });

        viewDataBinding.mapImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onMapClick();
            }
        });
    }

    /**
     * 편의시설
     *
     * @param layoutInflater
     * @param viewGroup
     * @param stringSparseArray
     * @return
     */
    private void setAmenitiesView(LayoutInflater layoutInflater, ViewGroup viewGroup, SparseArray<String> stringSparseArray)
    {
        if (layoutInflater == null || viewGroup == null || stringSparseArray == null)
        {
            return;
        }

        final int GRID_COLUMN_COUNT = 5;

        LayoutStayOutboundDetailAmenityDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_amenity_data, getViewDataBinding().scrollLayout, true);

        viewDataBinding.amenitiesGridLayout.removeAllViews();

        if (stringSparseArray.size() == 0)
        {
            viewDataBinding.amenitiesGridLayout.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.amenitiesGridLayout.setVisibility(View.VISIBLE);
        }

        // 화면에서 정한 5개를 미리 보여주고 그외는 더보기로 보여준다.
        final StayOutboundDetail.Amenity[] DEFAULT_AMENITIES = {StayOutboundDetail.Amenity.POOL//
            , StayOutboundDetail.Amenity.FITNESS, StayOutboundDetail.Amenity.FRONT24//
            , StayOutboundDetail.Amenity.SAUNA, StayOutboundDetail.Amenity.KIDS_PLAY_ROOM};
        boolean hasNextLine = true;

        // 줄수가 2개 이상인지 검사
        for (StayOutboundDetail.Amenity amenity : DEFAULT_AMENITIES)
        {
            if (stringSparseArray.get(amenity.getIndex(), null) == null)
            {
                hasNextLine = false;
                break;
            }
        }

        // Amenity 추가
        for (StayOutboundDetail.Amenity amenity : DEFAULT_AMENITIES)
        {
            if (stringSparseArray.get(amenity.getIndex(), null) != null)
            {
                viewDataBinding.amenitiesGridLayout.addView(getAmenityView(getContext(), amenity, stringSparseArray.get(amenity.getIndex()), hasNextLine));
            }
        }

        // 더보기가 존재하는 경우
        if (viewDataBinding.amenitiesGridLayout.getChildCount() < stringSparseArray.size())
        {
            View moreView = getAmenityMoreView(getContext(), layoutInflater, stringSparseArray.size() - viewDataBinding.amenitiesGridLayout.getChildCount(), false);
            viewDataBinding.amenitiesGridLayout.addView(moreView);
            moreView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onAmenityMoreClick();
                }
            });
        }

        int columnCount = viewDataBinding.amenitiesGridLayout.getChildCount() % GRID_COLUMN_COUNT;

        if (columnCount != 0)
        {
            int addEmptyViewCount = GRID_COLUMN_COUNT - columnCount;
            for (int i = 0; i < addEmptyViewCount; i++)
            {
                viewDataBinding.amenitiesGridLayout.addView(getAmenityView(getContext(), StayOutboundDetail.Amenity.NONE, null, false));
            }
        }
    }

    private DailyTextView getAmenityView(Context context, StayOutboundDetail.Amenity amenity, String amenityName, boolean hasNextLine)
    {
        DailyTextView dailyTextView = new DailyTextView(context);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(getColorStateList(R.color.default_text_c323232));
        dailyTextView.setText(amenityName);
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, amenity.getImageResId(), 0, 0);
        dailyTextView.setDrawableVectorTint(R.color.default_background_c454545);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (hasNextLine == true)
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 15));
        } else
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 2));
        }

        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    private View getAmenityMoreView(Context context, LayoutInflater layoutInflater, int amenityCount, boolean hasNextLine)
    {
        LayoutStayOutboundDetailAmenityMoreDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_amenity_more_data, null, false);

        viewDataBinding.moreTextView.setText("+" + amenityCount);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ScreenUtils.dpToPx(context, 60);
        layoutParams.setGravity(Gravity.CENTER_HORIZONTAL);
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (hasNextLine == true)
        {
            viewDataBinding.getRoot().setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 15));
        } else
        {
            viewDataBinding.getRoot().setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 2));
        }

        viewDataBinding.getRoot().setLayoutParams(layoutParams);

        return viewDataBinding.getRoot();
    }

    /**
     * 상세 스테이 정보
     *
     * @param layoutInflater
     * @param viewGroup
     * @param informationMap
     */
    private void setInformationView(LayoutInflater layoutInflater, ViewGroup viewGroup, LinkedHashMap<String, List<String>> informationMap)
    {
        if (layoutInflater == null || viewGroup == null || informationMap == null)
        {
            return;
        }

        LayoutStayOutboundDetail04DataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_04_data, viewGroup, true);

        Iterator<Map.Entry<String, List<String>>> iterator = informationMap.entrySet().iterator();

        while (iterator.hasNext() == true)
        {
            Map.Entry<String, List<String>> entry = iterator.next();

            if (entry == null)
            {
                continue;
            }

            setInformationView(layoutInflater, viewDataBinding.informationLayout, entry);
        }
    }

    private void setInformationView(LayoutInflater layoutInflater, ViewGroup viewGroup, Map.Entry<String, List<String>> information)
    {
        if (layoutInflater == null || viewGroup == null || information == null)
        {
            return;
        }

        LayoutStayOutboundDetail05DataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_05_data, viewGroup, true);

        viewDataBinding.titleTextView.setText(information.getKey());

        List<String> informationList = information.getValue();

        if (informationList != null && informationList.size() > 0)
        {
            for (String text : informationList)
            {
                if (DailyTextUtils.isTextEmpty(text) == true)
                {
                    continue;
                }

                LayoutStayOutboundDetailInformationDataBinding detailInformationDataBinding = DataBindingUtil.inflate(layoutInflater//
                    , R.layout.layout_stay_outbound_detail_information_data, viewDataBinding.informationLayout, true);

                detailInformationDataBinding.textView.setText(Html.fromHtml(text));
            }
        }
    }

    /**
     * 문의하기
     *
     * @param layoutInflater
     * @param viewGroup
     */
    private void setConciergeView(LayoutInflater layoutInflater, ViewGroup viewGroup)
    {
        if (layoutInflater == null || viewGroup == null)
        {
            return;
        }

        LayoutStayOutboundDetailConcierageDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_concierage_data, viewGroup, true);

        String[] hour = DailyPreference.getInstance(getContext()).getOperationTime().split("\\,");

        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyPreference.getInstance(getContext()).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        viewDataBinding.conciergeTimeTextView.setText(getString(R.string.message_consult02, startHour, endHour, startLunchTime, endLunchTime));
        viewDataBinding.conciergeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onConciergeClick();
            }
        });
    }

    private void setViewPagerLineIndicatorVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().moreIconView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        getViewDataBinding().viewpagerIndicator.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void setRoomList(StayBookDateTime stayBookDateTime, List<StayOutboundRoom> roomList)
    {
        if (getViewDataBinding() == null || stayBookDateTime == null || roomList == null || roomList.size() == 0)
        {
            return;
        }

        final int nights;

        try
        {
            nights = stayBookDateTime.getNights();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return;
        }

        // 처음 세팅하는 경우 객실 타입 세팅
        if (mRoomTypeListAdapter == null)
        {
            mRoomTypeListAdapter = new StayOutboundDetailRoomListAdapter(getContext(), roomList, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = getViewDataBinding().productTypeRecyclerView.getChildAdapterPosition(v);

                    if (position < 0)
                    {
                        return;
                    }

                    getEventListener().onRoomClick(mRoomTypeListAdapter.getItem(position));
                    mRoomTypeListAdapter.setSelected(position);
                    mRoomTypeListAdapter.notifyDataSetChanged();
                }
            });
        } else
        {
            // 재세팅 하는 경우
            mRoomTypeListAdapter.addAll(roomList);
            mRoomTypeListAdapter.setSelected(0);
        }

        ViewGroup.LayoutParams layoutParams = getViewDataBinding().productTypeRecyclerView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getViewDataBinding().productTypeRecyclerView.setLayoutParams(layoutParams);
        getViewDataBinding().productTypeRecyclerView.setAdapter(mRoomTypeListAdapter);

        // 객실 개수로 높이를 재지정해준다.
        getViewDataBinding().productTypeRecyclerView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                final int DEFAULT_TOP_MARGIN = getDimensionPixelSize(R.dimen.toolbar_height);

                // 화면 높이 - 상단 타이틀 - 하단 버튼
                final int maxHeight = getViewDataBinding().getRoot().getHeight() - DEFAULT_TOP_MARGIN;

                int topMargin = 0;

                if (getViewDataBinding().productTypeLayout.getHeight() > maxHeight)
                {
                    topMargin = DEFAULT_TOP_MARGIN;
                }

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getViewDataBinding().productTypeLayout.getLayoutParams();
                layoutParams.topMargin = DEFAULT_TOP_MARGIN;

                getViewDataBinding().productTypeLayout.setLayoutParams(layoutParams);
            }
        }, 100);

        getViewDataBinding().bookingTextView.setOnClickListener(this);
    }


    private PaintDrawable makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.60f};

        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setShape(new RectShape());

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        paintDrawable.setShaderFactory(sf);

        return paintDrawable;
    }
}
