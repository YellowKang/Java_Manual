# 首先我们需要现在Arthas的jar包

```
wget wget https://alibaba.github.io/arthas/arthas-boot.jar
```

# 然后我们启动这个jar包

```
java -jar arthas-boot.jar
```

​		启动后就会进入到命令窗口，他会自动扫描当前正在运行的jar包项目，然后我们根据查询出的项目选择id，输入然后回车，即可进入Arthas。

我们使用命令进行查看。

# 命令

```
dashboard
thread
jvm
sysprop
sysenv
getstatic
mc




```

