# 什么是加密算法？

​			数据加密的基本过程就是对原来为明文的文件或数据按某种算法进行处理，使其成为不可读的一段代码为“密文”，使其只能在输入相应的密钥之后才能显示出原容，通过这样的途径来达到保护数据不被非法人窃取、阅读的目的。 该过程的逆过程为解密，即将该编码信息转化为其原来数据的过程。

# 为什么需要加密算法？

​			例如在互联网中，我们传输数据，那么有一些比较重要的数据，例如密码，或者身份证号码以及手机号等等的隐私信息，那么对于这部分的敏感数据，如果被其他人所获取到了，或者看到了，那么这部分的数据就算被别人所获取到了，那么我们也不想被别人所得到，那么我们就需要对这个数据进行加密，其实早在古代就诞生了很多的加密算法，古人会将原来的信息进行一层转化，生成一个看不懂的信息，然后用一种特殊对应的方式将它一个一个解读出来，得到信息，例如在打仗的时候密文如果被敌军获取到了，那么作战计划也就暴露了，所以加密通常是对比较重要的数据的一个保护。

# 加密算法的分类？

​			那么加密算法到底分为哪些类呢？如下所示，在应用比较广泛的加密算法分为3大类，分别是：

​						1、对称加密

​									比较常见的对称加密有AES，RC4，3DES 

​						2、 非对称加密

​									比较常见的非对称加密有RSA，DSA/DSS 

​						3、 HASH算法

​									比较常见的HASH算法加密有MD5，SHA1，SHA256

# 对称加密



# 非对称加密



# HASH算法



# 其他加密算法

## 凯撒加密

​			凯撒加密法，或称恺撒加密、恺撒变换、变换加密，是一种最简单且最广为人知的加密技术。它是一种替换加密的技术，明文中的所有字母都在字母表上向后（或向前）按照一个固定数目进行偏移后被替换成密文。

​			恺撒密码（英语：Caesar cipher），或称恺撒加密、恺撒变换、变换加密，是一种最简单且最广为人知的加密技术。它是一种替换加密的技术，明文中的所有字母都在字母表上向后（或向前）按照一个固定数目进行偏移后被替换成密文。例如，当偏移量是3的时候，所有的字母A将被替换成D，B变成E，以此类推。这个加密方法是以罗马共和时期恺撒的名字命名的，当年恺撒曾用此方法与其将军们进行联系。

### Java代码实现

​			我们使用Java代码来实现凯撒加密：

```java
    public static void main(String[] args) {
        String message = "Bigkang 黄康 Nice";
        Integer key = 3;
        char[] chars = message.toCharArray();
        StringBuffer str = new StringBuffer();
        for (char aChar : chars) {
            // 将原来字符UTF8+3 转为新的字符
            char character = (char) ((int) aChar + key);
            // 将新的字符存储
            str.append(character);
        }
        System.out.println("加密后：" + str.toString());
        
        StringBuffer decrypt = new StringBuffer();
        for (char aChar : str.toString().toCharArray()) {
            // 将原来字符UTF8-3 还原为原来的的字符
            char character = (char) ((int) aChar - key);
            decrypt.append(character);
        }
        System.out.println("解密回：" + decrypt.toString());
    }
```

​			然后我们可以看到打印的值为

```
加密后：Eljndqj#黇庺#Qlfh
解密回：Bigkang 黄康 Nice
```

​			工具类编写后如下：

```java
/**
 * @Author BigKang
 * @Date 2020/6/23 6:33 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize 凯撒加密工具类
 */
public class CaesarCipherUtil {

    private static final Integer KEY = 10;

    /**
     * 重载方法
     * @param message 加密前信息
     * @return
     */
    public static String encrypt(String message){
        return encrypt(message,KEY);
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


    public static String decode(String message,Integer key){
        if(message == null || message.length() < 1){
            throw new RuntimeException("Message is Null");
        }
        if(key == null){
            throw new RuntimeException("Key is Null");
        }
    }

}

```

