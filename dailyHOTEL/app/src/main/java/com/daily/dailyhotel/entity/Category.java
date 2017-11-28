package com.daily.dailyhotel.entity;

import android.os.Parcel;

/**
 * District 검색이 고메와 합쳐져 있지 않아서 발생하는 이슈로 추후 합치게 되면 상속 받는 부분 삭제
 */
public class Category extends com.twoheart.dailyhotel.model.Category
{
    public Category(String code, String name)
    {
        super(name, code);
    }

    public Category(Parcel in)
    {
        super(in);
    }

    //    public static final Category ALL = new Category("all", "전체");
    //
    //    public String name;
    //    public String code;
    //
    //    public Category(String code, String name)
    //    {
    //        this.code = code;
    //        this.name = name;
    //    }
}
