# WebMagic简介

​		WebMagic是一款由国人开发的一款开源的Java垂直爬虫框架，目标是简化爬虫的开发流程，让开发者专注于逻辑功能的开发。 非常简单快捷，并且容易上手的轻量级爬虫框架。

# WebMagic入门

## 引入依赖

```
        <dependency>
            <groupId>us.codecraft</groupId>
            <artifactId>webmagic-core</artifactId>
            <version>0.7.3</version>
        </dependency>
        <dependency>
            <groupId>us.codecraft</groupId>
            <artifactId>webmagic-extension</artifactId>
            <version>0.7.3</version>
        </dependency>
```

## 编写代码

注意！！！！！（如果为Springboot环境请先将main方法运行一次，然后修改如下）

![](img\run-main.png)

![](img\idea-run-main.png)

![](img\main-run-idea.png)

我们现在需要爬取的是京东的商品的名称，那么我们首先打开京东随便找到一个商品

![](img\sp-1.png)

然后我们复制他的url放到一个地方有用例如

<https://item.jd.com/10870988811.html> 

然后我们浏览器右键查看源代码我们将标题复制然后ctrl+f搜索我们可以看到很多个，然后我们找到真正的商品名称

![](img\dm.png)

我们可以看到这个才是真正的名称，然后我们看他的标签，我们看到他在p-name这个class修饰的div中，我们需要用到这个css样式，这样准备工作就做好了，我们开始使用webmagic吧

新建TestSpider类

```
public class TestSpider implements PageProcessor {

    // 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(10).setSleepTime(1000);

    //设置站点
    public Site getSite() {
        return site;
    }

    //获取信息
    public void process(Page page) {
        System.out.println("成功抓取Jd数据，商品名称为：" + page.getHtml().css(".p-name"));
    }

    public static void main(String[] args) {
        Spider.create(new TestSpider()).addUrl("https://item.jd.com/10870988811.html").start();
    }
}
```

我们可以看到我们实现了PageProcessor，然后实现了两个方法site是设置站点信息，process是获取返回的信息，使用main方法运行，我们添加了这个商品的url，然后解析它的css样式为p-name的信息，并将它打印出来了，然后我们运行main方法出现以下信息则成功

![](img\success.png)