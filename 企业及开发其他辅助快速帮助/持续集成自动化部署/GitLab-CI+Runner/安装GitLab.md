# 首先创建挂载目录

```sh
mkdir -p /docker/gitlab/{config,logs,data}
```

# 启动容器

​		Docker安装官网地址：[点击进入](https://docs.gitlab.com/omnibus/docker/)

​		然后启动容器,我这采用的汉化版的gitlab，如果需要其他版本请搜索下载并且修改

```sh
docker run --restart=always -d \
--name gitlab \
-h 111.67.196.127 \
-e GITLAB_OMNIBUS_CONFIG="external_url 'http://192.168.157.134';gitlab_rails['time_zone']='Asia/Shanghai';gitlab_rails['lfs_enabled']=true;" \
-p 10443:443 \
-p 10080:80 \
-p 10022:22 \
-v /docker/gitlab/config:/etc/gitlab \
-v /docker/gitlab/logs:/var/log/gitlab \
-v /docker/gitlab/data:/var/opt/gitlab \
docker.io/twang2218/gitlab-ce-zh
```

​		然后访问10080，设置默认的root密码

​		docker-compose方式启动

```sh
version: '3'
 services:
    gitlab:
      image: 'gitlabcezh/gitlab-ce-zh'
      restart: always
      hostname: '192.168.157.134'
      container_name: 'gitlab'
      environment:
        TZ: 'Asia/Shanghai'
        GITLAB_OMNIBUS_CONFIG: |
        	# 暴露出去的Url
          external_url 'http://192.168.157.134'
          # 时区
          gitlab_rails['time_zone'] = 'Asia/Shanghai'
          # ssh端口，复制链接时地址上的，跟宿主机映射端口一致即可
          gitlab_rails['gitlab_shell_ssh_port'] = 22
          unicorn['port'] = 8888
          # Nginx监听端口
          nginx['listen_port'] = 80
      ports:
        - '80:80'
        - '8443:443'
        - '2222:22'
      volumes:
				- /docker/gitlab/config:/etc/gitlab
				- /docker/gitlab/logs:/var/log/gitlab
				- /docker/gitlab/data:/var/opt/gitlab
```

# 问题

​		切记不要使用root用户去操作，否则会出现权限问题

## GItLab的URl和SSH问题

修改了之后我们会发现url和ssh上都没有端口号了

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

