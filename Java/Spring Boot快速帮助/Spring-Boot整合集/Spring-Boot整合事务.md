# SpringBoot事务

​			事务（Transaction)是由一系列对系统中数据进行访问与更新的操作所组成的一个程序 执行逻辑单元（Unit)。事务具有四个特征，分别是原子性（Atomicity )、一致性（Consistency )、隔离性（Isolation) 和持久性（Durability),简称为事务的ACID特性。



​					原子性（Atomicity）：原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。

​					一致性（Consistency）：事务执行前后数据的完整性必须保持一致。比如在转账事务操作中，事务执行前后金额的总数应保持不变。

​					隔离性（Isolation）：事务的隔离性是多个用户并发访问数据库时，数据库为每一个用户开启的事务，不能被其他事务的操作数据所干扰，多个并发事务之间要相互隔离。

​					持久性（Durability）：持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，接下来即使数据库发生故障也不应该对其有任何影响。

​		1、什么是事务？

```
当我们在操作数据库的时候会发生一些异常，但是数据还是会提交上去，或者数据库报错导致数据错误，
这个时候我们就需要对他进行处理，如果发生异常直接回滚，有效的解决报错和异常
```

​		2、如何使用事物？

```
注：只在异常发生的层才生效，例如controller曾发生异常回滚，如果在service层发生异常在controoler没有异常则还是会执行：
在方法上面添加上@Transactional的注解就可以直接开启事务（建议类上直接添加注解） 
```

 		对此SpringBoot帮助我们使用注解直接集成了@Transactional注解

​		@Transactional注解有如下参数

## value（事务控制器）+ transactionManager



## label（标签）



## propagation（传播行为）



```properties
Propagation.REQUIRED: 如果当前存在事务，则加入该事务，如果当前不存在事务，则创建一个新的事务。( 也就是说如果A方法和B方法都添加了注解，在默认传播模式下，A方法内部调用B方法，会把两个方法的事务合并为一个事务 ）

Propagation.SUPPORTS: 如果当前存在事务，则加入该事务；如果当前不存在事务，则以非事务的方式继续运行。

Propagation.MANDATORY: 如果当前存在事务，则加入该事务；如果当前不存在事务，则抛出异常。

Propagation.REQUIRES_NEW: 重新创建一个新的事务，如果当前存在事务，暂停当前的事务。( 当类A中的 a 方法用默认Propagation.REQUIRED模式，类B中的 b方法加上采用 Propagation.REQUIRES_NEW模式，然后在 a 方法中调用 b方法操作数据库，然而 a方法抛出异常后，b方法并没有进行回滚，因为Propagation.REQUIRES_NEW会暂停 a方法的事务 )

Propagation.NOT_SUPPORTED: 以非事务的方式运行，如果当前存在事务，暂停当前的事务。

Propagation.NEVER: 以非事务的方式运行，如果当前存在事务，则抛出异常。

Propagation.NESTED: 和 Propagation.REQUIRED 效果一样。
```



## isolation（隔离级别）

```properties
isolation: 事务的隔离级别，默认值为


Isolation.DEFAULT: 使用底层数据库默认的隔离级别。
Isolation.READ_UNCOMMITTED: 读未提交级别
Isolation.READ_COMMITTED: 读已提交级别
Isolation.REPEATABLE_READ: 可重复读级别
Isolation.SERIALIZABLE: 串行化
```

## timeout（超时）+ timeoutString

```
事务的超时时间，默认值为 -1。如果超过该时间限制但事务还没有完成，则自动回滚事务。
默认没有超时时间
```

## readOnly（只读）

```
是否只读，默认为false
```



## rollbackFor（回滚）+ rollbackForClassName

```

```

## noRollbackFor（不回滚）+  noRollbackForClassName

```

```





