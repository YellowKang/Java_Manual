# 首先创建挂载目录

```
mkdir -p /docker/gitlab/{config,logs,data}
```

# 启动容器

然后启动容器,我这采用的汉化版的gitlab，如果需要其他版本请搜索下载并且修改

```
docker run --restart=always -d \
--name gitlab \
-h 111.67.196.127 \
-p 10443:443 \
-p 10080:80 \
-p 10022:22 \
-v /docker/gitlab/config:/etc/gitlab \
-v /docker/gitlab/logs:/var/log/gitlab \
-v /docker/gitlab/data:/var/opt/gitlab \
docker.io/twang2218/gitlab-ce-zh
```

然后访问10080，设置默认的root密码

# 问题

## GItLab的URl和SSH问题

修改了之后我们会发现url和ssh上都没有端口号了

![](img\gitlabssh连接.png)

我们就需要去修改他的配置了

进入挂载目录并且编辑配置文件

```
vim /docker/gitlab/config/gitlab.rb

然后添加上
external_url 'http://111.67.196.127:10080'
nginx['listen_port'] = 10080
gitlab_rails['gitlab_shell_ssh_port'] = 10022
nginx['listen_https'] = false
我们把它复制出来修后需要删除docker容器并且重新启动，然后修改端口

docker stop gitlab
docker rm gitlab

docker run --restart=always -d \
--name gitlab \
-h 111.67.196.127 \
-p 10443:443 \
-p 10080:10080 \
-p 10022:10022 \
-v /docker/gitlab/config:/etc/gitlab \
-v /docker/gitlab/logs:/var/log/gitlab \
-v /docker/gitlab/data:/var/opt/gitlab \
docker.io/twang2218/gitlab-ce-zh

```

