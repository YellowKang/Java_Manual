package test;

import java.util.concurrent.Semaphore;

public class TestSemaphore {
    public static void main(String[] args) {
        //创建Semaphore并且制定长度为3
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                try {
                    //信号灯使用+1
                    semaphore.acquire();
                    //开始使用
                    System.out.println("第" + Thread.currentThread().getName() + "辆车进来");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        //随机Id睡眠
                        Thread.sleep(Thread.currentThread().getId() * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("第" + Thread.currentThread().getName() + "辆车出去");
                    //使用完成释放信号灯
                    semaphore.release();
                }
            },String.valueOf(i+1)).start();
        }
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 6; i++) {
//            new Thread(() -> {
//                    System.out.println("第" + Thread.currentThread().getName() + "辆车进来");
//                    System.out.println("第" + Thread.currentThread().getName() + "辆车出去");
//            },String.valueOf(i+1)).start();
//        }
//    }
}
