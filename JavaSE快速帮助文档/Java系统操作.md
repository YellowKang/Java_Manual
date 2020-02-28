

# 获取对象的内存地址

```java
				String bigkang = "BigKang";
        System.out.println(System.identityHashCode(bigkang));
```

# 获取系统属性

通过下面这一步我们就可以获取到Java程序启动时的系统属性了

```java
Properties properties = System.getProperties();
```

基于系统属性我们可以获取很多个

例如我们获取当前的操作系统什么系统，以及系统版本

```java
Properties properties = System.getProperties();
System.out.println("操作系统：" + properties.get("os.name") + properties.get("os.version"));
```

下面我将一些比较重要的属性列了一下（不是全部）

```
java.vm.name													Java虚拟机名称
PID																		Java程序启动的pid
com.sun.management.jmxremote.port			Java监控远程连接端口
os.name																操作系统
user.timezone													用户时区
@appId																程序名称，也是SpringCloud服务名称
file.encoding													文件编码
user.name															系统用户名称，（root或者启动程序用户）
sun.arch.data.model										查看系统位数
java.version													JDK版本
java.home															用户Jre目录
```

等等还有其他很多属性获取