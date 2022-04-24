package com.ls.s02;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/23 13:07
 */
public class InterestingTest {

    public static void main(String[] args) {
        //Interesting interesting = new Interesting();
        //new Thread(() -> interesting.add()).start();
        //new Thread(() -> interesting.compare()).start();

        Interesting interesting2 = new Interesting();
        new Thread(() -> interesting2.add2()).start();
        new Thread(() -> interesting2.compare2()).start();
    }
}
