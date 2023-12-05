package br.ufsm.csi.pilacoin.utils;

public class StrUtil {
  public static String limitCharsAddEllipsis(String str, int limit) {
    if (str.length() <= limit) {
      return str;
    }

    return str.substring(0, Math.min(str.length(), limit)) + "...";
  }
}
