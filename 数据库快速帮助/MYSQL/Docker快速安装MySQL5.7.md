# 创建挂载目录

```sh
mkdir -p /docker/mysql/{conf,data}
```

# 编写配置文件

```sh
touch /docker/mysql/conf/my.cnf
```

​		配置文件中添加

```sh
echo "[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8" > /docker/mysql/conf/my.cnf
```

# 启动容器

```sh
docker run -p 3306:3306 \
--name mysql \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/mysql/data:/var/lib/mysql \
-v /docker/mysql/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```

# 安装MySQL8

和上面一样

```sh
mkdir -p /docker/mysql8/conf
mkdir -p /docker/mysql8/data

vim /docker/mysql8/conf/my.cnf
```

```sh
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

启动容器

```sh
docker run -p 13306:3306 \
--name mysql8 \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/mysql8/data:/var/lib/mysql \
-v /docker/mysql8/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:8.0.16
```

启动后执行sql命令

全都设置为utf8

```
SET NAMES utf8;
```







# 一键设置编码

```sql
set character_set_server = utf8;
set character_set_database = utf8;
set collation_connection = utf8_general_ci;
set collation_database = utf8_general_ci;
set collation_server = utf8_general_ci;
set character_set_client = utf8mb4;
set character_set_results = utf8mb4;
set character_set_connection = utf8mb4;
show variables like 'char%';
```





```sql
docker run -p 3306:3306 \
--name mysql \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/nacos/data:/var/lib/mysql \
-v /docker/nacos/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```



# MySQL配置文件详解

​		MySQL常用配置文件

```properties
[mysqld]
## MySQL服务器编码
character-set-server=utf8
## MySQL时区
default-time-zone='+8:00'
## 自定义唯一标识，集群以及主从同步需要，并且保证局域网Id唯一
server-id=mysql01
## mysql监听的ip地址，如果是127.0.0.1，表示仅本机访问
## bind_address=192.168.1.11
## 修改后是否自动提交事务
autocommit=1
## 禁用DNS主机名查找，用IP地址查找
skip_name_resolve=1
## MySQL最大连接数
max_connections=1000
## 错误链接次数,默认10，错误连接后会屏蔽IP
max_connect_errors=100
## 事务隔离级别默认为可重复读
## 1. READ-UNCOMMITTED(读未提交)
## 2. READ-COMMITTED(读已提交)
## 3. REPEATABLE-READ(可重复读)
## 4. SERIERLIZED(可串行化)
transaction_isolation=READ-COMMITTED
## FULL JOIN 就是explain中ALL,index,rang或者Index_merge的时候使用的buffer
join_buffer_size=128M
## mysql最大接受的数据包大小,sql长度很大可以调大点,比如批量插入的values
max_allowed_packet=16M
## sql_mode 模式，定义了sql语法，对数据的校验等等，限制一些不合法的操作
sql_mode="STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER"
## 读入缓冲区的大小，将对表进行顺序扫描的请求将分配一个读入缓冲区，MySQL会为它分配一段内存缓冲区
read_buffer_size=16M
## 随机读缓冲区大小，当按任意顺序读取行时（列如按照排序顺序）将分配一个随机读取缓冲区，进行排序查询时，MySQL会首先扫描一遍该缓冲，以避免磁盘搜索，提高查询速度
read_rnd_buffer_size=32M
## 是一个connection级参数，在每个connection第一次需要使用这个buffer的时候，一次性分配设置的内存
sort_buffer_size=32M
## 端口号
port=3306


## 错误日志位置
## log_error = 自定义
## 是否开启慢查询日志收集
slow_query_log = 1
## 慢查询日志位置
## slow_query_log_file=自定义
## 是否记录未使用索引的语句
log_queries_not_using_indexes=1
## 慢查询也记录那些慢的optimize table，analyze table和alter table语句
log_slow_admin_statements=1
## 记录由Slave所产生的慢查询
log_slow_slave_statements=1
## 设定每分钟记录到日志的未使用索引的语句数目，超过这个数目后只记录语句数量和花费的总时间
log_throttle_queries_not_using_indexes=10
## 日志自动过期清理天数
expire_logs_days=90
## 设置记录慢查询超时时间(特别注意 此处设置的时间是不包含锁的等待时间的,所以说高并发状态下很大可能是纪录不下来慢日志的,那是因为锁等待耗时造成的)
long_query_time=1
## 查询检查返回少于该参数指定行的SQL不被记录到慢查询日志
min_examined_row_limit=100



