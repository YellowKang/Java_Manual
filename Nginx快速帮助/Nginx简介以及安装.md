# Nginx简介

​			Nginx是一个高性能的HTTP和反向代理web服务器，同时也提供了IMAP/POP3/SMTP服务。Nginx是由伊戈尔·赛索耶夫为俄罗斯访问量第二的Rambler.ru站点（俄文：Рамблер）开发的，第一个公开版本0.1.0发布于2004年10月4日。

​			主要我们使用的功能有两个一个是反向代理，另外一个就是负载均衡

​			反向代理：

​			负载均衡：

# Nginx核心

# Docker安装Nginx

创建挂载文件

```sh
mkdir -p /docker/nginx/{conf,logs}
```

创建初始配置文件

touch  /docker/nginx/conf/nginx.conf

添加权限

```sh
chmod 777 /docker/nginx/
```

文件添加如下

```nginx
server {
    listen       80;
    server_name  localhost;

    #charset koi8-r;
    #access_log  /var/log/nginx/host.access.log  main;

    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }

    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

    # proxy the PHP scripts to Apache listening on 127.0.0.1:80
    #
    #location ~ \.php$ {
    #    proxy_pass   http://127.0.0.1;
    #}

    # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
    #
    #location ~ \.php$ {
    #    root           html;
    #    fastcgi_pass   127.0.0.1:9000;
    #    fastcgi_index  index.php;
    #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
    #    include        fastcgi_params;
    #}

    # deny access to .htaccess files, if Apache's document root
    # concurs with nginx's one
    #
    #location ~ /\.ht {
    #    deny  all;
    #}
}
```

启动容器

```sh
docker run -d \
-p 8088:80 \
--name nginx-server \
-v /docker/nginx/conf/:/etc/nginx/conf.d/ \
-v /docker/nginx/logs:/var/log/nginx nginx
```



# YUM安装Nginx

```sh
在Linux下面可以使用yum命令

	yum install nginx	这样就能安装了

在Windows下正常解压就行了
```

检测Nginx安装完成？

```sh
ngxin -?或者 nginx-h就能查看到所有的操作了（这是提示命令）
```


```sh
nginx -v	然后选择我们的-v也就是简单显示版本

nginx -V	详细的配置信息

nginx -t	配置文件的路径

nginx -T	显示配置信息类型
```

运行nginx


```sh
在windows服务的环境下

	首先进入nginx的目录
	然后
	start nginx
	或者直接进入nginx的目录然后双击nginx.exe
	关闭nginx的服务的话有两种情况
	nginx -s stop    或者    nginx -s quit	

		stop表示立即停止nginx,不保存相关信息

		quit表示正常退出nginx,并保存相关信息

	重启(因为改变了配置,需要重启)

		nginx -s reload

在Linux服务的环境下

	service nginx start

	service nginx stop	
```

# Nginx运行流程

