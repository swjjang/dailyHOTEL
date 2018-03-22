package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
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

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.base.BaseBlurView;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyDetailEmptyView;
import com.daily.dailyhotel.view.DailyDetailTitleInformationView;
import com.daily.dailyhotel.view.DailyDetailTrueReviewView;
import com.daily.dailyhotel.view.DailyRewardCardView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.daily.dailyhotel.view.carousel.DailyCarouselLayout;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDetailDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailAmenitiesDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMapDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail05DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailAmenityMoreDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailInformationDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.AlphaTransition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class StayOutboundDetailView extends BaseBlurView<StayOutboundDetailView.OnEventListener, ActivityStayOutboundDetailDataBinding>//
    implements StayOutboundDetailViewInterface, View.OnClickListener, RadioGroup.OnCheckedChangeListener
{
    private static final int ANIMATION_DELAY = 250;

    StayOutboundDetailRoomListAdapter mRoomTypeListAdapter;

    AnimatorSet mRoomAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onShareClick();

        void onWishClick();

        void onShareKakaoClick();

        void onMoreShareClick();

        void onImageClick(int position);

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

        void onRoomClick(StayOutboundRoom stayOutboundRoom);

        void onRecommendAroundItemClick(View view, android.support.v4.util.Pair[] pairs);

        void onRecommendAroundItemLongClick(View view, android.support.v4.util.Pair[] pairs);

        void onLoginClick();

        void onRewardClick();

        void onRewardGuideClick();
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

        viewDataBinding.nestedScrollView.setVisibility(View.INVISIBLE);
        viewDataBinding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (getViewDataBinding().scrollLayout.getChildCount() < 2)
                {
                    getViewDataBinding().toolbarView.setVisibility(View.GONE);
                    return;
                }

                View titleLayout = getViewDataBinding().scrollLayout.getChildAt(1);
                final int TOOLBAR_HEIGHT = getDimensionPixelSize(R.dimen.toolbar_height);

                if (titleLayout.getY() - TOOLBAR_HEIGHT > scrollY)
                {
                    getViewDataBinding().toolbarView.hideAnimation();
                } else
                {
                    getViewDataBinding().toolbarView.showAnimation();
                }
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge));

        // 객실 초기화
        viewDataBinding.roomsViewDataBinding.roomTypeTextView.setClickable(true);
        viewDataBinding.roomsViewDataBinding.includeTaxTextView.setClickable(true);
        viewDataBinding.roomsViewDataBinding.priceOptionLayout.setVisibility(View.GONE);

        viewDataBinding.roomsViewDataBinding.roomRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.roomsViewDataBinding.roomRecyclerView, getColor(R.color.default_over_scroll_edge));
        viewDataBinding.roomsViewDataBinding.roomTypeLayout.setVisibility(View.INVISIBLE);

        viewDataBinding.roomsViewDataBinding.closeView.setOnClickListener(this);

        viewDataBinding.bottomLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do nothing - 판매 완료 버튼이 뚤리는 이슈 수정
            }
        });

        viewDataBinding.recommendAroundListLayout.setTitleText(R.string.label_stay_outbound_recommend_around_title);
        viewDataBinding.recommendAroundListLayout.setCarouselListener(new DailyCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                // do nothing!
            }

            @Override
            public void onItemClick(View view, android.support.v4.util.Pair[] pairs)
            {
                getEventListener().onRecommendAroundItemClick(view, pairs);
            }

            @Override
            public void onItemLongClick(View view, android.support.v4.util.Pair[] pairs)
            {
                getEventListener().onRecommendAroundItemLongClick(view, pairs);
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeView:
                getEventListener().onHideRoomListClick(true);
                break;

            case R.id.bookingTextView:
                getEventListener().onActionButtonClick();
                break;
        }
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
                    int height = getViewDataBinding().roomsViewDataBinding.roomTypeLayout.getHeight();
                    int maxHeight = getViewDataBinding().getRoot().getHeight() - getViewDataBinding().bottomLayout.getHeight();

                    float toAnimationY = fromAnimationY - Math.min(height, maxHeight);

                    int startTransY = ScreenUtils.dpToPx(getContext(), height);
                    getViewDataBinding().roomsViewDataBinding.roomTypeLayout.setTranslationY(startTransY);

                    ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().roomsViewDataBinding.roomTypeLayout, "y", fromAnimationY, toAnimationY);
                    ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().productTypeBackgroundView, "alpha", 0.0f, 1.0f);

                    mRoomAnimatorSet = new AnimatorSet();
                    mRoomAnimatorSet.playTogether(transObjectAnimator, alphaObjectAnimator);
                    mRoomAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    mRoomAnimatorSet.setDuration(ANIMATION_DELAY);

                    mRoomAnimatorSet.addListener(new Animator.AnimatorListener()
                    {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {
                            getViewDataBinding().productTypeBackgroundView.setVisibility(View.VISIBLE);
                            getViewDataBinding().roomsViewDataBinding.roomTypeLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            if (mRoomAnimatorSet != null)
                            {
                                mRoomAnimatorSet.removeAllListeners();
                                mRoomAnimatorSet = null;
                            }

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
                    getViewDataBinding().roomsViewDataBinding.roomTypeLayout.setVisibility(View.VISIBLE);

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
                    final float y = getViewDataBinding().roomsViewDataBinding.roomTypeLayout.getY();

                    ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().roomsViewDataBinding.roomTypeLayout, "y", y, getViewDataBinding().bottomLayout.getTop());
                    ObjectAnimator alphaObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().productTypeBackgroundView, "alpha", 1.0f, 0.0f);

                    mRoomAnimatorSet = new AnimatorSet();
                    mRoomAnimatorSet.playTogether(transObjectAnimator, alphaObjectAnimator);
                    mRoomAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    mRoomAnimatorSet.setDuration(ANIMATION_DELAY);

                    mRoomAnimatorSet.addListener(new Animator.AnimatorListener()
                    {
                        @Override
                        public void onAnimationStart(Animator animation)
                        {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            if (mRoomAnimatorSet != null)
                            {
                                mRoomAnimatorSet.removeAllListeners();
                                mRoomAnimatorSet = null;
                            }

                            getViewDataBinding().productTypeBackgroundView.setVisibility(View.GONE);
                            getViewDataBinding().roomsViewDataBinding.roomTypeLayout.setVisibility(View.INVISIBLE);

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
                    getViewDataBinding().roomsViewDataBinding.roomTypeLayout.setVisibility(View.INVISIBLE);

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

        getViewDataBinding().nestedScrollView.setVisibility(View.VISIBLE);
        getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);

        setImageList(stayOutboundDetail.getImageList());

        // 이미지 상단에 빈화면 넣기
        setEmptyView();

        // 호텔 등급과 이름
        setTitleView(stayOutboundDetail.rating, stayOutboundDetail.name, stayOutboundDetail.nameEng//
            , stayOutboundDetail.activeReward && stayOutboundDetail.provideRewardSticker, stayOutboundDetail.couponPrice);

        // 트루 리뷰
        setTrueReviewView(stayOutboundDetail.tripAdvisorRating);

        //
        setCheckDateView(stayBookDateTime, people);

        // 주소 및 맵
        setAddressView(stayBookDateTime, stayOutboundDetail);

        // Amenity
        setAmenitiesView(stayOutboundDetail.getAmenityList());

        // 정보 화면
        setDescriptionsView(stayOutboundDetail.getInformationMap());

        // 카카오톡 문의
        setConciergeView();

        // 객실
        try
        {
            if (stayBookDateTime.getNights() > 1)
            {
                getViewDataBinding().roomsViewDataBinding.priceRadioGroup.check(R.id.averageRadioButton);
                getViewDataBinding().roomsViewDataBinding.priceOptionLayout.setVisibility(View.VISIBLE);
                getViewDataBinding().roomsViewDataBinding.priceRadioGroup.setOnCheckedChangeListener(this);
            } else
            {
                getViewDataBinding().roomsViewDataBinding.priceOptionLayout.setVisibility(View.GONE);
                getViewDataBinding().roomsViewDataBinding.priceRadioGroup.setOnCheckedChangeListener(null);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            getViewDataBinding().roomsViewDataBinding.priceOptionLayout.setVisibility(View.GONE);
            getViewDataBinding().roomsViewDataBinding.priceRadioGroup.setOnCheckedChangeListener(null);
        }

        // 객실 세팅
        setRoomList(stayBookDateTime, stayOutboundDetail.getRoomList(), stayOutboundDetail.activeReward);
    }

    @Override
    public void setRewardVisible(boolean visible, boolean hasRecommendAroundList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            if (hasRecommendAroundList == true)
            {
                getViewDataBinding().recommendAroundTopLineView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 1);
                getViewDataBinding().recommendAroundTopLineView.requestLayout();

                getViewDataBinding().conciergeTopLineView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 12);
                getViewDataBinding().conciergeTopLineView.requestLayout();
            } else
            {
                getViewDataBinding().recommendAroundTopLineView.setVisibility(View.GONE);

                getViewDataBinding().conciergeTopLineView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 1);
                getViewDataBinding().conciergeTopLineView.requestLayout();
            }

            getViewDataBinding().rewardCardLayout.setVisibility(View.VISIBLE);
        } else
        {
            if (hasRecommendAroundList == true)
            {
                getViewDataBinding().recommendAroundTopLineView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 12);
                getViewDataBinding().recommendAroundTopLineView.requestLayout();
            } else
            {
                getViewDataBinding().conciergeTopLineView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 12);
                getViewDataBinding().conciergeTopLineView.requestLayout();
            }

            getViewDataBinding().rewardCardLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRewardNonMember(String titleText, String optionText, int campaignFreeNights, String descriptionText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyRewardCardView rewardCardView = getViewDataBinding().rewardCardView;

        rewardCardView.setGuideVisible(true);
        rewardCardView.setOnGuideClickListener(v -> getEventListener().onRewardGuideClick());

        if (DailyTextUtils.isTextEmpty(optionText) == false)
        {
            rewardCardView.setOptionVisible(true);
            rewardCardView.setOptionText(optionText);
            rewardCardView.setOnOptionClickListener(v -> getEventListener().onLoginClick());
        } else
        {
            rewardCardView.setOptionVisible(false);
        }

        rewardCardView.setRewardTitleText(titleText);
        rewardCardView.setDescriptionText(descriptionText);

        rewardCardView.setCampaignFreeStickerCount(campaignFreeNights);
    }

    @Override
    public void setRewardMember(String titleText, String optionText, int nights, String descriptionText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyRewardCardView rewardCardView = getViewDataBinding().rewardCardView;

        rewardCardView.setGuideVisible(true);
        rewardCardView.setOnGuideClickListener(v -> getEventListener().onRewardGuideClick());

        if (DailyTextUtils.isTextEmpty(optionText) == false)
        {
            rewardCardView.setOptionVisible(true);
            rewardCardView.setOptionText(optionText);
            rewardCardView.setOnOptionClickListener(v -> getEventListener().onRewardClick());
        } else
        {
            rewardCardView.setOptionVisible(false);
        }

        rewardCardView.setRewardTitleText(titleText);
        rewardCardView.setDescriptionText(descriptionText);

        rewardCardView.setStickerCount(nights);
    }

    @TargetApi(value = 21)
    @Override
    public Observable<Boolean> getSharedElementTransition(int gradientType)
    {
        TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);

        Transition inBottomAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
        inBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
        inTransitionSet.addTransition(inBottomAlphaTransition);

        Transition inTopAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
        inTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
        inTransitionSet.addTransition(inTopAlphaTransition);

        getWindow().setSharedElementEnterTransition(inTransitionSet);

        TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);

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
                        setTransitionVisible(false);

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
            getViewDataBinding().imageLoopView.setLineIndicatorVisible(false);
            return;
        }

        DetailImageInformation detailImage = new DetailImageInformation();
        ImageMap imageMap = new ImageMap();
        imageMap.smallUrl = url;
        imageMap.mediumUrl = url;
        imageMap.bigUrl = url;
        detailImage.setImageMap(imageMap);

        List<DetailImageInformation> imageList = new ArrayList<>();
        imageList.add(detailImage);

        getViewDataBinding().imageLoopView.setImageList(imageList);
    }

    @Override
    public void setInitializedTransLayout(String name, String englishName, String url)
    {
        if (getViewDataBinding() == null || DailyTextUtils.isTextEmpty(name, url) == true)
        {
            return;
        }

        setInitializedImage(url);

        getViewDataBinding().transImageView.setImageURI(Uri.parse(url));
        getViewDataBinding().transNameTextView.setText(name);
        getViewDataBinding().transEnglishNameTextView.setText("(" + englishName + ")");
    }

    @Override
    public void setTransitionVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.INVISIBLE;

        getViewDataBinding().transImageView.setVisibility(flag);
        getViewDataBinding().transGradientBottomView.setVisibility(flag);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setSharedElementTransitionEnabled(boolean enabled, int gradientType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (enabled == true)
        {
            getViewDataBinding().transImageView.setVisibility(View.VISIBLE);
            getViewDataBinding().transGradientBottomView.setVisibility(View.VISIBLE);
            getViewDataBinding().transNameTextView.setVisibility(View.VISIBLE);
            getViewDataBinding().transEnglishNameTextView.setVisibility(View.VISIBLE);
            getViewDataBinding().transImageView.setTransitionName(getString(R.string.transition_place_image));
            getViewDataBinding().transGradientBottomView.setTransitionName(getString(R.string.transition_gradient_bottom_view));
            getViewDataBinding().imageLoopView.setTransGradientTopView(getString(R.string.transition_gradient_top_view));

            switch (gradientType)
            {
                case StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST:
                    getViewDataBinding().transGradientBottomView.setBackground(getGradientBottomDrawable());
                    break;

                case StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP:
                    getViewDataBinding().transGradientBottomView.setBackgroundResource(R.color.black_a28);
                    break;

                case StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE:
                default:
                    getViewDataBinding().transGradientBottomView.setBackground(null);
                    break;
            }
        } else
        {
            getViewDataBinding().transImageView.setVisibility(View.GONE);
            getViewDataBinding().transGradientBottomView.setVisibility(View.GONE);
            getViewDataBinding().transNameTextView.setVisibility(View.GONE);
            getViewDataBinding().transEnglishNameTextView.setVisibility(View.GONE);
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

            case StayOutboundDetailPresenter.STATUS_ROOM_LIST_LOADING:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.act_hotel_search_room_loading);
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

        // 예약 내역, 취소 내역 해외호텔은 안보여 주기로 함
        dataBinding.copyLinkLayout.setVisibility(View.GONE);

        dataBinding.moreShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onMoreShareClick();
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

        getViewDataBinding().nestedScrollView.scrollTo(0, 0);
        getViewDataBinding().nestedScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void setWishCount(int count)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        String wishCountText;

        if (count <= 0)
        {
            wishCountText = null;
        } else if (count > 9999)
        {
            int wishCount = count / 1000;

            if (wishCount % 10 == 0)
            {
                wishCountText = getString(R.string.wishlist_count_over_10_thousand, Integer.toString(wishCount / 10));
            } else
            {
                wishCountText = getString(R.string.wishlist_count_over_10_thousand, Float.toString((float) wishCount / 10.0f));
            }
        } else
        {
            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            wishCountText = decimalFormat.format(count);
        }

        if (getViewDataBinding().toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) == true)
        {
            getViewDataBinding().toolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_OFF, wishCountText, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onWishClick();
                }
            });
        } else if (getViewDataBinding().toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON) == true)
        {
            getViewDataBinding().toolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON, wishCountText, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onWishClick();
                }
            });
        }

        if (getViewDataBinding().fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) == true)
        {
            getViewDataBinding().fakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_OFF, wishCountText, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onWishClick();
                }
            });
        } else if (getViewDataBinding().fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON) == true)
        {
            getViewDataBinding().fakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON, wishCountText, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onWishClick();
                }
            });
        }
    }

    @Override
    public void setWishSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (selected == true)
        {
            if (getViewDataBinding().toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) == true)
            {
                getViewDataBinding().toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_FILL_ON, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onWishClick();
                    }
                });
            }

            if (getViewDataBinding().fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) == true)
            {
                getViewDataBinding().fakeToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_LINE_ON, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onWishClick();
                    }
                });
            }
        } else
        {
            if (getViewDataBinding().toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON) == true)
            {
                getViewDataBinding().toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON, DailyToolbarView.MenuItem.WISH_OFF, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onWishClick();
                    }
                });
            }

            if (getViewDataBinding().toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) == true)
            {
                getViewDataBinding().toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON, DailyToolbarView.MenuItem.WISH_OFF, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getEventListener().onWishClick();
                    }
                });
            }
        }
    }

    @Override
    public void showWishTooltip()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().wishTooltipLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWishTooltip()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().wishTooltipLayout.setVisibility(View.GONE);
    }

    @Override
    public void setRecommendAroundVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        getViewDataBinding().recommendAroundTopLineView.setVisibility(flag);
        getViewDataBinding().recommendAroundListLayout.setVisibility(flag);
    }

    @Override
    public void setRecommendAroundList(ArrayList<CarouselListItem> list, StayBookDateTime stayBookDateTime)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        boolean nightsEnabled = false;
        if (stayBookDateTime != null)
        {
            try
            {
                nightsEnabled = stayBookDateTime.getNights() > 1;
            } catch (Exception e)
            {

            }
        }

        getViewDataBinding().recommendAroundListLayout.setData(list, nightsEnabled);
    }

    @Override
    public void startCampaignStickerAnimation()
    {
        if (getViewDataBinding() == null || getViewDataBinding().rewardCardLayout.getVisibility() == View.GONE)
        {
            return;
        }

        getViewDataBinding().rewardCardView.startCampaignStickerAnimation();
    }

    @Override
    public void stopCampaignStickerAnimation()
    {
        if (getViewDataBinding() == null || getViewDataBinding().rewardCardLayout.getVisibility() == View.GONE)
        {
            return;
        }

        getViewDataBinding().rewardCardView.stopCampaignStickerAnimation();
    }

    private void initToolbar(ActivityStayOutboundDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.toolbarView.clearMenuItem();

        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.WISH_OFF, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onWishClick();
            }
        });

        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onShareClick();
            }
        });

        viewDataBinding.toolbarView.setVisibility(View.INVISIBLE);

        viewDataBinding.fakeToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.fakeToolbarView.clearMenuItem();

        viewDataBinding.fakeToolbarView.addMenuItem(DailyToolbarView.MenuItem.WISH_OFF, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onWishClick();
            }
        });

        viewDataBinding.fakeToolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onShareClick();
            }
        });
    }

    private void setImageList(List<DetailImageInformation> imageList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().imageLoopView.setImageList(imageList);
    }

    /**
     * 빈화면
     */
    private void setEmptyView()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().detailEmptyView.setOnEventListener(new DailyDetailEmptyView.OnEventListener()
        {
            @Override
            public void onStopMove(MotionEvent event)
            {
                getViewDataBinding().nestedScrollView.setScrollingEnabled(false);

                try
                {
                    getViewDataBinding().imageLoopView.onTouchEvent(event);
                } catch (Exception e)
                {
                }
            }

            @Override
            public void onHorizontalMove(MotionEvent event)
            {
                try
                {
                    getViewDataBinding().imageLoopView.onTouchEvent(event);
                } catch (Exception e)
                {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    event.setLocation(getViewDataBinding().imageLoopView.getPageScrollX(), getViewDataBinding().imageLoopView.getPageScrollY());
                    getViewDataBinding().imageLoopView.onTouchEvent(event);
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
                    getViewDataBinding().imageLoopView.onTouchEvent(event);
                } catch (Exception e)
                {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    event.setLocation(getViewDataBinding().imageLoopView.getPageScrollX(), getViewDataBinding().imageLoopView.getPageScrollY());
                    getViewDataBinding().imageLoopView.onTouchEvent(event);
                }

                getViewDataBinding().nestedScrollView.setScrollingEnabled(true);
            }

            @Override
            public void onImageClick()
            {
                getEventListener().onImageClick(getViewDataBinding().imageLoopView.getPosition());
            }
        });
    }

    /**
     * 호텔 등급 및 이름
     */
    private void setTitleView(float rating, String name, String englishName, boolean hasProviderSticker, int couponPrice)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyDetailTitleInformationView titleInformationView = getViewDataBinding().titleInformationView;

        // 호텔명
        titleInformationView.setNameText(name);
        titleInformationView.setEnglishNameVisible(true);
        titleInformationView.setEnglishNameText("(" + englishName + ")");

        // 등급
        if ((int) rating == 0)
        {
            titleInformationView.setCategory(null, null);
        } else
        {
            titleInformationView.setCategory(getString(R.string.label_stay_outbound_detail_grade, (int) rating), null);
        }

        // 리워드 여부
        titleInformationView.setRewardVisible(hasProviderSticker);

        // 쿠폰
        if (couponPrice > 0)
        {
            titleInformationView.setCouponVisible(true);
            titleInformationView.setCouponPriceText(getString(R.string.label_download_coupon_price, DailyTextUtils.getPriceFormat(getContext(), couponPrice, false)));
        } else
        {
            titleInformationView.setCouponVisible(false);
        }
    }

    private void setTrueReviewView(float tripAdvisorRating)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyDetailTrueReviewView trueReviewView = getViewDataBinding().trueReviewView;

        if (tripAdvisorRating <= 0.0f)
        {
            getViewDataBinding().trueReviewTopLineView.setVisibility(View.GONE);
            trueReviewView.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().trueReviewTopLineView.setVisibility(View.VISIBLE);
            trueReviewView.setVisibility(View.VISIBLE);

            trueReviewView.setSatisfactionVisible(false);
            trueReviewView.setTrueReviewCountVisible(false);
            trueReviewView.setTripAdvisorVisible(true);

            // 제목 변경
            trueReviewView.setNameText(getString(R.string.label_tripadvisorrating));

            // 만족도
            trueReviewView.setTripAdvisorRating(tripAdvisorRating);
        }
    }

    private void setCheckDateView(StayBookDateTime stayBookDateTime, People people)
    {
        if (stayBookDateTime == null || people == null)
        {
            return;
        }

        getViewDataBinding().dateInformationView.setDateVisible(true, true);

        try
        {
            String dateFormat = String.format(Locale.KOREA, "%s - %s, %s", stayBookDateTime.getCheckInDateTime("M.d(EEE)"), stayBookDateTime.getCheckOutDateTime("M.d(EEE)"), getString(R.string.label_nights, stayBookDateTime.getNights()));

            getViewDataBinding().dateInformationView.setDate1Text(getString(R.string.label_stay_outbound_detail_check_in_out), dateFormat);
            getViewDataBinding().dateInformationView.setDate1DescriptionTextColor(getColor(R.color.default_text_cb70038));
            getViewDataBinding().dateInformationView.setDate1DescriptionTextDrawable(0, 0, R.drawable.navibar_m_burg_ic_v, 0);
            getViewDataBinding().dateInformationView.setData1TextSize(13.0f, 13.0f);

            getViewDataBinding().dateInformationView.setCenterNightsVisible(false);

            getViewDataBinding().dateInformationView.setDate2Text(getString(R.string.label_stay_outbound_detail_number_of_people), people.toShortString(getContext()));
            getViewDataBinding().dateInformationView.setDate2DescriptionTextColor(getColor(R.color.default_text_cb70038));
            getViewDataBinding().dateInformationView.setDate2DescriptionTextDrawable(0, 0, R.drawable.navibar_m_burg_ic_v, 0);
            getViewDataBinding().dateInformationView.setData2TextSize(13.0f, 13.0f);

            getViewDataBinding().dateInformationView.setOnDateClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onCalendarClick();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onPeopleClick();
                }
            });
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void updateBookDateTime(StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        try
        {
            String dateFormat = String.format(Locale.KOREA, "%s - %s, %s", stayBookDateTime.getCheckInDateTime("M.d(EEE)")//
                , stayBookDateTime.getCheckOutDateTime("M.d(EEE)"), getString(R.string.label_nights, stayBookDateTime.getNights()));

            getViewDataBinding().dateInformationView.setDate1Text(getString(R.string.label_stay_outbound_detail_check_in_out), dateFormat);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void updatePeople(People people)
    {
        if (people == null)
        {
            return;
        }

        getViewDataBinding().dateInformationView.setDate2Text(getString(R.string.label_stay_outbound_detail_number_of_people), people.toShortString(getContext()));
    }

    /**
     * 호텔 주소 및 맵
     */
    private void setAddressView(StayBookDateTime stayBookDateTime, StayOutboundDetail stayOutboundDetail)
    {
        if (getViewDataBinding() == null || stayBookDateTime == null || stayOutboundDetail == null)
        {
            return;
        }

        LayoutGourmetDetailMapDataBinding viewDataBinding = getViewDataBinding().mapViewDataBinding;

        // 주소지
        viewDataBinding.detailAddressTextView.setText(stayOutboundDetail.address);

        // 주소 복사
        viewDataBinding.copyAddressLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onClipAddressClick(stayOutboundDetail.address);
            }
        });

        // 길찾기
        viewDataBinding.navigatorLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onNavigatorClick();
            }
        });

        // 맵보기
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
     * @param stringSparseArray
     * @return
     */
    private void setAmenitiesView(SparseArray<String> stringSparseArray)
    {
        LayoutGourmetDetailAmenitiesDataBinding viewDataBinding = getViewDataBinding().amenitiesViewDataBinding;

        viewDataBinding.amenitiesGridLayout.removeAllViews();

        if (stringSparseArray == null || stringSparseArray.size() == 0)
        {
            viewDataBinding.amenitiesGridLayout.setVisibility(View.GONE);
            return;
        }

        final int GRID_COLUMN_COUNT = 5;

        viewDataBinding.amenitiesGridLayout.setVisibility(View.VISIBLE);

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

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

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
     * @param informationMap
     */
    private void setDescriptionsView(LinkedHashMap<String, List<String>> informationMap)
    {
        if (getViewDataBinding() == null || informationMap == null)
        {
            return;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        Iterator<Map.Entry<String, List<String>>> iterator = informationMap.entrySet().iterator();

        getViewDataBinding().descriptionsLayout.removeAllViews();

        while (iterator.hasNext() == true)
        {
            Map.Entry<String, List<String>> entry = iterator.next();

            if (entry == null)
            {
                continue;
            }

            setDescriptionView(layoutInflater, getViewDataBinding().descriptionsLayout, entry, iterator.hasNext() == false);
        }
    }

    private void setDescriptionView(LayoutInflater layoutInflater, ViewGroup viewGroup, Map.Entry<String, List<String>> information, boolean lastView)
    {
        if (layoutInflater == null || viewGroup == null || information == null)
        {
            return;
        }

        LayoutStayOutboundDetail05DataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
            , R.layout.layout_stay_outbound_detail_05_data, viewGroup, true);

        viewDataBinding.titleTextView.setText(information.getKey());

        List<String> informationList = information.getValue();

        if (informationList == null || informationList.size() == 0)
        {
            return;
        }

        int size = informationList.size();

        for (int i = 0; i < size; i++)
        {
            if (DailyTextUtils.isTextEmpty(informationList.get(i)) == true)
            {
                continue;
            }

            LayoutStayOutboundDetailInformationDataBinding detailInformationDataBinding = DataBindingUtil.inflate(layoutInflater//
                , R.layout.layout_stay_outbound_detail_information_data, viewDataBinding.informationLayout, true);

            detailInformationDataBinding.textView.setText(Html.fromHtml(informationList.get(i).replaceAll("\\<p\\>", "").replaceAll("\\<\\/p\\>", "<br /><br />")));

            if (i == size - 1)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) detailInformationDataBinding.textView.getLayoutParams();
                layoutParams.bottomMargin = 0;
                detailInformationDataBinding.textView.setLayoutParams(layoutParams);
            }
        }

        if (lastView == true)
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewDataBinding.informationLayout.getLayoutParams();
            layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 20);
            viewDataBinding.informationLayout.setLayoutParams(layoutParams);
        }
    }

    /**
     * 문의하기
     */
    private void setConciergeView()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        LayoutGourmetDetailConciergeDataBinding viewDataBinding = getViewDataBinding().conciergeViewDataBinding;

        String[] hour = DailyPreference.getInstance(getContext()).getOperationTime().split("\\,");

        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigOperationLunchTime().split("\\,");
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

    private void setRoomList(StayBookDateTime stayBookDateTime, List<StayOutboundRoom> roomList, boolean activeReward)
    {
        if (getViewDataBinding() == null || stayBookDateTime == null || roomList == null || roomList.size() == 0)
        {
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
                    int position = getViewDataBinding().roomsViewDataBinding.roomRecyclerView.getChildAdapterPosition(v);

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

        mRoomTypeListAdapter.setRewardEnabled(activeReward);
        getViewDataBinding().roomsViewDataBinding.roomRecyclerView.setAdapter(mRoomTypeListAdapter);
        getViewDataBinding().bookingTextView.setOnClickListener(this);
    }

    /**
     * 리스트에서 사용하는것과 동일한다.
     *
     * @return
     */
    private PaintDrawable getGradientBottomDrawable()
    {
        // 그라디에이션 만들기.
        final int colors[] = {0x99000000, 0x66000000, 0x05000000, 0x00000000, 0x00000000};
        final float positions[] = {0.0f, 0.33f, 0.81f, 0.91f, 1.0f};

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
