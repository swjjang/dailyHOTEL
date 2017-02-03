package com.twoheart.dailyhotel.network.model;

import android.support.annotation.NonNull;

public class ErrorBuilder
{
    public int responseCode; // http 응답코드

    public int msgCode;
    public String message;

    public Throwable throwable;
    public String log;
    public AfterActive afterActive;
    public AfterType afterType;
    public AfterCallback mAfterCallback;

    public interface AfterCallback
    {
        void onAfterRun();
    }

    public enum AfterActive
    {
        // 결과를 내부 구조가 알아서
        DEFAULT,

        // 결과를 아무것도 하지 않음
        NONE,

        // 재시작
        RESTART,

        // 현재 화면 종료
        FINISH,
    }

    public enum AfterType
    {
        // 결과를 내부 구조가 알아서
        DEFAULT,

        // 아무것도 하지 않음
        NONE,

        // 토스트로 보여줌
        TOAST,

        // 팝업으로 보여줌.
        POPUP,
    }

    /**
     *
     * @param responseCode
     * @param msgCode
     * @param message null이면 "서버와의 통신 이슈로..." 메시지로 넣는다.
     */
    public ErrorBuilder(int responseCode, int msgCode, String message)
    {
        this.responseCode = responseCode;
        this.msgCode = msgCode;
        this.message = message;

        afterType = AfterType.DEFAULT;
        afterActive = AfterActive.DEFAULT;
    }

    public ErrorBuilder setException(@NonNull Throwable throwable, String log)
    {
        this.throwable = throwable;
        this.log = log;

        return this;
    }

    public ErrorBuilder setAfterActive(@NonNull AfterType afterType, @NonNull AfterActive afterActive)
    {
        this.afterType = afterType;
        this.afterActive = afterActive;

        return this;
    }

    public ErrorBuilder setAfterCallback(AfterCallback afterCallback)
    {
        mAfterCallback = afterCallback;

        return this;
    }
}
