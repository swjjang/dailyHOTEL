package com.twoheart.dailyhotel.util.exception;

/**
 * Created by sheldon on 2016. 2. 4..
 */
public class MemoryClearException extends RuntimeException
{
    private static final long serialVersionUID = 2784321896746548864L;

    /**
     * Constructs a new {@code NullPointerException} that includes the current
     * stack trace.
     */
    public MemoryClearException() {
    }

    /**
     * Constructs a new {@code NullPointerException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage
     *            the detail message for this exception.
     */
    public MemoryClearException(String detailMessage) {
        super(detailMessage);
    }
}