## slave保存master节点信息方式，设成file时 会生成master.info 和 relay-log.info2个文件，设成table，信息就会存在mysql.master_slave_info表中。不管是设置的哪种值，都不要移动或者编辑相关的文件和表
master_info_repository=TABLE
## 用于保存slave读取relay log的位置信息，可选值为“FILE”、“TABLE”，以便crash重启后继续恢复
relay_log_info_repository=TABLE
## binlog的保存位置，只能指定前缀名,如mysql-bin,不要写mysql-bin.log,因为后缀mysql会自动添加
log_bin=mysql-bin
## 这个参数是对于MySQL系统来说是至关重要的，他不仅影响到Binlog对MySQL所带来的性能损耗，而且还影响到MySQL中数据的完整性。对于“sync_binlog”参数的各种设置的说明如下：
## sync_binlog=0，当事务提交之后，MySQL不做fsync之类的磁盘同步指令刷新binlog_cache中的信息到磁盘，而让Filesystem自行决定什么时候来做同步，或者cache满了之后才同步到磁盘。
## sync_binlog=n，当每进行n次事务提交之后，MySQL将进行一次fsync之类的磁盘同步指令来将binlog_cache中的数据强制写入磁盘。
## 在MySQL中系统默认的设置是sync_binlog=0，也就是不做任何强制性的磁盘刷新指令，这时候的性能是最好的，但是风险也是最大的。
## 因为一旦系统Crash，在binlog_cache中的所有binlog信息都会被丢失。而当设置为“1”的时候，是最安全但是性能损耗最大的设置。因为当设置为1的时候，即使系统Crash，也最多丢失binlog_cache中未完成的一个事务，对实际数据没有任何实质性影响。从以往经验和相关测试来看，对于高并发事务的系统来说，“sync_binlog”设置为0和设置为1的系统写入性能差距可能高达5倍甚至更多。
sync_binlog=4
## 启用gtid类型，否则就是普通的复制架构
gtid_mode=on
## 强制GTID的一致性
enforce_gtid_consistency=1
## slave更新是否记入日志，在做双主架构时异常重要，影响到双主架构是否能互相同步
log_slave_updates
## binlog日志格式，可选值“MIXED”、“ROW”、“STATEMENT”，在5.6版本之前默认为“STATEMENT”，5.6之后默认为“MIXED”；因为“STATEMENT”方式在处理一些“不确定”性的方法时会造成数据不一致问题，我们建议使用“MIXED”或者“ROW”
binlog_format=row
## slave保存同步中继日志的位置
relay_log=/data/local/mysql-5.7.19/log/mysql-relay.log
## 当slave从库宕机后，假如relay-log损坏了，导致一部分中继日志没有处理，则自动放弃所有未执行的relay-log，并且重新从master上获取日志，这样就保证了relay-log的完整性
relay_log_recovery=1
## 这个参数控制了当mysql启动或重启时，mysql在搜寻GTIDs时是如何迭代使用binlog文件的。 这个选项设置为真，会提升mysql执行恢复的性能。因为这样mysql-server启动和binlog日志清理更快
binlog_gtid_simple_recovery=1
## 跳过指定error no类型的错误，设成all 跳过所有错误
slave_skip_errors=ddl_exist_errors


