package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.Sticker;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetDetailDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailAmenitiesDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailBenefitContentBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailBenefitDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailCouponDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMapDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMenuDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailMoreMenuDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetDetailTitleDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail05DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailInformationDataBinding;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.DailyDetailEmptyView;
import com.twoheart.dailyhotel.widget.TextTransition;

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
    implements GourmetDetailViewInterface, View.OnClickListener, ViewPager.OnPageChangeListener
{
    private GourmetDetailImageViewPagerAdapter mImageViewPagerAdapter;

    private LinearLayout mMoreMenuLayout;
    private LayoutGourmetDetailMoreMenuDataBinding mLayoutGourmetDetailMoreMenuDataBinding;

    private ObjectAnimator mShowBottomAnimator;
    private ObjectAnimator mHideBottomAnimator;
    private AnimatorSet mWishAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onShareClick();

        void onWishClick();

        void onShareKakaoClick();

        void onShareSmsClick();

        void onImageClick(int position);

        void onImageSelected(int position);

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

        void onMenuClick(int index);
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
                if (nestedScrollView.getScrollY() == 0//
                    || nestedScrollView.getScrollY() + TOOLBAR_HEIGHT > getViewDataBinding().menuLayout.getBottom()//
                    || scrollY + nestedScrollView.getHeight() < getViewDataBinding().dateInformationView.getTop())
                {
                    showBottomLayout(true);
                } else
                {
                    hideBottomLayout(true);
                }
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge));

        mImageViewPagerAdapter = new GourmetDetailImageViewPagerAdapter(getContext());
        viewDataBinding.imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        viewDataBinding.viewpagerIndicator.setViewPager(viewDataBinding.imageLoopViewPager);

        viewDataBinding.imageLoopViewPager.setOnPageChangeListener(this);
        viewDataBinding.viewpagerIndicator.setOnPageChangeListener(this);

        viewDataBinding.bottomLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onActionButtonClick();
            }
        });

        viewDataBinding.wishScrollView.setVisibility(View.GONE);
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
    public void setGourmetDetail(GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail, int trueReviewCount, int shownMenuCount)
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
        setTitleView(gourmetDetail.category, gourmetDetail.categorySub, gourmetDetail.name, gourmetDetail.ratingShow//
            , gourmetDetail.ratingValue, gourmetDetail.ratingPersons, trueReviewCount);

        // 쿠폰
        setCouponView(gourmetDetail.hasCoupon);

        // 메뉴 리스트
        setMenuListLayout(gourmetDetail.getGourmetMenuList(), shownMenuCount);

        // 방문일
        setVisitDateView(gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));

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
    public Observable<Boolean> getSharedElementTransition()
    {
        TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
        Transition inNameTextTransition = new TextTransition(getColor(R.color.white), getColor(R.color.default_text_c323232)//
            , 17, 18, new LinearInterpolator());
        inNameTextTransition.addTarget(getString(R.string.transition_place_name));
        inTransitionSet.addTransition(inNameTextTransition);

        Transition inBottomAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
        inBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
        inTransitionSet.addTransition(inBottomAlphaTransition);

        Transition inTopAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
        inTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
        inTransitionSet.addTransition(inTopAlphaTransition);

        getWindow().setSharedElementEnterTransition(inTransitionSet);

        TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
        Transition outNameTextTransition = new TextTransition(getColor(R.color.default_text_c323232), getColor(R.color.white)//
            , 18, 17, new LinearInterpolator());
        outNameTextTransition.addTarget(getString(R.string.transition_place_name));
        outTransitionSet.addTransition(outNameTextTransition);

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
            mImageViewPagerAdapter = new GourmetDetailImageViewPagerAdapter(getContext());
        }

        DetailImageInformation detailImage = new DetailImageInformation();
        detailImage.url = url;

        List<DetailImageInformation> imageList = new ArrayList<>();
        imageList.add(detailImage);

        mImageViewPagerAdapter.setData(imageList);
        getViewDataBinding().imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        getViewDataBinding().viewpagerIndicator.setViewPager(getViewDataBinding().imageLoopViewPager);
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

        if (mImageViewPagerAdapter == null)
        {
            mImageViewPagerAdapter = new GourmetDetailImageViewPagerAdapter(getContext());
        }

        DetailImageInformation detailImage = new DetailImageInformation();
        detailImage.url = url;

        List<DetailImageInformation> imageList = new ArrayList<>();
        imageList.add(detailImage);

        mImageViewPagerAdapter.setData(imageList);
        getViewDataBinding().imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        getViewDataBinding().viewpagerIndicator.setViewPager(getViewDataBinding().imageLoopViewPager);
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
            getViewDataBinding().transTitleLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().transImageView.setTransitionName(getString(R.string.transition_place_image));
            getViewDataBinding().transGradientBottomView.setTransitionName(getString(R.string.transition_gradient_bottom_view));
            getViewDataBinding().transGradientTopView.setTransitionName(getString(R.string.transition_gradient_top_view));
            getViewDataBinding().transNameTextView.setTransitionName(getString(R.string.transition_place_name));

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
            case GourmetDetailPresenter.STATUS_NONE:
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);
                break;

            case GourmetDetailPresenter.STATUS_SELECT_MENU:
                getViewDataBinding().bookingTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().soldoutTextView.setVisibility(View.GONE);

                getViewDataBinding().bookingTextView.setText(R.string.act_hotel_search_ticket);
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
        } else if (getViewDataBinding().toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_ON) == true)
        {
            getViewDataBinding().toolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_ON, wishCountText, new View.OnClickListener()
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
        } else if (getViewDataBinding().fakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_ON) == true)
        {
            getViewDataBinding().fakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_ON, wishCountText, new View.OnClickListener()
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
                getViewDataBinding().toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_ON, new View.OnClickListener()
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
                getViewDataBinding().fakeToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_ON, new View.OnClickListener()
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
            if (getViewDataBinding().toolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_ON) == true)
            {
                getViewDataBinding().toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_ON, DailyToolbarView.MenuItem.WISH_OFF, new View.OnClickListener()
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
                getViewDataBinding().toolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_ON, DailyToolbarView.MenuItem.WISH_OFF, new View.OnClickListener()
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
    public void scrollTop()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().nestedScrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void scrollTopMenu()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

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
                            mLayoutGourmetDetailMoreMenuDataBinding.moreImageView.setRotation(0);
                            mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.setText(getString(R.string.label_gourmet_detail_view_more, (int) mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.getTag()));
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

        return observable;
    }

    @Override
    public Observable<Boolean> showWishView(boolean myWish)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        if (mWishAnimatorSet != null && mWishAnimatorSet.isRunning() == true)
        {
            return null;
        }

        if (myWish == true)
        {
            getViewDataBinding().wishTextView.setText(R.string.wishlist_detail_add_message);
            getViewDataBinding().wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_fill_l, 0, 0);
            getViewDataBinding().wishTextView.setBackgroundResource(R.drawable.shape_filloval_ccdb2453);
        } else
        {
            getViewDataBinding().wishTextView.setText(R.string.wishlist_detail_delete_message);
            getViewDataBinding().wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_stroke_l, 0, 0);
            getViewDataBinding().wishTextView.setBackgroundResource(R.drawable.shape_filloval_a5000000);
        }

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(getViewDataBinding().wishTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f, 1.0f) //
        );
        objectAnimator1.setInterpolator(new AccelerateInterpolator());
        objectAnimator1.setDuration(300);


        ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(getViewDataBinding().wishTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 1.0f) //
        );
        objectAnimator2.setDuration(600);


        ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(getViewDataBinding().wishTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f) //
        );
        objectAnimator3.setDuration(200);

        mWishAnimatorSet = new AnimatorSet();
        mWishAnimatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                mWishAnimatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        getViewDataBinding().wishScrollView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        mWishAnimatorSet.removeAllListeners();
                        mWishAnimatorSet = null;

                        getViewDataBinding().wishScrollView.setVisibility(View.GONE);

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

                mWishAnimatorSet.start();
            }
        };

        return observable;
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

        if (imageList == null || imageList.size() == 0)
        {
            setViewPagerLineIndicatorVisible(false);
            return;
        } else if (imageList.size() == 1)
        {
            setViewPagerLineIndicatorVisible(false);
        } else
        {
            setViewPagerLineIndicatorVisible(true);
        }

        setDetailImageCaption(imageList.get(0).caption);

        if (mImageViewPagerAdapter == null)
        {
            mImageViewPagerAdapter = new GourmetDetailImageViewPagerAdapter(getContext());
        }

        mImageViewPagerAdapter.setData(imageList);
        getViewDataBinding().imageLoopViewPager.setAdapter(mImageViewPagerAdapter);
        getViewDataBinding().viewpagerIndicator.setViewPager(getViewDataBinding().imageLoopViewPager);
        mImageViewPagerAdapter.notifyDataSetChanged();
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
            getViewDataBinding().stickerSimpleDraweeView.setVisibility(View.GONE);
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

        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            getViewDataBinding().stickerSimpleDraweeView.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().stickerSimpleDraweeView.setVisibility(View.VISIBLE);
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
            {
                ViewGroup.LayoutParams layoutParams = getViewDataBinding().stickerSimpleDraweeView.getLayoutParams();

                int screenWidth = ScreenUtils.getScreenWidth(getContext());
                if (screenWidth > Sticker.DEFAULT_SCREEN_WIDTH && screenWidth < Sticker.LARGE_SCREEN_WIDTH)
                {
                    layoutParams.width = (int) (Sticker.MEDIUM_RATE * imageInfo.getWidth());
                    layoutParams.height = (int) (Sticker.MEDIUM_RATE * imageInfo.getHeight());
                } else
                {
                    layoutParams.width = imageInfo.getWidth();
                    layoutParams.height = imageInfo.getHeight();
                }

                getViewDataBinding().stickerSimpleDraweeView.setLayoutParams(layoutParams);
            }
        }).setUri(Uri.parse(url)).build();

        getViewDataBinding().stickerSimpleDraweeView.setController(controller);
    }

    /**
     * 등급 및 이름
     *
     * @return
     */
    private void setTitleView(String category, String categorySub, String name, boolean ratingShow//
        , int ratingValue, int ratingPersons, int trueReviewCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        LayoutGourmetDetailTitleDataBinding viewDataBinding = getViewDataBinding().titleViewDataBinding;

        // 카테고리
        if (DailyTextUtils.isTextEmpty(category) == true)
        {
            viewDataBinding.categoryTextView.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.categoryTextView.setVisibility(View.VISIBLE);
            viewDataBinding.categoryTextView.setText(category);
        }

        // 서브 카테고리
        if (DailyTextUtils.isTextEmpty(categorySub) == true)
        {
            viewDataBinding.categorySubTextView.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.categorySubTextView.setVisibility(View.VISIBLE);
            viewDataBinding.categorySubTextView.setText(categorySub);
        }

        // 레스토랑명
        viewDataBinding.nameTextView.setText(name);

        // 만족도
        if (ratingShow == false)
        {
            viewDataBinding.satisfactionTextView.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.satisfactionTextView.setVisibility(View.VISIBLE);

            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            viewDataBinding.satisfactionTextView.setText(getString(R.string.label_gourmet_detail_satisfaction, //
                ratingValue, decimalFormat.format(ratingPersons)));
        }

        // 리뷰
        if (trueReviewCount > 0)
        {
            viewDataBinding.trueReviewTextView.setVisibility(View.VISIBLE);
            viewDataBinding.trueReviewTextView.setText(getString(R.string.label_detail_view_review_go, trueReviewCount));
            viewDataBinding.trueReviewTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onTrueReviewClick();
                }
            });
        } else
        {
            viewDataBinding.trueReviewTextView.setVisibility(View.GONE);
        }
    }

    /**
     * @param hasCoupon
     */
    private void setCouponView(boolean hasCoupon)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        LayoutGourmetDetailCouponDataBinding viewDataBinding = getViewDataBinding().couponViewDataBinding;

        if (hasCoupon == true)
        {
            viewDataBinding.couponLayout.setVisibility(View.VISIBLE);
            viewDataBinding.downloadCouponLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onDownloadCouponClick();
                }
            });
        } else
        {
            viewDataBinding.couponLayout.setVisibility(View.GONE);
        }
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

    private void setMenuListLayout(List<GourmetMenu> gourmetMenuList, int shownMenuCount)
    {
        if (getViewDataBinding() == null || gourmetMenuList == null)
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

        int size = gourmetMenuList.size();

        if (size > shownMenuCount)
        {
            mMoreMenuLayout = new LinearLayout(getContext());
            mMoreMenuLayout.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < size; i++)
            {
                if (i < shownMenuCount)
                {
                    setMenuLayout(layoutInflater, getViewDataBinding().menuLayout, i, gourmetMenuList.get(i));
                } else if (i == shownMenuCount)
                {
                    getViewDataBinding().menuLayout.addView(mMoreMenuLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    mLayoutGourmetDetailMoreMenuDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_gourmet_detail_more_menu_data, getViewDataBinding().menuLayout, true);
                    mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.setText(getString(R.string.label_gourmet_detail_view_more, size));
                    mLayoutGourmetDetailMoreMenuDataBinding.moreTextView.setTag(size);
                    mLayoutGourmetDetailMoreMenuDataBinding.getRoot().setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            getEventListener().onMoreMenuClick();
                        }
                    });

                    setMenuLayout(layoutInflater, mMoreMenuLayout, i, gourmetMenuList.get(i));
                } else
                {
                    setMenuLayout(layoutInflater, mMoreMenuLayout, i, gourmetMenuList.get(i));
                }
            }

            mMoreMenuLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mMoreMenuLayout.setTag(mMoreMenuLayout.getHeight());

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMoreMenuLayout.getLayoutParams();
                    layoutParams.height = 0;
                    mMoreMenuLayout.requestLayout();
                }
            });
        } else
        {
            for (int i = 0; i < size; i++)
            {
                setMenuLayout(layoutInflater, getViewDataBinding().menuLayout, i, gourmetMenuList.get(i));
            }
        }
    }

    private void setMenuLayout(LayoutInflater layoutInflater, ViewGroup parent, int index, GourmetMenu gourmetMenu)
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
                getEventListener().onMenuClick(index);
            }
        });

        if (gourmetMenu.getPrimaryImage() == null)
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

            viewDataBinding.simpleDraweeView.setImageURI(Uri.parse(gourmetMenu.getPrimaryImage().url + "?impolicy=" + url));
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

        // 이용시간
        if (DailyTextUtils.isTextEmpty(gourmetMenu.openTime, gourmetMenu.closeTime) == true)
        {
            viewDataBinding.timeTextView.setVisibility(View.GONE);
        } else
        {
            String timeFormat = getString(R.string.label_office_hours) + " " + String.format(Locale.KOREA, "%s ~ %s", gourmetMenu.openTime, gourmetMenu.closeTime);
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

        // 마지막 라인 넣기
        final int DP_15 = ScreenUtils.dpToPx(getContext(), 15);
        View underLineView = new View(getContext());
        underLineView.setBackgroundResource(R.color.default_line_cdcdcdd);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layoutParams.setMargins(DP_15, 0, DP_15, 0);
        parent.addView(underLineView, layoutParams);
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

        if (informationList == null && informationList.size() == 0)
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

            detailInformationDataBinding.textView.setText(Html.fromHtml(informationList.get(i)));

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

    private void setViewPagerLineIndicatorVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            getViewDataBinding().moreIconView.setVisibility(View.VISIBLE);
            getViewDataBinding().viewpagerIndicator.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().moreIconView.setVisibility(View.INVISIBLE);
            getViewDataBinding().viewpagerIndicator.setVisibility(View.INVISIBLE);
        }
    }


    private void showBottomLayout(boolean animation)
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
                    mShowBottomAnimator.removeAllListeners();
                    mShowBottomAnimator = null;

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

    private void hideBottomLayout(boolean animation)
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
                    mHideBottomAnimator.removeAllListeners();
                    mHideBottomAnimator = null;

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
        final int colors[] = {Color.parseColor("#E6000000"), Color.parseColor("#99000000"), Color.parseColor("#1A000000"), Color.parseColor("#00000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.24f, 0.66f, 0.8f, 1.0f};

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
