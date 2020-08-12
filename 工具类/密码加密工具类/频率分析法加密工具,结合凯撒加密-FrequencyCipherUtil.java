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
        // 计算词频规律,首先+最大值然后减去下一个，一次排列，最后乘以2
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
