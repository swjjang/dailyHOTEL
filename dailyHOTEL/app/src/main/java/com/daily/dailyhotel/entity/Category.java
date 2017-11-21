package com.daily.dailyhotel.entity;

public class Category
{
    public static final Category ALL = new Category("all", "전체");

    public String name;
    public String code;

    public Category(String code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
