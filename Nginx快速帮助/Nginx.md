# 1、什么是Nginx？

	Nginx是一个反向代理服务器
		他可以将几个服务器在一起配置，也就是服务器的集群

# 2、如何安装Nginx？

	在Linux下面可以使用yum命令
	
		yum install nginx	这样就能安装了
	
	在Windows下正常解压就行了

# 3、如何检测Nginx安装完成？

	ngxin -?或者 nginx-h就能查看到所有的操作了（这是提示命令）


	nginx -v	然后选择我们的-v也就是简单显示版本
	
	nginx -V	详细的配置信息
	
	nginx -t	配置文件的路径
	
	nginx -T	显示配置信息类型

# 4、然后我们来运行Nginx?


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



# 5、然后找到我们的server服务修改配置

   server {

	下面这个是nginx的端口号！注意：服务端口不要和nginx一样否则访问不到，
	一般把本地环境的服务都关掉，这只是一个代理服务器
	    listen       80 default_server;
	    listen       [::]:80 default_server;
	    server_name  _;
	    root         /usr/share/nginx/html;
	
	    # Load configuration files for the default server block.
	    include /etc/nginx/default.d/*.conf;
	
	    location / {
	
		在这里配置我们的服务！
	
		例如：
	
			server 127.0.0.1:8080;
	
		这样就能添加我们本地的ip的8080端口号了
	
	    }
	
	    error_page 404 /404.html;
	        location = /40x.html {
	    }
	
	    error_page 500 502 503 504 /50x.html;
	        location = /50x.html {
	    }
	}
