/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * Crypto
 * 
 * 암호화 유틸 클래스이다. MD5로 암호화한 뒤 이것을 다시 BASE64 인코딩을
 * 수행하여 암호화된 String 값을 반환한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util;

import java.security.MessageDigest;

import android.util.Base64;

public class Crypto {
	/**
     * byte[] ret = HashUtil.digest("MD5", "abcd".getBytes());
     *  처럼 호출
     */
    public static byte[] digest(String alg, byte[] input) {
    	try {
	        MessageDigest md = MessageDigest.getInstance(alg);
	        return md.digest(input);
    	} catch (Exception e){
    		return null;
    	}
    }
	
	public static String encrypt(String inputValue) {
        
		try {
			if( inputValue == null ) throw new Exception("Can't conver to Message Digest 5 String value!!");
	        byte[] ret = digest("MD5", inputValue.getBytes());
	        String result = Base64.encodeToString(ret, 0);    
	        return result;
		} catch (Exception e){
			return null;
		}
    }
}
