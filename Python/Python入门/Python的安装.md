# Windows安装Python

​	首先进入官网

​	<https://www.python.org/downloads/windows/> 

​	下载windows版本，这里我们选择python3，自行选择

​	![](img\windows下载1.png)

进去之后找到下面的选项（往下滑动）

![](img\windows下载2.png)

我们这里选择windows64位的可执行文件，然后下载打开我们的执行文件

![](img\python安装1.png)

下一步点击上面的Customize installation

![](img\python安装2.png)

然后下一步，不用管

![](img\python安装3.png)

选择所有用户，然后安装，这一步可能有点慢等待一下，安装完成之后关闭页面，我们进入cmd命令窗口，输入python就能看到以下界面了，

![](img\python安装4.png)

如果出现了那么我们使用exit()命令就能退出了到此python安装完成

# Linux安装Python

查看版本

```
python --version
```

安装pip

```
yum -y install python-pip
pip install --upgrade pip
```

创建pip

```
mkdir ~/.pip
```

创建配置文件

```
echo "[global]
trusted-host=mirrors.aliyun.com
index-url=http://mirrors.aliyun.com/pypi/simple/" > ~/.pip/pip.conf
```

