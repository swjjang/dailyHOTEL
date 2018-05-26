package com.twoheart.dailyhotel.screen.review;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyScrollView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.StringFilter;

/**
 * Created by android_sam on 2016. 12. 5..
 */

public class WriteReviewCommentLayout extends BaseLayout
{
    private View mCompleteView;
    TextView mToolbarTitleView;
    View mBodyTitleLayout;
    DailyEditText mEditTextView;
    private TextView mBottomTextCountView;
    private View mBottomLayout;
    private DailyScrollView mScrollView;

    boolean mIsShowAnimationStart;
    boolean mIsHideAnimationStart;
    private AlphaAnimation mAlphaAnimation;

    int mBodyTitleBottom = 0;

    private static final int DEFAULT_TEXT_COUNT = 10;
    private static final int TOOLBAR_TITLE_ANIMATION_DURATION = 200;


    public interface OnEventListener extends OnBaseEventListener
    {
        void onCompleteClick(String text);

        void onBackPressed();
    }

    public WriteReviewCommentLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mToolbarTitleView = view.findViewById(R.id.titleTextView);
        mCompleteView = view.findViewById(R.id.completeTextView);
        mBodyTitleLayout = view.findViewById(R.id.bodyTitleLayout);
        mBottomLayout = view.findViewById(R.id.textCountLayout);
        mBottomTextCountView = view.findViewById(R.id.textCountView);
        mEditTextView = view.findViewById(R.id.writeReviewEditText);
        mScrollView = view.findViewById(R.id.scrollView);

        View backView = view.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onBackPressed();
            }
        });

        setToolbarTitleVisibility(false, false);

        mCompleteView.setEnabled(false);
        mBottomLayout.setVisibility(View.GONE);

        StringFilter stringFilter = new StringFilter(mContext);
        InputFilter[] allowReviewFilter = new InputFilter[2];
        allowReviewFilter[0] = stringFilter.allowReviewFilter;
        allowReviewFilter[1] = new InputFilter.LengthFilter(2000);

        mEditTextView.setFilters(allowReviewFilter);
        mEditTextView.addTextChangedListener(mEditTextWatcher);

        mScrollView.setScrollY(0);
        mScrollView.setOnScrollChangedListener(mScrollChangeListener);
    }

    public void setData(Constants.ServiceType serviceType, String text)
    {
        updateEditTextView(serviceType, text);
        updateCompleteLayout(text);
        updateTextCountLayout(text);
    }

    public String getReviewText()
    {
        if (mEditTextView == null)
        {
            return null;
        }

        return mEditTextView.getText().toString();
    }

    private void updateEditTextView(Constants.ServiceType serviceType, String text)
    {
        if (serviceType != null)
        {
            switch (serviceType)
            {
                case HOTEL:
                    mEditTextView.setHint(R.string.label_write_review_comment_hint_stay);
                    break;

                case GOURMET:
                    mEditTextView.setHint(R.string.label_write_review_comment_hint_gourmet);
                    break;

                case OB_STAY:
                    mEditTextView.setHint(R.string.label_write_review_comment_hint_stay);
                    break;
            }
        }

        mEditTextView.setText(text);

        if (DailyTextUtils.isTextEmpty(text) == false)
        {
            mEditTextView.setSelection(text.length());
        }
    }

    void updateCompleteLayout(String text)
    {
        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            text = "";
        }

        int remainder = DEFAULT_TEXT_COUNT - text.length();
        mCompleteView.setEnabled(remainder <= 0);
        mCompleteView.setOnClickListener(remainder <= 0 ? new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = mEditTextView == null ? "" : mEditTextView.getText().toString();
                ExLog.d("complete click : " + text);

                ((OnEventListener) mOnEventListener).onCompleteClick(text);
            }
        } : null);
    }

    void updateTextCountLayout(String text)
    {
        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            text = "";
        }

        int remainder = DEFAULT_TEXT_COUNT - text.length();
        if (remainder <= 0)
        {
            mBottomLayout.setVisibility(View.GONE);
        } else
        {
            mBottomLayout.setVisibility(View.VISIBLE);

            String countString = mContext.getString(R.string.label_write_review_comment_count_format, remainder);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(countString);

            int length = Integer.toString(remainder).length();

            stringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_text_c4d4d4d)), //
                0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBottomTextCountView.setText(stringBuilder);
        }
    }

    public void showKeyboard()
    {
        if (mEditTextView == null)
        {
            return;
        }

        mEditTextView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mEditTextView, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);
    }


    public void setToolbarTitleVisibility(boolean visible, boolean isAnimation)
    {
        if (mToolbarTitleView == null)
        {
            return;
        }

        if (visible == true)
        {
            if (mToolbarTitleView.getVisibility() != View.VISIBLE)
            {
                if (isAnimation == true && mIsShowAnimationStart == false)
                {
                    mIsHideAnimationStart = false;
                    showTitleViewAnimation();
                } else
                {
                    mToolbarTitleView.setVisibility(View.VISIBLE);

                    mIsShowAnimationStart = false;
                    mIsHideAnimationStart = false;
                }
            }
        } else
        {
            if (mToolbarTitleView.getVisibility() == View.VISIBLE)
            {
                if (isAnimation == true && mIsHideAnimationStart == false)
                {
                    mIsShowAnimationStart = false;
                    hideTitleViewAnimation();
                } else
                {
                    mToolbarTitleView.setVisibility(View.GONE);

                    mIsShowAnimationStart = false;
                    mIsHideAnimationStart = false;
                }
            }
        }
    }

    private void showTitleViewAnimation()
    {
        if (mToolbarTitleView == null)
        {
            return;
        }

        if (mIsShowAnimationStart == true)
        {
            return;
        } else
        {
            mIsShowAnimationStart = true;
        }

        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(TOOLBAR_TITLE_ANIMATION_DURATION);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                mToolbarTitleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mIsShowAnimationStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mToolbarTitleView.startAnimation(mAlphaAnimation);
    }

    private void hideTitleViewAnimation()
    {
        if (mToolbarTitleView == null)
        {
            return;
        }

        if (mIsHideAnimationStart == true)
        {
            return;
        } else
        {
            mIsHideAnimationStart = true;
        }

        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimation.setDuration(TOOLBAR_TITLE_ANIMATION_DURATION);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mToolbarTitleView.setVisibility(View.GONE);
                mIsHideAnimationStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mToolbarTitleView.startAnimation(mAlphaAnimation);
    }

    private TextWatcher mEditTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            String text = (s == null || s.length() == 0) ? "" : s.toString();
            updateTextCountLayout(text);
            updateCompleteLayout(text);
        }
    };

    private DailyScrollView.OnScrollChangedListener mScrollChangeListener = new DailyScrollView.OnScrollChangedListener()
    {
        @Override
        public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
        {
            if (scrollView != null)
            {
                if (mBodyTitleBottom == 0)
                {
                    int bottom = mBodyTitleLayout.getBottom();
                    if (bottom > 0)
                    {
                        mBodyTitleBottom = bottom;
                    }
                }

                setToolbarTitleVisibility((mBodyTitleBottom <= t), true);
            }
        }
    };
}
