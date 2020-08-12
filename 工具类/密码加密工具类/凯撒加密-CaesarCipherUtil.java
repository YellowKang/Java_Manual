/**
 * @Author BigKang
 * @Date 2020/6/23 6:33 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize 凯撒加密工具类
 */
public class CaesarCipherUtil {

    private static final Integer DEFAULT_KEY = 10;

    /**
     * 重载方法
     * @param message 加密前信息
     * @return
     */
    public static String encrypt(String message){
        return encrypt(message,DEFAULT_KEY);
    }

    /**
     *  凯撒加密
     * @param message 加密前信息
     * @param key Key长度
     * @return 返回字符
     */
    public static String encrypt(String message,Integer key){
        if(message == null || message.length() < 1){
            throw new RuntimeException("Message is Null");
        }
        if(key == null){
            throw new RuntimeException("Key is Null");
        }
        StringBuffer str = new StringBuffer();
        char[] chars = message.toCharArray();
        for (char aChar : chars) {
            // 将原来字符UTF8+key长度 转为新的字符
            char character = (char) ((int) aChar + key);
            // 将新的字符存储
            str.append(character);
        }
        return str.toString();
    }

    /**
     * 重载解密方法
     * @param message
     * @return
     */
    public static String decode(String message){
        return decode(message, DEFAULT_KEY);
    }

    /**
     * 凯撒解密
     * @param message 消息
     * @param key 秘钥
     * @return
     */
    public static String decode(String message,Integer key){
        if(message == null || message.length() < 1){
            throw new RuntimeException("Message is Null");
        }
        if(key == null){
            throw new RuntimeException("Key is Null");
        }
        char[] chars = message.toCharArray();
        StringBuffer str = new StringBuffer();
        for (char aChar : chars) {
            char character = (char) ((int) aChar - key);
            str.append(character);
        }
        return str.toString();
    }

    public static void main(String[] args) {
        String encrypt = encrypt("Bigkang Nice Nb");
        System.out.println(encrypt);
        String decode = decode(encrypt);
        System.out.println(decode);

    }
}
