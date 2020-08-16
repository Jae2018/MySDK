package com.jae.downsdk.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
    public static String encode(String pwd) {
        try {
            MessageDigest  digest = MessageDigest.getInstance("MD5");
            byte[]  bytes = digest.digest(pwd.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                String s = Integer.toHexString(0xff & aByte);

                if (s.length() == 1) {
                    sb.append("0").append(s);
                } else {
                    sb.append(s);
                }
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("NoSuchAlgorithmException");
        }
    }
}
