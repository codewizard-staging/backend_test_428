package com.replicacia.utils;

public class DBUtils {
  public static String generatePublicId() {
    return String.valueOf(System.nanoTime());
  }
}
