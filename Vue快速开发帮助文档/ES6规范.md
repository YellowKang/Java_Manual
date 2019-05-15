# Let定义变量

​	在Es6之前的规范中是有作用域的存在的但是为了兼容以前的代码所以使用var定义的变量是可以忽略作用域的，但是使用let定义的变量是有作用域的，并且let的变量只能申明一次，还有如果先使用后赋值变量则会报错

​	例如：

​	{

​     		var a = 1;

​    	 	let b = 2;

​    	}

   	console.log(a);

​    	console.log(b);

这样的语法a不会报错，会在控制台输出1，但是b就会报错

如图：

​		![](image\let.png)

​	变量只能声明一次：

​	 	var a = 100;

​    		var a = 200;

​    		let b = 100;

​    		let b = 200;

​    		console.log(a);

​    		confirm.log(b);

​	这样的b则会引起报错

​		如图：

​				![](image\let (2).png)



​		

# Const定义变量

​	Const定义的变量就是常量，是不允许被修改的定义之后就不能修改了，所以加载页面控制台就会报错，并且他是必须要初始化的，不能光申明不初始化

#  解构赋值

​	传统复制就是传统的定义变量并赋值，解构赋值他将每个变量拆开，然后每个值拆开并进行赋值

​    //传统赋值

​    var x = 1, y = 2 , z = 3;

​    //解构复制

​    let [ x , y , z] = [ 1 , 2 , 3 ];



​	还有可以使用

​		let user = {name:'黄康',age:18};

​    		let {name,age} = user;

​		注意：这里的字段名称必须和属性名称一致

​    		console.log(name+"\t"+age);

# 模板字符串

​	在ES6中可以使用模板字符串来获取对象，并且完美兼容字符串格式

​	例如：

 		let one = "Hello ";

​    		let two = "黄康";

​		console.log( ` ${one} 你好 ${two}`);

​		这里必须使用`隔开而不是单引号

​	这样格式就是

​		![](image\模板字符串.png)

​	这里他完美兼容了里面的字符串并且没有使用任何的转义的拼接

​	并且他还可以直接定义多行字符串不用换行，还会保留格式

​	还能在定义的字符串中调用方法

​		    	function name(){

​        			return '黄康';

​    			}

​    			let names = `你的名字 ${name()}`;

​			注意这里还是`反引号

​			结果如图

​			![](image\插值方法.png)

# 对象拓展运算符

​			使用对象运算符可以复制对象，还有合并对象，例如下面的

​		    	let one = {hello:"Hello"};

​    			let two = {name:"黄康"};

​    			let ni = { ...one,...two};

​    			console.log(ni);

​			![](image\对象运算符.png)

# 函数默认参数

​		  function showa(name , age = 19){

​    			console.log(`${name}    ${age}`);

  		}

  		showa("黄康");

​		这样我们可以在方法内定义一个默认的年龄，如果不传入年龄则默认就是19，如果传入就按照参数进行输出

# 可变不定长度参数

​		我们可以在方法中的参数中传入多个参数，或者一个，这样长度不一定的也是可以定义的

，并且他是一个数组的格式传入的



  function showa(...values){

​    console.log(values);

​    console.log(values[1]);

​    console.log(values.length);

  }

  showa("黄康",20);

![](image\不定长度参数.png)‘

# 箭头函数

​	他和Java中的Lambda表达式类似

​	 

​    let nice = (a) => a+1; 

​    console.log(nice(4));