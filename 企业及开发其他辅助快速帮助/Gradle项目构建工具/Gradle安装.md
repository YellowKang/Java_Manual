# Windows安装

### 下载Gradle

<http://services.gradle.org/distributions/>

进入网址选择相应的Gradle下载

### 解压安装环境变量

将Gradle解压到一个目录下安装

然后配置环境变量

例如解压为：E:\pugins\gradle-5.6-rc-1-bin\gradle-5.6-rc-1

那么新增环境变量为

![](img\环境变量.png)

然后新增Path

![](img\path.png)

然后我们去idea里面新建项目即可

# Mac安装

下载相应安装包后解压放入目录

配置环境变量

```
GRADLE_HOME=/Users/bigkang/Documents/gradle-4.10
export GRADLE_HOME
PATH=$PATH:$GRADLE_HOME/bin
```

# Linux安装

与mac一致