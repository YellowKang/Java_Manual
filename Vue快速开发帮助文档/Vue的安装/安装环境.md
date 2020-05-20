# 安装Node.js

​		在安装之前我们要知道现在很多的node的项目版本不一致所以我们使用nvm去进行node的版本管理以及且换

​		我们先下载nvm，他的git地址

​		<https://github.com/coreybutler/nvm-windows/releases>

​		我们找到[nvm-setup.zip](https://github.com/coreybutler/nvm-windows/releases/download/1.1.7/nvm-setup.zip)

​		然后下载他，然后安装，安装完成之后我们命令行输入nvm，出现版本即安装成功

# Nvm命令介绍

​		首先是安装node版本，我们安装一个10.16.2版本

```
nvm install 10.16.2
```

​		然后选中版本

```
nvm use 10.16.2
查看版本
node -v
```

​		查看已经下载的node的版本

```
 nvm ls
```

​		查看所有node版本

```
 nvm list available
```

# 安装Vue-cli

我们使用npm进行安装
安装2.0

```
npm install vue-cli
```

安装3.0

```
npm install -g @vue/cli
```

# 安装vue-cli-service-global

```
npm install @vue/cli-service-global
```

# NPM加速配置

别名法

```bash
alias cnpm="npm --registry=https://registry.npm.taobao.org 
```

配置文件

```cpp
npm config set registry https://registry.npm.taobao.org
```

直接使用第三方

```cpp
npm install -g cnpm --registry=https://registry.npm.taobao.org
cnpm install
```

