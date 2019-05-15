# 栈

# 堆

-Xms<size> 						设置堆的最小值 		

​	例如		-Xms256m	将堆内存最大设置为256mb

​	注：		生产环境下，-Xms和-Xmx是一样大的



-Xmx<size> 						设置堆的最大值 

​	例如		-Xms256m	将堆内存最大值设置为256mb

​	注：		生产环境下，-Xms和-Xmx是一样大的



-XX:NewSize=<size> 				设置新生代大小

​	例如		-XX:NewSize=1g  	将新生代大小设置为1个g

-XX:MaxNewSize=<size> 