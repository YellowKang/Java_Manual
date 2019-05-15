## 1、什么是PageHelper？

```
PageHelper是一款Mybatis的分页插件，功能强大配置简单
```

## 2、如何使用PageHelper？

```
首先我们先引入依赖：
```

```
版本随便，（！注意：至少1.1.0以上）

	<dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.2.3</version>
    </dependency>
```



```
然后我们就直接使用了，不多bb

Spring-Boot及其简便，甚至都不用配置，引入依赖直接干，如果需要配置详细的百度

@ResponseBody
@GetMapping("/")
public  List<Admin> getAdminAll(){

//开启分页，只需要查询所有的数据然后再开始加上startPage就可以了
    PageHelper.startPage(3,3);

//然后使用PageInfo获取所有的信息
    PageInfo<Admin> adminPageInfo = new PageInfo<>(adminMapper.getAdminAll());
```

?	
	//查询返回的行数
	    System.out.println(adminPageInfo.getSize());
	

```
//查询数据库中一共有多少行数
    System.out.println(adminPageInfo.getPages());
```

```
//查询数据库中的所有的数据的行数
    System.out.println(adminPageInfo.getTotal());
```

```
//返回一个list集合，也可以返回所有的adminPageInfo
    return  adminPageInfo.getList();
}
```



## 3、PageHelper使用Api

  	

```
	System.out.println(pageInfo.getPageNum());
	//表示打印查询到的数据返回的行数，例如查询到第3页只有1条数据返回1
    System.out.println(pageInfo.getSize());

    System.out.println(pageInfo.getStartRow());

    System.out.println(pageInfo.getEndRow());

	//查询数据库中的所有的数据的行数
    System.out.println(pageInfo.getTotal());

	//查询返回的数据的所有的页数，例如30条数据按10条一页，那就是3页
    System.out.println(pageInfo.getPages());

    System.out.println(pageInfo.getNavigateLastPage());

    System.out.println(pageInfo.getNavigateFirstPage());

	判断这一页是不是第一页页码，返回true或者false
    System.out.println(pageInfo.isIsFirstPage());

	判断这一页是不是最后一页页码，返回true或者false
    System.out.println(pageInfo.isIsLastPage());

    System.out.println(pageInfo.isHasPreviousPage());

	//判断还有没有下一页
    System.out.println(pageInfo.isHasNextPage());
```

?		

