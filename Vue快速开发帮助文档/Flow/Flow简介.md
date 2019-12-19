# 什么是Flow

​     	Flow 是 JavaScript 静态类型检查工具。Vue.js 的源码利用了 Flow 做了静态类型检查，所以了解 Flow 有助于我们阅读源码。

# 安装Flow

```
npm install -g --save-dev flow-bin
```

在项目或者文件夹中新建文件.babelrc，表示我们使用JavaScript 语法的编译器预置为flow

```
touch .babelrc
并且写入
{
  "presets": ["flow"]
}
```

初始化

```
flow init
```

初始化后会在本地当前目录生成一个.flowconfig文件，内容如下

```
[ignore]

[include]

[libs]

[lints]

[options]

[strict]
```

然后我们创建一个文件test.js，然后写入如下内容，再执行flow

```
// @flow
function square(n: number): number {
  return n * n;
}

square("2"); // Error!
```

我们发现报错了并且警告我们错误信息，以及行数