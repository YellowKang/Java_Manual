# 什么是WebPack？

​		本质上，**webpack** 是一个用于现代 JavaScript 应用程序的_静态模块打包工具_。当 webpack 处理应用程序时，它会在内部构建一个 [依赖图(dependency graph)](https://webpack.docschina.org/concepts/dependency-graph/)，此依赖图对应映射到项目所需的每个模块，并生成一个或多个 *bundle*。

​		例如我们引入Js等等操作，都可以通过WebPackz帮助我们进行打包。



# 基本安装

​		初始化		

```sh
npm init -y
```

​		安装WebPack

```sh
npm install webpack webpack-cli --save-dev
```

