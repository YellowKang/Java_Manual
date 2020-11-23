# 什么是Liquibase？

​		它具有管理数据库模式脚本的修订的功能。它适用于各种类型的数据库，并支持各种文件格式来定义数据库结构。Liquibase具有从特定点来回滚动更改的能力-使您无需知道什么是在特定数据库实例上运行的最新更改/脚本。

​		简单的来说，它是帮助我们管理数据库工作流程的工具，我们在开发项目时会不断的去修改表，数据，以及字段。或者新增表，字段等等，Liquibase可以更好地管理我们的数据库的开发流程，并且管理，我们可以直接通过Liquibase的文件，进行数据库的修改等等，类似于JPA的新建实体后自动帮助我们创建表字段等等，并且也能数据回滚，防止操作失误或者其他情况导致同步问题，简单的来说他就是我们在Java开发的过程中的一个数据库管理工具。

# 核心概念

## ChangeLog（更变日志）

​		官方文档中是这样解释的：开发人员将数据库更改存储在其本地开发计算机上的基于文本的文件中，并将其应用于其本地数据库。这些*变更日志*文件存储在源代码管理中，以实现协作。该*更新日志*可以用来更新所有不同的数据库环境，一个团队的用途-从本地开发数据库，测试，分期和生产。可以随意嵌套*Changelog*文件以进行更好的管理。所有Liquibase更改的根都是*changelog*文件。Liquibase使用变更*日志*按顺序列出对数据库所做的所有更改。将其视为分类帐。这是一个包含所有的数据库更改（记录文件*变更**小号*）。Liquibase使用此变更*日志*记录来审核您的数据库并执行尚未应用于数据库的所有更改。

​		那么从这段话中我们可以知道，ChangeLog是存储在我们的本地开发计算机中的，并且他可以用来更新所有不同的数据库环境，可以区分开发，测试，试运行，以及生产环境，他的主要的作用使用来存储我们的数据库更变的日志的。

