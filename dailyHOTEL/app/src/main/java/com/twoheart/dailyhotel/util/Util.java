package com.twoheart.dailyhotel.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.network.DailyOkHttpImagePipelineConfigFactory;
import com.twoheart.dailyhotel.view.widget.FontManager;

import net.simonvt.numberpicker.NumberPicker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

public class Util implements Constants
{
    public static void initializeFresco(Context context)
    {
        ImagePipelineConfig imagePipelineConfig;

        if (Util.isOverAPI11() == true && Util.getLCDWidth(context) > 720)
        {
            imagePipelineConfig = DailyOkHttpImagePipelineConfigFactory//
                .newBuilder(context, new OkHttpClient()).build();
        } else
        {
            imagePipelineConfig = DailyOkHttpImagePipelineConfigFactory//
                .newBuilder(context, new OkHttpClient())//
                .setBitmapsConfig(Config.RGB_565).build();
        }

        Fresco.initialize(context, imagePipelineConfig);

        System.loadLibrary("webp");
    }

    public static void requestImageResize(Context context, com.facebook.drawee.view.SimpleDraweeView simpleDraweeView, String imageUrl)
    {
        if (Util.isTextEmpty(imageUrl) == true)
        {
            simpleDraweeView.setImageURI(null);
            return;
        }

        if (Util.getLCDWidth(context) >= 720)
        {
            simpleDraweeView.setImageURI(Uri.parse(imageUrl));
        } else
        {
            final int resizeWidth = 360, resizeHeight = 240;

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))//
                .setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight))//
                .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()//
                .setOldController(simpleDraweeView.getController())//
                .setImageRequest(imageRequest)//
                .build();

            simpleDraweeView.setController(controller);
        }
    }

    public static int dpToPx(Context context, double dp)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static String storeReleaseAddress()
    {
        if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
        {
            return URL_STORE_GOOGLE_DAILYHOTEL;
        } else
        {
            return URL_STORE_T_DAILYHOTEL;
        }
    }

    public static String storeReleaseAddress(String newUrl)
    {

        if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
        {
            return URL_STORE_GOOGLE_DAILYHOTEL;
        } else
        {
            return newUrl;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static View getActionBarView(Activity activity)
    {
        Window window = activity.getWindow();
        View v = window.getDecorView();
        int resId = activity.getResources().getIdentifier("action_bar_container", "id", "android");
        return v.findViewById(resId);
    }

    public static String dailyHotelTimeConvert(String dailyHotelTime)
    {
        final int positionOfDashPreviousHour = 8; // yy-MM-dd-hh이므로
        String correctTime = null;

        char checkOut[] = dailyHotelTime.toCharArray();
        StringBuilder parsedCheckOutTime = new StringBuilder();
        for (int i = 0; i < checkOut.length; i++)
        {
            if (i == positionOfDashPreviousHour)
            {
                parsedCheckOutTime.append(" ");
            } else
            {
                parsedCheckOutTime.append(checkOut[i]);
            }
        }
        parsedCheckOutTime.append(":00:00");
        correctTime = parsedCheckOutTime.toString();

        return correctTime;
    }

    public static void setLocale(Context context, Locale locale)
    {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    public static void restartApp(Context context)
    {
        if (context == null)
        {
            return;
        }

        // 메모리 해지 및 기타 바탕화면으로 빠진후에 메모리가 해지 되는 경우가 있어 강제 종료후에 다시 재실행한다.
        // 에러 후에 알람으로 다시 실행시키기.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent);
        System.exit(0);
    }

    public static void finishOutOfMemory(BaseActivity activity)
    {
        if (activity == null || activity.isFinishing() == true)
        {
            return;
        }

        // 세션이 만료되어 재시작 요청.
        activity.showSimpleDialog(activity.getString(R.string.dialog_notice2), activity.getString(R.string.dialog_msg_outofmemory), activity.getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                System.exit(0);
            }
        }, null, false);
    }

    public static String getDeviceUUID(final Context context)
    {
        UUID uuid = null;

        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        try
        {
            if (!"9774d56d682e549c".equals(androidId))
            {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else
            {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e)
        {
            ExLog.d(e.toString());
        }

        if (uuid != null)
        {
            return uuid.toString();
        } else
        {
            return null;
        }
    }

    public static int getLCDWidth(Context context)
    {
        if (context == null)
        {
            return 0;
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        return displayMetrics.widthPixels;
    }

    public static int getLCDHeight(Context context)
    {
        if (context == null)
        {
            return 0;
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        return displayMetrics.heightPixels;
    }

    public static boolean isNameCharacter(String text)
    {
        boolean result = false;

        if (Util.isTextEmpty(text) == false)
        {
            result = Pattern.matches("^[a-zA-Z\\s.'-]+$", text);
        }

        return result;
    }

    public static boolean isOverAPI11()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isOverAPI21()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isOverAPI12()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean isOverAPI16()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isOverAPI14()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isOverAPI23()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isTelephonyEnabled(Context context)
    {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static boolean isTextEmpty(String text)
    {
        return (TextUtils.isEmpty(text) == true || "null".equalsIgnoreCase(text) == true || text.trim().length() == 0);
    }

    public static String getAppVersion(Context context)
    {
        String version = null;
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (NameNotFoundException e)
        {
            ExLog.d(e.toString());
        }

        return version;
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity)
    {
        try
        {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity) == ConnectionResult.SUCCESS)
            {
                return true;
            } else
            {
                return false;
            }
        } catch (Exception e)
        {
            return false;
        }
    }

    public static boolean isInstallGooglePlayService(Activity activity)
    {
        if (Util.isGooglePlayServicesAvailable(activity) == false)
        {
            return false;
        }

        boolean isInstalled = false;

        try
        {
            PackageManager packageManager = activity.getPackageManager();

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.google.android.gms", 0);
            PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_SIGNATURES);

            int version = activity.getResources().getInteger(com.google.android.gms.R.integer.google_play_services_version);

            if (packageInfo.versionCode < version)
            {
                isInstalled = false;
            } else
            {
                isInstalled = true;
            }
        } catch (PackageManager.NameNotFoundException e)
        {
            isInstalled = false;
        }

        return isInstalled;
    }

    public static boolean installGooglePlayService(final BaseActivity activity)
    {
        if (isInstallGooglePlayService(activity) == true)
        {
            return true;
        } else
        {
            if (activity == null || activity.isFinishing() == true)
            {
                return false;
            }

            // set dialog message
            activity.showSimpleDialog(activity.getString(R.string.dialog_title_googleplayservice), activity.getString(R.string.dialog_msg_install_update_googleplayservice), //
                activity.getString(R.string.dialog_btn_text_install), activity.getString(R.string.dialog_btn_text_cancel), //
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            intent.setPackage("com.android.vending");
                            activity.startActivity(intent);
                        } catch (ActivityNotFoundException e)
                        {
                            try
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                intent.setPackage("com.android.vending");
                                activity.startActivity(intent);
                            } catch (ActivityNotFoundException f)
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                activity.startActivity(intent);
                            }
                        }
                    }
                }, null, true);


            return false;
        }
    }

    public interface OnGoogleCloudMessagingListener
    {
        void onResult(String registrationId);
    }

    public static void requestGoogleCloudMessaging(final BaseActivity baseActivity, final OnGoogleCloudMessagingListener listener)
    {
        if (Util.isGooglePlayServicesAvailable(baseActivity) == false)
        {
            return;
        }

        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(baseActivity);
                String registrationId = null;

                try
                {
                    registrationId = instance.register(GCM_PROJECT_NUMBER);
                } catch (IOException e)
                {
                    ExLog.e(e.toString());
                }

                return registrationId;
            }

            @Override
            protected void onPostExecute(String registrationId)
            {
                if (listener != null)
                {
                    listener.onResult(registrationId);
                }
            }
        }.execute();
    }

    public static HashMap<String, String> getLoginParams(Context context)
    {
        String id = DailyPreference.getInstance(context).getUserId();
        String accessToken = DailyPreference.getInstance(context).getUserAccessToken();
        String pw = DailyPreference.getInstance(context).getUserPassword();
        String type = DailyPreference.getInstance(context).getUserType();

        HashMap<String, String> params = new HashMap<String, String>();

        if (Util.isTextEmpty(accessToken) == false && "0".equals(accessToken) == false)
        {
            params.put("social_id", accessToken);

            // 기존 페이스북 유저를 위한 코드
            if (Util.isTextEmpty(type) == true)
            {
                params.put("user_type", "facebook");

                DailyPreference.getInstance(context).setUserType("facebook");
            }
        } else
        {
            params.put("email", id);

            // 기존 데일리 유저를 위한 코드
            if (Util.isTextEmpty(type) == true)
            {
                params.put("user_type", "normal");

                DailyPreference.getInstance(context).setUserType("normal");
            }
        }

        params.put("is_auto", DailyPreference.getInstance(context).isAutoLogin() ? "true" : "false");
        params.put("pw", pw);

        if (Util.isTextEmpty(type) == false)
        {
            params.put("user_type", type);
        } else
        {
            // 만일 정보가 없으면 다시 로그인 하도록 수정한다
            DailyPreference.getInstance(context).clear();

            try
            {
                LoginManager.getInstance().logOut();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            try
            {
                UserManagement.requestLogout(null);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        return params;
    }

    private static String getValidateCountry(String code)
    {
        CountryCodeNumber countryCodeNumber = new CountryCodeNumber();

        return countryCodeNumber.getCountry(code);
    }

    private static boolean isValidateCountryCode(String code)
    {
        CountryCodeNumber countryCodeNumber = new CountryCodeNumber();

        return countryCodeNumber.hasCountryCode(code);
    }

    public static boolean isValidatePhoneNumber(String phonenumber)
    {
        if (Util.isTextEmpty(phonenumber) == true)
        {
            return false;
        }

        if (phonenumber.charAt(0) == '+')
        {
            String globalPhoneNumber = phonenumber.replaceFirst("\\s", "^");
            String[] text = globalPhoneNumber.split("\\^");

            // 국제 전화번호 존재 여부 확인
            if (isValidateCountryCode(text[0]) == false || text.length < 2)
            {
                return false;
            }

            text[1] = text[1].replace("-", "");

            if ("+82".equalsIgnoreCase(text[0]) == true)
            {
                if (text[1].startsWith("(0)10") || text[1].startsWith("(0)11") || text[1].startsWith("(0)16") //
                    || text[1].startsWith("(0)17") || text[1].startsWith("(0)18") || text[1].startsWith("(0)19"))
                {
                    if (TextUtils.isDigitsOnly(text[1].substring(5)) == true)
                    {
                        int length = text[1].length();
                        if (length == 12 || length == 13)
                        {
                            return true;
                        }
                    }
                }
            } else
            {
                text[1] = text[1].replace("\\(|\\)|\\s|\\-", "");

                // 국내가 아니면 숫자만 있는지 검증한다 7자리 ~ 15자리
                int length = text[1].length();
                if (length >= 7 && length <= 15)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * +82 (0)1012345678 (한국 핸드폰 전화번호만 가능)
     *
     * @param mobileNumber
     * @return
     */
    public static boolean isExistMobileNumber(String mobileNumber)
    {
        if (Util.isTextEmpty(mobileNumber) == true || mobileNumber.startsWith("+82") == false)
        {
            return false;
        }

        mobileNumber = mobileNumber.replaceFirst("\\s", "^");
        String[] number = mobileNumber.split("\\^");

        if (number.length < 2)
        {
            return false;
        }

        number[1] = number[1].replaceAll("\\(|\\)|-", "");

        String mobile01 = number[1].substring(0, 3);

        int middle = number[1].length() == 10 ? 6 : 7;
        String mobile02 = number[1].substring(3, middle);
        String mobile03 = number[1].substring(middle);

        final String PATTERN = "010|011|016|017|018|019{1}";
        final String PATTERN_3 = "111|222|333|444|555|666|777|888|999|000|012|123|234|345|456|567|678|789|987|876|765|654|543|432|321|210{1}";
        final String PATTENR_4 = "1111|2222|3333|4444|5555|6666|7777|8888|9999|0000|0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210{1}";

        Pattern pattern01 = Pattern.compile(PATTERN);
        Pattern pattern02 = mobile02.length() == 3 ? Pattern.compile(PATTERN_3) : Pattern.compile(PATTENR_4);
        Pattern pattern03 = Pattern.compile(PATTENR_4);

        return pattern01.matcher(mobile01).matches() && pattern02.matcher(mobile02).matches() && pattern03.matcher(mobile03).matches();
    }


    public static String getLine1Number(Context context)
    {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telManager.getLine1Number();
    }

    public static final String DEFAULT_COUNTRY_CODE = "대한민국\n+82";

    public static String[] getValidatePhoneNumber(String phonenumber)
    {
        if (Util.isTextEmpty(phonenumber) == true)
        {
            return null;
        }

        if (phonenumber.charAt(0) == '+')
        {
            String globalPhoneNumber = phonenumber.replaceFirst("\\s", "^");
            String[] text = globalPhoneNumber.split("\\^");
            String countryCode = getValidateCountry(text[0]);

            // 국제 전화번호 존재 여부 확인
            if (isTextEmpty(countryCode) == true)
            {
                return null;
            }

            if ("+82".equalsIgnoreCase(text[0]) == true)
            {
                if (text[1].startsWith("(0)10") || text[1].startsWith("(0)11") || text[1].startsWith("(0)16") //
                    || text[1].startsWith("(0)17") || text[1].startsWith("(0)18") || text[1].startsWith("(0)19"))
                {
                    if (TextUtils.isDigitsOnly(text[1].substring(5)) == true)
                    {
                        int length = text[1].length();
                        if (length == 12 || length == 13)
                        {
                            text[0] = DEFAULT_COUNTRY_CODE;
                            return text;
                        }
                    }
                }
            } else
            {
                text[0] = countryCode + "\n" + text[0];
                return text;
            }
        } else
        {
            String text = phonenumber.replace("-", "").replace(" ", "");

            if (text.startsWith("010") || text.startsWith("011") || text.startsWith("016") //
                || text.startsWith("017") || text.startsWith("018") || text.startsWith("019"))
            {
                if (TextUtils.isDigitsOnly(text) == true)
                {
                    int length = text.length();
                    if (length == 10 || length == 11)
                    {
                        return new String[]{DEFAULT_COUNTRY_CODE, text};
                    }
                }
            }
        }

        return null;
    }

    public static Dialog showDatePickerDialog(BaseActivity baseActivity, String titleText, final String[] values, String selectValue, String positive //
        , final View.OnClickListener positiveListener)
    {
        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return null;
        }

        final Dialog dialog = new Dialog(baseActivity);

        LayoutInflater layoutInflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_pickerdialog_layout, null, false);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (Util.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(baseActivity.getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);
        numberPicker.setDisplayedValues(values);
        numberPicker.setTextTypeface(FontManager.getInstance(baseActivity).getRegularTypeface());

        for (int i = 0; i < values.length; i++)
        {
            if (values[i].equalsIgnoreCase(selectValue) == true)
            {
                numberPicker.setValue(i);
                break;
            }
        }

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        twoButtonLayout.setVisibility(View.GONE);
        oneButtonLayout.setVisibility(View.VISIBLE);

        TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

        confirmTextView.setText(positive);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                if (positiveListener != null)
                {
                    v.setTag(numberPicker.getValue());
                    positiveListener.onClick(v);
                }
            }
        });

        dialog.setContentView(dialogView);

        if (baseActivity.isFinishing() == true)
        {
            return null;
        }

        try
        {
            dialog.show();

            return dialog;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    public static String addHippenMobileNumber(Context context, String mobileNumber)
    {
        if (Util.isTextEmpty(mobileNumber) == true)
        {
            return mobileNumber;
        }

        mobileNumber = mobileNumber.replace("-", "");

        if (Util.isValidatePhoneNumber(mobileNumber) == true)
        {
            String[] countryCode = Util.getValidatePhoneNumber(mobileNumber);
            String result;

            TextView textView = new TextView(context);

            if (Util.DEFAULT_COUNTRY_CODE.equals(countryCode[0]) == true)
            {
                textView.addTextChangedListener(new PhoneNumberKoreaFormattingTextWatcher(context));
            } else
            {
                textView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            }

            textView.setText(countryCode[1].replaceAll("\\(|\\)|-|\\s", ""));
            return countryCode[0].substring(countryCode[0].indexOf('\n') + 1) + " " + textView.getText().toString();
        } else
        {
            return mobileNumber;
        }
    }

    private static boolean isValidateNumber(String number)
    {
        int length = number.length();
        final int value = number.charAt(0) - number.charAt(1);

        switch (value)
        {
            case -1:
            case 0:
            case 1:
                for (int i = 2; i < length; i++)
                {
                    if (number.charAt(i - 1) - number.charAt(i) != value)
                    {
                        return false;
                    }
                }
                break;

            default:
                return false;
        }

        return true;
    }

    public static String getValueForLinkUrl(String linkUrl, String key)
    {
        if (Util.isTextEmpty(linkUrl) == true || Util.isTextEmpty(key) == true)
        {
            return null;
        }

        // view=hotel&idx=131&date=20151109&nights=1
        String[] keyValue = linkUrl.split("\\&|\\=");

        for (int i = 0; i < keyValue.length; i += 2)
        {
            if (key.equalsIgnoreCase(keyValue[i]) == true)
            {
                return keyValue[i + 1];
            }
        }

        return null;
    }
}
