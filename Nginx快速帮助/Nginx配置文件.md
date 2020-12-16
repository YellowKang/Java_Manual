# 配置负载均衡

首先我们先编写配置文件

```sh
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

# Load dynamic modules. See /usr/share/nginx/README.dynamic.
include /usr/share/nginx/modules/*.conf;

events {
    worker_connections 1024;
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;
    sendfile            on;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 2048;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    # Load modular configuration files from the /etc/nginx/conf.d directory.
    # See http://nginx.org/en/docs/ngx_core_module.html#include
    # for more information.
    include /etc/nginx/conf.d/*.conf;

    server {
        listen       80 default_server;
        listen       [::]:80 default_server;
        server_name  192.168.1.12;
        root         /usr/share/nginx/html;

        # Load configuration files for the default server block.
        include /etc/nginx/default.d/*.conf;

        location / {
        proxy_pass http://yuqing; 
        }
    }

    upstream yuqing{
                server 192.168.1.12:8888/yuqing weight=5;
                server 192.168.1.12:9999/yuqing weight=5;
    }
}

```

通过他的 upstream yuqing，来指定负载均衡的ip

# 二级域名映射

我们只需要配置文件添加一个server即可

```sh
    server {
        #监听端口号
        listen 80;

        #代理域名
        server_name nacos.bigkang.club;

        #代理路径
        location / {
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header Host $http_host;
        proxy_pass http://39.108.158.33:8848;
        }
    }
```

# 配置SSL证书

注意这里监听端口好为443

```sh
        #监听端口号
        listen 443;
        ssl             on;
        #证书公钥
        ssl_certificate /etc/nginx/certificate/1_www.bigkang.club_bundle.crt;    
         #证书私钥
        ssl_certificate_key /etc/nginx/certificate/2_www.bigkang.club.key; 
        ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2; 
        ssl_ciphers ECDH:AESGCM:HIGH:!RC4:!DH:!MD5:!3DES:!aNULL:!eNULL;
        ssl_prefer_server_ciphers  on;
        
```

# 单端口配置多项目

下面以zabbix为例子，在项目前后都加上/代理路径后加上/表示绝对路径

```sh
 location /zabbix/ {
        proxy_pass http://10.18.81.28:7000/;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;        
        proxy_set_header Host $host:$server_port;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_read_timeout 86400;
}
```

# 代理Tcp端口

### 代理mysql

我们代理172.17.58.91:3306这个ip的这个端口，我们本地从13106监听端口号

```nginx
stream {
    upstream cloudsocket {
       hash $remote_addr consistent;
      # $binary_remote_addr;
       server 172.17.58.91:3306 weight=5 max_fails=3 fail_timeout=30s;
    }
    server {
       listen 13106;#数据库服务器监听端口
       proxy_connect_timeout 10s;
       proxy_timeout 300s;#设置客户端和代理服务之间的超时时间，如果5分钟内没操作将自动断开。
       proxy_pass cloudsocket;
    }
}
```

# 代理端口号问题

```
proxy_set_header Host $host:$server_port;
```



```
stream {
    upstream mongosocket {
       hash $remote_addr consistent;
      # $binary_remote_addr;
       server 10.217.130.21:20168 weight=5 max_fails=3 fail_timeout=30s;
    }
    server {
       listen 20168;#数据库服务器监听端口
       proxy_connect_timeout 20s;
       proxy_timeout 300s;#设置客户端和代理服务之间的超时时间，如果5分钟内没操作将自动断开。
       proxy_pass mongosocket;
    }
}
```





# Nginx配置加密访问

​		安装 httpd-tools工具类

```sh
yum -y install httpd-tools
```

​		使用命令创建文件

```sh
htpasswd -c ./passwd bigkang
```

​		配置文件放入认证文件

```nginx

    server {
        #监听端口号
        listen 80;

        #代理域名
        server_name nacos.bigkang.club;
				auth_basic_user_file /usr/local/nginx/passwd;
        #代理路径
        location / {
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header Host $http_host;
        proxy_pass http://39.108.158.33:8848;
        }
    }
```



```
29420
0
34036
```

