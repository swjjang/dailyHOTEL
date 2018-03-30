package com.twoheart.dailyhotel.place.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.lang.ref.WeakReference;

@Deprecated
public abstract class PlaceCurationActivity extends BaseActivity implements View.OnClickListener
{
    private static final int HANDLE_MESSAGE_RESULT = 1;
    private static final int HANDLE_MESSAGE_DELAYTIME = 750;

    private TextView mConfirmView;

    private Handler mHandler;

    DailyLocationFactory mDailyLocationFactory;

    protected boolean mIsFixedLocation;

    protected abstract void initContentLayout(ViewGroup contentLayout);

    protected abstract void onComplete();

    protected abstract void onCancel();

    protected abstract void onReset();

    protected abstract void updateResultMessage();

    protected abstract void onSearchLocationResult(Location location);

    protected abstract BaseNetworkController getNetworkController(Context context);

    protected abstract PlaceCuration getPlaceCuration();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
        super.onCreate(savedInstanceState);
    }

    protected void initLayout()
    {
        setContentView(R.layout.activity_curation);

        mHandler = new UpdateHandler(this);

        initToolbar();

        mConfirmView = findViewById(R.id.confirmView);
        setConfirmOnClickListener(this);

        ScrollView contentScrollView = findViewById(R.id.contentScrollView);
        EdgeEffectColor.setEdgeGlowColor(contentScrollView, getResources().getColor(R.color.default_over_scroll_edge));

        View resetCurationView = findViewById(R.id.resetCurationView);
        resetCurationView.setOnClickListener(this);

        ViewGroup contentLayout = findViewById(R.id.contentLayout);

        initContentLayout(contentLayout);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.activity_curation_title);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCancel();
            }
        });
    }

    protected void setResultMessage(String text)
    {
        if (mConfirmView == null)
        {
            return;
        }

        mConfirmView.setText(text);
    }

    protected void setConfirmEnable(boolean enabled)
    {
        if (mConfirmView == null)
        {
            return;
        }

        mConfirmView.setEnabled(enabled);
    }

    protected void setConfirmOnClickListener(View.OnClickListener listener)
    {
        if (mConfirmView == null)
        {
            return;
        }

        mConfirmView.setOnClickListener(listener);
    }

    protected int getConfirmCount()
    {
        if (mConfirmView == null)
        {
            return 0;
        }

        String text = mConfirmView.getText().toString();
        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            return 0;
        }

        text = text.replaceAll("\\D", "");

        int count;
        try
        {
            count = Integer.parseInt(text);
        } catch (Exception e)
        {
            count = 0;
        }

        return count;
    }

    protected void requestUpdateResult()
    {
        mHandler.removeMessages(HANDLE_MESSAGE_RESULT);
        mHandler.sendEmptyMessage(HANDLE_MESSAGE_RESULT);
    }

    protected void requestUpdateResultDelayed()
    {
        setConfirmOnClickListener(null);

        mHandler.removeMessages(HANDLE_MESSAGE_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mDailyLocationFactory != null)
        {
            mDailyLocationFactory.stopLocationMeasure();
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                onComplete();
                break;

            case R.id.closeView:
            case R.id.exitView:
                onCancel();
                break;

            case R.id.resetCurationView:
                onReset();
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    protected DailyTextView getGridLayoutItemView(String text, int resId)
    {
        DailyTextView dailyTextView = new DailyTextView(this);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(getResources().getColorStateList(R.color.selector_curation_textcolor));
        dailyTextView.setText(text);
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = text == null ? 1 : ScreenUtils.dpToPx(this, 74d);
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        dailyTextView.setPadding(0, ScreenUtils.dpToPx(this, 12), 0, 0);
        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    protected void setDisabledSortLayout(View view, RadioGroup sortLayout)
    {
        if (sortLayout == null)
        {
            return;
        }

        sortLayout.setEnabled(false);

        int childCount = sortLayout.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            sortLayout.getChildAt(i).setEnabled(false);
        }
    }

    protected void resetLayout(ViewGroup viewGroup)
    {
        if (viewGroup == null)
        {
            return;
        }

        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            viewGroup.getChildAt(i).setSelected(false);
        }
    }

    private static class UpdateHandler extends Handler
    {
        private final WeakReference<PlaceCurationActivity> mWeakReference;

        public UpdateHandler(PlaceCurationActivity activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            PlaceCurationActivity placeCurationActivity = mWeakReference.get();

            if (placeCurationActivity == null)
            {
                return;
            }

            switch (msg.what)
            {
                case HANDLE_MESSAGE_RESULT:
                    placeCurationActivity.updateResultMessage();
                    break;
            }
        }
    }

    protected void searchMyLocation()
    {
        if (mIsFixedLocation == true)
        {
            PlaceCuration placeCuration = getPlaceCuration();

            if (placeCuration != null)
            {
                onSearchLocationResult(placeCuration.getLocation());
            }
        } else
        {
            lockUI();

            if (mDailyLocationFactory == null)
            {
                mDailyLocationFactory = new DailyLocationFactory(this);
            }

            if (mDailyLocationFactory.measuringLocation() == true)
            {
                return;
            }

            mDailyLocationFactory.checkLocationMeasure(new DailyLocationFactory.OnCheckLocationListener()
            {
                @Override
                public void onRequirePermission()
                {
                    unLockUI();

                    Intent intent = PermissionManagerActivity.newInstance(PlaceCurationActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
                }

                @Override
                public void onFailed()
                {
                    unLockUI();

                    onSearchLocationResult(null);
                }

                @Override
                public void onProviderDisabled()
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    mDailyLocationFactory.stopLocationMeasure();

                    PlaceCurationActivity.this.showSimpleDialog(//
                        getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                        getString(R.string.dialog_btn_text_dosetting), //
                        getString(R.string.dialog_btn_text_cancel), //
                        new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                            }
                        }, new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                onSearchLocationResult(null);
                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                onSearchLocationResult(null);
                            }
                        }, null, true);
                }

                @Override
                public void onProviderEnabled()
                {
                    mDailyLocationFactory.startLocationMeasure(null, new DailyLocationFactory.OnLocationListener()
                    {
                        @Override
                        public void onFailed()
                        {
                            unLockUI();

                            onSearchLocationResult(null);
                        }

                        @Override
                        public void onAlreadyRun()
                        {

                        }

                        @Override
                        public void onLocationChanged(Location location)
                        {
                            unLockUI();

                            if (isFinishing() == true)
                            {
                                return;
                            }

                            mDailyLocationFactory.stopLocationMeasure();

                            onSearchLocationResult(location);
                        }

                        @Override
                        public void onCheckSetting(ResolvableApiException exception)
                        {
                            unLockUI();

                            try
                            {
                                exception.startResolutionForResult(PlaceCurationActivity.this, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                            } catch (Exception e)
                            {

                            }
                        }
                    });
                }
            });
        }
    }
}