package test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TestCyclicBarrier {

    public static void main(String[] args) {
        //创建CyclicBarrier，收集到7颗之后执行的线程，也可以不执行
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,() -> {
            System.out.println("集齐7颗龙珠");
        });
        //创建线程收集7颗龙珠
        for (int i = 0; i < 7; i++) {
            new Thread(() -> {
                System.out.println("收集第"+ Thread.currentThread().getName()+"颗龙珠");
                try {
                    //cyclicBarrier每次await增量+1
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("退还第"+ Thread.currentThread().getName()+"颗龙珠");
            },String.valueOf(i+1)).start();
        }
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 7; i++) {
//            new Thread(() -> {
//                System.out.println("收集第"+ Thread.currentThread().getName()+"颗龙珠");
//            },String.valueOf(i+1)).start();
//        }
//        System.out.println("集齐7颗龙珠");
//    }

}
