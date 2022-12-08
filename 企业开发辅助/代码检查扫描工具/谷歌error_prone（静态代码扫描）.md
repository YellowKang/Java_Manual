# error_prone是什么？

​		官网中的解释是：Error Prone是Java的静态分析工具，用于在编译时捕获常见的编程错误。

​		简单的来说就是对我们的代码进行静态扫描，在编译的时候找出我们可能会引发异常以及错误的代码。

​		并且可以通过编译的时候动态修改Class避免这些错误的代码

​		例如如下代码：

```Java
  public static void main (String[] args) {
    Set<Short> s = new HashSet<>();
    for (short i = 0; i < 100; i++) {
      s.add(i);
      // 减去前一位
      s.remove(i - 1);
    }
    // 应该为1个最后
    System.out.println(s.size());
  }
}
```

​		执行后发现size为100，因为remove为Object类型，i-1后转成int导致删除失败

​		对于这种运行时才能发现的问题我们需要扫描出来

```java
error: [CollectionIncompatibleType] Argument 'i - 1' should not be passed to this method;
its type int is not compatible with its collection's type argument Short
      s.remove(i - 1);
              ^
    (see https://errorprone.info/bugpattern/CollectionIncompatibleType)
1 error
```

​		GitHub地址：[点击进入](https://github.com/google/error-prone)

​		官网地址：[点击进入](https://errorprone.info/)

​		文档地址：[点击进入](https://errorprone.info/docs/installation)

​		注意事项JDK8安装使用文档：[点击进入](https://github.com/google/error-prone/blob/f8e33bc460be82ab22256a7ef8b979d7a2cacaba/docs/installation.md)

# error_prone使用

​		pom.xml修改如下

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.0</version>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                    <encoding>UTF-8</encoding>
                    <compilerArgs>
                        <arg>-XDcompilePolicy=simple</arg>
                        <arg>-Xplugin:ErrorProne</arg>
                    </compilerArgs>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>com.google.errorprone</groupId>
                            <artifactId>error_prone_core</artifactId>
                            <version>${error-prone.version}</version>
                        </path>
                        <!--兼容Lombok-->
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
```

​		mvn编译使用

```sh
mvn clean verify -X 
```

​		问题

```properties
1、error_prone_core版本对应JDK比较高，Java8会用到javac的jar包，使用对应版本maven无法下载
2、使用中版本能够正常扫描出一些错误的代码，但是无法矫正Class，会抛出maven编译异常，error_prone_core无法处理，应该是和Java版本问题相关
3、参考上方文档地址，以及JDK8安装使用文档
```



# IDEA插件

​		搜索Error-prone下载

​		使用

```
Settings | 
Compiler | 
Java Compiler | 
Use compiler: Javac with error-prone and also make sure Settings | 
Compiler | 
Use external build is NOT selected.
```

