

# 五大数据类型

## String字符串

### 添加修改以及删除

简单添加一个String字符串：假设我们创建一个叫做name的key然后值为bigkang

```sh
# 设置name为bigkang
set name bigkang

# 设置name为bigkang并且设置超时时间为2000毫秒(PX为毫秒，EX为秒)
set name bigkang PX 2000

# 设置name为bigkang并且设置超时时间为2000毫秒(PX为毫秒，EX为秒)
# 如果存在则不设置，如果不存在则设置(NX不存在则设置，XX为存在才设置)
set name bigkang PX 2000 NX

# 获取name
get name


# 删除Key
del name
```

## Hash（哈希）

### 添加修改以及删除

```sh
# 设置一个订单的Hash,Key名为order，name为"小米手机",价格为3999.0
hset order name "小米手机" price 3999.0

# 添加order的type字段为手机,直接使用set即可
hset order type "手机"



# 获取所有的order的所有字段
hgetall order

# 获取单个字段
hget order name

# 判断order中的某个字段是否存在(存在返回1，不存在返回0)
hexists order user

# 删除单个字段（删除成功返回1，不存在则返回0）
hdel order price
```



## List（列表）

### 编辑

### 获取

## Set（集合）

### 编辑

### 获取

## Zset（有序集合）

### 编辑

### 获取