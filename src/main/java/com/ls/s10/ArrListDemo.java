package com.ls.s10;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/28 17:56
 */
@Slf4j
public class ArrListDemo {

    public static void main(String[] args) {
        demo4();
    }

    private static void demo1() {
        int[] arr = {1, 2, 3};
        List list = Arrays.asList(arr);
        log.info("list:{} size:{} class:{}", list, list.size(), list.get(0).getClass());
        //17:56:52.612 [main] INFO com.ls.s10.ArrListDemo - list:[[I@1f8302d] size:1 class:class [I
        // list长度是1  里面放的是一个 int数组   不能转换基本类似数组
    }

    private static void demo2() {
        int[] arr1 = {1, 2, 3};
        List list1 = Arrays.stream(arr1).boxed().collect(Collectors.toList());
        log.info("list:{} size:{} class:{}", list1, list1.size(), list1.get(0).getClass());
        //18:03:52.803 [main] INFO com.ls.s10.ArrListDemo - list:[1, 2, 3] size:3 class:class java.lang.Integer
        // 可以转换
    }

    private static void demo3() {
        String[] arr = {"1", "2", "3"};
        List<String> list = Arrays.asList(arr);
        log.info("list:{} size:{} class:{}", list, list.size(), list.get(0).getClass());
        //17:58:48.387 [main] INFO com.ls.s10.ArrListDemo - list:[1, 2, 3] size:3 class:class java.lang.String

        //但是不能正常使用,会报错
        //list.add("4");
        //Exception in thread "main" java.lang.UnsupportedOperationException
        // 因为返回的是arrays的内部类  没有实现add方法,直接调用父类的方法就会报错.
    }

    private static void demo4() {

        String[] arr = {"1", "2", "3"};
        List list = new ArrayList(Arrays.asList(arr));
        arr[1] = "4";
        try {
            list.add("5");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("arr:{} list:{}", Arrays.toString(arr), list);
        //18:11:45.360 [main] INFO com.ls.s10.ArrListDemo - arr:[1, 4, 3] list:[1, 2, 3, 5]
    }
}
