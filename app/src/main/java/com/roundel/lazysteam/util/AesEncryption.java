package com.roundel.lazysteam.util;

/**
 * Created by Krzysiek on 18/05/2017.
 */

import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryption
{

    @Nullable
    public static String encrypt(String key, String initVector, String value)
    {
        try
        {
            return encrypt(key.getBytes("UTF-8"), initVector.getBytes("UTF-8"), value);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String encrypt(byte[] key, byte[] initVector, String value)
    {
        try
        {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AesEncryption");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String decrypt(String key, String initVector, String value)
    {
        try
        {
            return decrypt(key.getBytes("UTF-8"), initVector.getBytes("UTF-8"), value);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String decrypt(byte[] key, byte[] initVector, String encrypted)
    {
        try
        {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AesEncryption");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

            return new String(original);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}