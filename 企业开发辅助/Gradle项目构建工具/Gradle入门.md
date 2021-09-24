

# 什么是Gradle？

​		Gradle是一个基于Apache Ant和Apache Maven概念的项目自动化构建开源工具。它使用一种基于Groovy的特定领域[语言](https://baike.baidu.com/item/语言/72744)(DSL)来声明项目设置，目前也增加了基于Kotlin语言的kotlin-based DSL，抛弃了基于XML的各种繁琐配置。

​		Gradle是一个基于JVM的构建工具，是一款通用灵活的构建工具，支持maven， Ivy仓库，支持传递性依赖管理，而不需要远程仓库或者是pom.xml和ivy.xml配置文件，基于Groovy，build脚本使用Groovy编写。

# Gradle的优势？

​	**Gradle实现了大量策略来保证构建速度更快**

- Gradle守护线程可以保证构建信息足够新
- 针对各种类型任务的增量任务输入和输出确保不需要每次运行清理命令
- 增量编译可以分析源文件和类文件之间的依赖关系，并只重新编译改变的部分
- 当二进制接口没有改变的时候，Gradle的智能类路径分析器避免了不必要的编译
- 利用Java类插件来提供更好的建模，减少了编译时类路径的体积，提高了性能

传统Maven依赖

```

```

Gradle依赖

```

```



# Windows安装

### 下载Gradle

​		<http://services.gradle.org/distributions/>

​		进入网址选择相应的Gradle下载

### 解压安装环境变量

​		将Gradle解压到一个目录下安装

​		然后配置环境变量

​		例如解压为：E:\pugins\gradle-5.6-rc-1-bin\gradle-5.6-rc-1

​		那么新增环境变量为

![](img\环境变量.png)

​		然后新增Path

![](img\path.png)

​		然后我们去idea里面新建项目即可

# Mac安装

​		下载相应安装包后解压放入目录

​		配置环境变量

​		vim ~/.bash_profile

```sh
export GRADLE_HOME=/Users/bigkang/Documents/gradle-4.10
export PATH=$PATH:$GRADLE_HOME/bin
```

​		然后刷新环境变量即可

```sh
source ~/.bash_profile
```

​		然后再查看Gradle版本即可

```sh
gradle -v
```

# Linux安装

与mac一致

# Gradle核心文件

## 文件结构

```shell

├── gradle  															##	为包装文件生成的文件夹
│   └── wrapper											
├── gradlew																##	Gradle包装器启动脚本
├── gradlew.bat														## 	Gradle包装器启动脚本
├── build.gradle													##	构建lib项目的脚本
├── gradle.properties											##	配置文件配置
├── settings.gradle												## 	用于定义生成名称和子项目的设置文件
```

## gradle.properties（配置文件）

​		gradle.properties用于存储通用的配置属性，我们可以统一地定义一些通用的属性

​		示例

```properties
group=com.kang.test
version=1.2.0
sourceCompatibility=11
springbootVersion=2.5.4
```

​		此处单纯用来配置属性十分简单

## settings.gradle（Gradle设置）

​		settings.gradle用来设置项目构建的

```groovy
// 插件管理器，用于管理插件地址，可以配置加速
pluginManagement {
    repositories {
        maven {
            url 'https://maven.aliyun.com/repository/gradle-plugin'
        }
    }
}

// Root根工程名称，等价Maven工程名称
rootProject.name = 'test-gradle'

// 引入的子依赖
include 'grd-common'


/*
		定义打印构建时的时间
*/
//初始化阶段开始时间
long beginOfSetting = System.currentTimeMillis()
//配置阶段开始时间
def beginOfConfig
//配置阶段是否开始了，只执行一次
def configHasBegin = false
//存放每个 build.gradle 执行之前的时间
def beginOfProjectConfig = new HashMap()
//执行阶段开始时间
def beginOfTaskExecute
//初始化阶段执行完毕
gradle.projectsLoaded {
    println "初始化总耗时 ${System.currentTimeMillis() - beginOfSetting} ms"
}
//build.gradle 执行前
gradle.beforeProject { Project project ->
    if (!configHasBegin) {
        configHasBegin = true
        beginOfConfig = System.currentTimeMillis()
    }
    beginOfProjectConfig.put(project, System.currentTimeMillis())
}
//build.gradle 执行后
gradle.afterProject { Project project ->
    def begin = beginOfProjectConfig.get(project)
    println "配置阶段，$project 耗时：${System.currentTimeMillis() - begin} ms"
}
//配置阶段完毕
gradle.taskGraph.whenReady {
    println "配置阶段总耗时：${System.currentTimeMillis() - beginOfConfig} ms"
    beginOfTaskExecute = System.currentTimeMillis()
}
//执行阶段
gradle.taskGraph.beforeTask { Task task ->
    task.doFirst {
        task.ext.beginOfTask = System.currentTimeMillis()
    }

    task.doLast {
        println "执行阶段，$task 耗时：${System.currentTimeMillis() - task.ext.beginOfTask} ms"
    }
}
//执行阶段完毕
gradle.buildFinished {
    println "执行阶段总耗时：${System.currentTimeMillis() - beginOfTaskExecute}"
}
```

## build.gradle（构建文件）

​		build.gradle属于我们项目的核心构建文件

```groovy
// 定义要使用的插件
plugins {
    id 'org.springframework.boot' version '2.3.0.RELEASE'
    id 'io.spring.dependency-management' version '1.0.9.RELEASE'
    id 'java'
}

// 定义项目的GroupId和Version版本
group "${group}"
version "${version}"

// 定义编译的版本，Java11
sourceCompatibility = 11
targetCompatibility = 11


// 定义代码仓库
repositories {
    // 默认Maven仓库
    // mavenCentral()
    // 本地的Maven仓库
    // mavenLocal()
    // 阿里云加速
    maven { url 'https://maven.aliyun.com/nexus/content/groups/public' }
    maven { url 'https://maven.aliyun.com/nexus/content/repositories/google' }
    maven { url 'https://maven.aliyun.com/nexus/content/repositories/jcenter'}

}

// 配置编译
configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

// 依赖，等价Maven依赖
dependencies {
    testImplementation "org.junit.jupiter:junit-jupiter-api:5.6.0"
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'
}

// 测试
test {
    useJUnitPlatform()
}
```





# Gradle构建SpringBoot多工程项目

