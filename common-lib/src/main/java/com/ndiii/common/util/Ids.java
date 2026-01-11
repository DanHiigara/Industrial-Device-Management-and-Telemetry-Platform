package com.ndiii.common.util;

import java.security.SecureRandom;

public final class Ids {
  private static final SecureRandom RND = new SecureRandom();
  private static final char[] ALPH = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

  private Ids() {}

  public static String apiKey(int len) {
    var sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append(ALPH[RND.nextInt(ALPH.length)]);
    return sb.toString();
  }
}
