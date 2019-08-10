# 配置负载均衡

首先我们先编写配置文件

```
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

        error_page 404 /404.html;
            location = /40x.html {
        }

        error_page 500 502 503 504 /50x.html;
            location = /50x.html {
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

```
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

```
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

