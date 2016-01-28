package com.twoheart.dailyhotel.model;

public class Category
{
    public static final Category ALL = new Category("전체", "all");

    public String name;
    public String code;

    public Category(String name, String code)
    {
        this.name = name;
        this.code = code;
    }
}
