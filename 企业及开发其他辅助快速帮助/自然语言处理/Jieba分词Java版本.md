# 结巴分词简介

结巴中文分词涉及到的算法包括：
 (1) 基于Trie树结构实现高效的词图扫描，生成句子中汉字所有可能成词情况所构成的有向无环图（DAG)；
 (2) 采用了动态规划查找最大概率路径, 找出基于词频的最大切分组合；
 (3) 对于未登录词，采用了基于汉字成词能力的HMM模型，使用了Viterbi算法

但是Java功能不全，并且由于长时间未更新，如果有更高要求，推荐使用Hanlp，Hanlp对Java支持更好，Jieba更佳轻量级

那么下面开始我们的使用吧



```
Ansj分词
```



# 引入依赖

我们引入springboot以及lombok的依赖，此示例使用springboot，自定义boot版本

```
        <dependency>
            <groupId>com.huaban</groupId>
            <artifactId>jieba-analysis</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.0</version>
        </dependency>
```

# 编写配置文件

这里我们使用自定义词典，这里是我编写的boot配置，上面intit

初始化词典路径，为data下面的两个文件使用逗号隔开，initAbsolute是否使用绝对路径，如果使用请将文件名写成全路径，如果不使用则放入resource下面并且指定类路径

stop为停用词词典，以及是否使用全路径

```
jieba.init.dict=data/user_dict_coalmine.txt,data/user_dict.txt
jieba.init.initAbsolute=false

jieba.stop.dict=data/user_stop_dict.txt,data/user_stop_dict_city.txt,data/stoped.txt
jieba.stop.stopAbsolute=false
```

yml版本

```
jieba:
    init:
        dict: data/user_dict_coalmine.txt,data/user_dict.txt
        initAbsolute: false
    stop:
        dict: data/user_stop_dict.txt,data/user_stop_dict_city.txt,data/stoped.txt
        stopAbsolute: false
```

# 编写配置类

首先编写第一个，用于存储停用词词汇（差评，Java版没有找到停用词配置，所以使用循环）

```java
@Data
public class JiebaDict {

    private List<String> jiebaStopDisct;

    private JiebaSegmenter segmenter;


    /**
     * 分词方法
     * @param keyword 需要分词的数据
     * @return
     */
    public List<String> keyword(String keyword) {
        List<String> list1 = segmenter.sentenceProcess(keyword);

        if(jiebaStopDisct != null && jiebaStopDisct.size() >= 1){
            list1 = list1.stream().filter(v -> {
                AtomicBoolean flag = new AtomicBoolean(true);
                jiebaStopDisct.forEach(z -> {
                    //如果发现停用词返回false，则不添加到结果中
                    if (v.equals(z)) {
                        flag.set(false);
                    }
                });
                //如果为空返回false
                if (StringUtils.isEmpty(v.trim())) {
                    return false;
                }
//            如果分词长度太短，则不添加，选择开启
//            if (v.length() == 1) {
//                return false;
//            }
                return flag.get();
            }).collect(Collectors.toList());
        }

        return list1;
    }
}
```

编写组件类

```java
/**
 * @Author BigKang
 * @Date 2019/7/24 16:37
 * @Summarize 结巴分词配置类
 */
@Configuration
@Slf4j
public class JiebaConfig {

    //初始化词典路径，使用“，”逗号隔开
    @Value("${jieba.init.dict:}")
    private String initDict;

    //是否绝对路径初始化文件，默认非绝对路径，为项目resource目录下
    @Value("${jieba.init.absolute:false}")
    private boolean initAbsolute;

    //停用词典路径，使用“，”逗号隔开
    @Value("${jieba.stop.dict:}")
    private String stopDict;

    //是否绝对路径停用词文件，默认非绝对路径，为项目resource目录下
    @Value("${jieba.stop.absolute:false}")
    private boolean stopAbsolute;

    /**
     * 初始化jieba分词并且加载停用词典
     *
     * @return
     */
    @Bean
    public JiebaDict jiebaDict() {

        WordDictionary instance = WordDictionary.getInstance();
        String[] init = null;
        if(StringUtils.isEmpty(initDict)){
            log.info("检测到未加载核心词库");
        }else{
            init = initDict.split(",");
            log.info("开始加载词库");
            //初始化结巴分词
            for (String s : init) {
                if (stopAbsolute) {
                    instance.loadUserDict(new File(s).toPath());
                } else {
                    instance.loadUserDict(new File(JiebaConfig.class.getClassLoader().getResource(s).getFile()).toPath());
                }
            }
        }

        //初始化结巴词典类
        JiebaDict jiebaDict = new JiebaDict();
        String[] stop = null;
        if(StringUtils.isEmpty(stopDict)){

        }else{
            stop =  stopDict.split(",");
            List<String> list = new ArrayList<>();
            try {
                log.info("初始化停用词典");
                list = fileToList(stop, initAbsolute);
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("初始化停用词" + list.size() + "个");
            jiebaDict.setJiebaStopDisct(list);
        }

        //将分词器注入
        JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
        jiebaDict.setSegmenter(jiebaSegmenter);
        return jiebaDict;
    }

    /**
     * 将文件转为停用词集合
     *
     * @param path
     * @param isAbsolute
     * @return
     * @throws IOException
     */
    public List<String> fileToList(String[] path, boolean isAbsolute) throws IOException {
        // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        List<String> list = new ArrayList<String>();
        for (String s : path) {
            FileInputStream fis = null;
            //判断是否从绝对路径读取
            if (isAbsolute) {
                fis = new FileInputStream(s);
            } else {
                fis = new FileInputStream(JiebaConfig.class.getClassLoader().getResource(s).getFile());
            }
            // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            br.close();
            isr.close();
            fis.close();
        }
        return list;
    }

}
```

将类放入能被springboot扫描到的位置，然后启动项目

# 功能测试

新建一个controller

```
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private JiebaDict jiebaDict;


    @GetMapping("keywords")
    public List<String> keywords(String keyword){
        return jiebaDict.keyword(keyword);
    }
    
}
```

# 添加词典注意事项

添加核心词典

```
我们在配置类中写到
jieba:
    init:
        dict: dic.txt
        initAbsolute: false
```

我们在resource下面新建一个dic.txt,里面写入，后面一个table 加上一个数字即可，注意源码中会去读取数据，如果不加则无法添加核心词典

```
大哥大嫂过年好	2
```

然后我们请求接口

就会返回，如果我们不加他就会给我们进行分词，我们请求接口输入"大哥大嫂过年好"，则不分词查询到了核心词典

```
[
  "大哥大嫂过年好"
]
```

添加停用词典,还是放在resource，并且非全路径，为类路径

```
jieba:
    stop:
        dict: stop.txt
        stopAbsolute: false
```

然后我们在词典中添加

```
大哥大嫂过年好
```

然后我们再次请求接口发现停用词以及分词，返回空数组，由于结巴分词Java实在缺缺少功能，所以简单轻量级分词使用可以，但是复杂的并且需要处理的流程较多的推荐使用Hanlp，它的Java API会丰富很多