# Portainer

## 简介

## 安装

​		安装环境Centos

```sh
# 创建挂载目录
docker volume create portainer_data

# 启动容器
docker run -itd \
--name=portainer \
--restart=always \
-p 8000:8000 \
-p 9000:9000 \
-v /var/run/docker.sock:/var/run/docker.sock \
-v portainer_data:/data \
portainer/portainer-ce
```

​		部署Agent

```sh
# 创建Agent代理，容器
docker run -itd \
--name portainer_agent \
-p 9001:9001 \
--restart=always \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /var/lib/docker/volumes:/var/lib/docker/volumes \
portainer/agent

# 注意var/lib/docker/volumes为默认的Docker挂载盘目录，如果修改了默认目录，那么请修改挂载盘

# 例如当前服务器的挂载盘我修改为/data/docker/volumes.那么命令如下
docker run -itd \
--name portainer_agent \
-p 9001:9001 \
--restart=always \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /data/docker/volumes:/var/lib/docker/volumes \
portainer/agent
```

​		安装后启动，第一次设置用户名以及密码

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1608527246399.png)

​		完成设置后创建用户,创建后选择Agent代理，然后填写数据端点的名称，然后数据IP端口即可		![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1608528642876.png)

​		然后点击Connect,即可连接

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1608528710035.png)

​		然后我们再来添加其他服务器，点击Endpoints，然后点击Add endpoint

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1608528746001.png)

​		点击Agent，然后填入Name，以及URL，以及公网IP（选填），然后点击Add endpoint

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1608528859413.png)

​		这样就会发现我们又添加进来了一个端点

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1608528930381.png)