## innodb每个数据页大小，这个参数在一开始初始化时就要加入my.cnf里，如果已经创建了表，再修改，启动MySQL会报错
innodb_page_size=16K
## 缓存innodb表的索引，数据，插入数据时的缓冲，专用mysql服务器设置的大小： 操作系统内存的70%-80%最佳
innodb_buffer_pool_size=4G
## 可以开启多个内存缓冲池，把需要缓冲的数据hash到不同的缓冲池中，这样可以并行的内存读写
innodb_buffer_pool_instances=8
## 默认为关闭OFF。如果开启该参数，启动MySQL服务时，MySQL将本地热数据加载到InnoDB缓冲池中
innodb_buffer_pool_load_at_startup=1
## 默认为关闭OFF。如果开启该参数，停止MySQL服务时，InnoDB将InnoDB缓冲池中的热数据保存到本地硬盘
innodb_buffer_pool_dump_at_shutdown=1
## 根据 官方文档 描述，它会影响page cleaner线程每次刷脏页的数量， 这是一个每1秒 loop一次的线程
innodb_lru_scan_depth=2000
## 事务等待获取资源等待的最长时间，超过这个时间还未分配到资源则会返回应用失败；参数的时间单位是秒
innodb_lock_wait_timeout=5
## 这两个设置会影响InnoDB每秒在后台执行多少操作. 大多数写IO(除了写InnoDB日志)是后台操作的. 如果你深度了解硬件性能(如每秒可以执行多少次IO操作),则使用这些功能是很可取的,而不是让它闲着
innodb_io_capacity=4000
innodb_io_capacity_max=8000
## 默认值为 fdatasync. 如果使用 硬件RAID磁盘控制器, 可能需要设置为 O_DIRECT. 这在读取InnoDB缓冲池时可防止“双缓冲(double buffering)”效应,否则会在文件系统缓存与InnoDB缓存间形成2个副本(copy). 如果不使用硬件RAID控制器,或者使用SAN存储时, O_DIRECT 可能会导致性能下降
innodb_flush_method=O_DIRECT
## innodb重做日志保存目录
innodb_log_group_home_dir=自定义
## innodb回滚日志保存目录
innodb_undo_directory=自定义
## undo回滚段的数量， 至少大于等于35，默认128
innodb_undo_logs=128
## 用于设定创建的undo表空间的个数，在mysql_install_db时初始化后，就再也不能被改动了；默认值为0，表示不独立设置undo的tablespace，默认记录到ibdata中；否则，则在undo目录下创建这么多个undo文件，例如假定设置该值为4，那么就会创建命名为undo001~undo004的undo tablespace文件，每个文件的默认大小为10M。修改该值会导致Innodb无法完成初始化，数据库无法启动，但是另两个参数可以修改
innodb_undo_tablespaces=0
## InnoDB存储引擎在刷新一个脏页时，会检测该页所在区(extent)的所有页，如果是脏页，那么一起刷新。这样做的好处是通过AIO可以将多个IO写操作合并为一个IO操作。对于传统机械硬盘建议使用，而对于固态硬盘可以关闭。
innodb_flush_neighbors=1
## 这个值定义了日志文件的大小，innodb日志文件的作用是用来保存redo日志。一个事务对于数据或索引的修改往往对应到表空间中的随机的位置，因此当刷新这些修改到磁盘中就会引起随机的I/O，而随机的I/O往往比顺序的I/O更加昂贵的开销，因为随机的I/O需要更多的开销来定位到指定的位置。innodb使用日志来将随机的I/O转为顺序的I/O，只要日志文件是安全的，那么事务就是永久的，尽管这些改变还没有写到数据文件中，如果出现了当机或服务器断电的情况，那么innodb也可以通过日志文件来恢复以及提交的事务。但是日志文件是有一定的大小的，所以必须要把日志文件记录的改变写到数据文件中，innodb对于日志文件的操作是循环的，即当日志文件写满后，会将指针重新移动到文件开始的地方重新写，但是它不会覆盖那些还没有写到数据文件中的日志，因为这是唯一记录了事务持久化的记录
#如果对 Innodb 数据表有大量的写入操作，那么选择合适的 innodb_log_file_size 值对提升MySQL性能很重要。然而设置太大了，就会增加恢复的时间，因此在MySQL崩溃或者突然断电等情况会令MySQL服务器花很长时间来恢复
innodb_log_file_size=4G
## 事务在内存中的缓冲。 分配原 则：控制在2-8M.这个值不用太多的。他里面的内存一般一秒钟写到磁盘一次
innodb_log_buffer_size=16M
## 控制是否使用，使用几个独立purge线程（清除二进制日志）
innodb_purge_threads=4
## mysql在5.6之前一直都是单列索引限制767，起因是256×3-1。这个3是字符最大占用空间（utf8）。但是在5.6以后，开始支持4个字节的uutf8。255×4>767, 于是增加了这个参数。这个参数默认值是OFF。当改为ON时，允许列索引最大达到3072
innodb_large_prefix=1
## InnoDB kernel并发最大的线程数。 1) 最少设置为(num_disks+num_cpus)*2。 2) 可以通过设置成1000来禁止这个限制
innodb_thread_concurrency=64
## 是否将死锁相关信息保存到MySQL 错误日志中
innodb_print_all_deadlocks=1
## 开启InnoDB严格检查模式，尤其采用了页数据压缩功能后，最好是开启该功能。开启此功能后，当创建表（CREATE TABLE）、更改表（ALTER TABLE）和创建索引（CREATE INDEX）语句时，如果写法有错误，不会有警告信息，而是直接抛出错误，这样就可直接将问题扼杀在摇篮里
innodb_strict_mode=1
## ORDER BY 或者GROUP BY 操作的buffer缓存大小
innodb_sort_buffer_size=64M


