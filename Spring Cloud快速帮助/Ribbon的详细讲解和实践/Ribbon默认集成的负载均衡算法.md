# Riboon的7中默认算法是哪七种分别又对应了哪些功能呢？

RoundRobinRule								轮询



RandomRule									随机



AvailabilityFilteringRule						会先过滤多次访问故障而处于断路器跳闸状态的服务



WeightedResponseTimeRule					根据平均响应时间计算所有的服务权重，响应时间越快服务权重越大就越容易被选中，刚启动时信息统计不足，会先使用轮询，等到响应数据有了之后在进行响应时间进行均衡负载
				

RetryRule			先按照轮询的方式，如果获取到服务失败等就会在指定的时间内进行重试，然后获取下一个可用的服务



BastAvailableRule		会先过滤掉由于多次访问故障而处于断路器跳闸的服务，然后选择一个并发量最小的服务



ZoneAvoidanceRule		默认规则，符合判断服务所在的区域的性能和服务的可用性选择服务器



# 那么我们如何来使用这些方法呢？

	首先我们需要在客户端中进行操作，一般来说是由我们的客户端调用服务端，然后对服务端的集群进行负载均衡，那么怎么调用他肯定是通过客户端来调用的，
	其实这个很简单我们只需要在客户端中添加一个组件，然后设置一个bean就可以了


    import com.netflix.loadbalancer.IRule;
    import com.netflix.loadbalancer.RandomRule;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    @Configuration
    public class MyRolr {
        //配置一个随机的算法，调用服务端的时候采取轮随机的模式
        @Bean
        public IRule iRule(){
            return new RandomRule();
        }
    }


	我们只需要把他标注然后注入到bean，他们就会执行我们返回的这个对象，我们现在执行的就是随机，如果要使用其他的均衡负载机制的话我们只需要修改一下
	
	返回值对象类型就可以了
	
	例如：
	   		 @Bean
	   		 public IRule iRule(){
	  		  	return new WeightedResponseTimeRule();
	  		 }
	这样我们就是用了根据响应时间而进行负载均衡
	那么为什么我们能使用返回值类型给他进行算法的实现呢？那是因为我们的这七种算法类里面继承了AbstractLoadBalancerRule，而AbstractLoadBalancerRule，里面又实现了IRule，
	所以我们要使用他的算法的核心就是IRule
	
	
	也可以使用自定的负载均衡算法