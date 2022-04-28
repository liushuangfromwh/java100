package com.ls.s03;

import jodd.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/27 18:34
 */
public class ThreadPoolHelper {

    /**
     * newCachedThreadPool 会在需要时创建必要多的线程，业务代码的一次业务操作会向线程池提交多个慢任务，
     * 这样执行一次业务操作就会开启多个线程。如果业务操作并发量较大的话，的确有可能一下子开启几千个线程。
     * <p>
     * 那，为什么我们能在监控中看到线程数量会下降，而不会撑爆内存呢？
     * <p>
     * 回到 newCachedThreadPool 的定义就会发现，它的核心线程数是 0，而 keepAliveTime 是 60 秒，也就是在 60 秒之后所有的线程都是可以回收的。
     *
     * @return
     */
    public static ThreadPoolExecutor getThreadPool() {
        //线程池没有复用
        return (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }


    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10, 50,
            2, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            new ThreadFactoryBuilder().setNameFormat("demo-threadpool-%d").get());

    public static ThreadPoolExecutor getRightThreadPool() {
        return threadPoolExecutor;
    }
}
