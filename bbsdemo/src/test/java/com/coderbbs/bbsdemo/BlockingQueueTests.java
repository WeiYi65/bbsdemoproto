package com.coderbbs.bbsdemo;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);//最多存10个数
        new Thread(new Producer(queue)).start();//生产者线程开始生产数据
        new Thread(new Consumer(queue)).start();//整个线程类比工厂，一个工厂不断生产，三个消费者并发消费
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();

    }
}

//生产者和消费者的类
class Producer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i<100; i++){//最大产能
                Thread.sleep(20);//生产间隔
                queue.put(i);//这就是一个能阻塞的队列
                System.out.println(Thread.currentThread().getName()+"produce:"+queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


class Consumer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while(true){
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"consume:"+queue.size());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}