​		官网中关于ChangeLog的信息如下： [点击进入](https://docs.liquibase.com/concepts/basic/changelog.html)

​		他下面的属属性节点分别有（这里采用Java代码中的配置属性，和官网中略有不同）

|     节点      |                             描述                             |
| :-----------: | :----------------------------------------------------------: |
|   changeSet   | 这是我们执行的更变集，也是我们的数据库管理中的核心，这个我们会经常用到 |
|    include    | 这个include节点用来引入文件，引入一个ChangeLog配置文件到ChangeLog中，类似于配置文件引用另一个配置文件 |
|  includeAll   | 这个includeAll节点用来引入文件夹，文件夹下包含的ChangeLog文件 |
| preConditions | 前提条件，用来判断我们是否需要执行这个ChangeLog，类似于if else的条件判断 |
|   property    |          用于设置属性的值（如果未通过其他方式设置）          |

​		根据不同的环境下使用我们分别有多种类型来创建，这里不列举sql方式

​		XML方式：

```xml
<?xml version="1.0" encoding="UTF-8"?>  
<databaseChangeLog  
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"  
    xmlns:pro="http://www.liquibase.org/xml/ns/pro"  
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd
    http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd
    http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-3.8.xsd ">  
  
  
</databaseChangeLog>
```

​		YAML方式：

```properties
databaseChangeLog:
	
```

​		JSON方式：

```properties
{  "databaseChangeLog":  [  ]  }
```



## ChangeSet（更变集）

​		官方解释是这样的：变更是变化的单位是Liquibase跟踪的执行。每个changeSet由author，id和filename属性唯一标识。当Liquibase运行时，它会查询该DATABASECHANGELOG表变更为执行被标记，然后执行所有的变更在更改日志尚未被执行的文件。

​		

## ChangeType（更变类型）

## Preconditions（条件）

## Contexts（上下文）

## Labels（标签）





# 引入依赖

```xml
        <dependency>
            <groupId>org.liquibase</groupId>
            <artifactId>liquibase-core</artifactId>
        </dependency>
       <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
       <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
```

# 编写配置

```properties
spring:
  datasource:
    url: jdbc:mysql://192.168.1.1:3306/test?characterEncoding=UTF-8&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.jdbc.Driver
  liquibase:
    # liquibase配置默认的change-log文件是LiquiBase用来记录数据库的变更，一般放在CLASSPATH下，然后配置到执行路径中。
    # 默认路径为 classpath:/db/changelog/db.changelog-master.yaml
    change-log: 'classpath:/db/changelog/db.changelog-master.yml'
    enabled: true
    contexts: dev
```



# 编写changelog

## 使用YML格式

### 编写master文件

​		master文件就是我们的变换日志的主文件，那么为了更好的阅读性，以及良好的规范我们尽量不要直接在master编写配置，而是采用引入的方式,那么我们首先是采用master引入按月份区分的文件目录，然后月份下面的文件再引入其他的天文件，并且天按每天的操作次序进行排序，如：

​		我们创建2020年文件夹，下面放入7，8，9，10月份文件夹，然后我们每次编写的一个按照日期+序列号 001 以及 操作 init初始化格式

```properties
- changelog
 - 2020
  -7
  	2020-07-10-001-init.yml
  -8
  -9
  -10
```

​		那么我们编写master的时候直接将这几个文件夹引入：

​		这里我们只做引入，不编写变化log文件

```properties
databaseChangeLog:
  - includeAll:
      path: db/changelog/2020/7/
  - includeAll:
      path: db/changelog/2020/8/
  - includeAll:
      path: db/changelog/2020/9/
  - includeAll:
      path: db/changelog/2020/10/
```

​		编写完了之后我们在2020/7下面创建一个2020-07-10-001-init-database.yml文件

​		创建完成之后如下：

![](http://yanxuan.nosdn.127.net/4ebaa4688796af21ce0880e778197055.png)

​		里面的内容我们先不写，并且也不启动,启动的话会报文件没有node节点的错误

​		如果想只引入单个文件可以这样写

```properties
databaseChangeLog:
  - include:
      file: db/changelog/2020/7/2020-07-10-001-init-database.yml
```

### 创建数据库表

​		现在我们开始来编辑这个2020-07-10-001-init-database.yml文件

​		首先我们创建一个更变集，并且我们设置作者以及其他，然后我们还初始化数据

​		



## 使用XML格式

## 使用JSON格式



```
  - changeSet:
      # 唯一ID，用于标识changeSet，后续可以用来回滚,不同作者都可以使用1，但是不允许一个作者出现两个1
      id: 1
      # 作者名称
      author: BigKang
      # changeSet描述
      comment: "初始化用户表，加载初始数据"
      # 启用事物
      runInTransaction: true
      # 变更脚本
      changes:
        # 创建数据库表
        - createTable:
            # 表名称
            tableName: t_user
            columns:
              - column:
                  name: id
                  type: int
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
                  remarks: 用户ID
              - column:
                  name: username
                  type: VARCHAR(50)
                  constraints:
                    nullable: false
                  remarks: 用户名称
              - column:
                  name: password
                  type: VARCHAR(50)
                  constraints:
                    nullable: false
                  remarks: 用户密码
              - column:
                  name: age
                  type: int
                  constraints:
                    nullable: true
                  remarks: 用户年龄
        # 加载数据
        - loadData:
            # 加载数据到哪个表
            tableName: t_user
            # 加载哪些字段
            columns:
              - column:
                  # csv文件头部名称
                  header: username
                  # 数据库名称
                  name: username
              - column:
                  header: password
                  name: password
            encoding: UTF-8
            file: db/data/2020/7/init_user.csv
        # 标记，用于回滚时指定版本
        - tagDatabase:
            tag: 1
```







```
[mysqld]
character-set-server=utf8
[mysqld]
character-set-server=utf8
## 同一局域网内注意要唯一
server-id=100  
## 开启二进制日志功能，可以随便取（关键）
log-bin=mysql-bin

[client]
default-character-set=utf8
[mysql]
default-character-set=utf8


[client]
default-character-set=utf8
[mysql]
default-character-set=utf8



```



```
databaseChangeLog:
  - changeSet:
    id: 1
    author: bigkang
    comment: "用户表"
    runInTransaction: true
    changes:
      - createTable:
        tableName: t_user
        columns:
          - column:
            name: id
            type: int
            autoIncrement: true
            constraints:
              primaryKey: true
              nullable: false
            remarks: 用户ID
          - column:
            name: username
            type: VARCHAR(50)
            constraints:
              nullable: false
            remarks: 用户名称
          - column:
            name: password
            type: VARCHAR(50)
            constraints:
              nullable: false
            remarks: 用户密码
          - column:
            name: phone
            type: varchar(50)
            constraints:
              nullable: true
            remarks: 用户手机号码
          - column:
            name: email
            type: varchar(80)
            constraints:
              nullable: true
            remarks: 用户邮箱
          - column:
            name: enable
            type: tinyint(1)
            constraints:
              nullable: true
            remarks: 用户是否启用
          - column:
            name: deleted
            type: int(11)
            constraints:
              nullable: true
            remarks: 逻辑删除
          - column:
            name: update_time
            type: datetime(0)
            constraints:
              nullable: true
            remarks: 修改时间
          - column:
            name: create_time
            type: datetime(0)
            constraints:
              nullable: true
            remarks: 创建时间
```