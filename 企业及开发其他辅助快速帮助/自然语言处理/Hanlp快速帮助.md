# Hanlp是什么

​		HanLP是一系列模型与算法组成的NLP工具包，由大快搜索主导并完全开源，目标是普及自然语言处理在生产环境中的应用。

​		HanLP具备功能完善、性能高效、架构清晰、语料时新、可自定义的特点。

# 环境准备

​		IDEA，SpringBoot使用经验即可（不会打死），以及引入swagger依赖

​		首先我们需要先下载Hanlp的数据包（注意版本，与maven同步）

​		下载链接为：http://nlp.hankcs.com/download.php?file=data

​		如果不行，到github官网上进行下载，地址为：https://github.com/hankcs/HanLP

​		下载完成放入磁盘（记住这个路径）

# 使用SpringBoot整合

​		首先我们使用maven引入jar包

```xml
        <dependency>
            <groupId>com.hankcs</groupId>
            <artifactId>hanlp</artifactId>
            <version>portable-1.7.3</version>
        </dependency>
```

# 创建配置文件

在resource下新建hanlp.properties

```properties
#本配置文件中的路径的根目录，根目录+其他路径=完整路径（支持相对路径，请参考：https://github.com/hankcs/HanLP/pull/254）
#Windows用户请注意，路径分隔符统一使用/
#root=E:/utils/hanlp/

root=/Users/bigkang/Documents/Hanlp/

#好了，以上为唯一需要修改的部分，以下配置项按需反注释编辑。

#核心词典路径
CoreDictionaryPath=data/dictionary/CoreNatureDictionary.txt
#2元语法词典路径
BiGramDictionaryPath=data/dictionary/CoreNatureDictionary.ngram.txt
#自定义词典路径，用;隔开多个自定义词典，空格开头表示在同一个目录，使用“文件名 词性”形式则表示这个词典的词性默认是该词性。优先级递减。
#所有词典统一使用UTF-8编码，每一行代表一个单词，格式遵从[单词] [词性A] [A的频次] [词性B] [B的频次] ... 如果不填词性则表示采用词典的默认词性。
CustomDictionaryPath=data/dictionary/custom/CustomDictionary.txt; 现代汉语补充词库.txt; 全国地名大全.txt ns; 人名词典.txt; 机构名词典.txt; 上海地名.txt ns;data/dictionary/person/nrf.txt nrf;
##停用词词典路径
CoreStopWordDictionaryPath=data/dictionary/stopwords.txt
#同义词词典路径
CoreSynonymDictionaryDictionaryPath=data/dictionary/synonym/CoreSynonym.txt
#人名词典路径
PersonDictionaryPath=data/dictionary/person/nr.txt
#人名词典转移矩阵路径
PersonDictionaryTrPath=data/dictionary/person/nr.tr.txt
#繁简词典根目录
tcDictionaryRoot=data/dictionary/tc
#HMM分词模型
HMMSegmentModelPath=data/model/segment/HMMSegmentModel.bin
#分词结果是否展示词性
ShowTermNature=true
#IO适配器，实现com.hankcs.hanlp.corpus.io.IIOAdapter接口以在不同的平台（Hadoop、Redis等）上运行HanLP
#默认的IO适配器如下，该适配器是基于普通文件系统的。
IOAdapter=com.hankcs.hanlp.corpus.io.FileIOAdapter
#感知机词法分析器
PerceptronCWSModelPath=data/model/perceptron/pku1998/cws.bin
PerceptronPOSModelPath=data/model/perceptron/pku1998/pos.bin
PerceptronNERModelPath=data/model/perceptron/pku1998/ner.bin
#CRF词法分析器
CRFCWSModelPath=data/model/crf/pku199801/cws.txt
CRFPOSModelPath=data/model/crf/pku199801/pos.txt
CRFNERModelPath=data/model/crf/pku199801/ner.txt
#更多配置项请参考 https://github.com/hankcs/HanLP/blob/master/src/main/java/com/hankcs/hanlp/HanLP.java#L59 自行添加
```

# 新建工具类

