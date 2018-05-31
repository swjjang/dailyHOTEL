package com.daily.dailyhotel.screen.home.stay.inbound.old_detail;

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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayRoom;
import com.daily.dailyhotel.entity.TrueAwards;
import com.daily.dailyhotel.entity.old_StayDetail;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyDetailEmptyView;
import com.daily.dailyhotel.view.DailyDetailTitleInformationView;
import com.daily.dailyhotel.view.DailyDetailTrueAwardsView;
import com.daily.dailyhotel.view.DailyDetailTrueReviewView;
import com.daily.dailyhotel.view.DailyRewardCardView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayDetailDataOldBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogDailyAwardsDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailAmenitiesDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailBenefitContentBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailBenefitDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMapDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayDetailWaitforbookingDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail05DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailInformationDataBinding;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.AlphaTransition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class StayDetailView extends BaseDialogView<StayDetailView.OnEventListener, ActivityStayDetailDataOldBinding>//
    implements StayDetailViewInterface, View.OnClickListener, RadioGroup.OnCheckedChangeListener
{
    private static final int ANIMATION_DELAY = 250;

    StayDetailRoomListAdapter mRoomTypeListAdapter;

    AnimatorSet mRoomAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onShareClick();

        void onWishClick();

        void onShareKakaoClick();

        void onCopyLinkClick();

        void onMoreShareClick();

        void onImageClick(int position);

        void onCalendarClick();

        void onMapClick();

        void onClipAddressClick(String address);

        void onNavigatorClick();

        void onConciergeClick();

        void onHideRoomListClick(boolean animation);

        void onActionButtonClick();

        void onPriceTypeClick(StayDetailPresenter.PriceType priceType);

        void onConciergeFaqClick();

        void onConciergeHappyTalkClick();

        void onConciergeCallClick();

        void onRoomClick(StayRoom stayRoom);

        void onTrueReviewClick();

        void onTrueVRClick();

        void onDownloadCouponClick();

        void onHideWishTooltipClick();

        void onLoginClick();

        void onRewardClick();

        void onRewardGuideClick();

        void onTrueAwardsClick();
    }

    public StayDetailView(BaseActivity baseActivity, StayDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayDetailDataOldBinding viewDataBinding)
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

                getViewDataBinding().fakeVRImageView.setEnabled(scrollY <= TOOLBAR_HEIGHT);
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

        viewDataBinding.wishTooltipLayout.setVisibility(View.GONE);
        viewDataBinding.wishTooltipLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onHideWishTooltipClick();
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
                getEventListener().onPriceTypeClick(StayDetailPresenter.PriceType.AVERAGE);
                break;

            case R.id.totalRadioButton:
                getEventListener().onPriceTypeClick(StayDetailPresenter.PriceType.TOTAL);
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
    public void setStayDetail(StayBookDateTime stayBookDateTime, old_StayDetail stayDetail, int trueReviewCount)
    {
        if (getViewDataBinding() == null || stayBookDateTime == null || stayDetail == null)
        {
            return;
        }

        getViewDataBinding().nestedScrollView.setVisibility(View.VISIBLE);
        getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);

        setImageList(stayDetail.getImageInformationList());

        // 이미지 상단에 빈화면 넣기
        setEmptyView();

        // 호텔 이름 / 쿠폰
        setTitleView(stayDetail.grade, stayDetail.name, stayDetail.activeReward && stayDetail.provideRewardSticker, stayDetail.couponPrice);

        // 트루 리뷰
        setTrueReviewView(stayDetail.ratingShow, stayDetail.ratingValue, stayDetail.ratingPersons, trueReviewCount);

        // 트루 어워드
        setTrueAwardsView(stayDetail.awards);

        // 체크인/체크아웃
        setCheckDateView(stayBookDateTime);

        // 주소 및 맵
        setAddressView(stayDetail.address);

        // Amenity
        setAmenitiesView(stayDetail.getPictogramList());

        // Benefit
        setBenefitView(stayDetail.benefit, stayDetail.getBenefitContentList());

        // 정보 화면
        setDescriptionsView(stayDetail.getRoomList(), stayDetail.getDescriptionList(), stayDetail.waitingForBooking);

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
        setRoomList(stayBookDateTime, stayDetail.getRoomList(), stayDetail.activeReward);
    }

    @Override
    public void setRewardVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            getViewDataBinding().conciergeTopLineView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 1);

            getViewDataBinding().rewardCardLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().conciergeTopLineView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 12);

            getViewDataBinding().rewardCardLayout.setVisibility(View.GONE);
        }

        getViewDataBinding().conciergeTopLineView.requestLayout();
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

        return new Observable<Boolean>()
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
        imageMap.smallUrl = null;
        imageMap.mediumUrl = url;
        imageMap.bigUrl = url;
        detailImage.setImageMap(imageMap);

        List<DetailImageInformation> imageList = new ArrayList<>();
        imageList.add(detailImage);

        getViewDataBinding().imageLoopView.setImageList(imageList);
    }

    @Override
    public void setInitializedTransLayout(String name, String url)
    {
        if (getViewDataBinding() == null || DailyTextUtils.isTextEmpty(name, url) == true)
        {
            return;
        }

        setInitializedImage(url);

        getViewDataBinding().transImageView.setImageURI(Uri.parse(url));
        getViewDataBinding().transNameTextView.setText(name);
    }

    @Override
    public void setTransitionVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().transImageView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        getViewDataBinding().transGradientBottomView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
            getViewDataBinding().transImageView.setTransitionName(getString(R.string.transition_place_image));
            getViewDataBinding().transGradientBottomView.setTransitionName(getString(R.string.transition_gradient_bottom_view));
            getViewDataBinding().imageLoopView.setTransGradientTopView(getString(R.string.transition_gradient_top_view));

            switch (gradientType)
            {
                case StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST:
                    getViewDataBinding().transGradientBottomView.setBackground(getGradientBottomDrawable());
                    break;

                case StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP:
                    getViewDataBinding().transGradientBottomView.setBackgroundResource(R.color.black_a28);
                    break;

                case StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE:
                default:
                    getViewDataBinding().transGradientBottomView.setBackground(null);
                    break;
            }
        } else
        {
            getViewDataBinding().transImageView.setVisibility(View.GONE);
            getViewDataBinding().transGradientBottomView.setVisibility(View.GONE);
            getViewDataBinding().transNameTextView.setVisibility(View.GONE);
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
            case StayDetailPresenter.STATUS_NONE:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);
                break;
            }

            case StayDetailPresenter.STATUS_ROOM_LIST:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.act_hotel_search_room);
                break;
            }

            case StayDetailPresenter.STATUS_BOOKING:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.act_hotel_booking);
                break;
            }

            case StayDetailPresenter.STATUS_SOLD_OUT:
            {
                getViewDataBinding().bookingTextView.setVisibility(View.GONE);
                getViewDataBinding().soldoutTextView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void setPriceType(StayDetailPresenter.PriceType priceType)
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

        dataBinding.copyLinkView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onCopyLinkClick();
            }
        });

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
    public Observable<Boolean> showWishView(boolean myWish)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return myWish ? getViewDataBinding().wishAnimationView.addWishAnimation() : getViewDataBinding().wishAnimationView.removeWishAnimation();
    }

    @Override
    public void setTrueVRVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        getViewDataBinding().vrImageView.setVisibility(flag);
        getViewDataBinding().fakeVRImageView.setVisibility(flag);
        getViewDataBinding().fakeVRImageView.setOnClickListener(v -> getEventListener().onTrueVRClick());
    }

    @Override
    public void showTrueVRDialog(CheckBox.OnCheckedChangeListener checkedChangeListener, View.OnClickListener positiveListener//
        , Dialog.OnDismissListener onDismissListener)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        showSimpleDialog(null, getString(R.string.message_stay_used_data_guide), getString(R.string.label_dont_again)//
            , getString(R.string.dialog_btn_do_continue), getString(R.string.dialog_btn_text_close)//
            , checkedChangeListener, positiveListener//
            , null, null, onDismissListener, true);
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

    @Override
    public void showTrueAwardsDialog(TrueAwards trueAwards, Dialog.OnDismissListener onDismissListener)
    {
        if (getViewDataBinding() == null || getContext() == null || trueAwards == null)
        {
            return;
        }

        DialogDailyAwardsDataBinding dataBinding = DataBindingUtil.inflate( //
            LayoutInflater.from(getContext()), R.layout.dialog_daily_awards_data, null, false);

        dataBinding.awardImageView.setImageResource(R.drawable.img_popup_detail_trueawards);

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFailure(String id, Throwable throwable)
            {
                super.onFailure(id, throwable);

                dataBinding.awardImageView.setImageResource(R.drawable.img_popup_detail_trueawards);
            }
        };

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
            .setControllerListener(controllerListener).setUri(trueAwards.imageUrl).build();

        dataBinding.awardImageView.setController(draweeController);

        dataBinding.awardTitleTextView.setText(getContext().getString(R.string.label_daily_true_awards_popup_title_formet, trueAwards.title));
        dataBinding.awardDescriptionTextView.setText(trueAwards.description);

        dataBinding.confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, onDismissListener, true);
    }

    private void initToolbar(ActivityStayDetailDataOldBinding viewDataBinding)
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
    private void setTitleView(Stay.Grade grade, String name, boolean hasProviderSticker, int couponPrice)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyDetailTitleInformationView titleInformationView = getViewDataBinding().titleInformationView;

        if (grade == null)
        {
            grade = Stay.Grade.etc;
        }

        // 호텔명
        titleInformationView.setNameText(name);
        titleInformationView.setEnglishNameVisible(false);

        // 등급
        titleInformationView.setCategory(grade.getName(getContext()), null);

        // 리워드 여부
        titleInformationView.setRewardVisible(hasProviderSticker);

        // 쿠폰
        if (couponPrice > 0)
        {
            titleInformationView.setCouponVisible(true);
            titleInformationView.setCouponPriceText(getString(R.string.label_download_coupon_price, DailyTextUtils.getPriceFormat(getContext(), couponPrice, false)));
            titleInformationView.setOnCouponClickListener(v -> getEventListener().onDownloadCouponClick());
        } else
        {
            titleInformationView.setCouponVisible(false);
        }
    }

    private void setTrueReviewView(boolean ratingShow, int ratingValue, int ratingPersons, int trueReviewCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyDetailTrueReviewView trueReviewView = getViewDataBinding().trueReviewView;

        if (ratingShow == false && trueReviewCount == 0)
        {
            getViewDataBinding().trueReviewTopLineView.setVisibility(View.GONE);
            trueReviewView.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().trueReviewTopLineView.setVisibility(View.VISIBLE);
            trueReviewView.setVisibility(View.VISIBLE);

            trueReviewView.setTripAdvisorVisible(false);

            // 만족도
            trueReviewView.setSatisfactionVisible(ratingShow);

            if (ratingShow == true)
            {
                DecimalFormat decimalFormat = new DecimalFormat("###,##0");
                trueReviewView.setSatisfactionVText(getString(R.string.label_stay_detail_satisfaction, //
                    ratingValue, decimalFormat.format(ratingPersons)));
            }

            // 리뷰
            if (trueReviewCount > 0)
            {
                trueReviewView.setTrueReviewCountVisible(true);
                trueReviewView.setTrueReviewCount(trueReviewCount);
                trueReviewView.setOnTrueReviewCountClickListener(v -> getEventListener().onTrueReviewClick());
            } else
            {
                trueReviewView.setTrueReviewCountVisible(false);
            }
        }
    }

    private void setTrueAwardsView(TrueAwards trueAwards)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (trueAwards == null || DailyTextUtils.isTextEmpty(trueAwards.title) == true)
        {
            getViewDataBinding().trueAwardsTopLineView.setVisibility(View.GONE);
            getViewDataBinding().trueAwardsView.setVisibility(View.GONE);
            getViewDataBinding().trueAwardsView.setOnClickListener(null);
            return;
        }

        getViewDataBinding().trueAwardsTopLineView.setVisibility(View.VISIBLE);
        getViewDataBinding().trueAwardsView.setVisibility(View.VISIBLE);
        getViewDataBinding().trueAwardsView.setListener(new DailyDetailTrueAwardsView.OnDailyDetailTrueAwardsListener()
        {
            @Override
            public void onQuestionClick()
            {
                getEventListener().onTrueAwardsClick();
            }
        });

        getViewDataBinding().trueAwardsView.setAwardsDetailText(trueAwards.title);
    }

    /**
     * @param stayBookDateTime
     */
    private void setCheckDateView(StayBookDateTime stayBookDateTime)
    {
        if (getViewDataBinding() == null || stayBookDateTime == null)
        {
            return;
        }

        getViewDataBinding().dateInformationView.setDateVisible(true, true);

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        getViewDataBinding().dateInformationView.setDate1Text(getString(R.string.label_check_in), stayBookDateTime.getCheckInDateTime(DATE_FORMAT));
        getViewDataBinding().dateInformationView.setDate1DescriptionTextColor(getColor(R.color.default_text_ceb2135));
        getViewDataBinding().dateInformationView.setDate1DescriptionTextDrawable(0, 0, R.drawable.navibar_m_burg_ic_v, 0);
        getViewDataBinding().dateInformationView.setData1TextSize(13.0f, 15.0f);

        getViewDataBinding().dateInformationView.setCenterNightsVisible(true);
        getViewDataBinding().dateInformationView.setCenterNightsText(getString(R.string.label_nights, stayBookDateTime.getNights()));

        getViewDataBinding().dateInformationView.setDate2Text(getString(R.string.label_check_out), stayBookDateTime.getCheckOutDateTime(DATE_FORMAT));
        getViewDataBinding().dateInformationView.setDate2DescriptionTextColor(getColor(R.color.default_text_ceb2135));
        getViewDataBinding().dateInformationView.setDate2DescriptionTextDrawable(0, 0, R.drawable.navibar_m_burg_ic_v, 0);
        getViewDataBinding().dateInformationView.setData2TextSize(13.0f, 15.0f);

        getViewDataBinding().dateInformationView.setOnDateClickListener(v -> getEventListener().onCalendarClick(), v -> getEventListener().onCalendarClick());
    }

    /**
     * 호텔 주소 및 맵
     */
    private void setAddressView(String address)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        LayoutGourmetDetailMapDataBinding viewDataBinding = getViewDataBinding().mapViewDataBinding;

        // 주소지
        viewDataBinding.detailAddressTextView.setText(address);

        // 주소지 COPY
        viewDataBinding.copyAddressLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onClipAddressClick(address);
            }
        });

        //길찾기
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
     * @return
     */
    private void setAmenitiesView(List<old_StayDetail.Pictogram> pictogramList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        LayoutGourmetDetailAmenitiesDataBinding viewDataBinding = getViewDataBinding().amenitiesViewDataBinding;

        if (viewDataBinding.amenitiesGridLayout.getChildCount() > 0)
        {
            viewDataBinding.amenitiesGridLayout.removeAllViews();
        }

        if (pictogramList == null || pictogramList.size() == 0)
        {
            viewDataBinding.amenitiesLayout.setVisibility(View.GONE);
            return;
        }

        final int GRID_COLUMN_COUNT = 5;

        viewDataBinding.amenitiesLayout.setVisibility(View.VISIBLE);

        boolean isSingleLine = pictogramList.size() <= GRID_COLUMN_COUNT;

        for (old_StayDetail.Pictogram pictogram : pictogramList)
        {
            viewDataBinding.amenitiesGridLayout.addView(getGridLayoutItemView(getContext(), pictogram, isSingleLine));
        }

        int columnCount = pictogramList.size() % GRID_COLUMN_COUNT;

        if (columnCount != 0)
        {
            int addEmptyViewCount = GRID_COLUMN_COUNT - columnCount;
            for (int i = 0; i < addEmptyViewCount; i++)
            {
                viewDataBinding.amenitiesGridLayout.addView(getGridLayoutItemView(getContext(), old_StayDetail.Pictogram.NONE, isSingleLine));
            }
        }
    }

    private DailyTextView getGridLayoutItemView(Context context, old_StayDetail.Pictogram pictogram, boolean isSingleLine)
    {
        DailyTextView dailyTextView = new DailyTextView(context);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(getColorStateList(R.color.default_text_c4d4d4d));
        dailyTextView.setText(pictogram.getName(context));
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, pictogram.getImageResId(), 0, 0);
        dailyTextView.setDrawableVectorTint(R.color.default_background_c454545);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (isSingleLine == true)
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 15));
        } else
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 2));
        }

        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    private void setBenefitView(String benefit, List<String> benefitContentList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        LayoutGourmetDetailBenefitDataBinding viewDataBinding = getViewDataBinding().benefitViewDataBinding;

        if (DailyTextUtils.isTextEmpty(benefit) == true)
        {
            // benefit 이 없으면 상단 라인으로 대체 하기때문에 비어있으면 리턴
            viewDataBinding.benefitLayout.setVisibility(View.GONE);
            return;
        }

        viewDataBinding.benefitLayout.setVisibility(View.VISIBLE);
        viewDataBinding.benefitTitleTextView.setText(benefit);

        if (viewDataBinding.benefitContentsLayout.getChildCount() > 0)
        {
            viewDataBinding.benefitContentsLayout.removeAllViews();
        }

        if (benefitContentList != null && benefitContentList.size() > 0)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());

            for (String content : benefitContentList)
            {
                LayoutGourmetDetailBenefitContentBinding contentViewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_gourmet_detail_benefit_content, viewDataBinding.benefitContentsLayout, true);
                contentViewDataBinding.textView.setText(content);
            }
        }
    }

    /**
     * 상세 스테이 정보
     *
     * @param descriptionList
     */
    private void setDescriptionsView(List<StayRoom> stayRoomList, List<LinkedHashMap<String, List<String>>> descriptionList, boolean waitingForBooking)
    {
        if (getViewDataBinding() == null || descriptionList == null)
        {
            return;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        boolean hasNRD = false;
        for (StayRoom stayRoom : stayRoomList)
        {
            if (stayRoom.nrd == true)
            {
                hasNRD = true;
                break;
            }
        }

        boolean hasRefundPolicy = false;

        if (getViewDataBinding().descriptionsLayout.getChildCount() > 0)
        {
            getViewDataBinding().descriptionsLayout.removeAllViews();
        }

        for (LinkedHashMap<String, List<String>> descriptionMap : descriptionList)
        {
            Iterator<Map.Entry<String, List<String>>> iterator = descriptionMap.entrySet().iterator();

            while (iterator.hasNext() == true)
            {
                boolean addRefundPolicy = false;

                Map.Entry<String, List<String>> entry = iterator.next();

                if (entry == null)
                {
                    continue;
                }

                if (hasNRD == true && getString(R.string.label_detail_cancellation_refund_policy).equalsIgnoreCase(entry.getKey()) == true)
                {
                    addRefundPolicy = true;
                    hasRefundPolicy = true;
                }

                // 마지막인 경우
                if (iterator.hasNext() == false)
                {
                    // 마지막인데 취소 환불대기 문구가 없는 경우
                    if (hasNRD == true && hasRefundPolicy == false)
                    {
                        setDescriptionView(layoutInflater, getViewDataBinding().descriptionsLayout, entry, false, false, false);
                    } else
                    {
                        setDescriptionView(layoutInflater, getViewDataBinding().descriptionsLayout, entry, true, addRefundPolicy, false);
                    }
                } else
                {
                    setDescriptionView(layoutInflater, getViewDataBinding().descriptionsLayout, entry, false, addRefundPolicy, false);
                }
            }
        }

        // 취소 대기 환불 문구를 넣어준다.
        if (hasNRD == true && hasRefundPolicy == false)
        {
            List<String> refundPolicyList = new ArrayList<>();

            Map<String, List<String>> refundPolicyMap = new HashMap<>();
            refundPolicyMap.put(getString(R.string.label_detail_cancellation_refund_policy), refundPolicyList);

            setDescriptionView(layoutInflater, getViewDataBinding().descriptionsLayout, refundPolicyMap.entrySet().iterator().next(), waitingForBooking == false, true, false);
        }

        // 대기 예약 안내를 추가한다.
        if (waitingForBooking == true)
        {
            LayoutStayDetailWaitforbookingDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater//
                , R.layout.layout_stay_detail_waitforbooking_data, getViewDataBinding().descriptionsLayout, true);

            viewDataBinding.contentTextView.setText(Html.fromHtml(getString(R.string.message_stay_waiting_reservation_guide)));
        }
    }

    private void setDescriptionView(LayoutInflater layoutInflater, ViewGroup viewGroup, Map.Entry<String, List<String>> information//
        , boolean lastView, boolean addRefundPolicy, boolean html)
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

            if (html == true)
            {
                detailInformationDataBinding.textView.setText(Html.fromHtml(informationList.get(i)));
            } else
            {
                detailInformationDataBinding.textView.setText(informationList.get(i));
            }

            if (i == size - 1 && addRefundPolicy == false)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) detailInformationDataBinding.textView.getLayoutParams();
                layoutParams.bottomMargin = 0;
                detailInformationDataBinding.textView.setLayoutParams(layoutParams);
            }
        }

        if (addRefundPolicy == true)
        {
            LayoutStayOutboundDetailInformationDataBinding detailInformationDataBinding = DataBindingUtil.inflate(layoutInflater//
                , R.layout.layout_stay_outbound_detail_information_data, viewDataBinding.informationLayout, true);

            detailInformationDataBinding.textView.setText(R.string.message_stay_detail_nrd);
            detailInformationDataBinding.textView.setTextColor(getColor(R.color.dh_theme_color));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) detailInformationDataBinding.textView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            detailInformationDataBinding.textView.setLayoutParams(layoutParams);
        }

        if (lastView == true)
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewDataBinding.informationLayout.getLayoutParams();
            layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 20);
            viewDataBinding.informationLayout.setLayoutParams(layoutParams);
        }
    }

    /**
     * 문의 상담
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

        viewDataBinding.conciergeTime01TextView.setText(getString(R.string.message_consult02, startHour, endHour));
        viewDataBinding.conciergeTime02TextView.setText(getString(R.string.message_consult03, startLunchTime, endLunchTime));
        viewDataBinding.conciergeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onConciergeClick();
            }
        });
    }

    private void setRoomList(StayBookDateTime stayBookDateTime, List<StayRoom> roomList, boolean activeReward)
    {
        if (getViewDataBinding() == null || stayBookDateTime == null || roomList == null || roomList.size() == 0)
        {
            return;
        }

        // 처음 세팅하는 경우 객실 타입 세팅
        if (mRoomTypeListAdapter == null)
        {
            mRoomTypeListAdapter = new StayDetailRoomListAdapter(getContext(), roomList, new View.OnClickListener()
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

        mRoomTypeListAdapter.setNights(stayBookDateTime.getNights());
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
