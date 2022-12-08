# 引入依赖

```xml
    <dependency>
                <groupId>org.codehaus.groovy</groupId>
                <artifactId>groovy-all</artifactId>
                <version>2.4.10</version>
    </dependency>
```

# GroovyShell（shell命令）

```java
        // 直接执行Groovy脚本
        GroovyShell groovyShell = new GroovyShell();
        groovyShell.evaluate("def soutHello(){  println 'Hello World!'  }\n" +
                "soutHello()");


-------------------------------------------
  
  
				// 使用脚本调用方法
				GroovyShell groovyShell = new GroovyShell();
        // 解析脚本执行
        Script script = groovyShell.parse("def soutHello(){  println 'Hello World!'  }\n" +
                "soutHello()");
        // 执行方法
        script.invokeMethod("soutHello", null);


-------------------------------------------

  
        GroovyShell groovyShell = new GroovyShell();
        // 解析脚本执行
        Script script = groovyShell.parse("def soutHello(a,b){  return a+'\t'+b  }\n" +
                "soutHello()");
        // 执行方法带参数
        String result = (String)script.invokeMethod("soutHello", Arrays.asList(1, 3).toArray());
        System.out.println(result);
```



# GroovyScriptEngine（引擎）

## 测试调用

​		首先创建脚本testABCD.groovy在src/main/resources/scripts目录下

​		内容如下

```groovy
package scripts

def runA = {
    a += 1
}
def runB = {
    b += "123"
}
def runC = {
    def c = c as Map<Object, Object>

    c['a'] = '1'
    c['b'] = '2'
}
def runD = {

    def d = d as ArrayList<Object>

    d.add(1)
    d.add(2)
    d.add(3)

}

runA.call()
runA.call()
runB.call()
runC.call()
runD.call()


return 'exec success!'
```

​		然后测试类执行

```java
        // 创建脚本引擎
        GroovyScriptEngine engine = null;
        try {
            // 定义脚本目录
            engine = new GroovyScriptEngine("src/main/resources/scripts");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 创建绑定数据
        Binding bindingData = new Binding();
        Integer a = 1;
        String b = "null";
        Map<Object, Object> c = new HashMap<>();
        ArrayList<Object> d = new ArrayList<>();
        // 绑定变量
        bindingData.setProperty("a", a);
        bindingData.setProperty("b", b);
        bindingData.setProperty("c", c);
        bindingData.setProperty("d", d);

        try {
            // 执行的脚本
            Object result = engine.run("testABCD.groovy", bindingData);
            System.out.println(result);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

        // 输出传入的变量
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
```

​		打印结果发现，执行成功基础数据类型变量没有变动和Java调用方法引用一致

```
exec success!
1

{a=1, b=2}
[1, 2, 3]
```

## 测试脚本类

​		那么我们可不可以使用Groovy工具类呢，答案是可以的。

​		脚本下新建GroovyUtil.groovy

```groovy
package scripts

class GroovyUtil {

    static Integer getInt(){
        return 9999
    }
}
```

​		创建脚本文件testGroovy.groovy，使用脚本调用静态方法

```groovy
import scripts.GroovyUtil

return GroovyUtil.getInt()
```

​		测试类调用即可

```java
   			// 创建脚本引擎
        GroovyScriptEngine engine = null;
        try {
            // 定义脚本目录
            engine = new GroovyScriptEngine("src/main/resources/scripts");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 创建绑定数据
        Binding bindingData = new Binding();
        try {
            // 执行的脚本
            Object result = engine.run("testGroovy.groovy", bindingData);
            System.out.println(result);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

```

