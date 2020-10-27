# 什么是Java版本控制？

​		在我们日常使用Java开发的使用中，我们有可能会遇到两个项目是属于不同的JDK版本进行开发的，那么我们想要运行调试的时候再去修改环境变量是特别麻烦的，那么我们需要使用到一些工具来控制我们安装的JDK版本，简单的来说就是我们可以随时切换到我们需要使用的JDK版本，然后把这些版本管理起来。



# 主流JDK版本控制工具





## Jenv

​		官网地址：[点击进入](https://github.com/jenv/jenv)

### 安装

​		MAC安装

```shell
brew install jenv
```

​		Linux安装

```
git clone https://github.com/jenv/jenv.git ~/.jenv
```

​		根据bash或者zsh设置环境变量

```shell
# Shell: bash
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.bash_profile
echo 'eval "$(jenv init -)"' >> ~/.bash_profile
# Shell: zsh
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
echo 'eval "$(jenv init -)"' >> ~/.zshrc
```

​		然后使用命令查看是否安装成功

```
jenv doctor
```

### 查看版本

​		查询当前安装的版本

```
jenv version
```

​		查询安装的所有JDK版本

```
jenv versions
```

### 新增JDK版本

​		将JDK进行解压然后添加目录

```shell
jenv add /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
```

### 版本控制

​		配置当前本地使用的版本

```shell
# 首先查看有哪些版本
jenv versions
# 然后进行切换
jenv local 1.8
```

​		配置全局使用的版本

```shell
# 首先查看有哪些版本
jenv versions
# 然后进行切换
jenv global 1.8
```

​		配置Shell使用的版本

```shell
# 首先查看有哪些版本
jenv versions
# 然后进行切换
jenv shell 1.8
```

