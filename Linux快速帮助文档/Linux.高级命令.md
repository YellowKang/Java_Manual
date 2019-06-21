# 批量kill进程，批量kill掉python数据

```
ps -ef | grep python | grep -v grep| awk '{print "kill -9 "$2}'|sh
```

# 清除缓存

```
echo 3 > /proc/sys/vm/drop_caches   //清除页面和目录缓存
```



# 批量删除容器

```
docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh
```

# 查看主机运行情况（CPU，IO，内存等等）

```
vmstat 1
```

就会出现如下的显示

![](img\vmstat.png)

```
Procs（进程）
	r:	运行队列中进程数量，这个值也可以判断是否需要增加CPU。（长期大于1）
	b	等待IO的进程数量。
Memory（内存）
	swpd	使用虚拟内存大小，如果swpd的值不为0，但是SI，SO的值长期为0，这种情况不会影响系统性能。
	free	空闲物理内存大小。
	buff	用作缓冲的内存大小。
	cache	用作缓存的内存大小，如果cache的值大的时候，说明cache处的文件数多，如果频繁访问到的文件都能			  被cache处，那么磁盘的读IO bi会非常小。
Swap
	si	每秒从交换区写到内存的大小，由磁盘调入内存。
	so	每秒写入交换区的内存大小，由内存调入磁盘。
	注意：内存够用的时候，这2个值都是0，如果这2个值长期大于0时，系统性能会受到影响，磁盘IO和CPU资源都会被消耗。有些朋友看到空闲内存（free）很少的或接近于0时，就认为内存不够用了，不能光看这一点，还要结合si和so，如果free很少，但是si和so也很少（大多时候是0），那么不用担心，系统性能这时不会受到影响的。因为linux总是先把内存用光

IO
	bi	每秒读取的块数
	bo	每秒写入的块数
	注意：随机磁盘读写的时候，这2个值越大（如超出1024k)，能看到CPU在IO等待的值也会越大。

system（系统）
	in	每秒中断数，包括时钟中断。
	cs	每秒上下文切换数。
	注意：上面2个值越大，会看到由内核消耗的CPU时间会越大。

CPU（以百分比表示）
	us	用户进程执行时间百分比(user time) us的值比较高时，说明用户进程消耗的CPU时间多，但是如果长期超50%的使用，那么我们就该考虑优化程序算法或者进行加速。
sy:	内核系统进程执行时间百分比(system time) sy的值高时，说明系统内核消耗的CPU资源多，这并不是良性表现，我们应该检查原因。
	wa	IO等待时间百分比 wa的值高时，说明IO等待比较严重，这可能由于磁盘大量作随机访问造成，也有可能磁盘出现瓶颈（块操作）。
	id	空闲时间百分比
```

使用参数

```
vmstat [-a] [-n] [-t] [-S unit] [delay [ count]]
vmstat [-s] [-n] [-S unit]
vmstat [-m] [-n] [delay [ count]]
vmstat [-d] [-n] [delay [ count]]
vmstat [-p disk partition] [-n] [delay [ count]]
vmstat [-f]
vmstat [-V]


-a：显示活跃和非活跃内存
-f：显示从系统启动至今的fork数量 。
-m：显示slabinfo
-n：只在开始时显示一次各字段名称。
-s：显示内存相关统计信息及多种系统活动数量。
delay：刷新时间间隔。如果不指定，只显示一条结果。
count：刷新次数。如果不指定刷新次数，但指定了刷新时间间隔，这时刷新次数为无穷。
-d：显示磁盘相关统计信息。
-p：显示指定磁盘分区统计信息
-S：使用指定单位显示。参数有 k 、K 、m 、M ，分别代表1000、1024、1000000、1048576字节（byte）。默认单位为K（1024 bytes）
-V：显示vmstat版本信息。
```

# 定时任务使用

定时任务使用的是linux的crontab

首先我们查询有没有定时任务

```
crontab -l
```

发现没有，我们先编写一个shell脚本，这个脚本可以用来删除docker的none镜像，也可以使用其他的脚本，自己定义

```
vim /root/rm-none-image-crontab.sh
```

然后脚本内写入

```
#!/bin/bash
source /etc/profile
docker images| grep none | grep -v grep| awk '{print "docker rmi "$3}'|sh
```

然后我们去定时任务添加

```
crontab -e
进入编辑
SHELL=/bin/bash
*/10 * * * * /bin/bash /root/rm-none-image-crontab.sh
```

我们每隔10分钟使用shell脚本一次

然后保存退出，重启定时任务

```
service crond restart
```

然后我们设置开机启动

```
systemctl enable crond.service
```

# 搜索文件

```
find -name "*.jar"
```

