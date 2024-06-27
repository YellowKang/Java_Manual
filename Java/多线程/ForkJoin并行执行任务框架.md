# 什么是ForkJoin并行执行任务框架？

​		Fork/Join框架是Java7提供的并行执行任务框架，思想是将大任务分解成小任务，然后小任务又可以继续分解，然后每个小任务分别计算出结果再合并起来，最后将汇总的结果作为大任务结果。其思想和MapReduce的思想非常类似。对于任务的分割，要求各个子任务之间相互独立，能够并行独立地执行任务，互相之间不影响。

​		简单的来说ForkJoin就是帮助我们把一个任务，拆分开来，并行执行，然后将结果进行一个汇总，例如我们的分批次分析数据，需要分析3亿条数据，我们将它拆分成10个3000w的数据，分别并行计算，然后再把这个执行的结果统一的进行返回。

​		ForkJoin采用了工作窃取（work-stealing）算法，若一个工作线程的任务队列为空没有任务执行时，便从其他工作线程中获取任务主动执行。为了实现工作窃取，在工作线程中维护了双端队列，窃取任务线程从队尾获取任务，被窃取任务线程从队头获取任务。这种机制充分利用线程进行并行计算，减少了线程竞争。但是当队列中只存在一个任务了时，两个线程去取反而会造成资源浪费。

# ForkJoin框架核心

## ForkJoinPool

​		ForkJoinPool是ForkJoin框架中的任务调度器，和ThreadPoolExecutor一样实现了自己的线程池，提供了三种调度子任务的方法，我们可以把它理解为线程中的线程池，这个ForkJoinPool就是任务池

|  方法名   |                    作用                    |
| :-------: | :----------------------------------------: |
|  execute  |        异步执行指定任务，无返回结果        |
|  invoke   |    异步执行指定任务，等待完成才返回结果    |
| invokeAll |    异步执行指定任务，等待完成才返回结果    |
|  submit   | 异步执行指定任务，并立即返回一个Future对象 |

## ForkJoinTask

​		ForkJoinTask就是我们的实际执行的任务，他和我们的线程实现的接口类似，例如Runable，和Callable，他们分别有两个任务类

|       类        |          作用          |
| :-------------: | :--------------------: |
| RecursiveAction | 用于无结果返回的子任务 |
|  RecursiveTask  | 用于有结果返回的子任务 |

# 使用ForkJoin处理

​		我们编写一个批量任务，传入requestId的批处理

​		编写实体类（用于标记请求和任务状态）

```java
public class RequestMark {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 是否请求成功
     */
    private Boolean successFlag;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Boolean getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(Boolean successFlag) {
        this.successFlag = successFlag;
    }
}
```

​		编写Join

```java

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class RequestJoin extends RecursiveTask<List<RequestMark>> {


    /**
     * 任务拆分阈值
     */
    private Integer threshold;

    /**
     * 请求Id标记
     */
    private List<String> requestIds;

    public RequestJoin(List<String> requestIds, Integer threshold) {
        this.requestIds = requestIds;
        this.threshold = threshold;

        // 阈值不能为空
        if (threshold == null) {
            throw new RuntimeException("threshold Can't be empty!");
        }
        // 阈值不能小于等于一
        if (threshold <= 1) {
            throw new RuntimeException("threshold Must be Is greater than 1!");
        }
    }

    @Override
    protected List<RequestMark> compute() {
        // 如果条件成立，说明这个任务所需要计算的数值拆分得足够小了，不需要再拆分可以正式进行累加计算了
        if (requestIds.size() <= threshold) {
            List<RequestMark> requestMarks = new ArrayList<>();

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
            String url = "$url";
            for (String requestId : requestIds) {
                RequestMark requestMark = new RequestMark();
                requestMark.setSuccessFlag(true);
                requestMark.setRequestId(requestId);
                HttpPost post = new HttpPost(url + "?requestId=" + requestId);
              	// 请求失败则标记失败
                try {
                    post.setHeader("Content-type", "application/json");
                    HttpResponse resp = closeableHttpClient.execute(post);
                    try {
                        String result = EntityUtils.toString(resp.getEntity(), "utf-8");
                        JSONObject responseData = JSON.parseObject(result);
                        JSONObject syncMeituanCaseVo = (JSONObject) responseData.get("data");
                        if (!syncMeituanCaseVo.get("returnCode").equals("00")) {
                            requestMark.setSuccessFlag(false);
                        }
                    } catch (Exception e) {
                        requestMark.setSuccessFlag(false);
                    }
                } catch (IOException e) {
                    requestMark.setSuccessFlag(false);
                }

                requestMarks.add(requestMark);
            }

            return requestMarks;
        }
        // 反之则任务超过阈值大小，需要进行拆分
        else {

            // 根据任务拆分，对半，如果奇数则左边多一位
            Integer mid = null;
            if (requestIds.size() % 2 == 0) {
                mid = requestIds.size() / 2;
            } else {
                mid = ((requestIds.size() - 1) / 2) + 1;
            }

            // 将requestId进行拆分
            RequestJoin left = new RequestJoin(requestIds.subList(0, mid), threshold);
            RequestJoin right = new RequestJoin(requestIds.subList(mid, requestIds.size()), threshold);

            // 执行left和right，并且join
            invokeAll(left,right);
 
            List<RequestMark> rightResult = right.join();
            List<RequestMark> leftResult = left.join();

            // 合并结果集
            leftResult.addAll(rightResult);
            return leftResult;
        }
    }


}
```

​		编写测试方法

```java

        List<String> requestIds = new ArrayList<>();
				// init  List（初始化数据）

        RequestJoin requestJoin = new RequestJoin(requestIds,100);
        // 获取处理器数量，定义线程数
        int tThread = Runtime.getRuntime().availableProcessors();
        ForkJoinPool forkJoinPool = new ForkJoinPool(tThread);
        // 获取返回结果
        List<RequestMark> requestMarks = forkJoinPool.invoke(requestJoin);

        // 过滤成功和失败的标记
        List<RequestMark> success = requestMarks.stream().filter(RequestMark::getSuccessFlag).collect(Collectors.toList());
        List<RequestMark> failure = requestMarks.stream().filter(v -> !v.getSuccessFlag()).collect(Collectors.toList());

        System.out.println(String.format("成功数量:%s",success.size()));
        System.out.println(String.format("失败数量:%s",failure.size()));
```

​		或者我们,打印出线程日志

```java
            for (String requestId : requestIds) {
                System.out.println(Thread.currentThread().getName() + "\t" + requestId);
            }
```

