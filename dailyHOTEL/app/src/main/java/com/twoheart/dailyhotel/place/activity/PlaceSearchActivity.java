package com.twoheart.dailyhotel.place.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.LocationTermsActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LocationFactory;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

import java.text.DecimalFormat;
import java.util.List;

public abstract class PlaceSearchActivity extends BaseActivity implements View.OnClickListener
{
    protected static final int REQUEST_ACTIVITY_SEARCHRESULT = 100;
    private static final int DELAY_AUTO_COMPLETE_MILLIS = 100;

    private static final int DEFAULT_ICON = 0;
    private static final int HOTEL_ICON = 1;
    private static final int GOURMET_ICON = 2;

    private View mToolbar;

    private View mSearchLayout;
    private ViewGroup mAutoCompleteLayout;
    private View mAutoCompleteScrollLayout;
    private View mSearchingView;
    private View mRecentSearchLayout;
    private ViewGroup mRcentContentsLayout;
    private View mDeleteAllRecentSearchesView;

    private EditText mSearchEditText;

    protected DailyRecentSearches mDailyRecentSearches;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            requestAutoComplete((String) msg.obj, onAutoCompleteResultListener);
        }
    };

    protected interface OnAutoCompleteResultListener
    {
        void onAutoCompleteResultListener(String text, List<Keyword> keywordList);
    }

    protected abstract void initIntent(Intent intent);

    protected abstract String getAroundPlaceString();

    protected abstract String getSearchHintText();

    protected abstract String getRecentSearches();

    protected abstract void writeRecentSearches(String text);

    protected abstract void deleteAllRecentSearches();

    protected abstract void requestAutoComplete(String text, OnAutoCompleteResultListener listener);

    protected abstract void showSearchResult(String text);

    protected abstract void showSearchResult(Keyword keyword);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        initIntent(getIntent());

        initLayout();
    }

    private void initLayout()
    {
        initToolbarLayout();
        initAroundLayout();
        initSearchLayout();
    }

    private void initToolbarLayout()
    {
        mToolbar = findViewById(R.id.toolbar);

        View backView = mToolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        mSearchEditText = (EditText) mToolbar.findViewById(R.id.searchEditText);
        mSearchEditText.setHint(getSearchHintText());

        StringFilter stringFilter = new StringFilter(this);
        InputFilter[] allowAlphanumericHangul = new InputFilter[2];
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;
        allowAlphanumericHangul[1] = new InputFilter.LengthFilter(20);

        mSearchEditText.setFilters(allowAlphanumericHangul);

        final View searchView = mToolbar.findViewById(R.id.searchView);
        searchView.setOnClickListener(this);
        searchView.setEnabled(false);

        final View deleteView = mToolbar.findViewById(R.id.deleteView);
        deleteView.setOnClickListener(this);
        deleteView.setVisibility(View.INVISIBLE);

        mSearchEditText.addTextChangedListener(new TextWatcher()
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
                mHandler.removeMessages(0);

                if (s.length() == 0)
                {
                    deleteView.setVisibility(View.INVISIBLE);
                    searchView.setEnabled(false);

                    updateAutoCompleteLayout(mAutoCompleteLayout, null, null);

                    showRecentSearchesView();
                } else
                {
                    if(s.length() == 1 && s.charAt(0) == ' ')
                    {
                        s.delete(0, 1);
                        return;
                    }

                    deleteView.setVisibility(View.VISIBLE);
                    searchView.setEnabled(true);

                    Message message = mHandler.obtainMessage(0, s.toString());
                    mHandler.sendMessageDelayed(message, DELAY_AUTO_COMPLETE_MILLIS);

                    showAutoCompleteView();
                }
            }
        });

        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if(lockUiComponentAndIsLockUiComponent() == true)
                        {
                            return false;
                        }

                        showSearchResult(v.getText().toString());
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void initAroundLayout()
    {
        View searchAroundLayout = findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(this);

        TextView text01View = (TextView) searchAroundLayout.findViewById(R.id.text01View);
        text01View.setText(getAroundPlaceString());

        if (DailyPreference.getInstance(this).isAgreeTermsOfLocation() == true)
        {
            TextView text02View = (TextView) searchAroundLayout.findViewById(R.id.text02View);
            text02View.setVisibility(View.GONE);
        }
    }

    private void initSearchLayout()
    {
        mSearchLayout = findViewById(R.id.searchLayout);
        mSearchingView = mSearchLayout.findViewById(R.id.searchingView);
        mSearchingView.setVisibility(View.GONE);

        // 내주변 호텔 보기
        View searchAroundLayout = mSearchLayout.findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(this);

        initRecentSearchesLayout(mSearchLayout);
        initAutoCompleteLayout(mSearchLayout);
    }

    private void initRecentSearchesLayout(View view)
    {
        // 최근 검색어
        // 전체 삭제
        mDeleteAllRecentSearchesView = view.findViewById(R.id.deleteAllView);
        mDeleteAllRecentSearchesView.setOnClickListener(this);

        mRecentSearchLayout = findViewById(R.id.recentSearchLayout);
        mRecentSearchLayout.setVisibility(View.VISIBLE);

        // 목록
        mRcentContentsLayout = (ViewGroup) findViewById(R.id.contentsLayout);

        mDailyRecentSearches = new DailyRecentSearches(getRecentSearches());
        updateRecentSearchesLayout(mRcentContentsLayout, mDailyRecentSearches.getList());
    }

    private void updateRecentSearchesLayout(ViewGroup viewGroup, List<String> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        viewGroup.removeAllViews();

        if (keywordList == null || keywordList.size() == 0)
        {
            mDeleteAllRecentSearchesView.setEnabled(false);

            View view = LayoutInflater.from(this).inflate(R.layout.list_row_search_recently, viewGroup, false);

            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setTextColor(getResources().getColor(R.color.search_hint_text));
            textView.setCompoundDrawablesWithIntrinsicBounds(getRecentSearchesIcon(DEFAULT_ICON), 0, 0, 0);
            textView.setText(R.string.label_search_recentsearches_none);

            View underLineView = view.findViewById(R.id.underLineView);
            underLineView.setVisibility(View.GONE);

            viewGroup.addView(view);
        } else
        {
            mDeleteAllRecentSearchesView.setEnabled(true);

            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(lockUiComponentAndIsLockUiComponent() == true)
                    {
                        return;
                    }

                    showSearchResult((String) v.getTag());
                }
            };

            int size = keywordList.size();
            String[] values;
            TextView textView;
            View view;

            for (int i = 0; i < size; i++)
            {
                values = keywordList.get(i).split("\\:");

                view = LayoutInflater.from(this).inflate(R.layout.list_row_search_recently, viewGroup, false);
                view.setOnClickListener(onClickListener);
                view.setTag(values[1]);

                textView = (TextView) view.findViewById(R.id.textView);
                textView.setCompoundDrawablesWithIntrinsicBounds(getRecentSearchesIcon(Integer.parseInt(values[0])), 0, 0, 0);
                textView.setText(values[1]);

                if (i == size - 1)
                {
                    View underLineView = view.findViewById(R.id.underLineView);
                    underLineView.setVisibility(View.GONE);
                }

                viewGroup.addView(view);
            }
        }
    }

    private void initAutoCompleteLayout(View view)
    {
        mAutoCompleteScrollLayout = view.findViewById(R.id.autoCompleteScrollLayout);
        mAutoCompleteLayout = (ViewGroup) mAutoCompleteScrollLayout.findViewById(R.id.autoCompleteLayout);

        mAutoCompleteScrollLayout.setVisibility(View.GONE);
    }

    private void updateAutoCompleteLayout(ViewGroup viewGroup, String text, List<Keyword> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        viewGroup.removeAllViews();

        if (keywordList == null || keywordList.size() == 0)
        {
            hideSearchView();
        } else
        {
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(lockUiComponentAndIsLockUiComponent() == true)
                    {
                        return;
                    }

                    showSearchResult((Keyword) v.getTag());
                }
            };

            for (Keyword keyword : keywordList)
            {
                View view = LayoutInflater.from(this).inflate(R.layout.list_row_search_autocomplete, viewGroup, false);
                view.setOnClickListener(onClickListener);
                view.setTag(keyword);

                TextView textView01 = (TextView) view.findViewById(R.id.textView01);
                TextView textView02 = (TextView) view.findViewById(R.id.textView02);

                if (keyword.price > 0)
                {
                    int startIndex = keyword.name.lastIndexOf(text);
                    int endIndex = startIndex + text.length();

                    if (startIndex >= 0)
                    {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(keyword.name);
                        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                            startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        textView01.setText(spannableStringBuilder);
                    } else
                    {
                        textView01.setText(keyword.name);
                    }

                    DecimalFormat comma = new DecimalFormat("###,##0");
                    String strPrice = comma.format(keyword.price);

                    textView02.setText(strPrice + getString(R.string.currency));
                } else
                {
                    textView01.setText(keyword.name);
                    textView02.setVisibility(View.INVISIBLE);
                }

                viewGroup.addView(view);
            }
        }
    }

    private void showRecentSearchesView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mRecentSearchLayout.setVisibility(View.VISIBLE);
    }

    private void showSearchingView()
    {
        mSearchingView.setVisibility(View.VISIBLE);
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mRecentSearchLayout.setVisibility(View.GONE);
    }

    private void showAutoCompleteView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteScrollLayout.setVisibility(View.VISIBLE);
        mRecentSearchLayout.setVisibility(View.GONE);
    }

    private void hideSearchView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mRecentSearchLayout.setVisibility(View.GONE);
    }

    private int getRecentSearchesIcon(int type)
    {
        switch (type)
        {
            case HOTEL_ICON:
                return R.drawable.search_ic_02_hotel;

            case GOURMET_ICON:
                return R.drawable.search_ic_02_gourmet;

            default:
                return R.drawable.search_ic_03_recent;
        }
    }

    private void showTermsOfLocationDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        ImageView titleIconView = (ImageView) dialogView.findViewById(R.id.titleIconView);
        titleIconView.setImageResource(R.drawable.popup_ic_location);

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
        messageTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());
        messageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(PlaceSearchActivity.this, LocationTermsActivity.class);
                startActivity(intent);
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
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                DailyPreference.getInstance(PlaceSearchActivity.this).setTermsOfLocation(true);

                searchMyLocation();
            }
        });

        confirmTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_signature_ok, 0, 0, 0);
        confirmTextView.setCompoundDrawablePadding(Util.dpToPx(this, 15));

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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchAroundLayout:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                showTermsOfLocationDialog();
                break;
            }

            case R.id.searchView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                showSearchResult(mSearchEditText.getText().toString());
                break;
            }

            case R.id.deleteAllView:
            {
                mDailyRecentSearches.clear();
                deleteAllRecentSearches();

                updateRecentSearchesLayout(mRcentContentsLayout, null);
                break;
            }

            case R.id.deleteView:
            {
                mSearchEditText.setText(null);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
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

                    if (keyword != null && keyword.price > 0)
                    {
                        mDailyRecentSearches.addString(String.format("1:%s", keyword.name));
                    } else
                    {
                        mDailyRecentSearches.addString(String.format("0:%s", keyword.name));
                    }

                    writeRecentSearches(mDailyRecentSearches.toString());
                    updateRecentSearchesLayout(mRcentContentsLayout, mDailyRecentSearches.getList());
                }

                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    setResult(resultCode);
                    finish();
                } else if (resultCode == CODE_RESULT_ACTVITY_HOME)
                {
                    finish();
                }
                break;
            }
        }
    }

    private void searchMyLocation()
    {
        lockUI();

        LocationFactory.getInstance(this).startLocationMeasure(this, null, new LocationFactory.LocationListenerEx()
        {
            @Override
            public void onRequirePermission()
            {
                if (Util.isOverAPI23() == true)
                {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                }

                unLockUI();
            }

            @Override
            public void onFailed()
            {
                unLockUI();

                if (Util.isOverAPI23() == true)
                {
                    if (isFinishing() == true)
                    {
                        return;
                    }

                    showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                        , getString(R.string.dialog_msg_used_gps_android6)//
                        , getString(R.string.dialog_btn_text_dosetting)//
                        , getString(R.string.dialog_btn_text_cancel)//
                        , new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                        }
                    }, null, true);
                }
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
                LocationFactory.getInstance(PlaceSearchActivity.this).stopLocationMeasure();

                showSimpleDialog(getString(R.string.dialog_title_used_gps)//
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
                if (isFinishing() == true)
                {
                    unLockUI();
                    return;
                }

                LocationFactory.getInstance(PlaceSearchActivity.this).stopLocationMeasure();

                if (location == null)
                {
                    DailyToast.showToast(PlaceSearchActivity.this, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                } else
                {
                    // 서버 연동
                }

                unLockUI();
            }
        });
    }

    private OnAutoCompleteResultListener onAutoCompleteResultListener = new OnAutoCompleteResultListener()
    {
        @Override
        public void onAutoCompleteResultListener(String text, List<Keyword> keywordList)
        {
            if (mSearchEditText.length() == 0)
            {
                return;
            }

            showAutoCompleteView();

            updateAutoCompleteLayout(mAutoCompleteLayout, text, keywordList);
        }
    };
}
