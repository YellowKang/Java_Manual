package com.sigreal.xp.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author HuangKang
 * @date 2023/1/5 2:26 PM
 * @describe AES工具类
 */
public class AESUtil {

    private static final Logger log = LoggerFactory.getLogger(AESUtil.class);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String KEY_AES = "AES";

    private static final String CIPHER_KEY = "AES/CBC/PKCS5Padding";

    /**
     * TryAes实体
     */
    static class TryAESEntity {

        /**
         * 是否加解密成功
         */
        private Boolean success = Boolean.TRUE;

        /**
         * 加解密后数据
         */
        private String data;

        /**
         * 异常信息
         */
        private String errorMessage;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public static TryAESEntity success(String data) {
            TryAESEntity tryAES = new TryAESEntity();
            tryAES.setData(data);
            return tryAES;
        }

        public static TryAESEntity error(String message) {
            TryAESEntity tryAES = new TryAESEntity();
            tryAES.setSuccess(Boolean.FALSE);
            tryAES.setErrorMessage(message);
            return tryAES;
        }

        @Override
        public String toString() {
            return "TryAESEntity{" +
                    "success=" + success +
                    ", data='" + data + '\'' +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }


    public static TryAESEntity tryDecrypt(String data, String key) {
        return tryDecrypt(data, key, null);
    }

    public static TryAESEntity tryEncrypt(String data, String key) {
        return tryEncrypt(data, key, null);
    }

    public static TryAESEntity tryDecrypt(String data, String key, String ivKey) {
        try {
            String body = doAES(data, key, ivKey, Cipher.DECRYPT_MODE);
            return TryAESEntity.success(body);
        } catch (Exception e) {
            return TryAESEntity.error(e.getMessage());
        }
    }

    public static TryAESEntity tryEncrypt(String data, String key, String ivKey) {
        try {
            return TryAESEntity.success(doAES(data, key, ivKey, Cipher.ENCRYPT_MODE));
        } catch (Exception e) {
            return TryAESEntity.error(e.getMessage());
        }
    }


    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     * @return 加密字符串
     */
    public static String encrypt(String data, String key) throws Exception {
        return encrypt(data, key, null);
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     * @return 解密字符串
     */
    public static String decrypt(String data, String key) throws Exception {
        return decrypt(data, key, null);
    }


    /**
     * @param data  加密原文
     * @param key   秘钥key
     * @param ivKey 协议参数Key
     * @return
     */
    public static String encrypt(String data, String key, String ivKey) throws Exception {
        return doAES(data, key, ivKey, Cipher.ENCRYPT_MODE);
    }

    /**
     * @param data  加密密文
     * @param key   秘钥key
     * @param ivKey 协议参数Key
     * @return 原文
     */
    public static String decrypt(String data, String key, String ivKey) throws Exception {
        return doAES(data, key, ivKey, Cipher.DECRYPT_MODE);
    }


    /**
     * @param data  报文字符串
     * @param key   秘钥key
     * @param ivKey 协议参数Key
     * @param mode  类型 加密 OR 解密
     * @return 加密 OR 解密 后报文
     */
    public static String doAES(String data, String key, String ivKey, int mode) throws Exception {
        Cipher cipher = null;
        SecretKeySpec keySpec =
                new SecretKeySpec(
                        key.getBytes(DEFAULT_CHARSET), KEY_AES);
        if (ivKey != null) {
            cipher = Cipher.getInstance(CIPHER_KEY);
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
            cipher.init(mode, keySpec, iv);
        } else {
            cipher = Cipher.getInstance(KEY_AES);
            cipher.init(mode, keySpec);
        }

        boolean encrypt = mode == Cipher.ENCRYPT_MODE;
        byte[] content;
        if (encrypt) {
            content = data.getBytes(DEFAULT_CHARSET);
        } else {
            content = Base64.decodeBase64(data);
        }
        byte[] result = cipher.doFinal(content);
        if (encrypt) {
            return new String(Base64.encodeBase64(result, false), DEFAULT_CHARSET);
        } else {
            return new String(result, DEFAULT_CHARSET);
        }
    }


    public static void main(String[] args) throws Exception {
        String data = "{\"id\":\"test\",\"order_id\":\"201238123\"}";
        // Key和参数ivKey长度都为16，由认证服务统一签发
        String key = "OI28Dm4hDpZPTt9i";
        String ivKey = "azqsxlpbi42t6ohx";

        String ivBody = encrypt(data, key, ivKey);
        String body = encrypt(data, key);

        System.out.println(String.format("原 报文 : %s", data));
        System.out.println(String.format("加密后 报文 : %s", body));
        System.out.println(String.format("带参加密后 报文 : %s", ivBody));

        System.out.println(String.format("解密后 报文 : %s", decrypt(body,key)));
        System.out.println(String.format("带参解密后 报文 : %s", decrypt(ivBody, key, ivKey)));


        System.out.println(String.format("try解密后 报文 : %s", tryDecrypt(body, key)));
        System.out.println(String.format("try带参解密后 报文 : %s", tryDecrypt(ivBody, key,ivKey)));

        // 模拟异常
        System.out.println(String.format("try带参解密后 报文 : %s", tryDecrypt(ivBody, key,ivKey+"1")));

    }

}
