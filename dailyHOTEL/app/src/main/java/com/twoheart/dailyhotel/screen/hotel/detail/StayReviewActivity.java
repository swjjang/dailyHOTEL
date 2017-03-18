package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.twoheart.dailyhotel.network.model.PlaceReview;
import com.twoheart.dailyhotel.network.model.PlaceReviewProgress;
import com.twoheart.dailyhotel.place.activity.PlaceReviewActivity;
import com.twoheart.dailyhotel.place.layout.PlaceReviewLayout;
import com.twoheart.dailyhotel.screen.common.ReviewTermsActivity;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class StayReviewActivity extends PlaceReviewActivity
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, StayReviewActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        List<PlaceReviewProgress> placeReviewProgressList = new ArrayList<>();

        placeReviewProgressList.add(new PlaceReviewProgress("청결", 45));
        placeReviewProgressList.add(new PlaceReviewProgress("위치", 18));
        placeReviewProgressList.add(new PlaceReviewProgress("서비스", 38));
        placeReviewProgressList.add(new PlaceReviewProgress("시설", 46));

        List<PlaceReview> placeReviewList = new ArrayList<>();
        placeReviewList.add(new PlaceReview("dai**", "2017-02-17T09:00:00+09:00", "호텔자체가 너무 좋아서 좋았으며 체크인하는과정에있어서 진정한 서비스 마인드에 또 한번 감동 받았고, 시설 또한 훌륭했습니다. 직원들의 서비스는 진짜 완벽할 정도로 훌륭했습니다."));
        placeReviewList.add(new PlaceReview("our**", "2017-01-12T09:00:00+09:00", "트윈베드로 배정받은것과 뷰가 공사장뷰라 좀 아쉬운것 말고는 전체적으로 만족했습니다."));
        placeReviewList.add(new PlaceReview("apc**", "2017-01-07T09:00:00+09:00", "매번 갈때마다 만족하는 곳"));
        placeReviewList.add(new PlaceReview("cadi**", "2017-01-03T09:00:00+09:00", "어플서 4인기준 코너스위트룸 구매해서 갔는데 데일리앱과 호텔간의 소통에 문제가 있었는지 침대와는 별도로, 매트와 베개는 있는데 이불은 없었어요. 프론트에 문의하니 2인기준에 2인추가 한거 아니냐고 추가 침구비용내고 구매가능하다고 얘기하더라구요. 객실선택 캡쳐화면 내용 그대로 읽어주니 4인기준 (퀸사이즈 베드 + 침구2채) 그제서야 알겠다고 갔다준다고 하더라구요. 일주일전 토요일에 숙박 잡았을뿐더러 그렇다고 저렴하게 예약한것도 아닌데 이러한 문제가 생겨서 기분이 별로였어요. 체크인 할때 호텔과 이런 문제가 있어서 체체크인시 기분은 정말 별로였지만 그래도 기분좋게 푹 쉬다왔습니다.  앞으로도 데일리호텔 이용할께요! 데일리호텔 너무 좋아요 이렇게 좋은 호텔 경험 할 수 있게 해주셔서 감사합니다~"));

        setReviewList(placeReviewProgressList, placeReviewList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();
    }

    @NonNull
    @Override
    protected PlaceReviewLayout createInstanceLayout()
    {
        return new StayReviewLayout(this, new StayReviewLayout.OnEventListener()
        {
            @Override
            public void onTermsClick()
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                startActivityForResult(ReviewTermsActivity.newInstance(StayReviewActivity.this), Constants.CODE_REQUEST_ACTIVITY_REVIEW_TERMS);
            }

            @Override
            public void finish()
            {
                StayReviewActivity.this.onBackPressed();
            }
        });
    }
}
