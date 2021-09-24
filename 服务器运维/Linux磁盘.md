# 定位磁盘满

```
在Linux使用的时候，我们会使用非常多的磁盘空间，当出现磁盘满了的时候随之服务器也不能正常工作了，那么我们如何去定位到底是在那里磁盘爆满了呢
```

首先我们使用df -h，查看磁盘使用率，我们就能看到如下的信息

​	Size是大小，Used是已经使用的大小，Avail是剩余的大小

![](img\df -h.png)

​	查看我们磁盘使用率为100%的盘，即可知道是哪一个盘符占满，我们进入盘符的目录	

​	那么我们想要精确定位到某个内存使用超大的目录怎么办呢，首先我们进入根目录，然后使用

```
du -sh *
```

​	我们就能查看到当前目录下的文件以及文件夹的大小了

```
du -sh * | grep G
```

​	我们grep G即可查询使用上G的文件夹，然后cd进去继续查询，即可查询到大文件

# 磁盘挂载

我们挂载磁盘首先需要分区，格式化，然后挂载

## 查看磁盘

```
fdisk -l
```

## 进行分区

```
fdisk /dev/xvdc
```

然后输入m就能看到命令提示了

输入n

出现

选择p

然后回车

选择1

然后两次回车

然后w保存



## 格式化磁盘

```
mkfs -t ext4 /dev/xvdc
```

## 挂载磁盘

```
mount /dev/xvdc /data
```

挂载完成后使用命令查看是否挂载完毕

```
mount | grep /dev/xvdc 
```

## 永久挂载（必须执行）

我们挂载了之后这只是临时挂载了上去如果我们想要重启后还有效果则需要修改配置文件	

首先我们查看fstab文件，这里面存储了我们永久挂载的信息

```
 cat /etc/fstab
```

一共有6个字段

设备（UUID或路径指定）  挂载点  文件系统类型   defaults  转储标志  fsck顺序  

如下

那么我们来获取自己的UUID

```
 blkid /dev/xvdc
 获取到了这个uuid
 d04f7568-91f4-496a-996e-e67cbb337300
```

然后我们在里面进行添加

这里中标麒麟是ext4

```
UUID=d04f7568-91f4-496a-996e-e67cbb337300 /data                       ext4     defaults        0 0
```





# NFS挂载

​		挂载我们将nfs.langfang.oceanstor9000-1.com:/sfs-anqscdsj-liantong-03 这个NFS资源，挂载到/data2

```sh
mount -t nfs nfs.langfang.oceanstor9000-1.com:/sfs-anqscdsj-liantong-03 /data2
```

​		永久挂载

```sh
# 将挂载写入到/etc/fstab
echo "nfs.langfang.oceanstor9000-1.com:/sfs-anqscdsj-liantong-03  /data2           nfs     rw,soft,intr    0 0" >>  /etc/fstab
# 查看文件
cat /etc/fstab
```

