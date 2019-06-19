package test;

import java.util.concurrent.CountDownLatch;

class TestCountDownLatch {

    public static void main(String[] args) {
        //创建CountDownLatch对象，并且初始化10个单位
        CountDownLatch countDownLatch = new CountDownLatch(10);
        //循环10个线程，每一个线程都杀一个提莫
        for (int i = 0; i < 10; i++) {
            new Thread( () -> {
                System.out.println("正在杀第"+Thread.currentThread().getName()+"个提莫");
                //杀完后countDownLatch计数countDown一下
                countDownLatch.countDown();
            },String.valueOf(i+1)).start();
        }
        try {
            //线程等待，等待计数完成释放线程
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("已经杀完提莫");
    }


//    public static void main(String[] args) {
//        //循环10个线程，每一个线程都杀一个提莫
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                System.out.println("正在杀第"+Thread.currentThread().getName()+"个提莫");
//            },String.valueOf(i+1)).start();
//        }
//        System.out.println("已经杀完提莫");
//    }

}
