# 初始化Vue项目

首先我们需要安装Vue，Vue-cli，webpack

我们进入我们需要创建项目的目录，然后输入

```
vue init webpack demo
```

这样就初始化了一个项目，然后我们根据他们的提示来进行

# Vue结构概述





# Vue基本操作

## 	模板语法

​		要使用Vue.js首先引入Vue的js包，

​		    <script src="vue.min.js"> 

​		然后我们来操作一个对象试下

​		

```
    <h1 v-bind:title="toto" id="ni">
        {{message}}
    </h1>

    <script src="vue.min.js">
    </script>
    
    <script>
        new Vue({
            el: '#ni',
            data: {
                message:"你好a",
                toto:"你好a的标题"
            }
        });
    </script>
```

这里我们操作了一个id为ni的一个元素，并且初始化了一个message数据，然后里面的内容是message，并且给他的title上绑定了一个toto的数据，在他的属性中绑定不能直接使用{{}}需要使用v-Bind

或者我们将v-bind省略，如下

```
 <h1 :title="toto" id="ni">
```



或者我们在里面添加一段html代码

```
<div id="ni" v-html="message">              
                                  
</div>                                      
                                            
<script>                                    
    new Vue({                               
        el: '#ni',                          
        data: {                             
            message:"<h1>你好a</h1>",         
            toto:"你好a的标题"                   
        }                                   
    });                                     
</script>                                   
```



## 数据双向绑定

​	我们可以动态的让数据双向绑定，例如显示姓名，姓名的数据来源于name属性，其他的元素操作了name属性，那么姓名也会跟着变

​	  

```
   <div id="searchH1">
            <input type="text" v-model="searchs.text" />  
            <h1> {{searchs.text}}</h1>
    </div>
    <script src="vue.min.js">
    </script>
    
    <script>
        new Vue({
            el: '#searchH1',
            data: {
                searchs:{
                    text:"测试"
                }
            }
        });
    </script>
```

这里我们使用search下面的text来示例，将文本中的数据在h1上显示，然后我们操作文本框，对text进行绑定，如果我们修改了他的值的话那么也会跟着改变

## 条件渲染显示

​     我们可以像选择同意许可一样来选择他，例如点击许可同意就不会出现一大堆，如果点击则会显示很多我们可以使用条件渲染

​	

```
    <div id="searchH1">
        <input type="checkbox" v-model="searchs.chek" />同意
        <p v-if="searchs.chek">同意</p>
        <p v-if="!searchs.chek">请同意许可！</p>
    </div>
    <script src="vue.min.js">
    </script>
    <script>
        new Vue({
            el: '#searchH1',
            data: {
                searchs:{
                    text:"测试",
                    chek:false
                }
            }
        });
    </script>
```



![](image\未同意.png)



![](image\同意.png)



这里我们也可以使用v-else

​     还能使用v-show，但是他们的使用的场景是不一样的，show是现实和隐藏，而if则是根本不会去渲染这个元素，也就是你选中了他就有，不选中不存在，频繁点击性能损耗大

## V-for循环

```
    <div id="searchH1">
        <p v-for="n in list">
            {{n}}
        </p>
    </div>
    <script src="vue.min.js">
    </script>
    <script>
        new Vue({
            el: '#searchH1',
            data: {
                list:[1,3,5,7,9]
            }
        });
    </script>
```

遍历数组

结果如下

​	![](image\v-for.png)



### 遍历对象属性

​	

```
<div id="searchH1">
      <table border="1">
        <tr v-for="(user,index) in users">
            <td>{{index}}</td>
            <td>{{user.stuon}}</td>
            <td>{{user.name}}</td>
            <td>{{user.age}}</td>
        </tr>
      </table>
    </div>
    <script src="vue.min.js">
    </script>
    <script>
        new Vue({
            el: '#searchH1',
            data: {
                users: [
                    {stuon:2001,name:"黄康",age:18},
                    {stuon:2002,name:"Bigkang",age:19},
                    {stuon:2003,name:"康哥",age:20}
                ]
            }
        });
    </script>
```

这样就可以遍历对象属性和index了

​	他遍历的user相当与遍历出来的每一个对象，index就是他的索引



##  计算属性（过滤属性）

```
   <div id="searchH1">
        <p v-for="n in list">
            {{n}}
        </p>
        <br />
        <br>
        <br>
        <p v-for="ns in relist">
            {{ns}}
        </p>
    </div>
    <script src="vue.min.js">
    </script>
    <script>
        new Vue({
            el: '#searchH1',
            data: {
                list:[1,3,5,7,9]
            },
            computed: {
                relist(){
                    return this.list.filter( num =>  num  > 3);
                }
            }
        });
    </script>
```

过滤掉大于3的属性值

## 监听属性

我们可以监听某一个属性当他发生改变时进行一系列的操作

例如改动文本框时

```
<div id="searchH1">
      <input type="text" v-model="firstname">
      <input type="text" v-model="lastname">
      <label>{{fullname}}</label>
    </div>
    <script src="vue.min.js">
    </script>
    <script>
        new Vue({
            el: '#searchH1',
            data: {
                firstname: "1",
                lastname: "2",
                fullname: "3"
            },
            watch: {
                firstname(val){
                    console.log("改动了firstname");
                    this.fullname = val +  this.lastname;
                },
                lastname(val){
                    console.log("改动了lastname");
                    this.fullname = this.firstname + val;
                }
            }
        });
    </script>
```



## 局部修饰

我们可以将数据进行局部修饰例如0和1的男女性别

```
    <div id="searchH1">
      <p v-for="(item,index) in users">
        {{item.name}}
        <br>
        {{item.gender | filtergender}}
      </p>
    </div>
    <script src="vue.min.js">
    </script>
    <script>
        new Vue({
            el: '#searchH1',
            data: {
               users: [
                   {name:"黄康",gender:0},
                   {name:"ytt",gender:1}
               ]
            },
            filters: {
                filtergender(gendera){
                    return gendera == 0 ? '男':'女'
                }
            }
        });
    </script>
```

这样我们就避免了再显示的元素中进行修饰

