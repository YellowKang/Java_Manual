# 什么是定时任务

​		在我们的项目中可能会出现一些需要不定期去进行操作的事情，比如清理垃圾数据，和过期数据，以及自动生成报表等等

# 如何使用定时任务？

​		在Springboot中已经默认的帮我们加入了定时任务，所以我们直接使用即可

​		首先我们需要标记为组件，然后启动定时任务组件

​		然后使用注解即可实现

​		

```
//表示是一個Spring組件
@Component
//开启Task组件
@EnableScheduling
public class TestTask {

    //每隔多少毫秒进行一次定时任务  3000就是3秒
    @Scheduled(fixedRate = 3000)
    public void nicea(){
        System.out.println(new Date());
    }
}
```

还能使用另一种表达式来进行创建

```
      //定时任务，每隔5秒钟进行一次定时任务如果直接写1 * * * * ？表示一分钟
      @Scheduled(cron = "*/5 * * * * ?")
      //开启异步
    	@Async
      public void nice(){
          System.out.println("1");
      }
```

下面是表达式的示例

```
//    0 0 10，14，16 * * ？每天上午10点、下午两点、下午4点整触发
//    0 0/30 9-17 * * ? 每天朝九晚五内每隔半小时触发
//    0 15 10 ? * MON-FRI 周一至周五的上午10:15触发
//    0 0/5 * * * ?每5分钟触发
//    10 0/5 * * * ？每隔5分钟的第10秒触发(即10:00:10、10:05:10、10:10:10等)
//    30 * * * * ? 每半分钟触发
//    30 10 * * * ? 每小时的10分30秒触发
//    30 10 1 * * ? 每天1点10分30秒触发
//    30 10 1 20 * ? 每月20号1点10分30秒触发
//    30 10 1 20 10 ? * 每年10月20号1点10分30秒触发
//    30 10 1 20 10 ? 2011 2011年10月20号1点10分30秒触发
//    30 10 1 ? 10 * 2011 2011年10月每天1点10分30秒触发
//    30 10 1 ? 10 SUN 2011           2011年10月每周日1点10分30秒触发
//    15,30,45 * * * * ?  每15秒，30秒，45秒时触发
//    15-45 * * * * ? 15  到45秒内，每秒都触发
//    15/5 * * * * ?      每分钟的每15秒开始触发，每隔5秒触发一次
//    15-30/5 * * * * ?   每分钟的15秒到30秒之间开始触发，每隔5秒触发一次
//    0 0/3 * * * ?       每小时的第0分0秒开始，每三分钟触发一次
//    0 15 10 ? *         MON-FRI 星期一到星期五的10点15分0秒触发
//    0 15 10 L * ?       每个月最后一天的10点15分0秒触发
//    0 15 10 LW * ?      每个月最后一个工作日的10点15分0秒触发
//    0 15 10 ? * 5L      每个月最后一个星期四的10点15分0秒触发
//    0 15 10 ? * 5#3     每个月第三周的星期四的10点15分0秒触发
```



```
    /**
     * 是否报修
     */
    private String applyRepair;

    /**
     * 维保结果
     */
    private String mainResult;

    /**
     * 维保记录
     */
    private String mainRecord;

    /**
     * 保养负责人ID
     */
    private String mainUserId;

    /**
     * 保养负责人
     */
    private String mainUser;

    /**
     * 实际完成日期
     */
    private Date finishDate;

    /**
     * 负责人电话
     */
    private String mainUserTel;

    /**
     * 计划点检日期
     */
    private Date checkPlanDate;

    /**
     * 点检周期
     */
    private int checkCycle;

    /**
     * 点检完成日期
     */
    private Date checkFinishDate;

    /**
     * 计划给油脂日期
     */
    private Date addGreasePlanDate;

    /**
     * 给油脂周期
     */
    private int addGreaseCycle;

    /**
     * 给油脂完成日期
     */
    private Date addGreaseFinishDate;

```

