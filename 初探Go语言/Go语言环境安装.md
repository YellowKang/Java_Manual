# Mac安装Go

​		我们采用下载方式安装

https://golang.google.cn/dl/

​		下载包，下载mac os包

​		点击安装

​		 配置环境变量

```sh
vim .bash_profile
```

写入

```sh
export GOPATH=/Users/lcore/dev/code/go
export GOBIN=$GOPATH/binexport 
PATH=$PATH:$GOBIN
```

加载环境

```sh
source .bash_profile
```

打印Go环境

```go
go env
```

如果输出环境表示安装成功

# windows安装Go

同样我们下载包

https://golang.google.cn/dl/

选择windows下载

选择windows的msi文件下载后点击安装，安装完成后直接进入cmd命令行输入go env即可

# Linux安装Go

我们首先下载go如果需要其他版本请点击上方mac安装的go官网下载地址

https://dl.google.com/go/go1.12.9.linux-amd64.tar.gz

下载

```sh
cd /usr/loca/
wget https://dl.google.com/go/go1.12.9.linux-amd64.tar.gz
```

配置环境变量，我还加上了其他的java以及maven，如果不用去掉即可

vim /etc/profile        文件中添加

```sh
export GOROOT=/usr/local/go
export PATH=$PATH:$MAVEN_HOME/bin:$JAVA_HOME/bin:$GOROOT/bin
```

退出，然后加载

```sh
source /etc/profile

输出环境
go env
```

出现环境即可