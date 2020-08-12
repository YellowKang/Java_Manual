# 官方地址

​		Gitee地址为：[点击进入](https://gitee.com/makejava/EasyCode)

# Easy Code的好处

​		Easy Code有什么好处呢？答案就是：EasyCode是基于IntelliJ IDEA Ultimate版开发的一个代码生成插件，主要通过自定义模板（基于velocity）来生成各种你想要的代码。我们注意这里的生成各种想要的代码，市面上的代码生成器非常多，但是，他们通常的支持来说，要么数据库支持种类不够多，要么生成流程逻辑复杂，又或者生成一个ZIP包还需要自己去手动解压在粘贴进去，以及我们有时想自己定义这块模板，但是模板一修改又不好区分，还需要修改模板路径，那么Easy Code带给我最大的感觉就是，透明，分层明确，代码简介，拓展方便，以及使用灵活。并且迁移的时候我们只需要导出模板再导入即可，是一个强大的IDEA生产力工具，帮助我们大大的减轻开发速度。

# 安装插件

​		我们使用IDEA下载插件，搜索easycode

![](http://yanxuan.nosdn.127.net/a4d5af261e0c3565d3a556dfb623e478.png)

# 插件基础设置

## 修改作者

​		下载安装后我们需要初始化一些东西以及基础配置

​		首先修改，此处的编码，以及相应的作者名称修改为自己的

​		![](http://yanxuan.nosdn.127.net/92650b501cdbc0cc994a7a329b2a98a9.png)

## 数据库实体映射

​		我们对应数据库的类型以及实体的映射我们可以自定义，也可以拓展，并且还能实现自己的，我们删除修改，以及新增组来进行数据库和实体的映射。

![](http://yanxuan.nosdn.127.net/844b9d67210b3e87b280ff6ba7f6323c.png)



## 模板列表

​		那么我们的代码生成最主要的一定是模板了，我们使用Mybatis-plus的代码生成，或者使使用其他的软件工具等，但是通常比较繁琐，并且需要自己解压或者定义设置，对我们的生成效率来说还是比较慢的，那么这里我们可以设置模板，编辑删除修改，以及新增模板组，可以根据自己来定义想要的模板。

![](http://yanxuan.nosdn.127.net/5c78e30dee161f8dfedeb8edf969c955.png)

​			这里也是有模板组的存在的，我们可以根据不同的框架，编辑不同的模板组，使用的时候选择切换即可快速生成，我们选择模板组，然后点击Apply应用，然后ok返回，再生成就会根据我们的相应模板进行选择了。

![](http://yanxuan.nosdn.127.net/521d8a197de4edf8a1267ecc02d22ef7.png)

# 代码生成

## 1、连接数据库

​		那么我们生成代码首先是需要连接数据库的，所以下面我们就先来连接数据库吧,点击IDEA右侧的Database

![](http://yanxuan.nosdn.127.net/2749da13747c66c50a3dcc04147dbe63.png)

​				然后点击新建，然后选择数据源，再选择相应的数据库即可

![](http://yanxuan.nosdn.127.net/e6369e8a118e4a7a7498b85fd99b03e4.png)

​		我们再来输入我们的数据库地址

![](http://yanxuan.nosdn.127.net/ce5481b073ac50b1c50cbf9ba811872d.png)

## 2、选择相应需要生成的表

​		我们打开这个库选择需要生成的表，选择Esay Code，点击Generate Code

![](http://yanxuan.nosdn.127.net/6076d010c96457d7e419ba51b2456ec5.png)

## 3、开始生成

​		我们选择包名，再选择模块，注意这里的Path选择了相应模块会自动获取，然后选择这些模板需要生成哪一些模板

![](http://yanxuan.nosdn.127.net/00d34da27476ce442722c407d5de687f.png)

​		然后选择完成后ok

![](http://yanxuan.nosdn.127.net/4cbf722619c6b3b0aeabc6d902e1c466.png)

​	然后出现提示是否创建，点击yes

![](http://yanxuan.nosdn.127.net/fe1203205bdd0da49f070abf70290e8d.png)

​	然后我们就能看到刚刚生成的代码了

![](http://yanxuan.nosdn.127.net/7d7ce6dc0254969ac168e6d0ec3791d8.png)

# 拓展

## 设置去除表前缀

​		找到全局设置，然后编辑init宏，即可修改去除表前缀

​		![](http://yanxuan.nosdn.127.net/7823ab21f8c060d2e167c596af0c3a0c.png)

​		但是我们还需要去看一下模板中有没有引用这个宏，如果引用了即可生成

​		![](http://yanxuan.nosdn.127.net/6a21657d82c300bb87817ba03c494f03.png)

## 修改注释风格

​		我们不想使用它的这样的格式的注释我们需要生成自己喜欢的注释，然后我们修改这个define这个宏，然后修改这块注释代码即可

​		![](http://yanxuan.nosdn.127.net/66083027e8ca84421bcccd65c8d3a0f4.png)

## 获取主键类型

​		我们直接在模板中编辑即可,我们这里调用tool工具，然后我们通过包全名获取类，，这个类就是tableinfo中的第一个主键的类型，这块代码我们放在模板中生成出来即可：

```velocity
$!{tool.getClsNameByFullName($!tableInfo.pkColumn[0].type)}
```

​		![](http://yanxuan.nosdn.127.net/4e315c5d5a53508f4c02a0f1d16868e3.png)

## 模板语法

​		模板采用Velocity模板，详情请参考官方文档：[点击进入](http://velocity.apache.org/engine/index.html)

# 自定义Mybatis-Plus模板

​		由于此处自定义Base包，所以此处模板只建议查阅后根据自身情况修改

### entity

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 保存文件（宏定义）
## 设置根据生成的包下，创建entity包
#save("/entity", ".java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为entity
#setPackageSuffix("entity")

## 引入@TableName注解标注表名
import com.baomidou.mybatisplus.annotation.TableName;
## 自定义Base实体
import com.kang.souti.mbp.base.BaseMpEntity;

## 自动导入包（全局变量）
$!autoImport

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("实体类")

## 使用注解，保存表名称
@TableName("$tableInfo.obj.name")
## 类创建信息
public class $!{tableInfo.name} extends BaseMpEntity{
## 循环遍历生成字段
#foreach($column in $tableInfo.fullColumn)
    #if($column.name == "createtime" || $column.name == "updatetime" || $column.name == "deleted" )
        #break
    #end
    #if(${column.comment})
/**
    * ${column.comment}
    */
    #end
private $!{tool.getClsNameByFullName($column.type)} $!{column.name};
#end

## 循环遍历生成get set方法
#foreach($column in $tableInfo.fullColumn)
    #if($column.name == "createtime" || $column.name == "updatetime" || $column.name == "deleted" )
        #break
    #end
    #getSetMethod($column)
#end

}
```

### dao

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+Dao，用于定义类名
#setTableSuffix("Dao")

##保存文件（宏定义）
## 设置根据生成的包下，创建dao包，并且生成实体名+Dao.java文件
#save("/dao", "Dao.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为entity
#setPackageSuffix("dao")

## 引入自定义Dao层继承
import com.kang.souti.mbp.base.BaseMpDao;

## 引入实体类
import $!{tableInfo.savePackageName}.entity.$!tableInfo.name;

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("表数据库访问层")
## 类创建信息
public interface $!{tableName} extends BaseMpDao<$!tableInfo.name,$!{tool.getClsNameByFullName($!tableInfo.pkColumn[0].type)}> {

}
```

### service

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+Service，用于定义类名
#setTableSuffix("Service")

## 保存文件（宏定义）
## 设置根据生成的包下，创建service包，并且生成实体名+Service.java文件
#save("/service", "Service.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为service
#setPackageSuffix("service")

## 引入自定义Service层继承
import com.kang.souti.mbp.base.BaseMpService;
## 引入实体类
import $!{tableInfo.savePackageName}.entity.$!tableInfo.name;

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("表服务接口")
## 类创建信息
public interface $!{tableName} extends BaseMpService<$!tableInfo.name,$!{tool.getClsNameByFullName($!tableInfo.pkColumn[0].type)}> {

}
```

### service impl

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+ServiceImpl，用于定义类名
#setTableSuffix("ServiceImpl")

## 保存文件（宏定义）
## 设置根据生成的包下，创建service包，并且生成实体名+ServiceImpl.java文件
#save("/service/impl", "ServiceImpl.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为service.impl
#setPackageSuffix("service.impl")

## 引入Dao层，以及实体类，以及Service层
import $!{tableInfo.savePackageName}.dao.$!{tableInfo.name}Dao;
import $!{tableInfo.savePackageName}.entity.$!{tableInfo.name};
import $!{tableInfo.savePackageName}.service.$!{tableInfo.name}Service;
## 引入自定义ServiceImpl继承
import com.kang.souti.mbp.base.BaseMpServiceImpl;
## 引入Spring注解
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("表服务实现类")
@Transactional
## 类创建信息
@Service("$!tool.firstLowerCase($tableInfo.name)Service")
public class $!{tableName} extends BaseMpServiceImpl<$!{tableInfo.name},$!{tool.getClsNameByFullName($!tableInfo.pkColumn[0].type)},$!{tableInfo.name}Dao> implements $!{tableInfo.name}Service {

}
```

### controller

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+ServiceImpl，用于定义类名
#setTableSuffix("Controller")

## 保存文件（宏定义）
## 设置根据生成的包下，创建controller包，并且生成实体名+Controller.java文件
#save("/controller", "Controller.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为controller.impl
#setPackageSuffix("controller")

##定义服务名
#set($serviceName = $!tool.append($!tool.firstLowerCase($!tableInfo.name), "Service"))

##定义实体对象名
#set($entityName = $!tool.firstLowerCase($!tableInfo.name))

## 引入Dao层，以及实体类，以及Service层
import $!{tableInfo.savePackageName}.entity.$!tableInfo.name;
import $!{tableInfo.savePackageName}.dto.$!{tableInfo.name}Dto;
import $!{tableInfo.savePackageName}.service.$!{tableInfo.name}Service;
## 引入自定义Controller继承
import com.kang.souti.mbp.base.BaseMpController;
## 引入业务类以及Spring类注解等
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.annotations.ApiOperation;
import com.kang.souti.mbp.web.ResultVo;
import org.springframework.web.bind.annotation.PostMapping;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kang.souti.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;


## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("表控制层")
## 类创建信息
@RestController
@Api(tags = "$!{tableInfo.comment}($!{tableInfo.name})" + "控制器")
@RequestMapping("$!tool.firstLowerCase($!tableInfo.name)")
public class $!{tableName} extends BaseMpController<$!{tableInfo.name},$!{tool.getClsNameByFullName($!tableInfo.pkColumn[0].type)},$!{tableInfo.name}Service> {

## 自定义Dto搜索接口
    @PostMapping("search")
    @ApiOperation("搜索查询接口")
    public ResultVo searchDto(@RequestBody $!{tableInfo.name}Dto dto){
        IPage<$!{tableInfo.name}> page = baseService.searchPage(dto);
        return ResultVo.ok(page);
    }

}

    
```



### dto

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+Dto，用于定义类名
#setTableSuffix("Dto")

## 保存文件（宏定义）
## 设置根据生成的包下，创建dto包，并且生成实体名+Dto.java文件
#save("/dto", "Dto.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为dto
#setPackageSuffix("dto")

## 引入Lombok以及自定通用查询
import com.kang.souti.mbp.base.MpSearch;
import com.kang.souti.mbp.core.ScaffoldBasePage;
import lombok.Data;

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("查询Dto")
## 类创建信息
@Data
public class $!{tableName} extends ScaffoldBasePage{
## 循环根据实体类创建出查询字段
#foreach($column in $tableInfo.fullColumn)
#if($column.name == "createtime" || $column.name == "updatetime")
    #break
#end

    #if(${column.comment})
/**
    * ${column.comment}
    */
    #end
@MpSearch
    private $!{tool.getClsNameByFullName($column.type)} $!{column.name};
#end

}
```

