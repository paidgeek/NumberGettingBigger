package com.moybl.topnumber.backend;

import java.security.SecureRandom;

public class Util {

	private static final int SESSION_TOKEN_LENGTH = 64;
	private static final String CHARS_64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generateSessionToken() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < SESSION_TOKEN_LENGTH; i++) {
			int r = RANDOM.nextInt(CHARS_64.length());
			sb.append(CHARS_64.charAt(r));
		}

		return sb.toString();
	}

}
