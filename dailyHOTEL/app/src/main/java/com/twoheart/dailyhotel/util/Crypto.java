/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * Crypto
 * <p>
 * 암호화 유틸 클래스이다. MD5로 암호화한 뒤 이것을 다시 BASE64 인코딩을
 * 수행하여 암호화된 String 값을 반환한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.util;

import android.os.Build;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto
{
    private final static String HEX = "0123456789ABCDEF";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // AES암호
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * byte[] ret = HashUtil.digest("MD5", "abcd".getBytes()); 처럼 호출
     */
    public static byte[] digest(String alg, byte[] input)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance(alg);
            return md.digest(input);
        } catch (Exception e)
        {
            return null;
        }
    }

    public static String encrypt(String inputValue)
    {
        try
        {
            if (inputValue == null)
            {
                throw new Exception("Can't conver to Message Digest 5 String value!!");
            }
            byte[] ret = digest("MD5", inputValue.getBytes());
            return Base64.encodeToString(ret, Base64.NO_WRAP);
        } catch (Exception e)
        {
            return null;
        }
    }

    public static String encrypt(String seed, String text) throws Exception
    {
        if (Util.isTextEmpty(text) == true)
        {
            return null;
        }

        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, text.getBytes());
        String fromHex = toHex(result);

        return Base64.encodeToString(fromHex.getBytes(), Base64.NO_WRAP);
    }

    public static String decrypt(String seed, String encrypted) throws Exception
    {
        if (Util.isTextEmpty(encrypted) == true)
        {
            return null;
        }

        byte[] seedByte = seed.getBytes();
        String base64 = new String(Base64.decode(encrypted, Base64.NO_WRAP));
        byte[] rawKey = getRawKey(seedByte);
        byte[] enc = toByte(base64);
        byte[] result = decrypt(rawKey, enc);

        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception
    {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else
        {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(seed);

        try
        {
            kgen.init(256, sr);
        } catch (Exception e)
        {
            try
            {
                kgen.init(192, sr);
            } catch (Exception e1)
            {
                kgen.init(128, sr);
            }
        }

        SecretKey skey = kgen.generateKey();

        return skey.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    private static byte[] toByte(String hexString)
    {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
        {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    private static String toHex(byte[] buffers)
    {
        if (buffers == null)
        {
            return "";
        }

        StringBuffer result = new StringBuffer(2 * buffers.length);

        for (byte buffer : buffers)
        {
            appendHex(result, buffer);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b)
    {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
