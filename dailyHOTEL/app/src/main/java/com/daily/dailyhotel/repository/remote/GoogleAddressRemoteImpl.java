package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.GoogleAddressInterface;
import com.daily.dailyhotel.repository.remote.model.GoogleAddressComponentsData;
import com.daily.dailyhotel.repository.remote.model.GoogleAddressData;
import com.twoheart.dailyhotel.network.dto.GoogleMapListDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2018. 1. 31..
 */

public class GoogleAddressRemoteImpl extends BaseRemoteImpl implements GoogleAddressInterface
{
    public GoogleAddressRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<String> getLocationAddress(double latitude, double longitude)
    {
        final String url = String.format(Locale.KOREA, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&location_type=APPROXIMATE&result_type=sublocality_level_2|administrative_area_level_1&key=%s&language=ko"//
            , Double.toString(latitude)//
            , Double.toString(longitude)//
            , Crypto.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        return mDailyMobileService.getSearchAddress(url).subscribeOn(Schedulers.io()).map(new Function<GoogleMapListDto<GoogleAddressData>, String>()
        {
            @Override
            public String apply(GoogleMapListDto<GoogleAddressData> googleAddressDataGoogleMapListDto) throws Exception
            {
                String address = null;

                if (googleAddressDataGoogleMapListDto != null)
                {
                    try
                    {
                        List<GoogleAddressData> list = googleAddressDataGoogleMapListDto.results;

                        if ("OK".equalsIgnoreCase(googleAddressDataGoogleMapListDto.status) == true && list != null)
                        {
                            GoogleAddressComponentsData countryComponentsData = getCountryComponentsData(list);

                            address = getAddressName(countryComponentsData, list);
                        } else
                        {
                            throw new BaseException(-1, null);
                        }
                    } catch (Exception e)
                    {
                        throw new BaseException(-1, null);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return address;
            }
        });
    }

    private String getAddressName(GoogleAddressComponentsData countryComponentsData, List<GoogleAddressData> googleAddressDataList)
    {
        if (googleAddressDataList == null || googleAddressDataList.size() == 0)
        {
            return null;
        }

        if (countryComponentsData == null)
        {
            return googleAddressDataList.get(0).formattedAddress;
        }

        String address = null;

        String searchType = "KR".equalsIgnoreCase(countryComponentsData.shortName) ? "sublocality_level_2" : "administrative_area_level_1";

        for (GoogleAddressData googleAddressData : googleAddressDataList)
        {
            if (googleAddressData.types.contains(searchType) == true)
            {
                address = googleAddressData.formattedAddress;
                break;
            }
        }

        if (DailyTextUtils.isTextEmpty(address) == false)
        {
            ExLog.d("sam - origin : " + address + " , country : " + countryComponentsData.longName + " , trim Address : " + address.replace(countryComponentsData.longName, "").trim());

            address = address.replace(countryComponentsData.longName, "").trim();
        }

        return address;
    }

    private GoogleAddressComponentsData getCountryComponentsData(List<GoogleAddressData> googleAddressDataList)
    {
        if (googleAddressDataList == null || googleAddressDataList.size() == 0)
        {
            return null;
        }

        List<GoogleAddressComponentsData> list = googleAddressDataList.get(0).addressComponents;
        if (list == null || list.size() == 0)
        {
            return null;
        }

        for (GoogleAddressComponentsData data : list)
        {
            if (data.types.contains("country") == true)
            {
                return data;
            }
        }

        return null;
    }
}
