package com.born.frame.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.born.frame.utils.JudgeUtil.isNotEmpty;

/**
 * Created by min on 2016/12/12.
 */

public class SecurityUtil {
    /**
     * 密码加密
     *
     * @param pwd
     * @return
     */
    public static String encodePwd(String pwd) {
        String result = pwd;
        if (isNotEmpty(pwd)) {
            String pwdAES = AESUtil.encode(pwd);
            result = URLEncoder.encode(pwdAES);
        }
        return result;

    }

    /**
     * 中文字符编码
     *
     * @param src
     * @return
     */
    public static String chineseEncode(String src) {
        src = TextUtils.isEmpty(src) ? "" : src;
        try {
            src = URLEncoder.encode(src, "utf-8");
        } catch (Exception e) {
        }
        return src;
    }

    public static String getMd5ByteByFile(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int length;
            while ((length = in.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            //32位加密
            return bytesToHexString(md.digest());
        } catch (Exception e) {
            return "";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    public static String getMD5(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
