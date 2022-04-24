package com.ls.s02;

import jdk.nashorn.internal.objects.annotations.Getter;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/23 13:17
 */
public class StaticLockDemo {
    /**
     * 静态字段属于类，类级别的锁才能保护；而非静态字段属于类实例，实例级别的锁就可以保护。
     */
    private static int counter = 0;

    public static int reset() {
        counter = 0;
        return counter;
    }

    /**
     * 在非静态的 wrong 方法上加锁，只能确保多个线程无法执行同一个实例的 wrong 方法，
     * 却不能保证不会执行不同实例的 wrong 方法。而静态的 counter 在多个实例中共享，
     * 所以必然会出现线程安全问题
     *
     * 解决办法1,将wrong方法变成静态方法,此时synchronized锁住的是类,可以达到效果
     * 但是这样就改变了原有的代码结构了非静态变成了静态
     */
    public synchronized void wrong() {
        counter++;
    }

    private static Object locker = new Object();

    public void right() {
        synchronized (locker) {
            counter++;
        }
    }

    public static int getCounter() {
        return counter;
    }

    /**
     * todo 思考题 代码块级别的 synchronized 和方法上标记 synchronized 关键字，在实现上有什么区别?
     */
}
