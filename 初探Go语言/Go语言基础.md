# Go语言基本数据类型

## 布尔型

bool

```go
package main

import "fmt"

var ak bool = true
func main()  {
	fmt.Println(ak)
}
```

## 数字类型

int（整形）

```go
package main

import "fmt"

var ak bool = true
func main()  {
	fmt.Println(ak)
}
```

float32（浮点型）

默认最多保留小数点后7位

```go
package main

import "fmt"

var money float32 = 1.1114126
func main()  {
	fmt.Println(money)
}
```

float64

默认最多保留小数点后16位

```go
package main

import "fmt"

var money float64 = 1.11141268880989787879877987879878
func main()  {
	fmt.Println(money)
}
```

## 字符串类型

string（字符串）

```go
package main

import "fmt"

var money float64 = 1.11141268880989787879877987879878
func main()  {
	fmt.Println(money)
}
```

## 派生类型

指针类型



数组类型









# 打印

引入fmt包，然后输出”Hello World“并且换行

```go
package main

import "fmt"

func main()  {
	fmt.Println("Hello World")
}
```

# 定义变量

```go
package main

import "fmt"

var age int
func main()  {
	age = 10
	fmt.Println(age)
}
```

