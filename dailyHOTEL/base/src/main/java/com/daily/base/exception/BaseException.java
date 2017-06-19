package com.daily.base.exception;

public class BaseException extends Exception
{
    public static final int CODE_UNAUTHORIZED = 401;

    private int mCode;

    public BaseException()
    {

    }

    public BaseException(int code, String message)
    {
        super(message);

        mCode = code;
    }

    public int getCode()
    {
        return mCode;
    }
}