```java

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.phrase.MutualInformationEntropyPhraseExtractor;
import com.hankcs.hanlp.seg.Segment;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * @Author BigKang
 * @Date 2019/6/28 16:47
 * @Summarize Hanlp工具类
 */
public class HanlpUtil {
    /**
     * 热点词统计。size为返回的热点词个数
     *
     * @param keyword
     * @param size
     * @return
     */
    public static List<String> corekeyword(String keyword, Integer size) {
        return HanLP.extractKeyword(keyword, size);
    }

    /**
     * 重载父类方法默认统计10个核心关键字
     *
     * @param keyword
     * @return
     */
    public static List<String> corekeyword(String keyword) {
        return corekeyword(keyword, 10);
    }

    public static List<String> baseCoreWord(String keyword,String type) {
        Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
        ConcurrentSkipListSet<String> strings = new ConcurrentSkipListSet<>();
        segment.seg(keyword).stream().filter(v ->
                v.nature.toString().equals(type)
        ).forEach(v -> {
            strings.add(v.word);
        });
        return strings.stream().collect(Collectors.toList());
    }

    /**
     * 关键内容摘要提取
     *
     * @param keyword
     * @param size
     * @return
     */
    public static List<String> coreContent(String keyword, Integer size) {
        return HanLP.extractSummary(keyword, size);
    }

    /**
     * 重载coreContent方法提取内容
     *
     * @param keyword
     * @return
     */
    public static List<String> coreContent(String keyword) {
        return coreContent(keyword, 10);
    }

    /**
     * 提取核心短语
     * @param keyword
     * @param size
     * @return
     */
    public static List<String> corePhrase(String keyword, Integer size) {
        return MutualInformationEntropyPhraseExtractor.extract(keyword, size);
    }

    /**
     * 重载提取核心短语
     * @param keyword
     * @return
     */
    public static List<String> corePhrase(String keyword) {
        return MutualInformationEntropyPhraseExtractor.extract(keyword, 10);
    }

}
```

# 新建控制器调试



```java
import com.hankcs.hanlp.HanLP;
import com.kang.boot.hanlp.util.HanlpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(tags = "测试")
@RequestMapping("HanlpTest")
public class HanlpTestController {

    /**
     * 查询核心地点
     * @param keyword
     * @return
     */
    @PostMapping("coreAddress")
    @ApiOperation("查询核心地点")
    public Object coreAddress(String keyword) {
        //ns为地点
        return HanlpUtil.baseCoreWord(keyword,"ns");
    }

    /**
     * 获取关键词
     * @param keyword
     * @param size
     * @return
     */
    @PostMapping("coreKeywork")
    @ApiOperation("获取关键词")
    public Object coreKeywork(String keyword,Integer size) {
        return StringUtils.isEmpty(keyword) ? null
                : size == null ? HanlpUtil.corekeyword(keyword)
                : HanlpUtil.corekeyword(keyword,size);
    }

    /**
     * 获取核心内容
     * @param keyword
     * @param size
     * @return
     */
    @PostMapping("coreContent")
    @ApiOperation("获取核心内容")
    public Object coreContent(String keyword,Integer size) {
        return StringUtils.isEmpty(keyword) ? null
                : size == null ? HanlpUtil.coreContent(keyword)
                : HanlpUtil.coreContent(keyword,size);
    }


    /**
     * 获取核心短语
     * @param keyword
     * @param size
     * @return
     */
    @PostMapping("corePhrase")
    @ApiOperation("获取核心短语")
    public Object corePhrase(String keyword,Integer size) {
        return StringUtils.isEmpty(keyword) ? null
                : size == null ? HanlpUtil.corePhrase(keyword)
                : HanlpUtil.corePhrase(keyword,size);
    }

    /**
     * 简体转换繁体
     * @param keyword
     * @return
     */
    @PostMapping("simplifiedToTraditional")
    @ApiOperation("简体转换繁体")
    public Object simplifiedToTraditional(String keyword) {
        return StringUtils.isEmpty(keyword) ? null
                : HanLP.convertToTraditionalChinese(keyword) ;
    }

    /**
     * 繁体转换简体
     * @param keyword
     * @return
     */
    @PostMapping("traditionalToSimplified")
    @ApiOperation("繁体转换简体")
    public Object traditionalToSimplified(String keyword) {
        return StringUtils.isEmpty(keyword) ? null
                : HanLP.convertToSimplifiedChinese(keyword) ;
    }

    /**
     * 机构以及公司识别
     * @param keyword
     * @return
     */
    @PostMapping("coreCompany")
    @ApiOperation("机构以及公司识别")
    public Object coreCompany(String keyword) {
        return StringUtils.isEmpty(keyword) ? null
                : HanlpUtil.baseCoreWord(keyword,"nt");
    }

}
```

