# 什么是JUC？

​	JUC是java.util.concurrent包下面给我们提供的关于并发编程中使用的工具类，他可以更好的帮助我们进行并发编程

# JUC核心

## 	Lock 同步锁

​			什么是Lock同步锁？

​			lock同步锁类似于多线程中的synchronized关键字，Lock实现提供更广泛的锁定操作可以比使用 	synchronized获得方法和声明更好。他们允许更灵活的结构，可以有完全不同的特性，可以支持多个相关的 Condition对象。 	

​			如何使用Lock锁？

下面这里是一个简单的线程的调用，但是这个代码是线程不安全的，因为他没有加锁如果对同一个数据进行操作会引起数据的不一致，以及脏数据

```
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Ticket //实例例eld +method
{
    private int number = 30;

    public void sale() {
        try {
            if(number>0) {
                System.out.println(Thread.currentThread().getName() + "卖出" + (number--) + "\t 还剩" + number);
            }
            } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

}


public class SaleTicket {

    public static void main(String[] args) {//main所有程序
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for (int i = 1; i <= 40; i++) ticket.sale();
        }, "AA").start();
        new Thread(() -> {
            for (int i = 1; i <= 40; i++) ticket.sale();
        }, "BB").start();
        new Thread(() -> {
            for (int i = 1; i <=40; i++) ticket.sale();
        }, "CC").start();
        new Thread(() -> {
            for (int i = 1; i <=40; i++) ticket.sale();
        }, "DD").start();
    }
}
```



我们运行了上面的方法发现他并不是按照一行一行的减的而是引发了数据的错误那么怎么解决这个问题呢、

```
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Ticket //实例例eld +method
{
    private int number = 30;

    private Lock lock = new ReentrantLock();
    public void sale() {
        lock.lock();
        try {
            if(number>0) {
                System.out.println(Thread.currentThread().getName() + "卖出" + (number--) + "\t 还剩" + number);
            }
            } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

public class SaleTicket {

    public static void main(String[] args) {//main所有程序
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for (int i = 1; i <= 40; i++) ticket.sale();
        }, "AA").start();
        new Thread(() -> {
            for (int i = 1; i <= 40; i++) ticket.sale();
        }, "BB").start();
        new Thread(() -> {
            for (int i = 1; i <=40; i++) ticket.sale();
        }, "CC").start();
        new Thread(() -> {
            for (int i = 1; i <=40; i++) ticket.sale();
        }, "DD").start();
    }
}
```

我们这里可以看到并没有加什么东西，只是加了一个Lock锁，lock.lock加锁lock.unlock释放锁，这个其实和多线程的synchronized理论上来说效果是一样的

### synchronized和Lock的区别

​		synchronized和Lock的区别其实不算太大，但是synchronized能做的lock都能做，并且lock在线程量较大的时候性能比synchronized要高很多，并且lock提供了很多的拓展能更加方便的使用线程，可以支持多个相关的 Condition对象。 更加灵活的操作线程

## Callable 接口

​		什么是Callable接口？

​				Callable接口类似于Runnable接口，但是在我们的JUC中它提供了更加多的优势



​		Callable的优势：

​			1、有返回值				可以更加好的观察线程的执行情况

​			2、可以异步完成任务		更加好的拓展了实现，减少了阻塞风险

​			3、可以抛出异常			发生异常时更快速地定位和处理

​			4、实现方法不同			Runnable使用runCallable使用call

### FutureTask任务

​		我们可以通过FutureTask来使用

```
        FutureTask<Integer> futureTask = new FutureTask<>(new Ticket());
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
```

​		这样就能获取到线程执行之后的结果了

​		这就是简单的使用我们的Callable

## Condition 控制线程通信

## ReadWriteLock 读写锁



## CountDownLatch计数器

​	什么是CountDownLatch计数器？他就是用来计数的一个工具在多线程中我们可以使用他来计数，可以在一定的高并发场合下使用他来进行计数，以便完成其他的操作‘

​	语法？

​		这里我们创建了一个CountDownLatch计数器，并且给他赋值为80，这里表示我们有

​		private CountDownLatch countDownLatch = new CountDownLatch(80);



​		上面是创建下面是使用

​		countDownLatch.countDown();				进行计数也就是-1

​		countDownLatch.getCount();				获取当前的计数器的值



​		如何使用计数？

​		private CountDownLatch countDownLatch = new CountDownLatch(80);

​		多线程代码

​		{	

​				//如果计数器为0从而进行操作

​				if(countDownLatch.getCount() == 0)

​				{    

​					System.out.println("卖完了");    		

​				}

​				进行操作后计数器减一

​				countDownLatch.countDown();	

​		}

## CyclicBarrier





## 线程池

# 