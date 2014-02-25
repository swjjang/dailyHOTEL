package com.twoheart.dailyhotel.utils;

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
