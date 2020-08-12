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

​			采用单钥密码系统的加密方法，同一个密钥可以同时用作信息的加密和解密，这种加密方法称为对称加密，也称为单密钥加密。简单的来说就是我们加密的秘钥都是同一个，



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

## 频率分析法

​			我们可以通过原文，进行原文评率进行一个评率的统计，然后根据这个频率去进行特定的加密，这样就算是在我们不知道秘钥的情况下也能够进行解密，以特定的方式去进行一个加密，然后根据原文计算出来Key值，然后根据特定的方式再次计算出Key直接对密文进行解析即可，下面就是拓展了凯撒加密的频率分析法：

​			大致思路为： 计算字频规律,统计出现最多的3个字频数量，首先+最大值然后减去下一个，一依次排列，最后乘以2，就得到了我们的秘钥（此处暂时尚未优化，如果出现0的情况或者其他则不准确问题，可以自行优化）。

### Java代码实现

​		注意此处只提供思路，加密采用上方凯撒加密工具类，引入类即可，重点是根据频率分析，然后是Key的生成方式，以及加密方式，都不是固定的，请根据自身环境酌情考虑。

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author BigKang
 * @Date 2020/6/24 10:03 上午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 频率分析法加密工具,结合凯撒加密（可自定义修改）
 */
public class FrequencyCipherUtil {

    /**
     * 词频分析计算Key，然后进行凯撒加密
     * @param s
     * @return
     */
    public static String encrypt(String s){
        Integer key = frequencyRouting(s);
        String encrypt = CaesarCipherUtil.encrypt(s, key);
        return encrypt;
    }

    /**
     * 词频分析计算Key，然后进行凯撒解密
     * @param s
     * @return
     */
    public static String decode(String s){
        Integer key = frequencyRouting(s);
        String decode = CaesarCipherUtil.decode(s, key);
        return decode;
    }

    /**
     * 词频路由，返回加密Key,自定义规则进行加密
     * @return
     */
    private static Integer frequencyRouting(String s){
        // 默认统计前3排名的词频
        List<CharFrequency> charFrequencies = frequencySort(s, 3);

        Integer key = 0;
        Boolean flag = true;
        // 计算词频规律,首先+最大值然后减去下一个，依次排列，最后乘以2
        for (CharFrequency charFrequency : charFrequencies) {
            if(flag){
                key += charFrequency.frequency;
                flag = false;
            }else {
                key -= charFrequency.frequency;
                flag = true;
            }
        }

        // 如果大于0则乘以2
        if(key > 0){
            key *= 2;
        }
        return key;
    }

    /**
     * 重载词频排序，默认不统计前几
     *
     * @param s
     * @return
     */
    public static List<CharFrequency> frequencySort(String s) {
        return frequencySort(s, null);
    }


    /**
     * 重载词频排序，默认排序
     *
     * @param s
     * @param length
     * @return
     */
    public static List<CharFrequency> frequencySort(String s, Integer length) {
        return frequencySort(s, length, true);
    }

    /**
     * 词频统计字符
     *
     * @param s      词频统计字符
     * @param length 返回的词频个数（排序后，前3或者前几）
     * @return
     */
    public static List<CharFrequency> frequencySort(String s, Integer length, Boolean sort) {
        // 初始化字符数组
        char[] chars = s.toCharArray();
        Map<Character, Integer> map = new TreeMap<>();
        // 遍历之后放入Map，并且统计次数
        for (char aChar : chars) {
            Integer frequency = map.get(aChar);
            if (frequency == null) {
                map.put(aChar, 1);
            } else {
                map.put(aChar, ++frequency);
            }
        }
        // 遍历Key放入CharFrequency集合对象
        List<CharFrequency> charFrequencies = new ArrayList<>();
        for (Character character : map.keySet()) {
            Integer integer = map.get(character);
            charFrequencies.add(new CharFrequency(character, integer));
        }

        if (sort != null && sort) {
            // 根据frequency频率排序
            charFrequencies.sort(null);
        }

        // 检查是否只需要统计前几位
        if (length != null && charFrequencies.size() > length) {
            return charFrequencies.subList(0, length);
        }
        return charFrequencies;
    }

    /**
     * 字符频率
     */
    public static class CharFrequency implements Comparable<CharFrequency> {
        public char character;
        public int frequency;

        public CharFrequency(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(CharFrequency o) {
            return o.frequency - this.frequency;
        }
    }


    /**
     * Main测试
     *
     * @param args
     */
    public static void main(String[] args) {
        String encrypt = encrypt("Big qweqwqe");
        System.out.println(encrypt);

        String decode = decode(encrypt);
        System.out.println(decode);
    }
}

```

