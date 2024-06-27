# 什么是Quarkus?

​        官网：https://quarkus.io/

​        官网介绍：Quarkus是专为 OpenJDK HotSpot 和 GraalVM 量身定制的 Kubernetes Native Java 堆栈，采用最佳 Java 库和标准精心打造。



# 依赖环境

- ​		**Maven 3.9.5** 							   下载地址：[点击进入](https://maven.apache.org/download.cgi)
- ​		**GraalVm JDK17**  						下载地址：[点击进入](https://www.graalvm.org/downloads/#)

- ​		**Quarkus 3.5.3**							 初始化地址：[点击进入](https://code.quarkus.io/)



# 打包部署

## 运行启动

```bash
# 选择quarkus.profile进行启动
./mvnw compile -Dquarkus.profile=prod quarkus:dev
```



## 打包原生应用

​		直接使用命令打包即可

```bash
# 设置环境变量，打包时用到了GRAALVM_HOME环境变量进行编译
export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-community-openjdk-17.0.9+9.1/Contents/Home

# 编译将项目代码编译，并且编译二进制执行文件
mvn -DskipTests=true  -Dquarkus.profile=prod install -Dnative 
```

## 打包Native Image镜像

```bash
# 设置环境变量，打包时用到了GRAALVM_HOME环境变量进行编译
export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-community-openjdk-17.0.9+9.1/Contents/Home


# 编译将项目代码编译，并且编译二进制执行文件
./mvnw -DskipTests=true -Dquarkus.profile=prod install -Dnative -X

# 构建Docker镜像
docker build -f src/main/docker/Dockerfile.native-micro -t registry.cn-guangzhou.aliyuncs.com/sigreal_iov/xp-external:snapshot .


# 启动Docker镜像
docker run -i --rm -p 8083:8083 registry.cn-guangzhou.aliyuncs.com/sigreal_iov/xp-external:snapshot
```

## 打包Jar包

```bash
# 将项目打包成jar包
/Users/bigkang/Documents/apache-maven-3.9.5/bin/mvn -DskipTests=true -Pnative package
```

# 代码生成-EasyCode

## 宏定义

### init

```velocity
##初始化区域

##去掉表的t_和template_前缀
##去掉表的t_和template_前缀
#if($tableInfo.obj.name.startsWith("t_"))
  $!tableInfo.setName($tool.getClassName($tableInfo.obj.name.replaceFirst("t_","")))
#end
#if($tableInfo.obj.name.startsWith("template_"))
  $!tableInfo.setName($tool.getClassName($tableInfo.obj.name.replaceFirst("template_","")))
#end

##参考阿里巴巴开发手册，POJO 类中布尔类型的变量，都不要加 is 前缀，否则部分框架解析会引起序列化错误
#foreach($column in $tableInfo.fullColumn)
#if($column.name.startsWith("is") && $column.type.equals("java.lang.Boolean"))
    $!column.setName($tool.firstLowerCase($column.name.substring(2)))
#end
#end

##实现动态排除列
#set($temp = $tool.newHashSet("testCreateTime", "otherColumn"))
#foreach($item in $temp)
    #set($newList = $tool.newArrayList())
    #foreach($column in $tableInfo.fullColumn)
        #if($column.name!=$item)
            ##带有反回值的方法调用时使用$tool.call来消除返回值
            $tool.call($newList.add($column))
        #end
    #end
    ##重新保存
    $tableInfo.setFullColumn($newList)
#end

##对importList进行篡改
#set($temp = $tool.newHashSet())
#foreach($column in $tableInfo.fullColumn)
    #if(!$column.type.startsWith("java.lang."))
        ##带有反回值的方法调用时使用$tool.call来消除返回值
        $tool.call($temp.add($column.type))
    #end
#end
##覆盖
#set($importList = $temp)
```

### define

```velocity
##（Velocity宏定义）

##定义设置表名后缀的宏定义，调用方式：#setTableSuffix("Test")
#macro(setTableSuffix $suffix)
    #set($tableName = $!tool.append($tableInfo.name, $suffix))
#end

##定义设置包名后缀的宏定义，调用方式：#setPackageSuffix("Test")
#macro(setPackageSuffix $suffix)
#if($suffix!="")package #end#if($tableInfo.savePackageName!="")$!{tableInfo.savePackageName}.#{end}$!suffix;
#end

##定义直接保存路径与文件名简化的宏定义，调用方式：#save("/entity", ".java")
#macro(save $path $fileName)
    $!callback.setSavePath($tool.append($tableInfo.savePath, $path))
    $!callback.setFileName($tool.append($tableInfo.name, $fileName))
#end

##定义表注释的宏定义，调用方式：#tableComment("注释信息")
#macro(tableComment $desc)
/**
 * $!{tableInfo.comment}($!{tableInfo.name})
 * @Author $!author
 * @Time $!time.currTime()
 * @Summarize $tableInfo.obj.name $desc
 */
#end

##定义GET，SET方法的宏定义，调用方式：#getSetMethod($column)
#macro(getSetMethod $column)

    public $!{tool.getClsNameByFullName($column.type)} get$!{tool.firstUpperCase($column.name)}() {
        return $!{column.name};
    }

    public void set$!{tool.firstUpperCase($column.name)}($!{tool.getClsNameByFullName($column.type)} $!{column.name}) {
        this.$!{column.name} = $!{column.name};
    }
#end
```

### autoimport

```velocity
##自动导入包（仅导入实体属性需要的包，通常用于实体类）
#foreach($import in $importList)
import $!import;
#end
```

### 