## 表示转储每个bp instance LRU上最热的page的百分比。通过设置该参数可以减少转储的page数
innodb_buffer_pool_dump_pct=40
## 为了提升扩展性和刷脏效率，在5.7.4版本里引入了多个page cleaner线程。从而达到并行刷脏的效果
## 在该版本中，Page cleaner并未和buffer pool绑定，其模型为一个协调线程 + 多个工作线程，协调线程本身也是工作线程。因此如果innodb_page_cleaners设置为8，那么就是一个协调线程，加7个工作线程
innodb_page_cleaners=4
## 是否开启在线回收（收缩）undo log日志文件，支持动态设置
innodb_undo_log_truncate=1
## 当超过这个阀值（默认是1G），会触发truncate回收（收缩）动作，truncate后空间缩小到10M
innodb_max_undo_log_size=2G
## 控制回收（收缩）undo log的频率。undo log空间在它的回滚段没有得到释放之前不会收缩， 想要增加释放回滚区间的频率，就得降低设定值
innodb_purge_rseg_truncate_frequency=128
## 这个参数控制了当mysql启动或重启时，mysql在搜寻GTIDs时是如何迭代使用binlog文件的。 这个选项设置为真，会提升mysql执行恢复的性能。因为这样mysql-server启动和binlog日志清理更快。该参数为真时，mysql-server只需打开最老的和最新的这2个binlog文件
binlog_gtid_simple_recovery=1
## 在MySQL 5.7.2 新增了 log_timestamps 这个参数，该参数主要是控制 error log、genera log，等等记录日志的显示时间参数。 在 5.7.2 之后改参数为默认 UTC 这样会导致日志中记录的时间比中国这边的慢，导致查看日志不方便。修改为 SYSTEM 就能解决问题
log_timestamps=system
## 这个神奇的参数5.7.6版本引入，用于定义一个记录事务的算法，这个算法使用hash标识来记录事务。如果使用MGR，那么这个hash值需要用于分布式冲突检测何处理，在64位的系统，官网建议设置该参数使用 XXHASH64 算法。如果线上并没有使用该功能，应该设为off
transaction_write_set_extraction=MURMUR32
## 从mysql5.7.6开始information_schema.global_status已经开始被舍弃，为了兼容性，此时需要打开 show_compatibility_56
show_compatibility_56=on

[client]
## MySQL连接端编码
default-character-set=utf8
[mysql]
## MySQL默认编码
default-character-set=utf8
```





# MySQL初始化脚本

​		直接将./mysql-init目录挂载至/docker-entrypoint-initdb.d，文件下方放入SQL文件即可

```
    volumes:
      - ./mysql-data:/var/lib/mysql         # 挂载数据目录
      - ./mysql-config:/etc/mysql/conf.d      # 挂载配置文件目录
      - ./mysql-init:/docker-entrypoint-initdb.d # 挂载初始化文件夹
```

