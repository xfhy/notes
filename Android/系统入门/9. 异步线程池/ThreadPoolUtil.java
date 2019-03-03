package com.xfhy.threadpooldemo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xfhy on 2019/1/27 12:37
 * Description : 线程池工具类
 */
public class ThreadPoolUtil {


    /**
     * 用来执行任务的
     */
    public static final Executor THREAD_POOL_EXECUTOR;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程: 最少2个,最多4个,尽量比CPU数量少1,这样避免饱和
     */
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    /**
     * 最大线程数量
     */
    private static final int MAXIUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * keep alive time
     */
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        //原子操作  详细解说:https://blog.csdn.net/fanrenxiang/article/details/80623884
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            //序号 增
            return new Thread(r, "ThreadPoolUtil #" + mCount.getAndIncrement());
        }
    };
    /**
     * 一个由链表结构组成的有界队列   此队列按照先进先出的顺序进行排序。
     */
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(16);

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        //设置核心线程也会超时
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    private ThreadPoolUtil() {
    }

    /**
     * 获取线程池单例
     */
    public static ThreadPoolUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 执行任务
     */
    public static void execute(Runnable task) {
        THREAD_POOL_EXECUTOR.execute(task);
    }

    private static class SingletonHolder {
        private static final ThreadPoolUtil INSTANCE = new ThreadPoolUtil();
    }

}
