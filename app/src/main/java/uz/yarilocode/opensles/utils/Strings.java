package uz.yarilocode.opensles.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Yaroslav on 12.12.15.
 * Copyright 2015 iYaroslav LLC.
 */
public class Strings {

	public static String sha1(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.reset();
			byte[] buffer = input.getBytes("UTF-8");
			md.update(buffer);
			byte[] digest = md.digest();

			String hexStr = "";
			for (byte aDigest : digest) {
				hexStr += Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1);
			}

			return hexStr;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static int hash(String input) {
		int hash = 7;
		int length = input.length();
		for (int i = 0; i < length; i++) {
			hash = hash * 31 + input.charAt(i);
		}

		return hash;
	}

}
