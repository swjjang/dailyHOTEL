package com.twoheart.dailyhotel.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class DailyRemoteConfig
{
    private static DailyRemoteConfig mInstance = null;

    private Context mContext;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public interface OnCompleteListener
    {
        void onComplete(String currentVersion, String forceVersion);
    }

    public synchronized static DailyRemoteConfig getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyRemoteConfig(context);
        }

        return mInstance;
    }

    private DailyRemoteConfig(Context context)
    {
        mContext = context;
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(Constants.DEBUG).build());
    }

    public void requestRemoteConfig(final OnCompleteListener listener)
    {
        if (Util.isTextEmpty(DailyPreference.getInstance(mContext).getRemoteConfigCompanyName()) == true)
        {
            writeCompanyInformation(mContext, mContext.getString(R.string.default_company_information));
        }

        mFirebaseRemoteConfig.fetch(0L).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful() == true)
                {
                    mFirebaseRemoteConfig.activateFetched();
                } else
                {
                    return;
                }

                String androidUpdateVersion = mFirebaseRemoteConfig.getString("androidUpdateVersion");
                String androidPaymentType = mFirebaseRemoteConfig.getString("androidPaymentType");
                String companyInfo = mFirebaseRemoteConfig.getString("companyInfo");
                String androidSplashImageUrl = mFirebaseRemoteConfig.getString("androidSplashImageLink");
                String androidSplashImageUpdateTime = mFirebaseRemoteConfig.getString("androidSplashImageUpdateTime");
                String androidText = mFirebaseRemoteConfig.getString("androidText");

                if (Constants.DEBUG == true)
                {
                    try
                    {
                        ExLog.d("androidUpdateVersion : " + new JSONObject(androidUpdateVersion).toString());
                        ExLog.d("androidPaymentType : " + new JSONObject(androidPaymentType).toString());
                        ExLog.d("companyInfo : " + new JSONObject(companyInfo).toString());
                        ExLog.d("androidSplashImageLink : " + new JSONObject(androidSplashImageUrl).toString());
                        ExLog.d("androidSplashImageUpdateTime : " + new JSONObject(androidSplashImageUpdateTime).toString());
                        ExLog.d("androidText : " + new JSONObject(androidText).toString());
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }

                // 버전
                String currentVersion = null, forceVersion = null;

                try
                {
                    JSONObject versionJSONObject = new JSONObject(androidUpdateVersion);

                    switch (Constants.RELEASE_STORE)
                    {
                        case PLAY_STORE:
                        {
                            JSONObject jsonObject = versionJSONObject.getJSONObject("play");
                            currentVersion = jsonObject.getString("current");
                            forceVersion = jsonObject.getString("force");
                            break;
                        }

                        case T_STORE:
                        {
                            JSONObject jsonObject = versionJSONObject.getJSONObject("one");
                            currentVersion = jsonObject.getString("current");
                            forceVersion = jsonObject.getString("force");
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                writeCompanyInformation(mContext, companyInfo);
                writePaymentType(mContext, androidPaymentType);
                writeText(mContext, androidText);

                // 이미지 로딩 관련(추후 진행)
                processIntroImage(mContext, androidSplashImageUpdateTime, androidSplashImageUrl);

                if (listener != null)
                {
                    listener.onComplete(currentVersion, forceVersion);
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                if (Constants.DEBUG == false)
                {
                    Crashlytics.logException(e);
                } else
                {
                    ExLog.e(e.toString());
                }

                listener.onComplete(null, null);
            }
        });
    }

    private void processIntroImage(Context context, String updateTime, String imageUrl)
    {
        if (Util.isTextEmpty(updateTime, imageUrl) == true)
        {
            return;
        }

        // 이미지 로딩 관련
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        String dpi;

        if (densityDpi < 240)
        {
            dpi = "hdpi";
        } else if (densityDpi < 640)
        {
            dpi = "xhdpi";
        } else
        {
            dpi = "xxxhdpi";
        }

        try
        {
            JSONObject updateTimeJSONObject = new JSONObject(updateTime);
            JSONObject imageUrlJSONObject = new JSONObject(imageUrl);

            // 아직은 고려하지 않도록 한다."default"
            //            String defaultType = imageUrlJSONObject.getString("default");
            String url = imageUrlJSONObject.getJSONObject("image").getString(dpi);
            String currentVersion = DailyPreference.getInstance(context).getRemoteConfigIntroImageVersion();
            String newVersion = updateTimeJSONObject.getString("time");

            if (Constants.DAILY_INTRO_CURRENT_VERSION.equalsIgnoreCase(newVersion) == true)
            {
                DailyPreference.getInstance(context).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_CURRENT_VERSION);
            } else if (Constants.DAILY_INTRO_DEFAULT_VERSION.equalsIgnoreCase(newVersion) == true)
            {
                DailyPreference.getInstance(context).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_DEFAULT_VERSION);
            } else
            {
                // 기존 버전과 비교해서 다르면 다운로드를 시도한다.
                if (Util.isTextEmpty(currentVersion) == true || currentVersion.equalsIgnoreCase(newVersion) == false)
                {
                    new ImageDownloadAsyncTask(context).execute(url, newVersion);
                }
            }
        } catch (JSONException e)
        {
            ExLog.d(e.toString());
        }
    }

    private void writeCompanyInformation(Context context, String companyInfo)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(companyInfo);

            String companyName = jsonObject.getString("name");
            String companyCEO = jsonObject.getString("ceo");
            String companyBizRegNumber = jsonObject.getString("bizRegNumber");
            String companyItcRegNumber = jsonObject.getString("itcRegNumber");
            String address = jsonObject.getString("address1");
            String phoneNumber = jsonObject.getString("phoneNumber1");
            String fax = jsonObject.getString("fax1");
            String privacyEmail = jsonObject.getString("privacyManager");

            DailyPreference.getInstance(context).setRemoteConfigCompanyInformation(companyName//
                , companyCEO, companyBizRegNumber, companyItcRegNumber, address, phoneNumber, fax, privacyEmail);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void writePaymentType(Context context, String androidPaymentType)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(androidPaymentType);
            JSONObject stayJSONObject = jsonObject.getJSONObject("stay");

            DailyPreference.getInstance(context).setRemoteConfigStaySimpleCardPaymentEnabled(stayJSONObject.getBoolean("easyCard"));
            DailyPreference.getInstance(context).setRemoteConfigStayCardPaymentEnabled(stayJSONObject.getBoolean("card"));
            DailyPreference.getInstance(context).setRemoteConfigStayPhonePaymentEnabled(stayJSONObject.getBoolean("phoneBill"));
            DailyPreference.getInstance(context).setRemoteConfigStayVirtualPaymentEnabled(stayJSONObject.getBoolean("virtualAccount"));

            JSONObject gourmetJSONObject = jsonObject.getJSONObject("gourmet");

            DailyPreference.getInstance(context).setRemoteConfigGourmetSimpleCardPaymentEnabled(gourmetJSONObject.getBoolean("easyCard"));
            DailyPreference.getInstance(context).setRemoteConfigGourmetCardPaymentEnabled(gourmetJSONObject.getBoolean("card"));
            DailyPreference.getInstance(context).setRemoteConfigGourmetPhonePaymentEnabled(gourmetJSONObject.getBoolean("phoneBill"));
            DailyPreference.getInstance(context).setRemoteConfigGourmetVirtualPaymentEnabled(gourmetJSONObject.getBoolean("virtualAccount"));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void writeText(Context context, String textInfo)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(textInfo);

            String version = jsonObject.getString("version");

            if (Util.isTextEmpty(version) == false //
                && version.equalsIgnoreCase(DailyPreference.getInstance(context).getRemoteConfigTextVersion()) == false)
            {
                String loginText01 = jsonObject.getString("loginText01");
                String signupText01 = jsonObject.getString("signupText01");
                String signupText02 = jsonObject.getString("signupText02");

                DailyPreference.getInstance(context).setRemoteConfigTextVersion(version);
                DailyPreference.getInstance(context).setRemoteConfigTextLoginText01(loginText01);
                DailyPreference.getInstance(context).setRemoteConfigTextSignUpText01(signupText01);
                DailyPreference.getInstance(context).setRemoteConfigTextSignUpText02(signupText02);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}