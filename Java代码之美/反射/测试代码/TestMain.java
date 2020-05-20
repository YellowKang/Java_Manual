package club.kang.blog.web.test.TestClassMy;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestMain {


    //    设置普通的类中的私有属性
    @Test
    public void testField() {

        try {
//            加载类模板
            Class classas = Class.forName("club.kang.blog.web.test.TestClassMy.Student");
//            设置创建对象,用来存放数据
            Object object = classas.newInstance();
//            设置获取到类中的属性
//            因为getField只能调用到公共的成员，
//            不能调用私有的我们用getDeclaredField聚能加载所有的甚至父类的
            Field field = classas.getDeclaredField("name");
//            这里设置一个可以设置所有的成员，而不是公共的，开启这个之后就可以设置私有的属性了
            field.setAccessible(true);
//            将object对象设置name属性，值为黄康
            field.set(object, "黄康");


//            下面我们来创建一个获取所有的成员,他是一个Field的数组我们获取所有的成员

            Field[] declaredFields = classas.getDeclaredFields();
            for (Field fe:declaredFields) {
                //打印所有的成员
                System.out.println(fe);
                //获取到成员的变量名
                System.out.println(fe.getName());
            }



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }


    //    设置和加载有参构造函数
    @Test
    public void testConstructor() {
        try {
//            获取类模板
            Class classas = Class.forName("club.kang.blog.web.test.TestClassMy.Student");
//            设置构造方法的参数格式
            Constructor constructor = classas.getConstructor(String.class, int.class, String.class);
//            将获取到的值赋给对象
            Object object = constructor.newInstance("张三", 18, "男");
//            现在创建的就是有有参数的一个对象了


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testMethoe(){
        try {
            Class classas = Class.forName("club.kang.blog.web.test.TestClassMy.Student");
            Object object = classas.newInstance();

//            单个无参公共方法调用
            Method method1 = classas.getMethod("hello");
//            这里我们需要写上参数的值得class类型
            Method method2 = classas.getDeclaredMethod("mySystem", String.class);

//            因为方法的返回值不定所以用object，如果是void类型的方法的话则返回null
            Object invoke1 = method1.invoke(object);
//            这里的object对象创建的一个初始化过的对象用来存放数据，而不是一个单单的模板
            Object invoke2 = method2.invoke(object,"张三");

//            获取到所有的方法，他是一个方法的数组，
//            这里的getDeclaredMethods表示获取到所有的一切方法而不是只是公共的
            Method[] declaredMethods = classas.getDeclaredMethods();
            for (Method me:declaredMethods) {
//                打印这个方法的地址和详细信息
                System.out.println(me);
//                打印这个方法的方法名
                System.out.println(me.getName());
            }

//            下面我们来介绍私有方法的调用

            Method method3 = classas.getDeclaredMethod("helloPrivate");
//            这里我们开启暴力的设置，可以设置所有能获取到的。不论是什么修饰的private也能调用
            method3.setAccessible(true);
            method3.invoke(object);

//            这就是基本简单方法反射调用的操作啦
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


//    测试加载父类
    @Test
    public void  testExtend(){
//        这个比较简单

        try {
//            我们以HashMap为例子
            Class classas = Class.forName("java.util.HashMap");
//            直接调用类模板的.getSuperclass()就能调用到父类的类了，可以为所欲为的操作父类
            Class superclass = classas.getSuperclass();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


//    测试反射接口
    @Test
    public void  testInterface(){
        try {
            //这里我们还是以HashMap为例子
            Class classas = Class.forName("java.util.HashMap");

            //加载对象，并初始化类模板
            Object object = classas.newInstance();

            //这里获取到的是一个Class的数组，表示他的所有的接口的类
            Class[] interfaces = classas.getInterfaces();

            for (Class cs:interfaces) {
                System.out.println(cs);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test6() {
        try {
            Class classas = Class.forName("java.util.HashMap");
            Object object = classas.newInstance();

            Field[] fields = classas.getDeclaredFields();
            Class cs = classas.getSuperclass();
            Class[] interfaces = classas.getInterfaces();
            Method[] methods = classas.getDeclaredMethods();
            Method[] declaredMethods = cs.getDeclaredMethods();
            classas.getInterfaces();


            System.out.println("父类：" + cs);


            for (Class ins : interfaces) {
                System.out.println("接口：" + ins);
            }
            for (Field f : fields) {
                System.out.println("属性" + f);
            }

            for (Method me:methods) {
                System.out.println("方法" + me);
            }

            for (Method me:declaredMethods) {
                System.out.println("父类方法：" + me);
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
