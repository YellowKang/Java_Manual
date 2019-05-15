# 下载jenkins最新镜像

```
docker pull jenkins/jenkins:lts
```

# 然后运行容器

我们这里需要挂载目录，由于在jenkins中需要安装jdk，以及maven所以我们将目录挂载到本地，将文件目录创建

```
创建挂载目录
mkdir /home/jenkins

启动容器
docker run -itd -p 8888:8080 -p 50000:50000 --name jenkins --privileged=true  -v /home/jenkins:/var/jenkins_home docker.io/jenkins/jenkins:lts 

如果启动失败，docker logs jenkins查看日志
如果是这个错误
	touch: cannot touch '/var/jenkins_home/copy_reference_file.log': Permission denied
	Can not write to /var/jenkins_home/copy_reference_file.log. Wrong volume permissions?
那么我们就给这个文件赋予权限
sudo chown -R 1000:1000 /home/jenkins
```

启动好了之后我们访问8888端口，我们可以看到他需要一个初始化的密码，我们进入到容器中获取，或者直接从挂载目录获取

```
挂载目录获取
cat /home/jenkins/secrets/initialAdminPassword

或者使用容器查看
docker exec -it jenkins  bash
cat /var/jenkins_home/secrets/initialAdminPassword
```

这样我们就获取到了密码，输入进去然后会进入到页面

![](img\jenkins01.png)

我们选择安装推荐插件，然后等待他安装完成，然后设置账号密码即可

# 安装maven插件

我们进入到jenkins，选择插件管理



![](img\系统管理.png)

![](img\插件管理.png)

可选插件搜索maven

![](img\maven插件.png)

找到这个插件，然后安装，完成后选择新建任务发现maven则安装完成

![](img\maven插件完成.png)

# 安装Jdk

​		我们安装jdk只需要将linux的jdk包放到/home/jenkins的路径下解压就可以了

​		![](img\jdk宿主机.png)

​		这样的话我们jdk就好了（因为jenkins只需要javahome，不需要环境变量，所以解压即可用）

​		然后我们配置jenkins

找到你的系统管理，然后点击全局工具

​		![](img\全局maven.png)

找到Jdk，我们给他取个名字，然后我们把自动安装取消掉。配上自己的路径（注意，是容器内部的路径，例如

本地是：/home/jenkins/jdk1.8，那么我们就需要配置成：/var/jenkins_home/jdk1.8），因为他是从容器内部进行查找的，所以我们要配置容器内部的

![](img\jdk配置.png)

这样Jdk就安装好了



# 安装Maven

​		我们需要在宿主机安装maven,进入jenkins挂载目录

​		cd /home/jenkins/maven	

​		下载maven然后解压，配置maven_home

​		此方法省略，安装maven不讲了吧（注意：一定要在jenkins的目录下解压否则找不到目录）

​		安装完成后mvn命令测试下

​		然后我们来使用Jenkins配置Maven插件

​		找到你的系统管理，然后点击全局工具

​		![](img\全局maven.png)

Maven配置不用管我们找到maven安装，给他取个名字（随便取），然后我们把自动安装取消掉，我们自己配置路径，注意（这里的路径是docker内部的路径，这也是为什么我们需要挂载目录的原因）



![](img\全局maven安装.png)

然后点击应用，保存，maven安装完成，（注意：在提醒一次，在docker中目录是/var/jenkins_home，而在宿主机中目录是/home/jenkins，后面的路径和你安装的maven一样，但是前面一定要改成docker容器内部路径否则他会找不到）

# 进行持续集成

我们首先