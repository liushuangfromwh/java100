package com.ls.s03;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/26 16:36
 */
@RestController
@RequestMapping("OomDemo")
@Slf4j
public class OomDemo {


    /**
     * Exception in thread "http-nio-45678-ClientPoller" java.lang.OutOfMemoryError: GC overhead limit exceeded
     * @throws InterruptedException
     */
    @GetMapping("oom1")
    public void oom1() throws InterruptedException {

        /**
         * 线程池的工作队列直接 new 了一个 LinkedBlockingQueue，而默认构造方法的 LinkedBlockingQueue
         * 是一个 Integer.MAX_VALUE 长度的队列，可以认为是无界的.
         * 虽然使用 newFixedThreadPool 可以把工作线程控制在固定的数量上，但任务队列是无界的。
         * 如果任务较多并且执行较慢的话，队列可能会快速积压，撑爆内存导致 OOM
         */
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        /**
         * 从日志中可以看到，这次 OOM 的原因是无法创建线程，翻看 newCachedThreadPool 的源码可以看到，
         * 这种线程池的最大线程数是 Integer.MAX_VALUE，可以认为是没有上限的，
         * 而其工作队列 SynchronousQueue 是一个没有存储空间的阻塞队列。
         * 这意味着，只要有请求到来，就必须找到一条工作线程来处理，如果当前没有空闲的线程就再创建一条新的。
         */
        //ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool(1);
        //打印线程池的信息，稍后我会解释这段代码
        System.out.println(threadPool);
        for (int i = 0; i < 100000000; i++) {
            threadPool.execute(() -> {
                String payload = IntStream.rangeClosed(1, 1000000)
                        .mapToObj(__ -> "a")
                        .collect(Collectors.joining("")) + UUID.randomUUID().toString();
                try {
                    //睡眠1小时
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                }
                log.info(payload);
            });
        }

        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);
    }

    /**
     * 一个事故：用户注册后，我们调用一个外部服务去发送短信，发送短信接口正常时可以在 100 毫秒内响应，TPS 100 的注册量，CachedThreadPool
     * 能稳定在占用 10 个左右线程的情况下满足需求。在某个时间点，外部短信服务不可用了，我们调用这个服务的超时又特别长，比如 1 分钟，
     * 1 分钟可能就进来了 6000 用户，产生 6000 个发送短信的任务，需要 6000 个线程，没多久就因为无法创建线程导致了 OOM，
     * 整个应用程序崩溃
     */

    /**
     * 使用建议 :
     * 不建议使用 Executors 提供的两种快捷的线程池，原因如下：
     * 1. 我们需要根据自己的场景、并发情况来评估线程池的几个核心参数，包括核心线程数、最大线程数、线程回收策略、工作队列的类型，
     * 以及拒绝策略，确保线程池的工作行为符合需求，一般都需要设置有界的工作队列和可控的线程数。
     * 2. 任何时候，都应该为自定义线程池指定有意义的名称，以方便排查问题。当出现线程数量暴增、线程死锁、线程占用大量 CPU、
     * 线程执行出现异常等问题时，我们往往会抓取线程栈。此时，有意义的线程名称，就可以方便我们定位问题。
     */


    private void printStats(ThreadPoolExecutor threadPool) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("=========================");
            log.info("Pool Size: {}", threadPool.getPoolSize());
            log.info("Active Threads: {}", threadPool.getActiveCount());
            log.info("Number of Tasks Completed: {}", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());

            log.info("=========================");
        }, 0, 1, TimeUnit.SECONDS);
    }


    /**
     * <pre>
     *      1. 不会初始化 corePoolSize 个线程，有任务来了才创建工作线程；
     *      2. 当核心线程满了之后不会立即扩容线程池，而是把任务堆积到工作队列中；
     *      3. 当工作队列满了后扩容线程池，一直到线程个数达到 maximumPoolSize 为止；
     *      4. 如果队列已满且达到了最大线程后还有任务进来，按照拒绝策略处理；
     *      5. 当线程数大于核心线程数时，线程等待 keepAliveTime 后还是没有任务需要处理的话，收缩线程到核心线程数。
     *
     *      1. 声明线程池后立即调用 prestartAllCoreThreads 方法，来启动所有核心线程；
     *      2. 传入 true 给 allowCoreThreadTimeOut 方法，来让线程池在空闲的时候同样回收核心线程。
     * </pre>
     *

     * @return
     * @author liushuang26
     */
    @GetMapping("right")
    public int right() throws InterruptedException {
        //使用一个计数器跟踪完成的任务数
        AtomicInteger atomicInteger = new AtomicInteger();
        //创建一个具有2个核心线程、5个最大线程，使用容量为10的ArrayBlockingQueue阻塞队列作为工作队列的线程池，使用默认的AbortPolicy拒绝策略
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2, 5,
                5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadFactoryBuilder().setNameFormat("demo-threadpool-%d").get(),
                new ThreadPoolExecutor.AbortPolicy());

        printStats(threadPool);
        //每隔1秒提交一次，一共提交20次任务
        IntStream.rangeClosed(1, 20).forEach(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int id = atomicInteger.incrementAndGet();
            try {
                threadPool.submit(() -> {
                    log.info("{} started", id);
                    //每个任务耗时10秒
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    log.info("{} finished", id);
                });
            } catch (Exception ex) {
                //提交出现异常的话，打印出错信息并为计数器减一
                log.error("error submitting task {}", id, ex);
                atomicInteger.decrementAndGet();
            }
        });

        TimeUnit.SECONDS.sleep(60);
        return atomicInteger.intValue();
    }


    /**
     * 不知道你有没有想过：Java 线程池是先用工作队列来存放来不及处理的任务，满了之后再扩容线程池。
     * 当我们的工作队列设置得很大时，最大线程数这个参数显得没有意义，因为队列很难满，或者到满的时候再去扩容线程池已经于事无补了。
     * todo
     * 1. 由于线程池在工作队列满了无法入队的情况下会扩容线程池，那么我们是否可以重写队列的 offer 方法，造成这个队列已满的假象呢？
     * 2. 于我们 Hack 了队列，在达到了最大线程后势必会触发拒绝策略，那么能否实现一个自定义的拒绝策略处理程序，这个时候再把任务真正插入队列呢？
     */



    /**
     * <pre>
     *      不久之前我遇到了这样一个事故：某项目生产环境时不时有报警提示线程数过多，超过 2000 个，收到报警后查看监控发现，
     *      瞬时线程数比较多但过一会儿又会降下来，线程数抖动很厉害，而应用的访问量变化不大。
     *
     *      为了定位问题，我们在线程数比较高的时候进行线程栈抓取，抓取后发现内存中有 1000 多个自定义线程池。
     *      一般而言，线程池肯定是复用的，有 5 个以内的线程池都可以认为正常，而 1000 多个线程池肯定不正常。
     *
     *      在项目代码里，我们没有搜到声明线程池的地方，搜索 execute 关键字后定位到，原来是业务代码调用了一个类库来获得线程池，
     *      类似如下的业务代码：调用 ThreadPoolHelper 的 getThreadPool 方法来获得线程池，然后提交数个任务到线程池处理，看不出什么异常。
     * </pre>
     *

     * @return
     * @author liushuang26
     */
    @GetMapping("wrong")
    public String wrong() {
        ThreadPoolExecutor threadPool = ThreadPoolHelper.getThreadPool();
        IntStream.rangeClosed(1, 10).forEach(i -> {
            threadPool.execute(() -> {
            //...
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
            });
        });
        return "OK";
    }


    /**
     * 如何合理的设置线程池呢
     * 要根据任务的“轻重缓急”来指定线程池的核心参数，包括线程数、回收策略和任务队列：
     * 1. 对于执行比较慢、数量不大的 IO 任务，或许要考虑更多的线程数，而不需要太大的队列。
     * 2. 而对于吞吐量较大的计算型任务，线程数量不宜过多，可以是 CPU 核数或核数 *2
     * （理由是，线程一定调度到某个 CPU 进行执行，如果任务本身是 CPU 绑定的任务，那么过多的线程只会增加线程切换的开销，并不能提升吞吐量），
     * 但可能需要较长的队列来做缓冲。
     */
}
