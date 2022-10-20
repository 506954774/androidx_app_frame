package com.ilinklink.tg.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * AESUtil
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/3/8  19:37
 * Copyright : 2014-2016 深圳趣动智能科技有限公司-版权所有
 **/
public class AESUtil {

    public static String KEY="E9276D72AF79899CACD1EED704B824D9";

    private int[] key1_hex={0xE9, 0x27, 0x6D, 0x72, 0xAF, 0x79, 0x89, 0x9C, 0xAC, 0xD1, 0xEE, 0xD7, 0x04, 0xB8, 0x24, 0xD9};
    private int[] key2_hex={0x58, 0x7D, 0x8F, 0x53, 0x93, 0x90, 0x8E, 0x8F, 0x9A, 0x03, 0x71, 0x03, 0x0F, 0x4A, 0x4F, 0x9B};
    private int[] key3_hex={0x8D, 0x0C, 0x8D, 0x9E, 0x8F, 0x9A, 0x02, 0x9C, 0x2C, 0x3D, 0x2B, 0x3D, 0x53, 0x94, 0x92, 0x93};
    private int[] key4_hex={0x01, 0x7F, 0x6C, 0x9A, 0x78, 0x90, 0x21, 0x34, 0x7C, 0x8B, 0x9B, 0x92, 0x93, 0x72, 0x93, 0x7A};



    public static String key1="E9276D72AF79899CACD1EED704B824D9";
    public static String key2="587D8F5393908E8F9A0371030F4A4F9B";
    public static String key3="8D0C8D9E8F9A029C2C3D2B3D53949293";
    public static String key4="017F6C9A789021347C8B9B929372937A";
    public static String key5="82040185301306750013D97056152C87";
    //0x82, 0x04, 0x01, 0x85, 0x30, 0x13, 0x06, 0x75, 0x00, 0x13, 0xd9, 0x70, 0x56, 0x15, 0x2c, 0x87,

    public static String[] KEY_ARRAY ={key1,key2,key3,key4,key5};



    private static String KEY_80=key1+key2+key3+key4+key5;

    static {
        String origial="123456789q123456789w123456789u123456789v123456789p123456789o123456789t1234567898";
        KEY_80=StringUtil.str2HexStr(origial);
    }

    public static String getKeyByIndex(int index){
        if(index>=0&&index<=63){
            //赋值之后再返回
            return KEY=KEY_80.substring(index*2,index*2+32);
        }
        return null;
    }



    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content, byte[] password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password));
            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param password  加密密码
     * @return
     */
    public static byte[] encrypt(byte[] content, byte[] password) {
        try {
            //NoPadding，PKCS5Padding，ISO10126Padding,
            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

            byte[] byteContent = content;

            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @method name:decrypt
     * @des:  动态解密
     * @param :[keyIndex, secretString]
     * @return type:java.lang.String
     * @date 创建时间:2017/3/13
     * @author Chuck
     **/
    public static String decrypt(int keyIndex,String secretString){

        String result=null;
        try {
            String hexKey = KEY_ARRAY[keyIndex];
            byte[] key= ByteUtil.hexStr2Bytes(hexKey);
            byte[] bytesOrignal=AESUtil.decrypt(ByteUtil.hexStr2Bytes(secretString),key);//解密
            result= ByteUtil.byteToHexString(bytesOrignal);//解密得到48个字节的原文
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }

    }


}
