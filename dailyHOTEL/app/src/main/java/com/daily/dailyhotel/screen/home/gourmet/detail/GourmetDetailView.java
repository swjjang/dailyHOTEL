package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.daily.base.widget.DailyRadioButton;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.Sticker;
import com.daily.dailyhotel.entity.TrueAwards;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyDetailEmptyView;
import com.daily.dailyhotel.view.DailyDetailTitleInformationView;
import com.daily.dailyhotel.view.DailyDetailTrueAwardsView;
import com.daily.dailyhotel.view.DailyDetailTrueReviewView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetDetailDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogDailyAwardsDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailAmenitiesDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailBenefitContentBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailBenefitDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMapDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMenuDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMoreMenuDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail05DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailInformationDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
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

public class GourmetDetailView extends BaseDialogView<GourmetDetailView.OnEventListener, ActivityGourmetDetailDataBinding>//
    implements GourmetDetailViewInterface, View.OnClickListener
{
    LinearLayout mMoreMenuLayout;
    LayoutGourmetDetailMoreMenuDataBinding mLayoutGourmetDetailMoreMenuDataBinding;

    ObjectAnimator mShowBottomAnimator;
    ObjectAnimator mHideBottomAnimator;

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

        void onActionButtonClick();

        void onConciergeFaqClick();

        void onConciergeHappyTalkClick();

        void onConciergeCallClick();

        void onTrueReviewClick();

        void onDownloadCouponClick();

        void onMoreMenuClick();

        void onMenuClick(int index, int position);

        void onHideWishTooltipClick();

        void onVisitTimeClick(String visitTime);

        void onBookingClick();

        void onTrueAwardsClick();

        void onTrueVRClick();
    }

    public GourmetDetailView(BaseActivity baseActivity, GourmetDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetDetailDataBinding viewDataBinding)
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

                // 겹치지 않은 경우
                if (scrollY + nestedScrollView.getHeight() >= getViewDataBinding().mapViewDataBinding.getRoot().getBottom())
                {
                    showBottomLayout(true);
                } else
                {
                    hideBottomLayout(true);
                }

                getViewDataBinding().fakeVRImageView.setEnabled(scrollY <= TOOLBAR_HEIGHT);
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.bottomLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onActionButtonClick();
            }
        });

        viewDataBinding.wishAnimationView.setVisibility(View.GONE);

        viewDataBinding.wishTooltipLayout.setVisibility(View.GONE);
        viewDataBinding.wishTooltipLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onHideWishTooltipClick();
            }
        });

        hideBottomLayout(false);
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
            case R.id.dateLayout:
                getEventListener().onCalendarClick();
                break;
        }
    }

    @Override
    public void setGourmetDetail(GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail//
        , List<String> operationTimeList, int trueReviewCount, int shownMenuCount)
    {
        if (getViewDataBinding() == null || gourmetBookDateTime == null || gourmetDetail == null)
        {
            return;
        }

        getViewDataBinding().nestedScrollView.setVisibility(View.VISIBLE);
        getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);

        setImageList(gourmetDetail.getImageInformationList());

        // 이미지 상단에 빈화면 넣기
        setEmptyView();

        // Sticker
        setStickerView(gourmetDetail.getSticker());

        // 타이틀
        setTitleView(gourmetDetail.category, gourmetDetail.categorySub, gourmetDetail.name, false, gourmetDetail.couponPrice);

        // 트루 리뷰
        setTrueReviewView(gourmetDetail.ratingShow, gourmetDetail.ratingValue, gourmetDetail.ratingPersons, trueReviewCount);

        // 트루 어워드
        setTrueAwardsView(gourmetDetail.awards);

        // 방문일
        setVisitDateView(gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));

        // 전체 시간 메뉴
        setOperationTimes(operationTimeList);

        // 메뉴 리스트
        //        setMenuListLayout(gourmetDetail.getGourmetMenuList(), shownMenuCount);

        // 주소 및 맵
        setAddressView(gourmetDetail.address);

        // Amenity
        setAmenitiesView(gourmetDetail.getPictogramList());

        // Benefit
        setBenefitView(gourmetDetail.benefit, gourmetDetail.getBenefitContentList());

        // 정보 화면
        setDescriptionsView(gourmetDetail.getDescriptionList());

        // 문의
        setConciergeView();
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
            case GourmetDetailPresenter.STATUS_NONE:
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);
                break;

            case GourmetDetailPresenter.STATUS_SELECT_MENU:
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.label_gourmet_detail_view_product_detail);
                break;

            case GourmetDetailPresenter.STATUS_BOOKING:
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.act_hotel_booking);
                break;

            case GourmetDetailPresenter.STATUS_SOLD_OUT:
                getViewDataBinding().bookingTextView.setVisibility(View.GONE);
                getViewDataBinding().soldoutTextView.setVisibility(View.VISIBLE);
                break;
        }
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
    public void scrollTopMenu()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        // 이전 버전에서는 이상이 없었는데 27버전으로 올린후에 발생하는 현상으로 임시로 수정하였음.
        getViewDataBinding().nestedScrollView.smoothScrollTo(0, getViewDataBinding().nestedScrollView.getScrollY());
        getViewDataBinding().nestedScrollView.smoothScrollTo(0, (int) getViewDataBinding().dateInformationView.getY()//
            - getDimensionPixelSize(R.dimen.toolbar_height) - ScreenUtils.dpToPx(getContext(), 12));
    }

    @Override
    public Observable<Boolean> openMoreMenuList()
    {
        if (getViewDataBinding() == null || mMoreMenuLayout == null)
        {
            return null;
        }

        Integer height = (Integer) mMoreMenuLayout.getTag();

        if (height == null)
        {
            return null;
        }

        if (isOpenedMoreMenuList() == true)
        {
            return null;
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                if (valueAnimator == null)
                {
                    return;
                }

                int val = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mMoreMenuLayout.getLayoutParams();
                layoutParams.height = val;
                mMoreMenuLayout.requestLayout();
            }
        });
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                valueAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        valueAnimator.removeAllUpdateListeners();
                        valueAnimator.removeAllListeners();

                        if (mLayoutGourmetDetailMoreMenuDataBinding != null)
                        {
                            mLayoutGourmetDetailMoreMenuDataBinding.moreImageView.setRotation(180);
                            mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.setText(R.string.label_collapse);
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

                valueAnimator.start();
            }
        };

        return observable;
    }

    @Override
    public boolean isOpenedMoreMenuList()
    {
        if (getViewDataBinding() == null || mMoreMenuLayout == null)
        {
            return false;
        }

        return mMoreMenuLayout.getHeight() > 0;
    }

    @Override
    public Observable<Boolean> closeMoreMenuList()
    {
        if (getViewDataBinding() == null || mMoreMenuLayout == null)
        {
            return null;
        }

        Integer height = (Integer) mMoreMenuLayout.getTag();

        if (height == null)
        {
            return null;
        }

        if (isOpenedMoreMenuList() == false)
        {
            return null;
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                if (valueAnimator == null)
                {
                    return;
                }

                int val = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mMoreMenuLayout.getLayoutParams();
                layoutParams.height = val;
                mMoreMenuLayout.requestLayout();
            }
        });

        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                valueAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        valueAnimator.removeAllUpdateListeners();
                        valueAnimator.removeAllListeners();

                        if (mLayoutGourmetDetailMoreMenuDataBinding != null)
                        {
                            mLayoutGourmetDetailMoreMenuDataBinding.moreImageView.setRotation(0);
                            mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.setText((String) mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.getTag());
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

                scrollTopMenu();
                valueAnimator.start();
            }
        };
    }

    @Override
    public void setMenus(List<GourmetMenu> gourmetMenuList, int shownMenuCount)
    {
        setMenuListLayout(gourmetMenuList, shownMenuCount);
    }

    @Override
    public void setToolbarCartMenusVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setMenuItemVisible(DailyToolbarView.MenuItem.ORDER_MENUS, visible);
        getViewDataBinding().fakeToolbarView.setMenuItemVisible(DailyToolbarView.MenuItem.ORDER_MENUS, visible);
    }

    @Override
    public void setToolbarCartMenusCount(int count)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.updateMenuItem(DailyToolbarView.MenuItem.ORDER_MENUS, null, count, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBookingClick();
            }
        });

        getViewDataBinding().fakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.ORDER_MENUS, null, count, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBookingClick();
            }
        });
    }

    @Override
    public void performVisitTimeClick(String time)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int childCount = getViewDataBinding().timesRadioGroup.getChildCount();
        View childView;

        for (int i = 0; i < childCount; i++)
        {
            childView = getViewDataBinding().timesRadioGroup.getChildAt(i);
            String tag = (String) childView.getTag();

            if (tag == null)
            {
                continue;
            }

            if (tag.equalsIgnoreCase(time) == true)
            {
                getViewDataBinding().timesScrollView.scrollTo((int) (childView.getX() - (getViewDataBinding().timesScrollView.getWidth() - childView.getWidth()) / 2), 0);
                childView.performClick();
                break;
            }
        }
    }

    @Override
    public void performFirstMenuClick()
    {
        if (getViewDataBinding() == null || getViewDataBinding().menuLayout.getChildCount() == 0)
        {
            return;
        }

        getViewDataBinding().menuLayout.getChildAt(0).performClick();
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

    private void initToolbar(ActivityGourmetDetailDataBinding viewDataBinding)
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

        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.ORDER_MENUS, null, 0, null);

        viewDataBinding.toolbarView.setMenuItemVisible(DailyToolbarView.MenuItem.ORDER_MENUS, false);

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

        viewDataBinding.fakeToolbarView.addMenuItem(DailyToolbarView.MenuItem.ORDER_MENUS, null, 0, null);

        viewDataBinding.toolbarView.setMenuItemVisible(DailyToolbarView.MenuItem.ORDER_MENUS, false);

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
     * 스티커
     *
     * @param sticker
     */
    private void setStickerView(Sticker sticker)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (sticker == null)
        {
            getViewDataBinding().imageLoopView.setStickerVisible(false);
            return;
        }

        String url;
        if (ScreenUtils.getScreenWidth(getContext()) <= Sticker.DEFAULT_SCREEN_WIDTH)
        {
            url = sticker.lowResolutionImageUrl;
        } else
        {
            url = sticker.defaultImageUrl;
        }

        getViewDataBinding().imageLoopView.setStickerVisible(true);
        getViewDataBinding().imageLoopView.setStickerUrl(url);
    }

    /**
     * 등급 및 이름
     *
     * @return
     */
    private void setTitleView(String category, String subCategory, String name, boolean dailyReward, int couponPrice)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyDetailTitleInformationView titleInformationView = getViewDataBinding().titleInformationView;

        // 고메명
        titleInformationView.setNameText(name);
        titleInformationView.setEnglishNameVisible(false);

        // 카테고리
        titleInformationView.setCategory(category, subCategory);

        // 리워드 여부
        // 고메에서는 점 이미지를 화살표 이미지로 바꾸어서 사용한다.
        //        titleInformationView.setRewardVisible(dailyReward);

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
                trueReviewView.setSatisfactionVText(getString(R.string.label_gourmet_detail_satisfaction, //
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
     * @param visitDate
     */
    private void setVisitDateView(String visitDate)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().dateInformationView.setDateVisible(true, false);
        getViewDataBinding().dateInformationView.setDate1Text(getString(R.string.label_visit_day), visitDate);
        getViewDataBinding().dateInformationView.setCenterNightsVisible(false);
        getViewDataBinding().dateInformationView.setDate1DescriptionTextColor(getColor(R.color.default_text_cb70038));
        getViewDataBinding().dateInformationView.setDate1DescriptionTextDrawable(0, 0, R.drawable.navibar_m_burg_ic_v, 0);
        getViewDataBinding().dateInformationView.setData1TextSize(13.0f, 15.0f);

        getViewDataBinding().dateInformationView.setOnDateClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onCalendarClick();
            }
        }, null);
    }

    private void setOperationTimes(List<String> operationTimeList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int size = operationTimeList == null ? 0 : operationTimeList.size();

        if (size == 0)
        {
            getViewDataBinding().timesTopLineView.setVisibility(View.GONE);
            getViewDataBinding().timesScrollView.setVisibility(View.GONE);

            return;
        }

        getViewDataBinding().timesRadioGroup.removeAllViews();

        getViewDataBinding().timesTopLineView.setVisibility(View.VISIBLE);
        getViewDataBinding().timesScrollView.setVisibility(View.VISIBLE);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onVisitTimeClick((String) v.getTag());
            }
        };

        // 전체 시간 넣기
        DailyRadioButton fullTimeTextView = new DailyRadioButton(getContext());
        fullTimeTextView.setText(R.string.label_gourmet_detail_full_time_menu);
        fullTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        fullTimeTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
        fullTimeTextView.setBackgroundResource(R.drawable.selector_gourmet_time_background_drawable);
        fullTimeTextView.setTag(GourmetDetailPresenter.FULL_TIME);
        fullTimeTextView.setButtonDrawable(new StateListDrawable());
        fullTimeTextView.setGravity(Gravity.CENTER);
        fullTimeTextView.setOnClickListener(onClickListener);

        RadioGroup.LayoutParams fullTimeLayoutParams = new RadioGroup.LayoutParams(ScreenUtils.dpToPx(getContext(), 95), ScreenUtils.dpToPx(getContext(), 30));
        fullTimeLayoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 15);
        fullTimeLayoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 10);

        getViewDataBinding().timesRadioGroup.addView(fullTimeTextView, fullTimeLayoutParams);

        // 실제 시간 넣기
        for (int i = 0; i < size; i++)
        {
            try
            {
                // 24시 이후 값은 00:00로 보이도록 한다.
                DailyRadioButton timeTextView = new DailyRadioButton(getContext());
                timeTextView.setText(DailyCalendar.convertDateFormatString(operationTimeList.get(i), DailyCalendar.ISO_8601_FORMAT, "HH:mm"));
                timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                timeTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
                timeTextView.setBackgroundResource(R.drawable.selector_gourmet_time_background_drawable);
                timeTextView.setTag(operationTimeList.get(i));
                timeTextView.setButtonDrawable(new StateListDrawable());
                timeTextView.setGravity(Gravity.CENTER);
                timeTextView.setOnClickListener(onClickListener);

                RadioGroup.LayoutParams timeLayoutParams = new RadioGroup.LayoutParams(ScreenUtils.dpToPx(getContext(), 56), ScreenUtils.dpToPx(getContext(), 30));

                if (i == size - 1)
                {
                    timeLayoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 15);
                } else
                {
                    timeLayoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 10);
                }

                getViewDataBinding().timesRadioGroup.addView(timeTextView, timeLayoutParams);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        fullTimeTextView.setChecked(true);
    }

    private void setMenuListLayout(List<GourmetMenu> gourmetMenuList, int shownMenuCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mMoreMenuLayout != null)
        {
            mMoreMenuLayout.removeAllViews();
            mMoreMenuLayout = null;
        }

        getViewDataBinding().menuLayout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        int visibleCount = 0;
        int menuCount = gourmetMenuList == null ? 0 : gourmetMenuList.size();

        if (menuCount == 0)
        {
            getViewDataBinding().menuTopLineView.setVisibility(View.GONE);
            getViewDataBinding().menuLayout.setVisibility(View.GONE);
        } else
        {
            for (GourmetMenu gourmetMenu : gourmetMenuList)
            {
                visibleCount += gourmetMenu.visible ? 1 : 0;
            }

            getViewDataBinding().menuTopLineView.setVisibility(View.VISIBLE);
            getViewDataBinding().menuLayout.setVisibility(View.VISIBLE);
        }

        if (visibleCount > shownMenuCount)
        {
            mMoreMenuLayout = new LinearLayout(getContext());
            mMoreMenuLayout.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0, position = 0; i < menuCount; i++)
            {
                if (gourmetMenuList.get(i).visible == false)
                {
                    continue;
                }

                if (position < shownMenuCount)
                {
                    setMenuLayout(layoutInflater, getViewDataBinding().menuLayout, i, position, gourmetMenuList.get(i), true);
                } else if (i == shownMenuCount)
                {
                    getViewDataBinding().menuLayout.addView(mMoreMenuLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    mLayoutGourmetDetailMoreMenuDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_gourmet_detail_more_menu_data, getViewDataBinding().menuLayout, true);
                    mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.setText(getString(R.string.label_gourmet_detail_view_more, visibleCount));
                    mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.setTag(mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.getText());
                    mLayoutGourmetDetailMoreMenuDataBinding.getRoot().setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            getEventListener().onMoreMenuClick();
                        }
                    });

                    setMenuLayout(layoutInflater, mMoreMenuLayout, i, position, gourmetMenuList.get(i), true);
                } else
                {
                    setMenuLayout(layoutInflater, mMoreMenuLayout, i, position, gourmetMenuList.get(i), true);
                }

                position++;
            }

            mMoreMenuLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @Override
                public boolean onPreDraw()
                {
                    try
                    {
                        mMoreMenuLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        mMoreMenuLayout.setTag(mMoreMenuLayout.getHeight());

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMoreMenuLayout.getLayoutParams();

                        if (layoutParams != null)
                        {
                            layoutParams.height = 0;
                            mMoreMenuLayout.setLayoutParams(layoutParams);
                        }
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }

                    return false;
                }
            });
        } else
        {
            for (int i = 0, position = 0; i < menuCount; i++)
            {
                if (gourmetMenuList.get(i).visible == false)
                {
                    continue;
                }

                setMenuLayout(layoutInflater, getViewDataBinding().menuLayout, i, position, gourmetMenuList.get(i), position != visibleCount - 1);

                position++;
            }
        }
    }

    private void setMenuLayout(LayoutInflater layoutInflater, ViewGroup parent, int index, int position, GourmetMenu gourmetMenu, boolean insertUnderLine)
    {
        if (layoutInflater == null || parent == null || gourmetMenu == null)
        {
            return;
        }

        LayoutGourmetDetailMenuDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_gourmet_detail_menu_data, parent, true);

        viewDataBinding.getRoot().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onMenuClick(index, position);
            }
        });

        if (gourmetMenu.getPrimaryImage() == null || gourmetMenu.getPrimaryImage().getImageMap() == null)
        {
            viewDataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_failure_image);
        } else
        {
            int dpi = getDpi();
            String url;

            if (dpi <= 240)
            {
                url = "android_gourmet_product_hdpi";
            } else if (dpi <= 480)
            {
                url = "android_gourmet_product_xhdpi";
            } else
            {
                url = "android_gourmet_product_xxxhdpi";
            }

            viewDataBinding.simpleDraweeView.setImageURI(Uri.parse(gourmetMenu.baseImageUrl + gourmetMenu.getPrimaryImage().getImageMap().bigUrl + "?impolicy=" + url));
            viewDataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder_s);
            viewDataBinding.simpleDraweeView.getHierarchy().setFailureImage(R.drawable.layerlist_failure_image);
        }

        // 메뉴 이름
        viewDataBinding.productNameTextView.setText(gourmetMenu.name);

        // 메뉴 가격
        String price = DailyTextUtils.getPriceFormat(getContext(), gourmetMenu.price, false);
        String discountPrice = DailyTextUtils.getPriceFormat(getContext(), gourmetMenu.discountPrice, false);

        if (gourmetMenu.price <= 0 || gourmetMenu.price <= gourmetMenu.discountPrice)
        {
            viewDataBinding.priceTextView.setVisibility(View.GONE);
            viewDataBinding.priceTextView.setText(null);
        } else
        {
            viewDataBinding.priceTextView.setText(price);
            viewDataBinding.priceTextView.setPaintFlags(viewDataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewDataBinding.priceTextView.setVisibility(View.VISIBLE);
        }

        viewDataBinding.discountPriceTextView.setText(discountPrice);

        // 인원수
        if (gourmetMenu.persons > 0)
        {
            viewDataBinding.personsTextView.setVisibility(View.VISIBLE);
            viewDataBinding.personsTextView.setText("/" + getString(R.string.label_persons, gourmetMenu.persons));
        } else
        {
            viewDataBinding.personsTextView.setVisibility(View.GONE);
        }

        // 이용시간
        if (DailyTextUtils.isTextEmpty(gourmetMenu.openTime, gourmetMenu.closeTime) == true)
        {
            viewDataBinding.timeTextView.setVisibility(View.GONE);
        } else
        {
            String closeTime = "00:00".equalsIgnoreCase(gourmetMenu.closeTime) ? "24:00" : gourmetMenu.closeTime;

            String timeFormat = getString(R.string.label_office_hours) + " " + String.format(Locale.KOREA, "%s ~ %s", gourmetMenu.openTime, closeTime);
            viewDataBinding.timeTextView.setText(timeFormat);
            viewDataBinding.timeTextView.setVisibility(View.VISIBLE);
        }

        // 베네핏
        if (DailyTextUtils.isTextEmpty(gourmetMenu.menuBenefit) == true)
        {
            viewDataBinding.benefitTextView.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.benefitTextView.setText(gourmetMenu.menuBenefit);
            viewDataBinding.benefitTextView.setVisibility(View.VISIBLE);
        }

        if (insertUnderLine == true)
        {
            // 마지막 라인 넣기
            final int DP_15 = ScreenUtils.dpToPx(getContext(), 15);
            View underLineView = new View(getContext());
            underLineView.setBackgroundResource(R.color.default_line_cdcdcdd);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            layoutParams.setMargins(DP_15, 0, DP_15, 0);
            parent.addView(underLineView, layoutParams);
        }
    }

    /**
     * 주소 및 맵
     *
     * @return
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
    private void setAmenitiesView(List<GourmetDetail.Pictogram> pictogramList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        LayoutGourmetDetailAmenitiesDataBinding viewDataBinding = getViewDataBinding().amenitiesViewDataBinding;
        viewDataBinding.amenitiesGridLayout.removeAllViews();

        if (pictogramList == null || pictogramList.size() == 0)
        {
            viewDataBinding.amenitiesLayout.setVisibility(View.GONE);
            return;
        }

        final int GRID_COLUMN_COUNT = 5;

        viewDataBinding.amenitiesLayout.setVisibility(View.VISIBLE);

        boolean isSingleLine = pictogramList.size() <= GRID_COLUMN_COUNT;

        for (GourmetDetail.Pictogram pictogram : pictogramList)
        {
            viewDataBinding.amenitiesGridLayout.addView(getGridLayoutItemView(getContext(), pictogram, isSingleLine));
        }

        int columnCount = pictogramList.size() % GRID_COLUMN_COUNT;

        if (columnCount != 0)
        {
            int addEmptyViewCount = GRID_COLUMN_COUNT - columnCount;
            for (int i = 0; i < addEmptyViewCount; i++)
            {
                viewDataBinding.amenitiesGridLayout.addView(getGridLayoutItemView(getContext(), GourmetDetail.Pictogram.none, isSingleLine));
            }
        }
    }

    private DailyTextView getGridLayoutItemView(Context context, GourmetDetail.Pictogram pictogram, boolean isSingleLine)
    {
        DailyTextView dailyTextView = new DailyTextView(context);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(getColorStateList(R.color.default_text_c323232));
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

    /**
     * 고메 Benefit
     */
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

        viewDataBinding.benefitContentsLayout.removeAllViews();

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
     * 정보
     *
     * @return
     */
    private void setDescriptionsView(List<LinkedHashMap<String, List<String>>> descriptionList)
    {
        if (getViewDataBinding() == null || descriptionList == null)
        {
            return;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        getViewDataBinding().descriptionsLayout.removeAllViews();

        for (LinkedHashMap<String, List<String>> descriptionMap : descriptionList)
        {
            Iterator<Map.Entry<String, List<String>>> iterator = descriptionMap.entrySet().iterator();

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

            detailInformationDataBinding.textView.setText(informationList.get(i));

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

    void showBottomLayout(boolean animation)
    {
        if (getViewDataBinding() == null || mShowBottomAnimator != null)
        {
            return;
        }

        if (mHideBottomAnimator != null && mHideBottomAnimator.isRunning() == true)
        {
            mHideBottomAnimator.cancel();
            mHideBottomAnimator = null;
        }

        if (animation == true)
        {
            mShowBottomAnimator = ObjectAnimator.ofFloat(getViewDataBinding().bottomLayout, View.ALPHA//
                , getViewDataBinding().bottomLayout.getAlpha(), 1.0f);
            mShowBottomAnimator.setDuration(300);
            mShowBottomAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mShowBottomAnimator != null)
                    {
                        mShowBottomAnimator.removeAllListeners();
                        mShowBottomAnimator = null;
                    }

                    getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);
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
            mShowBottomAnimator.start();
        } else
        {
            getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().bottomLayout.setAlpha(1.0f);
        }
    }

    void hideBottomLayout(boolean animation)
    {
        if (getViewDataBinding() == null || mHideBottomAnimator != null)
        {
            return;
        }

        if (mShowBottomAnimator != null && mShowBottomAnimator.isRunning() == true)
        {
            mShowBottomAnimator.cancel();
            mShowBottomAnimator = null;
        }

        if (animation == true)
        {
            mHideBottomAnimator = ObjectAnimator.ofFloat(getViewDataBinding().bottomLayout, View.ALPHA//
                , getViewDataBinding().bottomLayout.getAlpha(), 0.0f);
            mHideBottomAnimator.setDuration(300);
            mHideBottomAnimator.addListener(new Animator.AnimatorListener()
            {
                boolean mCanceled;

                @Override
                public void onAnimationStart(Animator animation)
                {
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mHideBottomAnimator != null)
                    {
                        mHideBottomAnimator.removeAllListeners();
                        mHideBottomAnimator = null;
                    }

                    if (mCanceled == false)
                    {
                        getViewDataBinding().bottomLayout.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mCanceled = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });
            mHideBottomAnimator.start();
        } else
        {
            getViewDataBinding().bottomLayout.setVisibility(View.INVISIBLE);
            getViewDataBinding().bottomLayout.setAlpha(0.0f);
        }
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
