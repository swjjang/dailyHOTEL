package com.twoheart.dailyhotel.place.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.information.terms.LocationTermsActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

public abstract class PlaceSearchFragment extends BaseFragment
{
    private boolean mShowSearchKeyboard;
    protected BaseActivity mBaseActivity;

    protected static final int REQUEST_ACTIVITY_SEARCHRESULT = 10000;
    protected static final int REQUEST_ACTIVITY_CALENDAR = 10001;

    protected DailyRecentSearches mDailyRecentSearches;
    protected PlaceSearchLayout mPlaceSearchLayout;
    protected PlaceSearchNetworkController mPlaceSearchNetworkController;
    protected OnSearchFragmentListener mOnSearchFragmentListener;

    protected abstract PlaceSearchLayout getPlaceSearchLayout(Context context);

    protected abstract PlaceSearchNetworkController getPlaceSearchNetworkController(Context context);

    protected abstract String getRecentSearches();

    protected abstract void writeRecentSearches(String text);

    protected abstract PlaceSearchLayout getLayout();

    protected abstract void onSearch(Location location);

    public abstract void startSearchResultActivity();

    public interface OnSearchFragmentListener
    {
        void finish();

        void finish(int resultCode);

        void onSearchEnabled(boolean enabled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mPlaceSearchLayout = getPlaceSearchLayout(mBaseActivity);
        mPlaceSearchNetworkController = getPlaceSearchNetworkController(mBaseActivity);

        View view = mPlaceSearchLayout.onCreateView(R.layout.fragment_search, container);

        initContents();

        return view;
    }

    protected void initContents()
    {
        mDailyRecentSearches = new DailyRecentSearches(getRecentSearches());

        mPlaceSearchLayout.updateRecentSearchesLayout(mDailyRecentSearches.getList());
    }

    public void setOnSearchFragmentListener(OnSearchFragmentListener listener)
    {
        mOnSearchFragmentListener = listener;
    }

    public void resetSearchKeyword()
    {
        if(mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.resetSearchKeyword();
    }

    public void clearSearchKeywordFocus()
    {
        if(mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.clearSearchKeywordFocus();
    }

    public void showSearchKeyboard()
    {
        if(mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.showSearchKeyboard();
    }

    public void hideSearchKeyboard()
    {
        if(mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.hideSearchKeyboard();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mShowSearchKeyboard == true)
        {
            mShowSearchKeyboard = false;
            mPlaceSearchLayout.showSearchKeyboard();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_SEARCHRESULT:
            {
                if (data != null)
                {
                    Keyword keyword = data.getParcelableExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_KEYWORD);
                    mDailyRecentSearches.addString(keyword);

                    writeRecentSearches(mDailyRecentSearches.toString());
                    mPlaceSearchLayout.updateRecentSearchesLayout(mDailyRecentSearches.getList());
                }

                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    mOnSearchFragmentListener.finish(resultCode);
                } else if (resultCode == CODE_RESULT_ACTIVITY_HOME)
                {
                    mOnSearchFragmentListener.finish();
                } else
                {
                    mShowSearchKeyboard = true;
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                searchMyLocation();
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    searchMyLocation();
                }
                break;
            }
        }
    }

    protected void showTermsOfLocationDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) mBaseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

        final Dialog dialog = new Dialog(mBaseActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(getString(R.string.label_search_agree_termsoflocation));

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);

        String message = getString(R.string.message_search_agree_termsoflocation);

        int startIndex = message.lastIndexOf('\n') + 1;
        int endIndex = message.length();

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
            startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        messageTextView.setText(spannableStringBuilder);
        messageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mBaseActivity, LocationTermsActivity.class);
                startActivity(intent);

                //                AnalyticsManager.getInstance(PlaceSearchActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                //                    , AnalyticsManager.Action.LOCATION_AGREEMENT_POPPEDUP, AnalyticsManager.Label.TERMSOF_LOCATION, null);
            }
        });

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        twoButtonLayout.setVisibility(View.GONE);
        oneButtonLayout.setVisibility(View.VISIBLE);

        TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

        confirmTextView.setText(R.string.label_search_agree_search_location);
        oneButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                DailyPreference.getInstance(mBaseActivity).setTermsOfLocation(true);

                mPlaceSearchLayout.updateTermsOfLocationLayout();

                Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);

                //                AnalyticsManager.getInstance(PlaceSearchActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                //                    , AnalyticsManager.Action.LOCATION_AGREEMENT_POPPEDUP, AnalyticsManager.Label.AGREE_AND_SEARCH, null);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.LOCATION_AGREEMENT_POPPEDUP, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            dialog.setContentView(dialogView);
            dialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    protected void searchMyLocation()
    {
        lockUI();

        DailyLocationFactory.getInstance(mBaseActivity).startLocationMeasure(this, null, new DailyLocationFactory.LocationListenerEx()
        {
            @Override
            public void onRequirePermission()
            {
                unLockUI();

                Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
            }

            @Override
            public void onFailed()
            {
                unLockUI();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                DailyLocationFactory.getInstance(mBaseActivity).stopLocationMeasure();

                mBaseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                    , getString(R.string.dialog_msg_used_gps)//
                    , getString(R.string.dialog_btn_text_dosetting)//
                    , getString(R.string.dialog_btn_text_cancel)//
                    , new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                        }
                    }, null, false);
            }

            @Override
            public void onLocationChanged(Location location)
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                DailyLocationFactory.getInstance(mBaseActivity).stopLocationMeasure();

                if (location == null)
                {
                    DailyToast.showToast(mBaseActivity, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                } else
                {
                    // 서버
                    onSearch(location);
                }
            }
        });
    }
}
